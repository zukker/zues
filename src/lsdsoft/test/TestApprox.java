package lsdsoft.test;

import com.lsdsoft.math.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
//import lsdsoft.welltools.im.ins60.BrokenTableException;
import java.util.Properties;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class TestApprox {
    // coeffs for y=cA/cB(x-X0)^2 + cC/cD *(x-X0)
    private TableFunction table = new TableFunction();
    private long cX0, cA, cB, cC, cD;
    //private String name;
    public void loadTable(String fileName) throws Exception {
        System.out.println( "Loading table " + fileName );
        FileInputStream ins = new FileInputStream(fileName);
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
        for(int i = 0; i < lines.size(); i++) {
            try {
                double[] nums = readNumbers( lines, i, 2 );
                table.add( nums[0], nums[1] );
            } catch ( Exception ex ) {
                System.err.print( "Error at line " + ( i + 1 ) + ":" +
                                  ex.getMessage() );
            }

        }
        System.out.println("Table loaded. Contains "+table.size() + " points.");

    }
    static private double[] readNumbers( List list, int line, int count ) throws
        Exception {
        double[] nums;
        String s = (String)list.get(line);
        nums = parseNumbers(s);
        if(nums.length < count) {
            throw new Exception("Broken table at line " + Integer.toString(line+1));
        }
        return nums;
    }

    public void calc() {
        try {
            double[] X = table.getX();
            double[] Y = table.getY();
            // расчет сквадратичного приближения
            regress.RegressPoly(2,X.length,X,Y,A);
            // поиск нулей функции
            // дискриминант
            D = A[1]*A[1] - 4*A[0]*A[2];
            if(D>=0) {
                X1 = ( -A[1] + Math.sqrt(D) ) / ( 2 * A[2] );
                X2 = ( -A[1] - Math.sqrt(D) ) / ( 2 * A[2] );
            }
            // целые значения корней
            iX1 = (int)Math.round(X1);
            iX2 = (int)Math.round(X2);
            // должны уложиться в unsigned short
            if((iX1 > 0) && (iX1< 65535)) {
                iX0 = iX1;
            } else
            if((iX2 > 0) && (iX2< 65535)) {
                iX0 = iX2;
            } else
            if( (iX1 < 0) && (iX1+65536) > 0){
                iX0 = iX1;
                System.err.println("Error calculating zero cross point, possible incoorect data.");
            }
            System.out.println("X0 = " + iX0);
            // пересчет функции
            for(int i = 0; i < X.length; i++) {
                X[i] -= iX0;
            }
            regress.RegressPoly(2,X.length,X,Y,A);

            DecimalFraction num1 = Approximant.approx(A[0], 6);
            DecimalFraction num2 = Approximant.approx(A[1], 0.00001);
            DecimalFraction num3 = Approximant.approx(A[2], 0.0000000001);
            cA = (int)num3.nominator;
            cB = (int)num3.denominator;
            cC = (int)num2.nominator;
            cD = (int)num2.denominator;
            cX0 = iX0;

            System.out.println("X0 = " + cX0);
            System.out.println("A = " + cA);
            System.out.println("B = " + cB);
            System.out.println("C = " + cC);
            System.out.println("D = " + cD);

            double R = 0, M = 0;
            for(int i = 0; i < X.length; i++) {
                // X already substructed by X0
                long dX = (long)(X[i]);
                long y = cA*dX*dX/cB + cC*dX/cD;
                System.out.print("y["+i+"] = " + y);
                System.out.print("  Y["+i+"] = " + (long)Y[i]);
                double err = Math.abs(Y[i] - (double)y);
                System.out.println("  Err = " + err);
                R += err*err;
                if(err > M ) {
                    M = err;
                }
            }
            R = Math.sqrt(R/X.length);

            System.out.println("num1 = " + num1);
            System.out.println("num2 = " + num2);
            System.out.println("num3 = " + num3);
            System.out.println("Max err = " + M);
            System.out.println("R err = " + Math.round(R*1000.0)/1000.0);


        } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());
        }

    }


    public TestApprox(  ) {
    }
    double[] Y = {16, 520, 1017, 1515, 2016, 2516, 3017, 3517, 4020, 4519, 5019,
        5520, 6020, 6520, 7021, 7521, 8020, 8521, 9020, 9520, 10022, 10520, 10919};
    double[] X = { 28274, 28816, 29349, 29882, 30418, 30951, 31484, 32017, 32549,
        33076, 33607, 34136, 34665, 35190, 35716, 36242, 36766, 37288, 37810,
        38332, 38854, 39372, 39785
    };
    double[] A = new double[3];
    double D, X1, X2;
    int iX0, iX1, iX2;

    private void initTable() {
        table.clear();
        for(int i = 0; i < X.length; i++) {
            table.add(X[i], Y[i]);
        }
    }
    public void run() {
        initTable();
        calc();
    }
    public void writeFile(String fileName) {
        Properties props = new Properties();
        props.setProperty("X0", String.valueOf(cX0));
        props.setProperty("A", String.valueOf(cA));
        props.setProperty("B", String.valueOf(cB));
        props.setProperty("C", String.valueOf(cC));
        props.setProperty("D", String.valueOf(cD));
        try {
            props.store( new FileOutputStream( fileName ), "No comment" );
        } catch ( IOException ex ) {
            System.err.println(ex.getMessage());
        }
    }
    public static void main(String[] args) {
        TestApprox approx = new TestApprox();
        if(args.length > 0) {
            try {
                approx.loadTable( args[0] );
                approx.calc();
                approx.writeFile( args[0] + ".grad" );
            } catch ( Exception ex ) {
            }
        }
    }

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

}
