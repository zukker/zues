package lsdsoft.welltools.im.immn73;

import com.lsdsoft.math.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class IMMN73CorrectionTable {
    // ����
    SensorNormal bSensor = new SensorNormal();
    // ������������
    SensorNormal mSensor = new SensorNormal();

    // �������� �������� �� ������
    //SplineInterpolation zenithCorr = null;
    double[] zenithCorrX;
    double[] zenithCorrY;
    double[] zeniths; // �������� ������� ��� ������������ ���������
    double[][] azimuthCorrX;
    double[][] azimuthCorrY;
    //SurfaceInterpolation azimuthCorr = null;
    // ����� �������� �������� �� ������
    double zenithOffset = 0;
    double azimuthOffset = 0;
    // �������� ������� ��� ������������ ���������
    private double[] azZeniths = null;
    // ����������� ������� ��� ������������ ����������
    // ����������� ������� ��� �������� ����������

    //ZenitTable  zenitTable;
    //AzimutTable azimutTable;
//  SensorNormal bSensor; // ���� � ��������� ����
//  SensorNormal mSensor; // ���� � ��������� ������������
    //private IMMN73Computer computer;

    String number; // ��������� ����� �������
    boolean loaded;
    //void load(void * buf, int size);
    // ���������� ��������� �� ������ ������ �� ������� lineNumber
    // ��������� � ����
    //char* getLine(char * buf, int lineNumber);
    // ������ ��������� ���� �� ���������� �������� �������
    // ������ ������� ������� � IMMN73DataSource.hpp
    //void clearAzimutTable();
    //void setDefaults();
    public IMMN73CorrectionTable() {
        setDefaults();
    }
    public IMMN73CorrectionTable(String num) {
        setNumber( num );

    }
    //~IMMN73CorrectionTable();
    public void setNumber(String num) {
        DecimalFormat format = new DecimalFormat( "000" );
        int intnum = Integer.parseInt( num );
        number = format.format( intnum );
    }

    public String buildFileName() {
        String s;
        s = "IM73_" + number + ".CLB";
        return s;
    }

    private void setDefaults() {
        bSensor.zeroX = 2048;
        bSensor.zeroY = 2048;
        bSensor.amplitudeX = -2000; // wrong way to store neg values ^((
        bSensor.amplitudeY = -2000;
        mSensor.zeroX = 2048;
        mSensor.zeroY = 2048;
        mSensor.amplitudeX = -2000;
        mSensor.amplitudeY = -2000;
    }

    // loads table from text file
    //void load();
    public void load(InputStream ins) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(ins));
        String line = null;
        int linesCount = 0;
        ArrayList lines = new ArrayList(32);
        while ((line = br.readLine()) != null) {
            lines.add(line);
            linesCount++;
        }
        br.close();
        if( linesCount < 8 )
            throw new Exception("������������ ��� �������� ����������� �������");
        String s = (String)lines.get(3);
        bSensor.zeroX = Integer.parseInt( s.substring(11, 19).trim());
        bSensor.zeroY = Integer.parseInt( s.substring(18).trim());
        s = (String)lines.get(4);
        bSensor.amplitudeX = Integer.parseInt( s.substring(11, 19).trim());
        bSensor.amplitudeY = Integer.parseInt( s.substring(18).trim());
        s = (String)lines.get(6);
        mSensor.zeroX = Integer.parseInt( s.substring(11, 19).trim());
        mSensor.zeroY = Integer.parseInt( s.substring(18).trim());
        s = (String)lines.get(7);
        mSensor.amplitudeX = Integer.parseInt( s.substring(11, 19).trim());
        mSensor.amplitudeY = Integer.parseInt( s.substring(18).trim());
        if(linesCount > 10 ) { // may load zenith correction table
            //zenithCorr = new SplineInterpolation();
            s = (String)lines.get(8);
            zenithOffset = Double.parseDouble( s.substring( 63 ).trim() );
            s = (String)lines.get(9);  // �������� ��������
            String s2 = (String)lines.get(10); // �������� ��������
            StringTokenizer st1 = new StringTokenizer( s, " " );
            StringTokenizer st2 = new StringTokenizer( s2, " " );
            int size = st1.countTokens();
            zenithCorrX = new double[size];
            zenithCorrY = new double[size];
            if( st2.countTokens() < size )
                throw new Exception("������������ ����������� �������");
            for( int i = 0; i < size; i++ ){
                zenithCorrX[i] = Double.parseDouble(st1.nextToken());
                zenithCorrY[i] = Double.parseDouble(st2.nextToken());
            }
        }
        // loading azimuth correction
        if ( linesCount >= 24 ) { // may load azimuth correction table
            //zenithCorr = new SplineInterpolation();
            s = ( String ) lines.get( 11 );
            azimuthOffset = Double.parseDouble( s.substring( 60 ).trim() );
            int zens = ( linesCount - 12 ) / 3;
            azimuthCorrX = new double[zens][];
            azimuthCorrY = new double[zens][];
            zeniths = new double[zens];
            for(int z = 0; z < zens; z++ ) {
                s = ( String ) lines.get( 12 + z * 3 ); // �����
                zeniths[z] = Double.parseDouble( s.substring( 25 ).trim() );
                s = ( String ) lines.get( 12 + z * 3 + 1 ); // �������� ��������
                String s2 = ( String ) lines.get( 12 + z * 3 + 2 ); // �������� ��������
                StringTokenizer st1 = new StringTokenizer( s, " " );
                StringTokenizer st2 = new StringTokenizer( s2, " " );
                int size = st1.countTokens();
                azimuthCorrX[z] = new double[size];
                azimuthCorrY[z] = new double[size];
                if ( st2.countTokens() < size )
                    throw new Exception( "������������ ����������� �������" );
                for( int i = 0; i < size; i++ ){
                    double X  = Double.parseDouble( st1.nextToken() );
                    if(i > 0 && (azimuthCorrX[z][i - 1] - X) > 180)  {
                        X += 360.0;
                    }
                    azimuthCorrX[z][i] = X;
                    azimuthCorrY[z][i] = Double.parseDouble( st2.nextToken() );
                }

            }
        }
        lines.clear();
        loaded = true;


    }
    //void save();

    //bool isLoaded() { return loaded; }
    //void compute(int * sensorValues, InklinometerAngles& ang);
    //void correct(InklinometerAngles& angles);
    //SplineInterpolation* getZenitSpline();
    //SplineInterpolation* getAzimutSpline(unsigned int zenit);
    //double getZenit(unsigned int zenit);

};


