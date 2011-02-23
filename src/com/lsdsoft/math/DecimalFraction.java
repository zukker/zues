package com.lsdsoft.math;


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
public class DecimalFraction {
    public long integer = 0;
    public long nominator = 0;
    public long denominator = 1;
    public DecimalFraction() {
    }
    public DecimalFraction(long nom, long denom) {
        nominator = nom;
        denominator = denom;
    }
    public double value() {
        return ((double)integer)+ ((double)nominator)/((double)denominator);
    }
    public void calcInt() {
        if(nominator >= denominator) {
            integer += nominator/denominator;
            nominator %= denominator;
        }
    }
    public void add(DecimalFraction num) {
        integer += num.integer;
        nominator = nominator* num.denominator + num.nominator * denominator;
        denominator *= num.denominator;
    }
    /**
     * Сокращение дроби. Числитель и знаменатель делятся на наибольший
     * общий делитель
     */
    public void cancel() {

    }
    public String toString() {
        String s = "";
        if(integer != 0) {
            s += integer + " ";
        }
        return (s + nominator + "/" + denominator);
    }
}
