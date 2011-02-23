package com.lsdsoft.math;

import java.util.*;

/**
 * <p>Title: ћедианный фильтр</p>
 * <p>Description: ѕоиск медианы дл€ нечетного числа измерений. ћединой дл€ k
 * элементов последовательности называетс€ такое ее число M, дл€ которого
 * существует (k-1)/2 элементов меньших либо равных M и существует (k-1)/2
 * элементов больших либо равных M. јлгоритм реализуетс€ путем сортировки всех
 * элеменов и выбора центрального. ѕри этом центральный элемент M с индексом
 * (k-1)/2+1 удовлетвор€ет требовани€м определени€ медианы: все элементы левее
 * M меньше либо равны M, а с права больше либо равны M.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class MedianFilter {
    private double[] buffer;
    private double[] tempbuf;
    private int capacity;
    private int index;
    private int itemsCount = 0;
    public MedianFilter() {
        init( 5 );
    }
    public MedianFilter( int capacity ) {
        init(capacity);
    }
    private void init( int capacity ) {
        // если размер - четное число делаем его нечетным
        if( (capacity&1) == 0 )
            capacity++;
        this.capacity = capacity;
        buffer = new double[capacity];
        tempbuf = new double[capacity];
        index = 0;
        itemsCount = 0;
    }
    private void copy() {
        for( int i = 0; i < itemsCount; i++ ) {
            tempbuf[i] = buffer[i];
        }
    }
    public int getCapacity() {
        return capacity;
    }
    public int getSize() {
        return itemsCount;
    }
    public void add( double value ) {
        buffer[index] = value;
        if(itemsCount < capacity) {
            itemsCount ++;
        }
        index = (index + 1) % capacity;

    }
    public double average() {
        double res = 0;
        for( int i = 0; i < itemsCount; i++ ) {
            res += buffer[i];
        }
        if( itemsCount > 0 ) {
            res /= (double)itemsCount;
        }
        return res;
    }
    public double cuttedAverage() {
        int i = 0;
        int l = (capacity - 1)/4;
        copy();
        Arrays.sort(tempbuf);
        double sum = 0;
        for(i = l;i < capacity - l; i++) {
            sum += tempbuf[i];
        }
        return sum/(double)(capacity - 2 * l);
    }
    public double median() {
        int i = 0;
        copy();
        Arrays.sort(tempbuf);
        return tempbuf[capacity/2 + 1];
    }
    /**
     * –асчет среднеквадратичного отклонени€
     * @return double
     */
     public double calcSko() {
        double sko = 0;
        double avr = average();
        for ( int i = 0; i < itemsCount; i++ ) {
            double val = avr - buffer[i];
            val *= val;
            sko += val;
        }
        sko = Math.sqrt(sko);
        return sko;
    }
}
