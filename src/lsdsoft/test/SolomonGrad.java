package lsdsoft.test;

import com.lsdsoft.math.*;
import java.io.*;
//import lsdsoft.units.InclinometerAngles;
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

public class SolomonGrad {
    // coeffs for y=cA/cB(x-X0)^2 + cC/cD *(x-X0)
    private TableFunction table = new TableFunction();
    private long cX0, cA, cB, cC, cD;
    private double[] A = new double[3];


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
            throw new Exception("Broken table at line: " + (line+1));
        }
        return nums;
    }

    public void calcLine() {
        try {
            double[] X = table.getX();
            double[] Y = table.getY();
            // расчет сквадратичного приближения
            regress.RegressLine(X.length,X,Y,A);
            A[0] /= A[1];
            cX0 = -(int)A[0];
            DecimalFraction num2 = Approximant.approx(A[1], 0.00001);
            cC = (int)num2.nominator;
            cD = (int)num2.denominator;
            System.out.println("Coeffs for line function y(x)=C/D*(x-X0)");
            System.out.println("X0 = " + cX0);
            System.out.println("C = " + cC);
            System.out.println("D = " + cD);
            double R = 0, M = 0;
            for ( int i = 0; i < X.length; i++ ) {
                // X already substructed by X0
                long dX = ( long ) ( X[i] -(double) cX0 );
                long y = cC * dX / cD;
                System.out.print( "y[" + i + "] = " + y );
                System.out.print( "  Y[" + i + "] = " + ( long )Y[i] );
                double err = Math.abs( Y[i] - ( double )y );
                System.out.println( "  Err = " + err );
                R += err * err;
                if ( err > M ) {
                    M = err;
                }
            }

        } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());
        }


    }
    public void calc() {
        double D, X1 = 0, X2 = 0;
        int iX0 = 0, iX1, iX2;

        try {
            System.out.println("Coeffs for polynome function y(x)=A/B*(x-X0)^2+C/D*(x-X0)");
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

            System.out.println("A/B = " + num2);
            System.out.println("C/D = " + num3);
            System.out.println("Max err = " + M);
            System.out.println("R err = " + Math.round(R*1000.0)/1000.0);


        } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());
        }

    }


    public SolomonGrad(  ) {
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

    public static void main(String[] args) {
        SolomonGrad approx = new SolomonGrad();
        if(args.length > 0) {
            try {
                approx.loadTable( args[0] );
                approx.calcLine();
                approx.calc();
                approx.writeFile( args[0] + ".grad" );
            } catch ( Exception ex ) {
            }
        }
    }

}
