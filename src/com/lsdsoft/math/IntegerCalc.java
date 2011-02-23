package com.lsdsoft.math;


import java.lang.Object;


public class IntegerCalc {
    public static int sqrt_i( int n ) {
        if ( n < 2 ) {
            return n;
        }
        int a = n, b = n / 2;
        while ( a > b ) {
            a = b;
            b = ( n / b + b ) >> 1;
        }
        return a;
    }
}
