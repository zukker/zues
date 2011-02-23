// Управление поверочной установкой УАК-ГК
package lsdsoft.test;


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
import lsdsoft.metrolog.unit.*;


public class TestION3
    extends AbstractCheckUnit
    implements
        SerialPortEventListener,
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
    private InclinometerAngles lirAngles = new InclinometerAngles();
    //private RespondEventListener respondListener = null;
    private PostChannelDataSource postSource = new PostChannelDataSource();
    private PostChannelDataSource toolSource = new PostChannelDataSource();
    private ChannelDataSource toolOutSource;
    //private int respondMask = 0;
    private Timer timer = new Timer( true );
    ;
    private Timer toolTimer = new Timer();
    private Timer accTimer = new Timer();
    //private RemainderTask task = new RemainderTask( this );
    //private ToolAskTask toolTask = new ToolAskTask( this );
    //private AccurateAskTask accTask = new AccurateAskTask( this );
    private Channel chan1;
    //private ChannelDataSourceFilter filter;
    private Zeus zeus = Zeus.getInstance();
    private char selectedPlane = 'x';
    boolean arrived;
    private boolean isResponded = false;
    private boolean makeMagneticCorrection = true;
    MagneticCorrectionTable corrTable = new MagneticCorrectionTable();
    InclinometerAngles toolAngles = new InclinometerAngles();
    // average value of angles
    InclinometerAngles avAngles;
    // количество замеров углов
    int measureCount;
    // углы скважинного прибора
    InclinometerAngles devAngles;
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
    int prevBit = 0;
    int buf[] = new int[1000];
    int outBuf[] = new int[1000];
    int bufpos = 0;
    public TestION3() {
        //loadProperties();
        connection.setPortName( "COM4" );
        init();
    }

    public TestION3( String portName ) {
        //connection = new CommConnection( portName );
        connection.setPortName( portName );
        init();
        //stepsPerRound = 250 * 200;
    }
    public static void main( String[] args ) {
        TestION3 test = new TestION3();
        test.run();
    }
    public void init() {

    }
    public void run() {
        System.out.println("Testing ION3");
        try {
            System.out.println("Available ports:");
            ArrayList list = connection.getPortNames();
            for(int i = 0; i<list.size();i++)
                System.out.println((String)list.get(i));

            connect();
            SerialPort port = ( SerialPort ) connection.getPort();
            InputStream ins = port.getInputStream();
            for(;;) {
                processInput();
            }

        } catch ( Exception ex ) {
            System.out.println(ex.getLocalizedMessage());
        }

    }
    void processInput() {
        try {
            if ( ins.available() > 0 ) {
                int c = ins.read();
                if( c < 20 && c > 9 ) {
                    buf[bufpos++] = c;
                    //System.out.print( 's' );
                } else
                if( c < 60 && c > 20 ) {
                    buf[bufpos++] = c;
                    //System.out.print( 'D' );
                } else
                if( c == 83 ) {
                    if(prevBit == 83) {
                        processBuffer();
                        bufpos = 0;
                        System.out.println( ' ' );
                        System.out.println( "->" );
                    }
                } else {
                    //..System.out.print( ' ' );
                    //System.out.print( c );
                    //System.out.print( ' ' );
                }
                prevBit = c;
            }
        } catch ( IOException ex ) {
        }

    }
    void processBuffer() {
        int len = buf.length;
        if(buf[0] <20 && buf[1] < 20 && buf[2] < 20 & buf[3] > 20 ) {
            System.out.println( "Invalid input!" );
        }
        int outPos = 0;
        for(int i = 0; i< len - 1; i++) {
            int cur = buf[i];
            int next = buf[i+1];
            if(cur >9 && cur < 20) {
                if( next > 9 && next < 20) { // two short
                    //System.out.print( '.' );
                    i= i+1;
                    outBuf[outPos] = 0;
                    outPos++;
                } else { // error
                    System.out.print( 'E' );
                }
            } else
            if(cur >= 20 && cur < 60 ) {
                outBuf[outPos++] = 1;
                //System.out.print( '!' );
            }

        }
        int bits = 0;
        int word = 0;
        int s = 12;
        int mask = 1<<s;
        /*
        if ( outBuf[4] == 0 &&
             outBuf[5] == 0 &&
             outBuf[6] == 0 &&
             outBuf[7] == 0 &&
             outBuf[8] == 1 &&
             outBuf[9] == 0 &&
             outBuf[10] == 1 )
*/
        {
            int i;
            for ( i = 0; i < s; i++ ) {
                if ( outBuf[i] > 0 ) {
                    word |= mask;
                }
                mask = mask >> 1;
            }
            System.out.print( word );
            System.out.print( ' ' );

            bits = 0;
            mask = 0x8000;
            word = 0;

            for ( ; i < outPos; i++ ) {
                if ( outBuf[i] > 0 ) {
                    word |= mask;
                }
                bits++;
                mask = mask >> 1;
                if ( bits == 16 ) {
                    bits = 0;
                    mask = 0x8000;
                    System.out.print( word );
                    System.out.print( ' ' );
                    word = 0;
                }
            }
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
            lirAngles.azimuth.setAngle( val / 1000.0 );
            //lirAngles.azimut.sub( accDelta.azimut );
        }
        if ( channel == 1 ) {
            double val = ev.getValue().getAsDouble();
            lirAngles.zenith.setAngle( val / 1000.0 );
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


    /**
     * Подключение к установке
     * @throws Exception general exception class
     */
    public void connect() throws Exception {
        //connection.setPortName();
        connection.connect();
        System.out.print("conn.");
        initCommPort();
        toolTimer = new Timer();
        accTimer = new Timer();

    }
    protected void initCommPort() throws Exception {
        SerialPort port = ( SerialPort ) connection.getPort();
        port.setSerialPortParams( 57600,
                                  SerialPort.DATABITS_8,
                                  SerialPort.STOPBITS_1,
                                  SerialPort.PARITY_NONE );
        port.setInputBufferSize(24);
        outs = port.getOutputStream();
        ins = port.getInputStream();
        port.removeEventListener();
        port.addEventListener(this);
    }
    /**
     * Отключение от установки
     */
    public void disconnect() {
        toolTimer.cancel();
        //toolTask.cancel();
        //accTimer.cancel();
        //accTask.cancel();
        //task.cancel();
        //dataSource.disconnect();
        //dataSource = null;
        try {
            Thread.currentThread().sleep( 1000 );
        } catch ( InterruptedException ex ) {
        }
        connection.disconnect();
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
    public void serialEvent(SerialPortEvent ev) {
        switch(ev.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE :
                getInput();
            default:
        }
    }
    private synchronized void getInput() {
        int size, size2;
        try {
            while(ins.available() > 0) {
                inputBuffer.push( ins.read() );
            }
        } catch(Exception ex) {
            System.err.print(ex.toString());
        }
        //    for(int i = 0; i < LIR_PACKET_SIZE; i++)
        //      packetBuffer[i] = inputBuffer[i];

    }

    public void read() throws Exception {
        //delay(50);
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
