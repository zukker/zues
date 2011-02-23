package lsdsoft.welltools.im.ins60;


import java.lang.*;
import lsdsoft.units.*;
import com.lsdsoft.math.*;


public class INS60Computer {

    INS60CorrectionTable table = null;

    //public INS60Computer() {
    //
    //}
    public INS60Computer( INS60CorrectionTable table ) throws Exception {
        if ( table == null ) {
            throw new Exception( "Empty table." );
        }
        this.table = table;
        precalc();
    }

    /**
     *
     * @param T double
     * @param Coeffs double[]
     * @return double
     */
    private static double CalcTA( double T, double[] Coeffs ) {
        return T * ( T * ( T * Coeffs[3] + Coeffs[2] ) + Coeffs[1] ) + Coeffs[0];
    }

    public InclinometerAngles CalcAngles( float AX, float AY, float AZ,
                                          float FX, float FY, float FZ,
                                          float T ) {
        InclinometerAngles RetVal = new InclinometerAngles();

        double cos_df = Math.cos( table.df );
        double sin_df = Math.sin( table.df );

        /******************************************************/
        double tax = CalcTA( T, table.tCoeffX );
        double RealAX = AX - table.aSens.axisX.zero - tax;
        if ( RealAX < 0 ) {
            RealAX /= table.aSens.axisX.ampNegative;
        } else {
            RealAX /= table.aSens.axisX.ampPositive;
        }

        double tay = CalcTA( T, table.tCoeffY );
        double RealAY = AY - table.aSens.axisY.zero - tay;
        if ( RealAY < 0 ) {
            RealAY /= table.aSens.axisY.ampNegative;
        } else {
            RealAY /= table.aSens.axisY.ampPositive;
        }

        double taz = CalcTA( T, table.tCoeffZ );
        double RealAZ = AZ - table.aSens.axisZ.zero - taz;
        if ( RealAZ < 0 ) {
            RealAZ /= table.aSens.axisY.ampNegative;
        } else {
            RealAZ /= table.aSens.axisY.ampPositive;
        }

        /******************************************************/

        double RealFX = FX - table.mSens.axisX.zero;
        if ( RealFX < 0 ) {
            RealFX /= table.mSens.axisX.ampNegative;
        } else {
            RealFX /= table.mSens.axisX.ampPositive;
        }

        double RealFY = FY - table.mSens.axisY.zero;
        if ( RealFY < 0 ) {
            RealFY /= table.mSens.axisY.ampNegative;
        } else {
            RealFY /= table.mSens.axisY.ampPositive;
        }

        double RealFZ = FZ - table.mSens.axisZ.zero;
        if ( RealFZ < 0 ) {
            RealFZ /= table.mSens.axisZ.ampNegative;
        } else {
            RealFZ /= table.mSens.axisZ.ampPositive;
        }

        /******************************************************/

        double dAX =
            RealAX * table.aSens.dXx + RealAY * table.aSens.dXy +
            RealAZ * table.aSens.dXz;
        double dAY =
            RealAX * table.aSens.dYx + RealAY * table.aSens.dYy +
            RealAZ * table.aSens.dYz;
        double dAZ =
            RealAX * table.aSens.dZx + RealAY * table.aSens.dZy +
            RealAZ * table.aSens.dZz;

        double dd = table.aSens.d;
        // seems 'dd' may be excluded, below it cancelled
        double ax = ( dAX / dd ) * cos_df - ( dAY / dd ) * sin_df;
        double ay = ( dAX / dd ) * sin_df + ( dAY / dd ) * cos_df;
        double az = dAZ / dd;

        /******************************************************/

        double dFX =
            RealFX * table.mSens.dXx + RealFY * table.mSens.dXy +
            RealFZ * table.mSens.dXz;
        double dFY =
            RealFX * table.mSens.dYx + RealFY * table.mSens.dYy +
            RealFZ * table.mSens.dYz;
        double dFZ =
            RealFX * table.mSens.dZx + RealFY * table.mSens.dZy +
            RealFZ * table.mSens.dZz;

        dd = table.mSens.d;

        double fx = ( dFX * cos_df - dFY * sin_df ) / dd;
        double fy = ( dFX * sin_df + dFY * cos_df ) / dd;
        double fz = dFZ / dd;

        /******************************************************/
        /******************************************************/

        final double M_PI = Math.PI;
        final double M_PI_2 = 2 * Math.PI;

        double Zenith, Vizir, Azimuth;

        // Calc Zenith
        Zenith = Math.atan2( Math.sqrt( ax * ax + ay * ay ), az );
        if ( Zenith < 0 ) {
            Zenith += M_PI;
        }

        // Calc Vizir
        Vizir = Math.atan2( ay, -ax );
        if ( Vizir < 0 ) {
            Vizir += M_PI_2;
        }
        /// TODO: Check this !!!!
        Vizir += table.df;
        if ( Vizir < 0 ) {
            Vizir += M_PI_2;
        }

        // Calc Azimuth
        double x = fx * Math.sin( Vizir ) + fy * Math.cos( Vizir );
        double y = ( fx * Math.cos( Vizir ) - fy * Math.sin( Vizir ) ) *
            Math.cos( Zenith );
        Azimuth = Math.atan2( -x, ( y + fz * Math.sin( Zenith ) ) );
        if ( Azimuth < 0 ) {
            Azimuth += M_PI_2;
        }

        RetVal.azimuth.setAngle( Azimuth * 180.0 / Math.PI );
        RetVal.vizir.setAngle( Vizir * 180.0 / Math.PI );
        RetVal.zenith.setAngle( Zenith * 180.0 / Math.PI );

        return RetVal;
    }

