// Управление поверочной установкой УАК-СИ
package lsdsoft.metrolog.unit;


import java.io.*;
import java.text.*;
import java.util.*;
import gnu.io.*;
//import javax.comm.*;

import com.lsdsoft.comm.*;
import lsdsoft.metrolog.*;
import lsdsoft.units.*;
import lsdsoft.zeus.*;
import lsdsoft.util.*;
import lsdsoft.metrolog.im.*;
import org.w3c.dom.*;


public class UAKSI2CheckUnit
    extends AbstractCheckUnit
    implements
    ChannelDataEventListener {
    private static final int UAKSI_COMMAND_IDLE = 0x00;
    private static final int UAKSI_COMMAND_GOTO_POINT = 0x55;
    //private static final int UAKSI_COMMAND_AUTO_FIND_MARKER = 0x56;
    public static final int DEVICE_STATE_BAD  =  0x00;
    public static final int DEVICE_STATE_OK   =  0x01;
    public static final int DEVICE_STATE_ABSENT = 0x02;
    public static final int DEVICE_STATE_ERROR  = 0x03;

    // номер версии УАК-СИ: влияет на формат команд
    // пока только первая версия
    public static final int version = 2;
    protected double[] UAKSI_ERRORS = {0.5, 0.08, 1};
    private static final char[] possiblePlanes = {
        'x', 'y', 'z'
    };
    private CommConnection connection = new CommConnection();
    private OutputStream outs;
    private InputStream ins;
    private FIFOBuffer inputBuffer = new FIFOBuffer(4096);
    private FIFOBuffer outputBuffer = new FIFOBuffer(128);
    //private byte[] commandBuffer = new byte[5];
    //private byte[] inputBuffer = new byte[32];
    private int[] executedCommands = new int[3];
    //private Angle angleLimit = new Angle( 1000 );
    //private RespondEventListener respondListener = null;
    private PostChannelDataSource postSource = new PostChannelDataSource();
    private PostChannelDataSource toolSource = new PostChannelDataSource();
    //private ChannelDataSource toolOutSource;
    //private int respondMask = 0;
    //private Timer timer = new Timer( true );
    private Timer toolTimer = new Timer();
    private Timer accTimer = new Timer();
    //private RemainderTask task = new RemainderTask( this );
    private ToolAskTask toolTask = new ToolAskTask( this );
    private AccurateAskTask accTask = new AccurateAskTask( this );
    private Channel chan1;
    //private ChannelDataSourceFilter filter;
    private Zeus zeus = Zeus.getInstance();
    private char selectedPlane = 'x';
    boolean arrived;
    private boolean isResponded = false;
    private boolean makeMagneticCorrection = true;
    private int[] comSpeeds = {9600, 115200};
    MagneticCorrectionTable corrTable = new MagneticCorrectionTable();
    private InclinometerAngles lirAngles = new InclinometerAngles();
    InclinometerAngles toolAngles = new InclinometerAngles();
    // average value of angles
    InclinometerAngles avAngles;
    // количество замеров углов
    int measureCount;
    // углы скважинного прибора
    InclinometerAngles devAngles;
    DecimalFormat df;
    protected String handlerName = "ion1";
    /**
     * Поправки к показаниям угловых датчиков относительно абсолютного нуля.
     * Значения поправок берутся из сертификата на установку.
     * Для получения точных показания значение поправки вычитается
     * из показаний датчиков.
     */
    InclinometerAngles accOffset = new InclinometerAngles();
    // предел точности выхода к заданному углу
    Angle normDelta;
    Object sync = new Object();
    private double accDeltaAzimuth = 0;
    private double accDeltaZenith = 0;
    private double accDeltaRotate = 0;
    public String postUaksi = "";
    public String postWt = "";
    {
    	df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
        df.applyPattern("#0.00");
        toolInfo.type = "uaksi2";
    }
    public UAKSI2CheckUnit() {
    }

    public UAKSI2CheckUnit( String portName ) {
        //connection = new CommConnection( portName );
        connection.setPortName( portName );
        
    }

    public void init() {
        
        System.out.println( "Tool type: " + zeus.getToolType() );
        System.out.println( "Tool number: " + toolInfo.number );
        String propName = "uaksi2_" + toolInfo.number + ".properties";

        try {
            zeus.loadProperties( properties, propName );
            /**
             * @todo Доделать загрузку пропертей (случай, если их нет в файле)
             */
            accDeltaAzimuth = Double.parseDouble(Zeus.getInstance().getProperty("azimuth.delta", "0.5"));
            accDeltaZenith = Double.parseDouble(Zeus.getInstance().getProperty("zenith.delta", "0.08"));
            accDeltaRotate = Double.parseDouble(Zeus.getInstance().getProperty("rotate.delta", "1.0"));
            UAKSI_ERRORS[0] = accDeltaAzimuth;
            UAKSI_ERRORS[1] = accDeltaZenith;
            UAKSI_ERRORS[2] = accDeltaRotate;


        } catch ( Exception ex ) {
            System.err.println( "Unable load properties '" + propName + "'" );
        }
        connection.setPortName( properties.getProperty( "checkunit.port",
            "auto" ) );
        toolSource.getProperties().setProperty( "number",
                                                zeus.getToolNumber() );
        chan1 = addChannel( "angles", 0, 3 );
        addChannel( postSource.getChannel("sensors") );
        addChannel( postSource.getChannel("values") );
        try {
            //filter = new ION1Filter( toolSource );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
        loadCorrectionTable();
        inited = true;
    }

    public double getErrorLimit(char plane, Object conditions) {
        return UAKSI_ERRORS[planeToIndex(plane)];
    }

    private void loadCorrectionTable() {
        //InclinometerAngles angs = new InclinometerAngles();
        try {
        	String path = properties.getProperty( "magnetic.correction" );
        	if(path != null) {
        		Document doc = DataFactory.getDataStorage().loadXML( path );
        		corrTable.load( doc.getDocumentElement());
        	} else {
        		System.out.println("#WARNING: не указана поправочная таблица для УАКСИ");
        	}
        } catch ( Exception ex ) {
        	System.err.println("#ERROR: не удлось загрузить поправочную таблицу для УАКСИ");
            System.err.println( ex.getMessage() );
        }
    }
    public void setMakeMagneticCorrection( boolean make ) {
        makeMagneticCorrection  = make;
    }
    public boolean getMakeMagneticCorrection() {
        return makeMagneticCorrection;
    }
    public ChannelDataSource getToolDataSource() {
        return toolSource;
    }

    public ChannelDataSource getDataSource() {
        return postSource;
    }

    public char[] getPossiblePlanes() {
        return possiblePlanes;
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
        	if(isConnected()) {
        		outs.write(outputBuffer.pop());
        	} else {
        		throw new CheckUnitException("UAKSI is not connected");
        	}
        }
    }
    private void readInputBuffer() throws Exception {
        while(ins.available() > 0) {
            inputBuffer.push(ins.read());
        }
    }
    protected void exec() throws Exception {
        //flushOutput();
        //System.out.print(outputBuffer.toString());
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
        //int channel = ev.getChannel();
        //isResponded = true;
        //if ( channel == 0 ) {
        //    double val = ev.getValue().getAsDouble();
        //    lirAngles.azimuth.setAngle( val / 1000.0 );
        //    lirAngles.azimuth.sub( accDelta.azimuth );
       // }
        //if ( channel == 1 ) {
         //   double val = ev.getValue().getAsDouble();
         //   lirAngles.zenith.setAngle( val / 1000.0 );
         //   lirAngles.zenith.sub( accDelta.zenith );
         //   sendEvent();
       // }
    }

    public void setAccurateDelta( InclinometerAngles angles ) {
        accOffset = angles;
    }

    public InclinometerAngles getAccurateDelta() {
        return accOffset;

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
    public boolean checkConnection() {
        byte[] buf = new byte[16];
        try {
            Thread.sleep( 100 );
            ins.skip( ins.available() );
            outs.write( 'd' );
            Thread.sleep( 100 );
            if(ins.available() > 0) {
                ins.read( buf );
            }
            String s = new String( buf );
            if ( s.startsWith( "post" ) ) {
                return true;
            }
        } catch ( Exception ex ) {
        }
        return false;
    }

    public void connect(String portName, int speed) throws Exception {
        connection.disconnect();
        connection.setPortName( portName );
        connection.connect();
        SerialPort port = ( SerialPort )connection.getPort();
        port.setSerialPortParams( speed,
                                  SerialPort.DATABITS_8,
                                  SerialPort.STOPBITS_1,
                                  SerialPort.PARITY_NONE );
        outs = port.getOutputStream();
        ins = port.getInputStream();
    }
    /**
     * Подключение к установке
     * @throws Exception general exception class
     */
    public void connect() throws Exception {
        String portNames[] = null;
        if(!inited) {
            init();
        }
        boolean hasConn = false;
        String portName = connection.getPortName();
        if(portName.equalsIgnoreCase("auto")){
            portNames = CommConnection.getStringPortNames();
        } else {
            portNames = new String[1];
            portNames[0] = portName;
        }
        int size = portNames.length;
        for(int i = 0; i < size; i++) {
            hasConn = connect(portNames[i]);
            if(hasConn) {
                break;
            }
        }

        // установка найдена
        if(hasConn) {
            // на всякий случай перейдем в режим приложения
            outs.write( 'i' );
            outs.write( 'S' );
            selectHandler(handlerName);
            //task = new RemainderTask( this );
            //timer.schedule( task, 3000, 3000 );
            toolTimer = new Timer();
            accTimer = new Timer();
            toolTimer.schedule( new ToolAskTask( this ), 0, 310 );
            accTimer.schedule( new AccurateAskTask( this ), 0, 100 );
        } else {
            throw new Exception("UAKSI not found");
        }
    }

    public boolean connect(String portName) {
        boolean hasConn = false;
        for(int i = 0; i < comSpeeds.length; i++) {
            System.out.print( "searching on '" + portName +
                                         "' at speed " +
                                         comSpeeds[i] + " bps..." );

            try {
                connect( portName, comSpeeds[i] );
            } catch ( Exception ex ) {
                System.out.println( "failed: "+ ex.getMessage());
            }
            hasConn = checkConnection();
            if ( hasConn ) {
                System.out.println( "found");
                System.out.println( "UAKSI found on '" + portName +
                                    "' at speed " +
                                    comSpeeds[i] + " bps" );
                break;
            } else {
                System.out.println( "not found");
            }
        }
        return hasConn;
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
            Thread.sleep( 1000 );
        } catch ( InterruptedException ex ) {
        }
        connection.disconnect();
    }

    public void selectPlane( Object plane ) throws Exception {
        selectedPlane = plane.toString().charAt( 0 );
        selectPlane( selectedPlane );
    }

    public void selectHandler(String name) throws Exception {
        handlerName = name;
        selectHandler();
    }

    public void selectHandler() throws Exception {
        if(isConnected()) {
            synchronized ( sync ) {
                outs.write( 'w' );
                outs.write( handlerName.getBytes() );
                outs.write( ' ' );
            }
        }
    }


    private void selectMotor( char id ) throws Exception {
        selectPlane( id );
        //outs.write( 'S' );
        //outs.write( id );
    }



    /**
     * Вращение двигателя в соответствии с axis
     * @param id Символьное обозначение двигателя: 'x', 'y', 'z'
     * @param speed Скорость 0-255
     */
    public synchronized void rotate( char id, int speed ) throws Exception {
        selectPlane( id );
        setMotorSpeed( speed );
    }

    public synchronized void setMotorSpeed( int speed ) throws Exception {
        outs.write( 'r' );
        outs.write( speed );
    }

    /**
     *
     * @param dir can be 'c' or 'C'
     */
    public void setRotateDirection( char dir ) throws Exception {
        //outs.flush();
    	outputBuffer.push( 'c' );
        outputBuffer.push( dir );
        exec();
    }

    public double getDelta( char plane ) {
        if ( plane == 'x' ) {
            return accOffset.azimuth.getValue();
        }
        if ( plane == 'y' ) {
            return accOffset.zenith.getValue();
        }
        if ( plane == 'z' ) {
            return accOffset.rotate.getValue();
        }
        return 0;
    }

    public void goToPoint( Object value ) throws CheckUnitException {
        goToPoint( selectedPlane, value );
    }

    public void goToPoint( char plane, Object value ) throws CheckUnitException {
        if ( value instanceof Double ) {
            goToPoint( plane, ( ( Double ) value ).doubleValue() );
        } else {
            goToPoint( plane, Double.parseDouble( value.toString() ) );
        }
    }

    public void goToPoint( char plane, double value ) throws CheckUnitException {
        try {
            goTo( plane, value );
        } catch ( Exception ex ) {
            throw new CheckUnitException( ex.toString() );
        }
    }
    protected void prepareFindMarker() throws Exception {
        outputBuffer.push( 'f' );
    }

    public void findMarker( char id ) throws Exception {
        synchronized(sync) {
            prepareSelectPlane( id );
            prepareFindMarker();
            exec();
        }
    }
    public void reset( char id ) throws Exception {
        Angle ang = accOffset.rotate;
        accOffset.rotate = ang.add( lirAngles.rotate );
    }

    protected void prepareSelectPlane( char plane ) throws Exception {
        outputBuffer.push( 's' );
        outputBuffer.push( plane );
    }
    private void selectPlane( char plane ) throws Exception {
        prepareSelectPlane( plane );
        //writeOutputBuffer();
        exec();
    }
    protected void prepareGoTo( char plane, double angle) throws Exception {
        executedCommands[planeToIndex(plane)] = UAKSI_COMMAND_GOTO_POINT;
        //DecimalFormat form = new DecimalFormat( "##0.0" );
        //form.setL
        //form.setMaximumFractionDigits( 1 );
        String str = df.format( angle + getDelta( plane ) );
        outputBuffer.push( 'G' );
        for ( int i = 0; i < str.length(); i++ ) {
            outputBuffer.push( str.charAt( i ) );
        }
        outputBuffer.push( ' ' );
    }
    public void goTo( char plane, double angle ) throws Exception {
        synchronized(sync) {
            prepareSelectPlane( plane );
            prepareGoTo( plane, angle );
            System.out.println( outputBuffer.toString() );
            exec();
        }
    }

    public int planeToIndex( char plane ) {
        for ( int i = 0; i < possiblePlanes.length; i++ ) {
            if ( plane == possiblePlanes[i] ) {
                return i;
            }
        }
        return -1;
    }

    public boolean isComplete( char plane ) {
        //int command = UAKSI_COMMAND_IDLE;
       // try {
       //     synchronized ( sync ) {
        //        selectPlane( plane );
        //        command = getExecutedCommand();
        //    }
        //} catch ( Exception ex ) {
        //    System.err.print( ex.getMessage() );
       // }
        return executedCommands[planeToIndex( plane )] == UAKSI_COMMAND_IDLE;
    }

    public int getExecutedCommand() throws Exception {
        flushInput();
        outs.write( 'x' );
        return ins.read();
    }

    private void readAngle( Angle ang ) throws Exception {
        Util.delay( 50 );
        flushInput();
        outs.write( 'P' );
        Util.delay( 50 );
        StringBuffer str = new StringBuffer( 8 );
        if ( ins.available() > 7 ) {
            while ( ins.available() > 0 ) {
                str.append( ( char ) ins.read() );
            }
            //String s = str.toString().substring(0, 8);
            String s = str.toString();
            //int val = (int) Long.parseLong(s, 16);
            double val = Double.parseDouble( s );
            //ang.setAngle(val * 360.0 / (double) 10240.0);
            ang.setAngle( val );
        }

    }
    protected void prepareReadAngles() throws Exception {
        outputBuffer.push('d');
    }
    public void readAngles() throws Exception {
        //delay(50);
        //byte b = 0;
        StringBuffer str = new StringBuffer( 80 );
        synchronized ( sync ) {
            flushInput();
            prepareReadAngles();
            exec();
            readPostString(str);
        }

        //String s = str.toString().substring(0, 8);
        String s = str.toString();
        //int val = (int) Long.parseLong(s, 16);
        if(s.length() < 5)
            return;
        postUaksi = s;
        postSource.parse( s );
        Channel chan = postSource.getChannel( "values" );
        if ( chan != null ) {
            lirAngles.azimuth.setAngle( chan.getValue( 0 ).getAsDouble() );
            lirAngles.azimuth.sub( accOffset.azimuth );
            lirAngles.zenith.setAngle( chan.getValue( 1 ).getAsDouble() );
            lirAngles.zenith.sub( accOffset.zenith );
            lirAngles.rotate.setAngle( chan.getValue( 2 ).getAsDouble() );
            lirAngles.rotate.sub( accOffset.rotate );
            if( makeMagneticCorrection )
                corrTable.correct(lirAngles);
        }
        ChannelValue val;
        val = chan1.getValue( 0 );
        //val.setAsDouble( lirAngles.azimuth.getValue() );
        val.setAsValue( lirAngles.azimuth.getValue(), accDeltaAzimuth );
        val = chan1.getValue( 1 );
        //val.setAsDouble( lirAngles.zenith.getValue() );
        val.setAsValue( lirAngles.zenith.getValue(), accDeltaZenith );
        val = chan1.getValue( 2 );
        //val.setAsDouble( lirAngles.rotate.getValue() );
        val.setAsValue( lirAngles.rotate.getValue(), accDeltaRotate );

        chan = postSource.getChannel( "sensors" );
        if ( chan != null ) {
            // выполняемые команды по плоскостям
            for ( int i = 0; i < 3; i++ ) {
                val = chan.getValue( i + 6 );
                executedCommands[i] = val.getAsInteger();
            }
        }

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
            Util.delay( 20 );
        }

    }
    protected void prepareReadWellToolValues() throws Exception {
        outputBuffer.push('n');
    }
    public void readWellToolValues() throws Exception {
        /** @todo оповещение об отключении прибора (нет связи) */
        //delay(50);
        //byte b = 0;
        StringBuffer str = new StringBuffer( 80 );
        synchronized(sync) {
            flushInput();
            prepareReadWellToolValues();
            exec();
            readPostString( str );
        }
        //String s = str.toString().substring(0, 8);
        String s = str.toString();
        //System.out.println(s);
        //int val = (int) Long.parseLong(s, 16);

        if(toolSource.parse( s )>0) {
            postWt = s;
            toolSource.sendSignal( new SignalEvent( SignalEvent.
                SIG_DATA_ARRIVED,
                toolSource ) );
        } else {

        }
    }

    public Object getValue( char plane ) throws CheckUnitException {
        Angle angle = new Angle();
        double value = 0;
        try {
            value = getAngle( plane, angle ).getValue();
        } catch ( Exception ex ) {
            throw new CheckUnitException( ex.getMessage() );
        }
        return new Double( value );
    }

    public Angle getAngle( char id, Angle ang ) throws Exception {
    	ang = getAngle(id);
        return ang;
    }
    
    public Angle getAngle( char id ) {
    	Angle ang;
    	if ( id == 'x' ) {
            ang = lirAngles.azimuth;
        } else {
            if ( id == 'y' ) {
                ang = lirAngles.zenith;
            } else {
                ang = lirAngles.rotate;
                //Angle ang = new Angle();
                //selectSensor(id);
                //readAngle(ang);
            }
        }
        return ang;
    }   
    
    public double getPosition( char id ) {
    	return getAngle(id).getValue();
    }
        
    public Angle getAzimut() throws Exception {
        //selectSensor('x');
        //readAngle(lirAngles.azimut);
        return lirAngles.azimuth;
    }

    public Angle getZenit() throws Exception {
        //selectSensor('y');
        //readAngle(lirAngles.zenit );
        return lirAngles.zenith;
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
        private UAKSI2CheckUnit cu;
        public RemainderTask( UAKSI2CheckUnit unit ) {
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

        private UAKSI2CheckUnit unit;
        AccurateAskTask( UAKSI2CheckUnit unit ) {
            this.unit = unit;
        }

        public void run() {
            try {
                if ( unit.isConnected() ) {
                    //System.out.print( "A");
                    unit.readAngles();
                }
            } catch ( Exception ex ) {
                System.err.println( ex );
                ex.printStackTrace();
            }
        }
    } // end of ToolAskTask


    class ToolAskTask
        extends TimerTask {

        private UAKSI2CheckUnit unit;
        ToolAskTask( UAKSI2CheckUnit unit ) {
            this.unit = unit;
        }

        public void run() {
            try {
                if ( unit.isConnected() ) {
                    //System.out.print( "T");
                    unit.readWellToolValues();
                    //System.out.print( "t");
                }
            } catch ( Exception ex ) {
                System.err.println( ex );
                ex.printStackTrace();
            }
        }
    } // end of ToolAskTask
}
