package lsdsoft.welltools.im.ion3;


import lsdsoft.metrolog.*;
import lsdsoft.units.*;
import com.lsdsoft.math.MedianFilter;


/**
 * <p>Title: Фильтр источника данных прибора ИОН-1
 * "показания датчиков прибора -> инклинометрические углы"</p>
 * <p>Description: Входной источник должен содержать канал "sensors" - показания датчиков
     * прибора ИОН-1, также должно быть назначено свойство "number" - заводской номер
 * скважинного прибора, по которому будет загружаться поправочная таблица. Выходной
 * источник содержит два канала: "angles" - инклинометрические углы, angles[0] - угол поворота,
 * angles[1] - зенитный угол, angles[2] - азимутальный угол; "angles2" - углы датчиков Ax, Ay, Az, Hxy, Hxz, Hyz
 * с учетом поправок и нулей датчиков</p>
 * Properties: <br>
 * 'number' - номмер прибора <br>
 * 'table.filename' - имя файла поправочной таблицы<br>
 * 'table.date' - дата создания файла поправочной таблицы<br>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 * @see lsdsoft.metrolog.ChannelDataSourceFilter
 * @see lsdsoft.metrolog.ChannelDataSourceFilter
 */

public class ION3Filter
    extends ChannelDataSource
    implements ChannelDataSourceFilter,
    SignalEventListener {
    private ChannelDataSource source;
    //private ChannelDataSource output;
    //private ION1Computer computer = new ION1Computer();
    //private ION1CorrectionTable table = null;
    private int[] input = new int[24];
    private int[] prev = new int[12];
    private double[] out = new double[3];
    private double[] angles2 = new double[24];
    private MedianFilter f1 = new MedianFilter(3);
    private MedianFilter f2 = new MedianFilter(3);
    private MedianFilter f3 = new MedianFilter(3);
    private Channel chan1;
    private Channel chan2;
    private String id = "";
    public ION3Filter() {

    }
    public ION3Filter( ChannelDataSource source ) throws Exception {
        init(source);
    }
    protected void init( ChannelDataSource source ) throws Exception {
        this.source = source;
        source.addSignalListener(this);
        //output = new ChannelDataSource();
        channels.add( source.getChannel( "sensors" ));
        channels.add( source.getChannel( "values" ));
        chan1 = addChannel( "angles", 0, 3 );
        chan2 = addChannel( "errnorm", 1, 3 );
        //loadTable();
        //computer.setTable( table );

    }
    public ChannelDataSource getOutputSource() {
        return this;
    }

    public void process() {
        Channel chan = source.getChannel( "sensors" );
        if ( chan == null ) {
            return;
        }
        String s = source.getProperty("id");
        if(s == null) {
            return;
        }
        setProperty("id", s);
        // берем данные только с новым id
        if(s.equals(id))
            return;
        id = s;
        // значения по датчикам текущие и предыдущие
        for ( int i = 0; i < 6; i++ ) {
            input[i] = chan.getValue( i ).getAsInteger();
        }
        calc( input, out );
        fillValues();
        hasNewData = true;
        synchronized (this) {
            notify();
        }
        sendSignal( new SignalEvent( SignalEvent.SIG_DATA_ARRIVED, this) );
    }
    public void signalEvent(SignalEvent ev){
        if(ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
            process();
        }

    }
    /**
     * input[0] - поворот
     * input[1] - зенит
     * input[2] - азимут
    */
    protected void calc(int[] input, double[] out) {
        double a1 = input[1]/180.0;
        double a2 = input[2]/180.0;
        double a3 = input[3]/180.0;
        if ( a1 > 350.0 ) {
            a1 -= 360.0;
        }
        if ( a2 > 350.0 ) {
            a2 -= 360.0;
        }
        if ( a3 > 350.0 ) {
            a3 -= 360.0;
        }
        f1.add(a1);
        f2.add(a2);
        f3.add(a3);

        out[0] = f1.cuttedAverage();
        out[1] = f2.cuttedAverage();
        out[2] = f3.cuttedAverage();
    }

    protected double getAzimuthError(double zenith) {
        double err;
        if( zenith < 1) {
            err = 60;
        } else
        if( zenith < 2) {
            err = 30;
        } else
        if( zenith < 3) {
            err = 10;
        } else
        if( zenith < 7) {
            err = 3;
        } else  {
            err = 1.5;
        }
        return err;
    }

    private void fillValues() {
        double zenith = out[1];
        // азимут
        chan1.getValue( 0 ).setAsDouble( out[2] );
        // предел погрешности по азимуту, зависит от зенита
        chan2.getValue( 0 ).setAsDouble( getAzimuthError( zenith ) );
        // set azimuth value
        chan1.getValue( 1 ).setAsDouble( zenith );
        chan2.getValue( 1 ).setAsDouble( 0.25 );
        // поворот
        chan1.getValue( 2 ).setAsDouble( out[0] );
        chan2.getValue( 2 ).setAsDouble( 3 );


    }
}