    private void precalc() {
        table.df = deg2Rad( table.dViz );
        wraps2Delta( table.aSens );
        wraps2Delta( table.mSens );
    }

    /**
     *
     * @param info SensorInfo
     */
    private static void wraps2Delta( INS60CorrectionTable.SensorInfo info ) {

        double AlphaX = deg2Rad( info.alphaX );
        double AlphaY = deg2Rad( info.alphaY );
        double AlphaZ = deg2Rad( info.alphaZ );

        double BettaX = deg2Rad( info.bettaX );
        double BettaY = deg2Rad( info.bettaY );
        double BettaZ = deg2Rad( info.bettaZ );
        double CAX = Math.cos( AlphaX );
        double CAZ = Math.cos( AlphaZ );
        double CBX = Math.cos( BettaX );
        double CBY = Math.cos( BettaY );
        double CBZ = Math.cos( BettaY );
        double SAX = Math.sin( AlphaX );
        double SAZ = Math.sin( AlphaZ );
        double SBX = Math.sin( BettaX );
        double SBY = Math.sin( BettaY );
        double SBZ = Math.sin( BettaY );

        info.d =
            CAX * CBX * CBY * CAZ * CBZ +
            SAX * CBX * SBY * SAZ * CBZ +
            SBX * CBY * SAZ * CBZ +
            CAX * CBX * SBY * SBZ;
        /*
                info.d =
         Math.cos( AlphaX ) * Math.cos( BettaX ) * Math.cos( BettaY ) *
                    Math.cos( AlphaZ ) * Math.cos( BettaZ )
                    +
         Math.sin( AlphaX ) * Math.cos( BettaX ) * Math.sin( BettaY ) *
                    Math.sin( AlphaZ ) * Math.cos( BettaZ )
                    +
         Math.sin( BettaX ) * Math.cos( BettaY ) * Math.sin( AlphaZ ) *
                    Math.cos( BettaZ )
                    +
         Math.cos( AlphaX ) * Math.cos( BettaX ) * Math.sin( BettaY ) *
                    Math.sin( BettaZ );
         */
        // dX?
        info.dXx =
            Math.cos( BettaY ) * Math.cos( AlphaZ ) * Math.cos( BettaZ ) +
            Math.sin( BettaY ) * Math.sin( BettaZ );
        info.dXy =
            Math.sin( AlphaX ) * Math.sin( BettaZ ) -
            Math.sin( AlphaX ) * Math.cos( BettaX ) * Math.cos( AlphaZ );
        info.dXz =
            Math.sin( AlphaX ) * Math.cos( BettaX ) * Math.sin( BettaY ) +
            Math.sin( BettaX ) * Math.cos( BettaY );

        // dY?
        info.dYx =
            Math.sin( BettaY ) * Math.sin( AlphaZ ) * Math.cos( BettaZ );
        info.dYy =
            Math.cos( AlphaX ) * Math.cos( BettaX ) * Math.cos( AlphaZ ) *
            Math.cos( BettaZ ) +
            Math.sin( BettaX ) * Math.sin( AlphaZ ) * Math.sin( BettaZ );
        info.dYz =
            Math.cos( AlphaX ) * Math.cos( BettaX ) * Math.sin( BettaY );

        // dZ?
        info.dZx =
            -Math.cos( BettaY ) * Math.sin( AlphaZ ) * Math.cos( BettaZ );
        info.dZy =
            Math.sin( AlphaX ) * Math.cos( BettaX ) * Math.sin( AlphaZ ) *
            Math.cos( BettaZ ) +
            Math.cos( AlphaX ) * Math.cos( BettaX ) * Math.sin( BettaZ );
        info.dZz =
            Math.cos( AlphaX ) * Math.cos( BettaX ) * Math.cos( BettaY );
    }

    /**
     * Convert degrees to radians
     * @param Grad double
     * @return double
     */
    private static double deg2Rad( double deg ) {
        return deg * Math.PI / 180.0;
    }

};
