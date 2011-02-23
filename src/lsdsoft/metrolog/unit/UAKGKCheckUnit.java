// Управление поверочной установкой УАК-ГК
package lsdsoft.metrolog.unit;


import java.io.*;
import java.text.*;
import java.util.*;
import javax.comm.*;

import com.lsdsoft.comm.*;
import lsdsoft.metrolog.*;
import lsdsoft.units.*;
import lsdsoft.zeus.*;
import lsdsoft.util.*;
import lsdsoft.metrolog.im.*;
import org.w3c.dom.*;


public class UAKGKCheckUnit
    extends AbstractCheckUnit
    implements
    ChannelDataEventListener {
    private static final int UAKSI_COMMAND_IDLE = 0x00;
    private static final int UAKSI_COMMAND_GOTO_POINT = 0x55;
    private static final int UAKSI_COMMAND_AUTO_FIND_MARKER = 0x56;
    public static final int DEVICE_STATE_BAD  =  0x00;
    public static final int DEVICE_STATE_OK   =  0x01;
    public static final int DEVICE_STATE_ABSENT = 0x02;
    public static final int DEVICE_STATE_ERROR  = 0x03;
    public static int sections[] = { 0, 300, 600, 900, 1200, 1500 };
    // номер версии УАК-СИ: влияет на формат команд
    // пока только первая версия
    private int version = 2;
    private CommConnection connection = new CommConnection();
    private OutputStream outs;
    private InputStream ins;
    private FIFOBuffer inputBuffer = new FIFOBuffer(4096);
    private FIFOBuffer outputBuffer = new FIFOBuffer(128);
    private byte[] commandBuffer = new byte[5];
    //private byte[] inputBuffer = new byte[32];
    private int[] executedCommands = new int[3];
    private Angle angleLimit = new Angle( 1000 );
    //private InсlinometerAngles lirAngles = new InсlinometerAngles();
    //private RespondEventListener respondListener = null;
    private PostChannelDataSource postSource = new PostChannelDataSource();
    private PostChannelDataSource toolSource = new PostChannelDataSource();
    private ChannelDataSource toolOutSource;
    //private int respondMask = 0;
    private Timer timer = new Timer( true );
    ;
    private Timer toolTimer = new Timer();
    private Timer accTimer = new Timer();
    private RemainderTask task = new RemainderTask( this );
    private ToolAskTask toolTask = new ToolAskTask( this );
    private AccurateAskTask accTask = new AccurateAskTask( this );
    private Channel chan1;
    //private ChannelDataSourceFilter filter;
    private Zeus zeus = Zeus.getInstance();
    private char selectedPlane = 'x';
    boolean arrived;
    private boolean isResponded = false;
    private boolean makeMagneticCorrection = true;
    MagneticCorrectionTable corrTable = new MagneticCorrectionTable();
    //InсlinometerAngles toolAngles = new InсlinometerAngles();
    // average value of angles
    //InсlinometerAngles avAngles;
    // количество замеров углов
    int measureCount;
    // углы скважинного прибора
    //InсlinometerAngles devAngles;
    /**
     * Поправки к показаниям угловых датчиков относительно абсолютного нуля.
     * Значения поправок берутся из сертификата на установку.
     * Для получения точных показания значение поправки вычитается
     * из показаний датчиков.
     */
    //InklinometerAngles accDelta = new InklinometerAngles();
    // предел точности выхода к заданному углу
    Angle normDelta;
    int currentSection = 1;
    Object sync = new Object();
    public UAKGKCheckUnit() {
        loadProperties();
        connection.setPortName( properties.getProperty( "checkunit.port", "auto" ) );
        init();
    }

    public UAKGKCheckUnit( String portName ) {
        loadProperties();
        //connection = new CommConnection( portName );
        connection.setPortName( portName );
        init();
        //stepsPerRound = 250 * 200;
    }
    protected void loadProperties() {
        String s = zeus.getProperty("checkunit.type")+ "_" +
                   zeus.getProperty("checkunit.number")+ ".properties";
        try {
            zeus.loadProperties( properties, s );
        } catch ( Exception ex ) {
            System.err.println("Unable to load properties " + s );
        }
    }
    public void init() {
        toolSource.getProperties().setProperty( "number",
                                                zeus.getToolNumber() );
        //chan1 = addChannel( "", 0, 3 );
        addChannel( postSource.getChannel("sensors") );
        addChannel( postSource.getChannel("values") );
        try {
            //filter = new ION1Filter( toolSource );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
        try {
            for ( int i = 0; i < 6; i++ ) {
                sections[i] = Integer.parseInt(
                    properties.getProperty( "section." + ( i + 1 ), "0" ) );
            }
        } catch( Exception ex ) {
            System.err.println( "Error during loading section info:" + ex.getMessage() );
        }

    }
    public Value getMED(int section) {
        return new Value(properties.getProperty("med."+section, "0"));
    }
    public int getCurrentSection() {
        return currentSection;
    }
    public int getClosestSection(int loc) {
        int section = 0;
        int delta = 9999999;
        for(int i = 0; i < 6; i++ ) {
            int d = Math.abs(sections[i] - loc);
            if( d < delta ) {
                section = i;
                delta = d;
            }
        }
        return section + 1;
    }

    public ChannelDataSource getToolDataSource() {
        return toolSource;
    }

    public ChannelDataSource getDataSource() {
        return postSource;
    }

    public void selectHandler(String name) throws Exception {
        synchronized(sync) {
            outs.write( 'w' );
            outs.write( name.getBytes() );
            outs.write( ' ' );
        }
    }
    //private void delay( int milli ) {
    //    try {
    //        Thread.sleep( milli );
    //    } catch ( Exception ex ) {}
   // }

    private void flushInput() throws Exception {
        inputBuffer.clear();
        ins.skip( ins.available() );
    }
    private void flushOutput() throws Exception {
        outputBuffer.clear();
        outs.flush();
    }
    private void writeOutputBuffer() throws Exception {
        while(!outputBuffer.isEmpty()) {
            outs.write(outputBuffer.pop());
        }
    }
    private void readInputBuffer() throws Exception {
        while(ins.available() > 0) {
            inputBuffer.push(ins.read());
        }
    }
    protected void exec() throws Exception {
        //flushOutput();
        System.out.print(outputBuffer.toString());
        writeOutputBuffer();
    }
    /**
     * Посылка события о получении новых инклинометрических углов
     */
    private void sendEvent() {
    }

    /**
     * Проверка отклика от установки
     */
    public boolean hasRespond() {
        return isResponded;
    }

    /**
     * Обработчик канального события
     * @param ev Данные по каналу. Канал 0 - азимут, канал 1 - зенит
     */
    public void channelEvent( ChannelDataEvent ev ) {
        int channel = ev.getChannel();
        isResponded = true;
        if ( channel == 0 ) {
            double val = ev.getValue().getAsDouble();
            //lirAngles.azimuth.setAngle( val / 1000.0 );
            //lirAngles.azimut.sub( accDelta.azimut );
        }
        if ( channel == 1 ) {
            double val = ev.getValue().getAsDouble();
            //lirAngles.zenith.setAngle( val / 1000.0 );
            //lirAngles.zenit.sub( accDelta.zenit );
            sendEvent();
        }
    }


    public void setPortName( String name ) {
        connection.setPortName( name );
    }

    /**
     * Проверка соединения с установкой
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    public String testOnPorts() {
        ArrayList ports = connection.getPortNames();
        String ret = "";
        System.out.print( "searching..." );
//        testOnPort("COM4");
        for ( int i = 0; i < ports.size(); i++ ) {
            String portName = ( String )ports.get( i );
            if(testOnPort(portName) ) {
                System.out.println( "UAKGK found on " + portName );
                ret = portName;
                break;
            }
        }
        return ret;
    }

    public boolean testOnPort(String portName) {
        //ArrayList ports = connection.getPortNames();
        byte[] buf = new byte[20];
        boolean ret = false;
        //System.out.print("searching...");
        //for( int i = 0; i < ports.size(); i++ ) {
        // if not COM port - immidiatly return false
        if(!portName.startsWith("COM") )
            return false;
        System.out.println("searching on " + portName + "...");

        if(connection.isConnected())
            connection.disconnect();
        connection.setPortName(portName);
        try {
            connection.connect();
            initCommPort();
            Thread.sleep( 200 );
            connection.getPort().setInputBufferSize(150);
            connection.getPort().setOutputBufferSize(150);
            //outs.write( "test".getBytes() );
            // flush input buffer
            //ins.skip(ins.available());
            for(int i = 0; i < 3; i++) {
                outs.write( '~' );
                Thread.sleep( 200 );
                int size = ins.available();
                if ( ins.available() > 5 ) {
                    ins.read( buf, 0, ins.available() );
                    String s = new String();
                    s = buf.toString();
                    if ( startsWith( buf, "uakgk" ) ) {
                        ret = true;
                        break;
                    }
                }
            }
        } catch ( Exception ex ) {
            System.out.print(ex.getMessage());
        }
        connection.disconnect();
        if(!ret)
            System.out.println( "not found" );
        return ret;
    }
    /**
     * Подключение к установке
     * @throws Exception general exception class
     */
    public void connect() throws Exception {
        //connection.setPortName();
        if(connection.getPortName().equals( "auto" )) {
            String port = testOnPorts();
            if(port.equals(""))
                throw new Exception("UAKGK not connected!");
            connection.setPortName(port);

        }
        connection.connect();
        System.out.print("conn.");
        initCommPort();
        initSpeed();
        // на всякий случай перейдем в режим приложения
        //outs.write( 'i' );
        //outs.write( 'S' );
        //task = new RemainderTask( this );
        //timer.schedule( task, 3000, 3000 );
        toolTimer = new Timer();
        accTimer = new Timer();
        toolTimer.schedule( new ToolAskTask( this ), 0, 10 );
        accTimer.schedule( new AccurateAskTask( this ), 0, 10 );

    }
    protected void initCommPort() throws Exception {
        SerialPort port = ( SerialPort ) connection.getPort();
        port.setSerialPortParams( 115200,
                                  SerialPort.DATABITS_8,
                                  SerialPort.STOPBITS_1,
                                  SerialPort.PARITY_NONE );
        port.setInputBufferSize(1024);
        outs = port.getOutputStream();
        ins = port.getInputStream();
    }
    /**
     * Отключение от установки
     */
    public void disconnect() {
        toolTimer.cancel();
        toolTask.cancel();
        accTimer.cancel();
        accTask.cancel();
        //task.cancel();
        //dataSource.disconnect();
        //dataSource = null;
        try {
            Thread.currentThread().sleep( 1000 );
        } catch ( InterruptedException ex ) {
        }
        connection.disconnect();
    }

    public void setSpeed( int speed ) {

    }


    /**
     * Вращение двигателя в соответствии с axis
     * @param id Символьное обозначение двигателя: 'x', 'y', 'z'
     * @param speed Скорость 0-255
     */
    public synchronized void rotate( char id, int speed ) throws Exception {
        //selectPlane( id );
        //setMotorSpeed( speed );
    }

    public synchronized void setMotorSpeed( int speed ) throws Exception {
        if(speed >255 )
            speed = 255;
        if(speed < 0 )
            speed = 0;
        System.out.println("Setting speed to " + speed );
        String s = "r";
        s += speed + " ";
        outs.write( s.getBytes() );
    }
    public void initSpeed() throws Exception {
        String speed = properties.getProperty("speed", "200");
        int sp = Integer.parseInt(speed);
        setMotorSpeed(sp);
    }


    public void goToPoint( int section ) throws CheckUnitException {
        try {
            goToLocation( sections[section] );
        } catch ( Exception ex ) {
            throw new CheckUnitException( ex.getMessage() );
        }
    }
    public void findMarker() throws Exception   {
        //outs.write("exec fmf\r".getBytes());
        outs.write('1');
        outs.write('1');
    }

    public void goToLocation(int loc) throws Exception {
        /*
        outs.write("exec goto ".getBytes());
        outs.write(String.valueOf(loc).getBytes());
        outs.write('\r');
        */
        outs.write('G');
        outs.write(String.valueOf(loc).getBytes());
        outs.write('\r');
    }
    /**
     * Command: make amount steps relative current postition
     * @param steps amount of steps to make, positive value meen
     * CW direction of rotation, negative - CCW
     * @throws Exception
     */
    public void makeSteps(int steps) throws Exception {
        outs.write("exec ms ".getBytes());
        outs.write(String.valueOf(steps).getBytes());
        outs.write('\r');
    }
    public boolean startsWith(byte[] buf, String pref) {
        byte[] b = pref.getBytes();
        boolean ret = true;
        for( int i = 0; i < b.length; i++ ) {
            if(b[i] != buf[i]) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    public void read() throws Exception {
        //delay(50);
        byte b = 0;
        StringBuffer str = new StringBuffer( 80 );
        synchronized ( sync ) {
            flushInput();
            outs.write('d');
            readPostString(str);
        }

        //String s = str.toString().substring(0, 8);
        String s = str.toString();
        //int val = (int) Long.parseLong(s, 16);
        if(s.length() < 5)
            return;
        postSource.parse( s );
        Channel chan;
        chan = postSource.getChannel( "sensors" );
        if ( chan != null ) {
            // текущее положение барабана в шагах
            int loc = chan.getValue(1).getAsInteger();
            currentSection = getClosestSection(loc);
            chan.setValue(14, currentSection);
        }
        chan = postSource.getChannel( "values" );
        if ( chan != null ) {
            ChannelValue c = new ChannelValue();
            Value med = getMED(getCurrentSection());
            c.setAsValue(med.value, med.delta);
            chan.setValue(0, c);
            {
                //FileOutputStream fout = new FileOutputStream("d:/www/tt.txt");
                //fout.write(String.valueOf(chan.getValue(1).getAsDouble()).getBytes());
                //fout.close();
            }
        }
        //ChannelValue val;

        sendSignal( new SignalEvent( SignalEvent.SIG_DATA_ARRIVED, this ) );
        hasNewData = true;
        //notify();

    }

    public void readPostString( StringBuffer str ) throws Exception {
        long time = System.currentTimeMillis();
        boolean end = false;
        while ( !end ) {
            if ( System.currentTimeMillis() - time > 2000 ) {
                sendSignal( new SignalEvent( SignalEvent.SIG_TIMEOUT, this ) );
                break;
            }
            readInputBuffer();
            while(!inputBuffer.isEmpty() && !end ) {
                char c = (char) inputBuffer.pop();
                if ( c == 0x0d ) {
                    end = true;
                }
                if(c >= ' ')
                    str.append( c );
            }
            Util.delay( 10 );
        }
        //System.out.println("_"+str);

    }
    protected void prepareReadWellToolValues() throws Exception {
        outputBuffer.push('n');
    }
    public void readWellToolValues() throws Exception {
        /** @todo оповещение об отключении прибора (нет связи) */
        //delay(50);
        //byte b = 0;
        StringBuffer str = new StringBuffer( 256 );
        synchronized(sync) {
            prepareReadWellToolValues();
            exec();
            readPostString( str );
        }

        //String s = str.toString().substring(0, 8);
        String s = str.toString();
        //System.out.println(s);
        //int val = (int) Long.parseLong(s, 16);
        toolSource.parse( s );
        toolSource.sendSignal( new SignalEvent( SignalEvent.SIG_DATA_ARRIVED,
                                                toolSource ) );
    }


    public void stop() throws Exception {
        outputBuffer.push('S');
        exec();
    }
    //unsigned char * getRespondBuffer() { return respondBuffer; }
    //void addEventListener(ChannelDataEventListener* evListener);
    //void removeEventListener();
    private class RemainderTask
        extends TimerTask {
        private UAKGKCheckUnit cu;
        public RemainderTask( UAKGKCheckUnit unit ) {
            cu = unit;
        }

        public void run() {
            //if ( !cu.isResponded ) {
            //    cu.processRespondEvent( RespondEvent.EVENT_NO_RESPOND );
            // }
            // cu.isResponded = false;
        }
    }


    class AccurateAskTask
        extends TimerTask {

        private UAKGKCheckUnit unit;
        AccurateAskTask( UAKGKCheckUnit unit ) {
            this.unit = unit;
        }

        public void run() {
            try {
                if ( unit.isConnected() ) {
                    unit.read();
                }
            } catch ( Exception ex ) {
                System.err.println( ex );
                ex.printStackTrace();
            }
        }
    } // end of ToolAskTask


    class ToolAskTask
        extends TimerTask {

        private UAKGKCheckUnit unit;
        ToolAskTask( UAKGKCheckUnit unit ) {
            this.unit = unit;
        }

        public void run() {
            try {
                if ( unit.isConnected() ) {
                    unit.readWellToolValues();
                }
            } catch ( Exception ex ) {
                System.err.println( ex );
                ex.printStackTrace();
            }
        }
    } // end of ToolAskTask
}
