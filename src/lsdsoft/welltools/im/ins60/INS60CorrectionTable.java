package lsdsoft.welltools.im.ins60;

import com.lsdsoft.math.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class INS60CorrectionTable {
    public class SensorInfo {
        public SensorAxisInfo axisX = new SensorAxisInfo();
        public SensorAxisInfo axisY = new SensorAxisInfo();
        public SensorAxisInfo axisZ = new SensorAxisInfo();

        public double alphaX, alphaY, alphaZ;  // неперпендикулярности
        public double bettaX, bettaY, bettaZ;

        public double dXx; // смещения из-за неперпендикулярности осей
        public double dXy; // вычисляются
        public double dXz;
        public double dYx;
        public double dYy;
        public double dYz;
        public double dZx;
        public double dZy;
        public double dZz;
        public double d;

    }

    public class SensorAxisInfo {
        public int zero;
        public int ampPositive;
        public int ampNegative;
        public SensorAxisInfo() {
            zero = 32768;
            ampPositive = 13000;
            ampNegative = 13000;
        }
    }

    // Акселерометры
    public SensorInfo aSens = new SensorInfo();

    // магнитометры
    public SensorInfo mSens = new SensorInfo();
    public double dViz; // смещение визира
    public double df;

    public double[] tCoeffX = new double[4];
    public double[] tCoeffY = new double[4];
    public double[] tCoeffZ = new double[4];

    private String number; // заводской номер прибора
    private boolean loaded;

    public INS60CorrectionTable() {
        setDefaults();
    }
    public INS60CorrectionTable(String num) {
        setDefaults();
        setNumber( num );
    }

    public void setNumber(String num) {
        DecimalFormat format = new DecimalFormat( "000" );
        int intnum = Integer.parseInt( num );
        number = format.format( intnum );
    }

    public String buildFileName() {
        String s;
        s = "ins-" + number + ".clb";
        return s;
    }

    private void setDefaults() {
        aSens.axisX.zero = 32768;
        aSens.axisX.ampPositive = 13000;
        aSens.axisX.ampNegative = 13000;
        aSens.axisY.zero = 32768;
        aSens.axisY.ampPositive = -13000;
        aSens.axisY.ampNegative = -13000;
        aSens.axisZ.zero = 32768;
        aSens.axisZ.ampPositive = -13000;
        aSens.axisZ.ampNegative = -13000;

        mSens.axisX.zero = 32768;
        mSens.axisX.ampPositive = 13000;
        mSens.axisX.ampNegative = 13000;
        mSens.axisY.zero = 32768;
        mSens.axisY.ampPositive = -13000;
        mSens.axisY.ampNegative = -13000;
        mSens.axisZ.zero = 32768;
        mSens.axisZ.ampPositive = -13000;
        mSens.axisZ.ampNegative = -13000;

    }

    static private double[] readNumbers( List list, int line, int count ) throws
        Exception {
        double[] nums;
        String s = (String)list.get(line);
        nums = parseNumbers(s);
        if(nums.length < count) {
            throw new BrokenTableException(line+1);
        }
        return nums;
    }
    // loads table from text file
    public void load(InputStream ins) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(ins));
        String line = null;
        int linesCount = 0;
        ArrayList lines = new ArrayList(32);
        // загрузка строк калибровочного файла
        while ((line = br.readLine()) != null) {
            lines.add(line);
            linesCount++;
        }
        br.close();
        if( linesCount < 23 ) {
            throw new BrokenTableException();
        }
        double[] nums;
        // акселерометры
        // нули акселерометров
        // в 5-й строке 3 числа
        nums = readNumbers(lines, 4, 3);
        aSens.axisX.zero = (int)nums[0];
        aSens.axisY.zero = (int)nums[1];
        aSens.axisZ.zero = (int)nums[2];
        // положительные амплитуды акселерометров
        nums = readNumbers(lines, 5, 3);
        aSens.axisX.ampPositive = (int)nums[0];
        aSens.axisY.ampPositive = (int)nums[1];
        aSens.axisZ.ampPositive = (int)nums[2];
        // отрицательные амплитуды
        nums = readNumbers(lines, 6, 3);
        aSens.axisX.ampNegative = (int)nums[0];
        aSens.axisY.ampNegative = (int)nums[1];
        aSens.axisZ.ampNegative = (int)nums[2];
        // несоосности
        nums = readNumbers(lines, 9, 2);
        aSens.alphaX = nums[0];
        aSens.bettaX = nums[1];

        nums = readNumbers(lines, 10, 1);
        aSens.bettaY = nums[0];
        aSens.alphaY = 0.0;

        nums = readNumbers(lines, 11, 2);
        aSens.alphaZ = nums[0];
        aSens.bettaZ = nums[1];

        // -----------------------------------------------------------------
        // магнитометры
        nums = readNumbers(lines, 13, 3);
        mSens.axisX.zero = (int)nums[0];
        mSens.axisY.zero = (int)nums[1];
        mSens.axisZ.zero = (int)nums[2];
        //
        nums = readNumbers(lines, 14, 3);
        mSens.axisX.ampPositive = (int)nums[0];
        mSens.axisY.ampPositive = (int)nums[1];
        mSens.axisZ.ampPositive = (int)nums[2];
        nums = readNumbers(lines, 15, 3);
        mSens.axisX.ampNegative = (int)nums[0];
        mSens.axisY.ampNegative = (int)nums[1];
        mSens.axisZ.ampNegative = (int)nums[2];

        // несоосности
        nums = readNumbers(lines, 17, 2);
        mSens.alphaX = nums[0];
        mSens.bettaX = nums[1];

        nums = readNumbers(lines, 18, 1);
        mSens.bettaY = nums[0];
        mSens.alphaY = 0.0;

        nums = readNumbers(lines, 19, 2);
        mSens.alphaZ = nums[0];
        mSens.bettaZ = nums[1];

        nums = readNumbers(lines, 20, 1);
        dViz = nums[0];

        // температурные коэффbциенты
        nums = readNumbers(lines, 21, 4);
        tCoeffX[0] = nums[3];
        tCoeffX[1] = nums[2];
        tCoeffX[2] = nums[1];
        tCoeffX[3] = nums[0];

        nums = readNumbers(lines, 22, 4);
        tCoeffY[0] = nums[3];
        tCoeffY[1] = nums[2];
        tCoeffY[2] = nums[1];
        tCoeffY[3] = nums[0];

        nums = readNumbers(lines, 23, 4);
        tCoeffZ[0] = nums[3];
        tCoeffZ[1] = nums[2];
        tCoeffZ[2] = nums[1];
        tCoeffZ[3] = nums[0];


