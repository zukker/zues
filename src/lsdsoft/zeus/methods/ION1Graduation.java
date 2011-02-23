package lsdsoft.zeus.methods;

import lsdsoft.welltools.im.ion1.*;
import com.lsdsoft.math.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.metrolog.*;
import lsdsoft.units.*;
import lsdsoft.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class ION1Graduation
    extends InclinMethod {
    protected ION1GraduationData graduation = new ION1GraduationData();
    protected static int FILTER_SIZE = 9;

    public ION1Graduation() {
    }

    /**
     * Прямой ход градуировки датчиков Az Hxy
     */
    public void doAzHxyF() {
        MeasureTable tAz = data.selectTable( "name", "Az" );
        MeasureTable tHxy = data.selectTable( "name", "Hxy" );
        tAz.clear();
        tHxy.clear();
        graduation.tableAz.clear();
        graduation.tableHxy.clear();
        Channel chan1 = toolSource.getChannel( "angles2" );
        Channel achan = unit.getChannel( "angles" );
        message1 = "градуировка датчиков Az и Hxy";
        doGoTo( 'x', new Double( 90.0 ) );
        doGoTo( 'y', new Double( 90.0 ) );
        // @todo unit must be abstract here but not UAKSI
        UAKSI2CheckUnit unit = ( UAKSI2CheckUnit )this.unit;
        InclinometerAngles ang = unit.getAccurateDelta();
        ang.rotate.setAngle( 0 );
        doGoTo( 'z', new Double( 0 ) );
        message2 = "стабилизация";
        Util.delay( 4000 );
        message2 = "совмещение углов";
        for ( int i = 0; i < FILTER_SIZE; i++ ) {
            message2 = "замер  " + ( i + 1 );
            toolSource.waitNewData();
            // фильтруем Az
            //mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
            // Hxy
            //mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
        }
        double az = 0;
        //az = Angle.norm( 360.0 - mfilter1.cuttedAverage() );
        //double az0 = az;
        double hxy = 0;
        //double hxy0 = mfilter2.cuttedAverage();
        //ang.rotate.setAngle( doMeasureAccurate( 'z' ) - az );
        //hxy0 += ang.rotate.getValue();
        for ( double a = -1; a < 363.0; a += 3.0 ) {
            MeasureChain chainAz = tAz.addChain();
            chainAz.ensureSize( 9 );
            chainAz.setReproductionValue( a );
            MeasureChain chainHxy = tHxy.addChain();
            chainHxy.ensureSize( 9 );
            chainHxy.setReproductionValue( a );
            //doGoTo( 'z', new Double( a ) );
            //lMessage2.setText( "стабилизация" );
            Util.delay( 4000 );
            for ( int i = 0; i < FILTER_SIZE; i++ ) {
                //lMessage2.setText( "замер  " + ( i + 1 ) );
                Util.delay( 1000 );
                toolSource.waitNewData();
                // фильтруем Az
                //mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
                // Hxy
                //mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
                chainAz.getPoint( i ).setAccurateValue( achan.getValue( 2 ).
                    getAsDouble() );
                chainAz.getPoint( i ).setToolValue( chan1.getValue( 14 ).
                    getAsDouble() );
                chainHxy.getPoint( i ).setAccurateValue( achan.getValue( 2 ).
                    getAsDouble() );
                chainHxy.getPoint( i ).setToolValue( chan1.getValue( 15 ).
                    getAsDouble() );
            }
            double rotate = achan.getValue( 2 ).getAsDouble();
            //az = Angle.norm( 360.0 - mfilter1.cuttedAverage() );
            if ( Math.abs( rotate ) + 180.0 < az )
                az -= 360.0;
                //hxy = Angle.norm( mfilter2.cuttedAverage() - hxy0 );
            double daz = Angle.norm( rotate ) - az;
            double dhxy = Angle.norm( rotate ) - hxy;
            //lMessage3.setText( "Daz=" + daz );
            graduation.tableAz.add( rotate, daz );
            graduation.tableHxy.add( rotate, dhxy );
            //grAz.repaint();
            //grHxy.repaint();

        }
    }


}
