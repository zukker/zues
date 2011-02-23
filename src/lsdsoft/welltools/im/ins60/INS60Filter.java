package lsdsoft.welltools.im.ins60;


import lsdsoft.metrolog.*;
import lsdsoft.units.*;
import lsdsoft.zeus.Zeus;
import java.io.FileInputStream;
import java.util.Properties;


/**
 * <p>Title: Фильтр источника данных прибора ИОН-1
 * "показания датчиков прибора -> инклинометрические углы"</p>
 * <p>Description: Входной источник должен содержать канал "sensors" - показания датчиков
     * прибора ИОН-1, также должно быть назначено свойство "number" - заводской номер
 * скважинного прибора, по которому будет загружаться поправочная таблица. Выходной
 * источник содержит два канала: "angles" - инклинометрические углы, angles[0] - угол поворота,
 * angles[1] - зенитный угол, angles[2] - азимутальный угол; "angles2" - углы датчиков Ax, Ay, Az, Hxy, Hxz, Hyz
 * с учетом поправок и нулей датчиков</p>
 *
 * Файл с настройками: ins60.properties
 * 'path.tables' - путь где лежат калибровочные файлы
 * Properties: <br>
 * 'number' - номмер прибора <br>
 * 'table.filename' - имя файла поправочной таблицы<br>
 * 'table.date' - дата создания файла поправочной таблицы<br>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 * @see lsdsoft.metrolog.ChannelDataSourceFilter
 */

public class INS60Filter
    extends ChannelDataSource
    implements ChannelDataSourceFilter,
    SignalEventListener {
    private ChannelDataSource source;
    //private ChannelDataSource output;
    private INS60Computer computer = null;
    private INS60CorrectionTable table = null;
    private int[] input = new int[24];
    private int[] prev = new int[12];
    private double[] out = new double[3];
    private double[] angles2 = new double[24];
    private Channel chan1;
    private Channel chan2;
    private Channel chanerr;
    private String id = "";
    // modern - indicate type of tool that has internal calculations,
    // need not use correction table
    protected boolean modern = false;
    protected Properties props = new Properties();

    public INS60Filter() {

    }
    public INS60Filter( ChannelDataSource source ) throws Exception {
        init(source);
    }
    protected void init( ChannelDataSource source ) throws Exception {
        this.source = source;
        source.addSignalListener(this);
        //output = new ChannelDataSource();
        channels.add( source.getChannel( "sensors" ));
        channels.add( source.getChannel( "values" ));
        chan1 = addChannel( "angles", 0, 3 );
        chan2 = addChannel( "angles2", 1, 24 );
        chanerr = addChannel( "errnorm", 1, 3 );
        if( !modern) {
            loadTable();
            computer = new INS60Computer( table );
        }
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
        // берем данные только с новым id
        if(s.equals(id)) return;
        id = s;
        // значения по датчикам текущие и предыдущие
        for ( int i = 0; i < 16; i++ ) {
            //input[i] = input[i + 12]; // сохраняем предыдущие
            input[i] = chan.getValue( i ).getAsInteger();
        }
        if ( !modern ) {
            lsdsoft.units.InclinometerAngles angs;
            angs =
                computer.CalcAngles( input[0], input[1], input[2],
                                     input[3], input[4], input[5], input[7] );
            out[2] = angs.azimuth.getValue();
            out[1] = angs.zenith.getValue();
            out[0] = angs.rotate.getValue();
        } else {
            out[2] = input[9]/88.0;
            out[1] = input[10]/88.0;
            out[0] = input[11]/88.0;
        }
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
    private void loadTable() throws Exception {
        table = new INS60CorrectionTable();
        table.setNumber( source.getProperties().getProperty( "number" ) );
        Zeus.getInstance().loadProperties(props, "ins60.properties");
        String fileName = props.getProperty( "path.tables" );
        if ( ! ( fileName.endsWith( "/" ) || fileName.endsWith( "\\" ) ) )
            fileName += '/';
        fileName += table.buildFileName();
        FileInputStream ins = new FileInputStream(fileName);
        table.load(ins);
        setProperty("table.filename", fileName);
        //setProperty("table.date", table.getDate());
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
        ChannelValue val;
        chan1.getValue( 0 ).setAsValue( out[2], getAzimuthError( out[1] ) );
        // set azimuth value
        //val.setAsDouble( out[2] );
        chan1.getValue( 1 ).setAsValue( out[1], 0.17 );
        //val.angle = new Angle(out[1]);
        chan1.getValue( 2 ).setAsValue( out[0], 3 );
        //val.angle = new Angle(out[0]);
        chanerr.getValue( 0 ).setAsDouble( getAzimuthError( out[1] ) );
        chanerr.getValue( 1 ).setAsDouble( 0.17 );
        chanerr.getValue( 2 ).setAsDouble( 3 );
        for ( int i = 0; i < chan2.getSubCount(); i++ ) {
            val = new ChannelValue();
            val.setAsDouble( angles2[i] );
            //val.type = ChannelValue.CV_ANGLE;
            //val.angle = new Angle( angles2[i] );
            chan2.setValue( i, val );
        }

    }
}
