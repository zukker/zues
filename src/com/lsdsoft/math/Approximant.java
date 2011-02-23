package com.lsdsoft.math;

import java.util.Vector;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: Ural-Geo</p>
 *
 * @author lsdsoft
 * @version 1.0
 */
public class Approximant {
    public Approximant() {
    }
    /**
     * Расчет подходящей дроби для числа num, глубина дроби - deep
     * @param num double исходное число
     * @param deep int глубина приближения
     * @return DecimalFraction полученная дробь
     */
    public static DecimalFraction approx(double num, int deep) {
        DecimalFraction frac = new DecimalFraction();
        Vector vect = new Vector();
        //frac.integer = (long)num;
        //num -= frac.integer;
        for(int i =0; i< deep; i++) {
            long n = (long)num;
            vect.add(new Long(n));
            num -= n;
            num = 1.0/num;
        }
        int size = vect.size();
        long a = 1;
        long b = ((Long)vect.get( size - 1)).longValue();
        //System.out.println("a=" + a+"; b="+b);
        for(int i = 1; i < size; i++) {
            long t = b;
            long k = ((Long)vect.get( size - i- 1)).longValue();
            b = b* k +a;
            a = t;
            //System.out.println("a=" + a+"; b="+b);
        }
        frac.nominator = b;
        frac.denominator = a;
        return frac;
    }
    public static DecimalFraction approx(double num, double error) {
        DecimalFraction frac = null;
        for(int i =2;;i++) {
            frac = approx( num, i );
            double err = Math.abs(num - frac.value());
            if(error> err) {
                break;
            }
        }
        return frac;
    }

}
