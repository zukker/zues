package lsdsoft.zeus.methods;

import lsdsoft.metrolog.unit.*;
import lsdsoft.metrolog.*;
import lsdsoft.units.*;
//import bsh.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class InclinMethod {
    protected AbstractCheckUnit unit = null;
    protected ChannelDataSource toolSource = null;
    protected InclinometerAngles toolAngles = new InclinometerAngles();
    protected InclinometerAngles accAngles = new InclinometerAngles();
    //protected Interpreter bsh = new Interpreter();
    protected MeasureDatas data = null;
    protected double lastAccurate = 0;
    protected static int measureCount = 4;
    public String message1 = "";
    public String message2 = "";
    public String message3 = "";

    public InclinMethod() {
    }
    /**
     * Init BeanShell interpreter with local variables
     */
    protected void initShell() throws Exception {
        //bsh.set( "unit", unit );
        //bsh.set( "toolSource", toolSource );
        //bsh.set( "data", data );
    }
    /**
     *
     * @param datas
     */
    public void setMeasureDatas( MeasureDatas datas ) {
        this.data = datas;
    }
    /**
     *
     * @param plane
     * @param value
     */
    public void doGoTo( char plane, Double value ) {
        try {
            String mess = "Задание азимутального угла ";
            if ( plane == 'y' ) {
                mess = "Задание зенитного угла ";
            }
            if ( plane == 'z' ) {
                mess = "Задание угла поворота ";
            }
            message2 = mess + value;
            unit.goToPoint( plane, value );
            doWaitComplete( plane );
            message2 = "";
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }
    /**
     *
     * @param plane
     */
    public void doWaitComplete( char plane ) {
        unit.waitNewData();
        unit.waitNewData();
        unit.waitNewData();
        unit.waitNewData();
        try {
            while ( !unit.isComplete( plane ) ) {
                Thread.sleep( 500 );
            }
        } catch ( InterruptedException ex ) {
        }
    }
    /**
     *
     * @param plane
     * @return
     */
    public double doMeasureAccurate( char plane ) {
        double value = 0;
        int subchan = planeToIndex( plane );
        Channel chan = unit.getChannel( "angles" );
        for ( int i = 0; i < measureCount; i++ ) {
            unit.waitNewData();
            value += chan.getValue( subchan ).getAsDouble();
        }
        value /= (double)measureCount;
        lastAccurate = value;
        return value;
    }
    /**
     *
     * @param plane
     * @return
     */
    public double doMeasureTool( char plane ) {
        double value = 0;
        int subchan = planeToIndex( plane );
        Channel chan = toolSource.getChannel( "angles" );
        if ( chan == null ) {
            /** @todo throw exce */
            return 0;
        }
        for ( int i = 0; i < measureCount; i++ ) {
            toolSource.waitNewData();
            double v1 = chan.getValue( subchan ).getAsDouble();
            value += normalize( lastAccurate, v1 );
        }
        value /= (double)measureCount;
        return value;
    }
    /**
     * Приведение показаний инклинометра к сравниваемым с эталонными
     * значениями
     * @param acc Показания эталона
     * @param tool Показания прибора
     * @return приведенное значение
     */
    public double normalize( double acc, double tool ) {
        double val = tool;
        if( acc > 180.0 )
            if( tool < 0 )
                tool += 360.0;
        if( acc < 100 )
            if( tool > 200 )
                tool -= 360.0;
        return val;
    }
    /**
     * Convert plane name to plane index
     * 'x' - 0; 'y' - 1; 'z' - 2
     * @param plane
     * @return
     */
    public int planeToIndex( char plane ) {
        int subchan = 0;
        if ( plane == 'y' ) {
            subchan = 1;
        } else if ( plane == 'z' ) {
            subchan = 2;
        }
        return subchan;
    }


}
