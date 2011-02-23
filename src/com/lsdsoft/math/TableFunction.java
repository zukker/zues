package com.lsdsoft.math;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class TableFunction {
    public class Point {
        public double x,y;
        public Point( double x, double y ) {
            this.x = x;
            this.y = y;
        }
    }
    class ComparatorX implements Comparator {
        public int compare( Object p1, Object p2 ) {
            return (((Point)p1).x < ((Point)p2).x)?-1:
                ((((Point)p1).x > ((Point)p2).x)?1:0);
        }
    }
    class ComparatorY implements Comparator {
        public int compare( Object p1, Object p2 ) {
            return (((Point)p1).y < ((Point)p2).y)?-1:
                ((((Point)p1).y > ((Point)p2).y)?1:0);
        }
    }
    private ComparatorX comparatorX = new ComparatorX();
    private ComparatorY comparatorY = new ComparatorY();

    protected ArrayList points = new ArrayList(32);
    public TableFunction() {
    }
    public void add( double x, double y ) {
        points.add(new Point(x,y));
    }
    public void clear() {
        points.clear();
    }
    public Point get(int index) {
        return (Point)points.get(index);
    }
    public double[] getX() {
        double[] X = new double[points.size()];
        for(int i = 0; i < points.size(); i++) {
            X[i] = get(i).x;
        }
        return X;
    }
    public double[] getY() {
        double[] Y = new double[points.size()];
        for(int i = 0; i < points.size(); i++) {
            Y[i] = get(i).y;
        }
        return Y;
    }
    public int size() {
        return points.size();
    }
    public void sortByX() {
        Collections.sort(points, comparatorX );
    }
    public void sortByY() {
        Collections.sort(points, comparatorY );
    }

}
