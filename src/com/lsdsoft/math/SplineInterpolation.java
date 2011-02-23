///////////////////////////////////////////////////////////////////////
//
// (c) Copyright 2003 "Ural-Geo"
//     info@uralgeo.com
//
// author: lsdsoft@mail.ru
//
///////////////////////////////////////////////////////////////////////
//
// Кубическая сплайн-интерполяция, -аппроксимация
// порядок работы:
// 1. установка размерности (количества узлов) - setDimension()
// 2. установка значений в узлах - []
// 3. предварительный расчет - preCalc()
// 4. расчет значений в произвольных точках - calc()
//
package com.lsdsoft.math;

import java.lang.*;
import java.util.*;

public class SplineInterpolation {
    //---------------------------------------------------------------------------
    public class Pair {
      public double x, y;
      public Pair() {
          x = 0;
          y = 0;
      }
      public Pair( double x, double y ) {
          this.x = x;
          this.y = y;
      }
      public int compare(Pair a) {
        return 0;
      }
    }
    //---------------------------------------------------------------------------
    private class PairComparator implements Comparator {
      public int compare(Object o1, Object o2) {
        return (int)(((Pair)o1).x - ((Pair)o2).x);
      }

    }
    //---------------------------------------------------------------------------
    private Pair[] points = null;
    private double[] coef;
    private PairComparator comparator = new PairComparator();
    /**
     * Building this spline by table setted function.
     * After building need call 'preCalc()'
     * @param table is table setted function
     */
    public void buildByTable( TableFunction table ) {
        int size = table.size();
        table.sortByX();
        setDimention( size );
        // copying function datas
        for(int i = 0; i < size;i++) {
            SplineInterpolation.Pair p = get(i);
            p.x = table.get(i).x;
            p.y = table.get(i).y;
        }
    }
    //---------------------------------------------------------------------------
    // check source 'x' values for incremental behavior
    private boolean check() {
      for (int i = 1; i < points.length; i++)
        if (points[i].x <= points[i - 1].x)
          return false;
      return true;
    }
    //---------------------------------------------------------------------------
    // sorting source points
    private void sort() {
      Arrays.sort(points, comparator);
    }
    //---------------------------------------------------------------------------
    public SplineInterpolation() {
        setDimention(0);
    }
    //---------------------------------------------------------------------------
    public void setDimention(int size) {
      points = new Pair[size];
      for(int i = 0; i < size; i++) {
          points[i] = new Pair();
      }
      coef = new double[size];
    }
    //---------------------------------------------------------------------------
    public int getDimension() {
      return points.length;
    }
    //---------------------------------------------------------------------------
    public void preCalc() throws Exception{
      int length = points.length;
      if(!check()) sort();
      if(length < 3) throw new Exception("SplineI: too low nodes");
      double d, e = 0, f, h;
      double[] L = new double[length];
      double[] R = new double[length];
      double[] S = new double[length];
      d = points[1].x - points[0].x;
      if(d == 0) return;
      //if(d != 0)
      e = (points[1].y - points[0].y) / d;
      for(int k = 1; k < length - 1; k++) {
        h = d;
        d = points[k + 1].x - points[k].x;
        f = e;
        if(d == 0) continue;
        if(d + h == 0) continue;
        e = (points[k + 1].y - points[k].y) / d;
        L[k] = d / (d + h);
        R[k] = 1 - L[k];
        S[k] = 6 * (e - f) / (h + d);
      }
      for(int k = 1; k < length - 1; k++) {
        double p = 1 / (R[k] * L[k - 1] + 2);
        L[k] = -L[k] * p;
        S[k] = (S[k] - R[k] * S[k - 1]) * p;
      }
      coef[length - 1] = 0;
      L[length - 2] = S[length - 2];
      coef[length - 2] = L[length - 2];
      for(int k = length - 3; k + 1 > 0; k--) {
        L[k] = L[k] * L[k + 1] + S[k];
        coef[k] = L[k];
      }
    }
    //---------------------------------------------------------------------------
    public double calc(double X) {
      double Y;
      if(points == null ) return 0;
      if(points.length < 1) return 0;
      int last = points.length - 1;
      if(X <= points[0].x) {
        double d = points[1].x - points[0].x;
        if(d == 0) return 0;
        Y = -d * coef[1] / 6 + (points[1].y - points[0].y) / d;
        Y = Y * (X - points[0].x) + points[0].y;
      } else
      if(X > points[last].x) {
        double d = points[last].x - points[last - 1].x;
        if(d == 0) return 0;
        Y = -d * coef[last - 1] / 6 + (points[last].y - points[last - 1].y) / d;
        Y = Y * (X - points[last].x) + points[last].y;
      } else {
        int i = 0, j;
        do {
          i++;
        } while(X > points[i].x);
        j = i - 1;
        double d = points[i].x - points[j].x;
        if(d == 0) return 0;
        double h = X - points[j].x;
        double r = points[i].x - X;
        double p = d * d / 6;
        Y = (coef[j] * r * r * r + coef[i] * h * h * h) / 6 /d;
        Y = Y + ((points[j].y - coef[j] * p) * r + (points[i].y - coef[i] * p) * h ) /d;
      }
      return Y;
    }
    //---------------------------------------------------------------------------
    public Pair get(int index) {
      return points[index];
    }
    //---------------------------------------------------------------------------
    public void set(int index, Pair value) {
      points[index] = value;
    }


};

//---------------------------------------------------------------------------
