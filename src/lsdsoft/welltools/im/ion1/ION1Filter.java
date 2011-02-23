package lsdsoft.welltools.im.ion1;


import lsdsoft.metrolog.*;
import lsdsoft.units.*;


/**
 * <p>Title: ������ ��������� ������ ������� ���-1
 * "��������� �������� ������� -> ������������������ ����"</p>
 * <p>Description: ������� �������� ������ ��������� ����� "sensors" - ��������� ��������
     * ������� ���-1, ����� ������ ���� ��������� �������� "number" - ��������� �����
 * ����������� �������, �� �������� ����� ����������� ����������� �������. ��������
 * �������� �������� ��� ������: "angles" - ������������������ ����, angles[0] - ���� ��������,
 * angles[1] - �������� ����, angles[2] - ������������ ����; "angles2" - ���� �������� Ax, Ay, Az, Hxy, Hxz, Hyz
 * � ������ �������� � ����� ��������</p>
 * Properties: <br>
 * 'number' - ������ ������� <br>
 * 'table.filename' - ��� ����� ����������� �������<br>
 * 'table.date' - ���� �������� ����� ����������� �������<br>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 * @see lsdsoft.metrolog.ChannelDataSourceFilter
 * @see lsdsoft.metrolog.ChannelDataSourceFilter
 */

public class ION1Filter
    extends ChannelDataSource
    implements ChannelDataSourceFilter,
    SignalEventListener {
    private ChannelDataSource source;
    //private ChannelDataSource output;
    protected ION1Computer computer = new ION1Computer();
    protected ION1CorrectionTable table = null;
    private int[] input = new int[24];
    private int[] prev = new int[12];
    private double[] out = new double[3];
    private double[] angles2 = new double[24];
    private Channel chan1;
    private Channel chan2;
    private Channel chanerr;
    //private String id = "";
    public ION1Filter() {

    }
    public ION1Filter( ChannelDataSource source ) throws Exception {
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

        try {
            loadTable();
            computer.setTable( table );
        } catch ( Exception ex ) {
            //System.err.println( ex.getLocalizedMessage() );
            throw new Exception(
                "�� ������� ��������� ����������� ������� ��� ���-1 �" +
                table.getNumber() + ".\r\n" + ex.getLocalizedMessage() );
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
        // ����� ������ ������ � ����� id
        if(s.equals(id))
            return;
        id = s;
        // �������� �� �������� ������� � ����������
        for ( int i = 0; i < 12; i++ ) {
            input[i] = input[i + 12]; // ��������� ����������
            input[i + 12] = chan.getValue( i ).getAsInteger();
        }
        computer.calc( input, angles2, out );
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
        table = new ION1CorrectionTable();
        table.setNumber( source.getProperties().getProperty( "number" ) );
        table.load();
        setProperty("table.filename", table.getFileName());
        setProperty("table.date", table.getDate());
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
        // fill azimuth value and error
        chan1.getValue( 0 ).setAsValue( out[2], getAzimuthError( out[1] ));
        // fill zenith value & error
        chan1.getValue( 1 ).setAsValue( out[1], 0.25 );
        chan1.getValue( 2 ).setAsValue( out[0], 3 );

        chanerr.getValue( 0 ).setAsDouble( getAzimuthError( out[1] ) );
        chanerr.getValue( 1 ).setAsDouble( 0.25 );
        chanerr.getValue( 2 ).setAsDouble( 3 );
        //val.angle = new Angle(out[0]);
        for ( int i = 0; i < chan2.getSubCount(); i++ ) {
            val = new ChannelValue();
            val.setAsDouble( angles2[i] );
            //val.type = ChannelValue.CV_ANGLE;
            //val.angle = new Angle( angles2[i] );
            chan2.setValue( i, val );
        }

    }
}