/*
        if(linesCount > 10 ) { // may load zenith correction table
            //zenithCorr = new SplineInterpolation();
            s = (String)lines.get(8);
            zenithOffset = Double.parseDouble( s.substring( 63 ).trim() );
            s = (String)lines.get(9);  // заданные значения
            String s2 = (String)lines.get(10); // значения поправок
            StringTokenizer st1 = new StringTokenizer( s, " " );
            StringTokenizer st2 = new StringTokenizer( s2, " " );
            int size = st1.countTokens();
            zenithCorrX = new double[size];
            zenithCorrY = new double[size];
            if( st2.countTokens() < size )
                throw new Exception("Поврежденная поправочная таблица");
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
                s = ( String ) lines.get( 12 + z * 3 ); // зенит
                zeniths[z] = Double.parseDouble( s.substring( 25 ).trim() );
                s = ( String ) lines.get( 12 + z * 3 + 1 ); // значения поправок
                String s2 = ( String ) lines.get( 12 + z * 3 + 2 ); // значения поправок
                StringTokenizer st1 = new StringTokenizer( s, " " );
                StringTokenizer st2 = new StringTokenizer( s2, " " );
                int size = st1.countTokens();
                azimuthCorrX[z] = new double[size];
                azimuthCorrY[z] = new double[size];
                if ( st2.countTokens() < size )
                    throw new Exception( "Поврежденная поправочная таблица" );
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
 */
        lines.clear();
        loaded = true;


   }
    //void save();

    static private double[] parseNumbers(String str) {
        double[] nums;
        List list = new ArrayList();
        str = str.trim();
        // TODO: поддержка разделителей не только пробелов
        String[] strs = str.split(" ");
        for(int i = 0; i < strs.length; i++) {
            if(strs[i].length() > 0) {
                Double num = null;
                // выбираем только числа
                try {
                    num = new Double( strs[i] );
                } catch ( NumberFormatException ex ) {
                }
                if(num != null ) {
                    list.add( num );
                }
            }
        }
        int size = list.size();
        nums = new double[size];
        for(int i = 0; i < size; i++) {
            nums[i] = ((Double)list.get(i)).doubleValue();
        }
        return nums;
    }

};


