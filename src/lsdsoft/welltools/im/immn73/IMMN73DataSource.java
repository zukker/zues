/**
 * Источник данны прибора ИММН-73 через стандартную панель.
 */
package lsdsoft.welltools.im.immn73;

import java.io.*;
import java.util.*;
import javax.comm.*;

import com.lsdsoft.comm.*;
import lsdsoft.metrolog.*;
import lsdsoft.util.*;
import lsdsoft.zeus.*;
import com.lsdsoft.math.*;


public class IMMN73DataSource
    extends ChannelDataSource {
    private static final int IMMN73_SENSORS_COUNT = 16;
    private static final double CALMAN_DEEP = 2.0;
    private static final int MEDIAN_DEEP = 7;
    private MedianFilter[] filters = {
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP ),
        new MedianFilter( MEDIAN_DEEP )
    };

    private int[] aSensors = new int[IMMN73_SENSORS_COUNT];
    private double[] aValues = new double[16];
    private double[] aCorr = new double[2];
    private CommConnection connection = new CommConnection();
    private OutputStream outs;
    private InputStream ins;
    private FIFOBuffer inputBuffer = new FIFOBuffer(128);
    private FIFOBuffer outputBuffer = new FIFOBuffer(16);
    private byte[] commandBuffer = new byte[5];
    private byte[] chunk = new byte[4];
    private Timer toolTimer = new Timer();
    private ToolThread toolTask = new ToolThread( this );
    private Channel sensors;
    private Channel values;
    private Channel errors;
    private IMMN73Computer computer = null;
    private IMMN73CorrectionTable table = new IMMN73CorrectionTable();
    private Thread thr;
    // количество циклов считывания данных с прибора перед отправкой полученных значений
    private int readCycles = 4;


    public IMMN73DataSource() {
        sensors = addChannel("sensors", 0, 20);
        values = addChannel("values", 0, 16);
        errors = addChannel("errors", 0, 2);
        try {
            Zeus.getInstance().loadProperties( properties, "immn73.properties" );
            readCycles = Integer.parseInt(properties.getProperty("cycles", "4" ));
        } catch ( Exception ex ) {
            System.err.println( ex.getLocalizedMessage() );
        }
    }

    public void connect() throws Exception {
        // loading correction table
        table.setNumber( properties.getProperty( Zeus.PROP_TOOL_NUMBER ));
        String fileName = properties.getProperty("path.tables") +
            "/" + table.buildFileName();
        table.load( new FileInputStream( fileName ) );
        computer = new IMMN73Computer(table);
        connection.setPortName( properties.getProperty( "port", "COM2" ) );
        connection.connect();
        System.out.print( "immn73conn." );
        SerialPort port = ( SerialPort ) connection.getPort();
        //port.disableReceiveThreshold();
        //port.disableReceiveFraming();
        port.setSerialPortParams( 2400,
                                  SerialPort.DATABITS_8,
                                  SerialPort.STOPBITS_1,
                                  SerialPort.PARITY_ODD );
        //dataSource.addEventListener(this);
        // check respond for five seconds
        outs = port.getOutputStream();
        ins = port.getInputStream();
        // на всякий случай перейдем в режим приложения
        //outs.write( 'i' );
        //outs.write( 'S' );
        //task = new RemainderTask( this );
        //timer.schedule( task, 3000, 3000 );
        //toolTimer = new Timer();
        toolTask = new ToolThread( this );
        toolTask.start();



    }

    public boolean isConnected() {
        return connection.isConnected();
    }
    // transfer data from channal 'sensors' to int array
    private void fillSensors() {
        for(int i = 0; i < IMMN73_SENSORS_COUNT; i++ ) {
            aSensors[i] = sensors.getValue(i).getAsInteger();
        }
    }
    protected void precalc() {
        fillSensors();
        computer.compute( aSensors, aValues );
        //aValues[0] = filter( oldZenith, aValues[0] );
        //aValues[1] = filter( oldAzimuth, aValues[1] );
        //aValues[8] += aValues[0] / (double)readCycles;
        //aValues[9] += aValues[1] / (double)readCycles;
    }
    public void calc() {
        //double oldZenith = aValues[0];
        //double oldAzimuth = aValues[1];
        //fillSensors();
        //computer.compute( aSensors, aValues );
        //aValues[0] = filter( oldZenith, aValues[0] );
        //aValues[1] = filter( oldAzimuth, aValues[1] );
        //aValues[8] += aValues[0] / (double)readCycles;
        //aValues[9] += aValues[1] / (double)readCycles;
        //aValues[0] = aValues[8];
        //aValues[1] = aValues[9];
        values.setValue( 0, aValues[0] ); // зенит
        values.setValue( 1, aValues[1] ); // азимут
        computer.correct( aValues, aCorr );
        values.setValue( 4, aCorr[0] ); // скорректированный зенит
        values.setValue( 5, aCorr[1] ); // скорректированный азимут
    }
    public double filter(double oldValue, double newValue ) {
        return ((CALMAN_DEEP - 1.0) * oldValue + newValue ) / CALMAN_DEEP;
    }
    public void readSensorValues() {
        try {
            //writeReset();
            //Thread.sleep(200);
            //readParam( 2 );
            long time = System.currentTimeMillis();
            prepareParam( 4 );
            Thread.sleep(500);
            readParam( 4 );
            Thread.sleep(50);
            readParam( 4 );
            Thread.sleep(50);
            readParam( 4 );
            //Thread.sleep(50);
            //readParam( 4 );
            Thread.sleep(500);
            prepareParam( 5 );
            readParam( 5 );
            Thread.sleep(50);
            readParam( 5 );
            Thread.sleep(50);
            readParam( 5 );
            //Thread.sleep(50);
            //readParam( 5 );
            //Thread.sleep(100);
            time -= System.currentTimeMillis();
        } catch ( Exception ex ) {
            System.err.println(ex.getMessage());
        }
    }
    private void resetValues() {
        aValues[8] = 0;
        aValues[9] = 0;
    }
    public void process() {
        //resetValues();
        //for(int i = 0; i < readCycles; i++ )
        {
            readSensorValues();
            precalc();
        }
        calc();
        sendSignal();
    }
    protected void readChunk() throws Exception {
        /* @todo add timeout checking */
        long time = System.currentTimeMillis();
        while(ins.available() < 4 ) {
            if( System.currentTimeMillis() - time > 2000 )
                throw new Exception("IMMN73 timeout");
            Thread.sleep( 3 );
        }
        ins.read( chunk );
    }
    protected int decodeChunk( byte[] chunk ) throws Exception {
        int value = 0;
        int mul = 1;
        // только три последние цифры значимые
        for(int i = 0; i < 3; i++ ) {
            int h = hexCharToInt( chunk[3 - i] );
            if( h < 0 )
                throw new Exception("Invalid input data from IMMN73:" + chunk[3 - i]);
            value += h * mul;
            mul *= 16;
        }
        return value;
    }

    protected void prepareParam( int param ) throws Exception {
        writeParam( param );
        Thread.sleep( 200 );
    }

    protected void readParam( int param ) throws Exception {
        int index = param * 2;
        ins.skip( ins.available() );
        writeReset();
        Thread.sleep( 100 );
        readChunk();
        int value = decodeChunk( chunk );
        filters[index].add( value );
        sensors.setValue( index + 8, value );
        sensors.setValue( index, filters[index].median() );
        readChunk();
        index++;
        value = decodeChunk( chunk );
        filters[index].add( value );
        sensors.setValue( index + 8, value );
        sensors.setValue( index, filters[index].median() );
    }

    protected void writeReset() throws Exception {
        commandBuffer[0] = 'R';
        commandBuffer[1] = 0x0d;
        outs.write( commandBuffer, 0, 2 );
    }

    protected void writeParam( int param ) throws Exception {
        commandBuffer[0] = 'S';
        commandBuffer[1] = (byte)(0x30 + param);
        commandBuffer[2] = 0x0d;
        outs.write( commandBuffer, 0, 3 );
    }
    protected int hexCharToInt( byte hex ) {
        if(hex >= '0' && hex <= '9') return (hex - '0');
        if(hex >= 'A' && hex <= 'F') return (hex - 'A' + 10);
        return -1;
    }
    public void sendSignal() {
        hasNewData = true;
        sendSignal( new SignalEvent( SignalEvent.SIG_DATA_ARRIVED, this) );

    }
  /*
  значения по каналам от фильтра
    4 - нуль по магнитометру
    5 - нуль по бскт
    8 - значение магнитометра Y
    9 - значение БСКТ Х
    10 - значение магнитометра X
    11 - значение БСКТ Y
  */
 /*
  private int[] channelValue = new int[IMMN73_CHANNEL_COUNT];

  // нули магнитометров
//  int zeroM_X, zeroM_Y;
  // нули БСКТ
//  int zeroA_X, zeroA_Y;
  // амплитуды магнитометров
//  int amplitudeM_X, amplitudeM_Y;
  // амплитуды БСКТ
//  int amplitudeA_X, amplitudeA_Y;
  private void calcAngles();
//  void calcZenit();
//  void calcAzimut();
  private void sendAngleEvent();
  private IMMN73CorrectionTable corrTable;
  public IMMN73DataSource() {
  }
  //virtual ~IMMN73DataSource();
  public void setWellTool();
  public void connect();
  public void channelEvent(ChannelDataEvent ev);
  public void getAngles(InklinometerAngles ang);
  public void exec(String command);
  // return spline for zenit correction
  SplineInterpolation getZenitSpline();
  SplineInterpolation getAzimutSpline(int zenit);
  double getZenit(int zenit);
        */
       class ToolThread
           extends Thread {
           private boolean exit = false;
           private IMMN73DataSource source;
           ToolThread( IMMN73DataSource source ) {
               this.source = source;
           }
           public void exit() {
               exit = true;
           }
           public void run() {
               while(!exit) {
                   try {
                       if ( source.isConnected() ) {
                           source.process();
                       }
                   } catch ( Exception ex ) {
                       System.err.println( ex );
                       ex.printStackTrace();
                   }
               }
           }
       } // end of ToolThread

};




