///////////////////////////////////////////////////////////////////////
//
// (c) Copyright 2003 "Ural-Geo"
//     info@uralgeo.com
//
// author: lsdsoft@mail.ru
//
///////////////////////////////////////////////////////////////////////
//
// Расчет инклинометрических углов для прибора ИММН73 по показаниям датчиков
// Для расчета надо задать значения нулей и амплитуд через getBSensor() и
// getMSensor() для БСКТ и магнитометров соответственно.
//

package lsdsoft.welltools.im.immn73;
import java.lang.*;
import lsdsoft.units.*;
import com.lsdsoft.math.*;

public class IMMN73Computer {

    //public SensorNormal bSensor; // нули и амплитуды БСКТ
    //public SensorNormal mSensor; // нули и амплитуды магнитометра
    IMMN73CorrectionTable table = null;
    SplineInterpolation zenithCorr = new SplineInterpolation();
    SplineInterpolation[] azimuthCorr = null;
    SplineInterpolation zenaz = new SplineInterpolation();
    boolean hasZenithCorr = false;
    boolean hasAzimuthCorr = false;

    private void build() throws Exception {
        if(table == null )
            return;
        hasZenithCorr = false;
        hasAzimuthCorr = false;
        int size = table.zenithCorrX.length;
        if(size > 3 ) {
            zenithCorr.setDimention( size );
            for ( int i = 0; i < size; i++ ) {
                zenithCorr.get( i ).x = table.zenithCorrX[i];
                zenithCorr.get( i ).y = table.zenithCorrY[i];
            }
            zenithCorr.preCalc();
            hasZenithCorr = true;
        }
        size = table.zeniths.length;
        if( size > 0 ) {
            zenaz.setDimention( size );
            azimuthCorr = new SplineInterpolation[size];
            for ( int z = 0; z < size; z++ ) {
                zenaz.get( z ).x = table.zeniths[z];
                int l = table.azimuthCorrX[z].length;
                azimuthCorr[z] = new SplineInterpolation();
                azimuthCorr[z].setDimention( l );
                for ( int i = 0; i < l; i++ ) {
                    double dX = table.azimuthCorrX[z][i];
                    double Y = table.azimuthCorrY[z][i];
                    if((i == 0) && (dX > 330.0)) {
                        dX -= 360.0;
                    }
                    azimuthCorr[z].get( i ).x = dX;
                    azimuthCorr[z].get( i ).y = Y;
                }
                azimuthCorr[z].preCalc();
            }
            hasAzimuthCorr = true;
        }
    }
    private double calcZenit(int[] sensorValues) {
        double aX, aY;
        double value = 0;
        // cos
        aX = sensorValues[11] - table.bSensor.zeroX;
        aX /= (double)table.bSensor.amplitudeX;
        // sin
        aY = sensorValues[9] - table.bSensor.zeroY;
        aY /= (double)table.bSensor.amplitudeY;
        if(aY == 0) {
            value = 90;
        } else {
            if(aX > aY) {
                value = Math.toDegrees(Math.atan(aY / aX));
            } else
                value = 90 - Math.toDegrees(Math.atan(aX / aY));
            value += table.zenithOffset;
            value = Math.abs(value);
        }
        return value;

    }
////////////////////////////////////////////////////////////////////////////////////////////////
    private double calcAzimut(int[] sensorValues) {
        double mX, mY, value;
        mX = sensorValues[10] - table.mSensor.zeroX;
        mX /= (double)table.mSensor.amplitudeX;
        mY = sensorValues[8] - table.mSensor.zeroY;
        mY /= (double)table.mSensor.amplitudeY;
        if(Math.abs(mX) > Math.abs(mY)) {
            if(mX == 0.0) value = 0.0;
            else
                value = Math.toDegrees(Math.atan(mY / mX));
        } else {
            if(mY == 0.0) value = 90.0;
            else
                value = 90 - Math.toDegrees(Math.atan(mX / mY));
        }
        if(value < 0) value += 180;
        value += table.azimuthOffset;
        if(mY < 0) {
            value += (mX < 0)?180:180;
        } else
            value += (mX < 0)?0:0;
        return value;
    }


    public IMMN73Computer(IMMN73CorrectionTable table) {
        this.table = table;
        try {
            build();
        } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    /*
      Расчет инлинометрических углов по показаниям датчиков прибора.
      sensorValues - массив, содержащий значения датчиков, соответствия
              элемнтов массива и датчиков орисаны в ...
      angles - результат вычислений: angles[0] - зенит, angles[1] - азимут
     */
    public void compute( int[] sensorValues, double[] angles ) {
        angles[0] = calcZenit( sensorValues );
        angles[1] = calcAzimut( sensorValues );
        System.out.println("Az="+angles[1]);
    }
    /**
     * Корректировка показаний прибора в соответствии с поправочной таблицей
     * @param angles значения углов angles[0] - зенит, angles[1] - азимут
     */
    public void correct( double[] angles, double[] out ) {
        // коррекция зенита
        out[0] = angles[0];
        if(hasZenithCorr) {
            out[0] += zenithCorr.calc( angles[0] );
        }
        // коррекция азимута
        out[1] = angles[1];
        if( hasAzimuthCorr ) {
            int size = zenaz.getDimension();
            // построение сплайна по зениту от азимута
            for ( int i = 0; i < size; i++ ) {
                // вычисление поправок во всех зенитных точках
                double azc = azimuthCorr[i].calc( angles[1] );
                zenaz.get( i ).y = azc;
            }
            try {
                zenaz.preCalc();
                double rr= zenaz.calc( out[0] );
                out[1] += rr;
                System.out.println( "zen=" + out[0] );
                System.out.println( "corraz=" + rr );
            } catch ( Exception ex ) {
                System.err.println( ex.getLocalizedMessage() );
            }
        }
    }
};


