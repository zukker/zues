///////////////////////////////////////////////////////////////////////
//
// (c) Copyright 2004 "Ural-Geo"
//     info@uralgeo.com
//
// author: lsdsoft@mail.ru
//
///////////////////////////////////////////////////////////////////////

package com.lsdsoft.math;


import java.lang.*;
import java.util.*;


public class SurfaceInterpolation {
    private int dimX; // количество точек по Х (раменость)
    private int dimY; // количество точек по У
    private double[] values; // значения функции в узлах (dimX*dimY значений)
    private double[] gridX; // значения узлов по Х
    private double[] gridY; // значения узлов по Y
    private SplineInterpolation[] xSplines; // сплайны
    private SplineInterpolation[] ySplines;

    // check source 'x' values for incremental behavior
    protected boolean check() {
        return true;
    }

    // sorting source points
    protected void sort() {

    }

    public SurfaceInterpolation( int sizeX, int sizeY ) {
        if ( sizeX <= 2 )
            sizeX = 2;
        if ( sizeY <= 2 )
            sizeY = 2;
        dimX = sizeX;
        dimY = sizeY;
        values = new double[ dimX * dimY ];
        gridX = new double[ dimX ];
        gridY = new double[ dimY ];
        xSplines = new SplineInterpolation[ dimX ];
        for ( int i = 0; i < dimX; i++ ) {
            xSplines[i] = new SplineInterpolation();
            xSplines[i].setDimention( dimY );
        }

        ySplines = new SplineInterpolation[ dimY ];
        for ( int i = 0; i < dimY; i++ ) {
            ySplines[i] = new SplineInterpolation();
            ySplines[i].setDimention( dimX );
        }

    }

    public void setGridX( int x, double gridValue ) {
        if ( x < 0 || x >= dimX )
            return;
        gridX[x] = gridValue;
        for ( int y = 0; y < dimY; y++ )
            ySplines[y].get( x ).x = gridValue;

    }

    public void setGridY( int y, double gridValue ) {
        if ( y < 0 || y >= dimY )
            return;
        gridY[y] = gridValue;
        for ( int x = 0; x < dimX; x++ )
            xSplines[x].get( y ).x = gridValue;

    }

    //void setDimention(int size);
    //int getDimension();
    // используется для заполнения массива 'values'
    public void setValue( int x, int y, double value ) {
        if ( x < 0 || x >= dimX )
            return;
        if ( y < 0 || y >= dimY )
            return;
        xSplines[x].get( y ).y = value;
        ySplines[y].get( x ).y = value;
        values[y * dimX + x] = value;
    }

    public void preCalc() throws Exception {
        for ( int i = 0; i < dimX; i++ )
            xSplines[i].preCalc();
        for ( int i = 0; i < dimY; i++ )
            ySplines[i].preCalc();
    }

    public double calc( double X, double Y ) {
        int x1 = -1, x2 = -1;
        int y1 = -1, y2 = -2;
        // find x node
        for ( int i = 0; i < dimX; i++ )
            if ( X < gridX[i] ) {
                x1 = i - 1;
                x2 = i;
                break;
            }
        if ( x2 == 0 )
            x1 = 0;
        if ( x1 == -1 )
            x1 = x2 = dimX - 1;
            // find y node
        for ( int i = 0; i < dimY; i++ )
            if ( Y < gridY[i] ) {
                y1 = i - 1;
                y2 = i;
                break;
            }
        if ( y2 == 0 )
            y1 = 0;
        if ( y1 == -1 )
            y1 = y2 = dimY - 1;
        double fx1, fx2, fy1, fy2;
        double f1, f2;
        fx1 = xSplines[x1].calc( Y );
        fx2 = xSplines[x2].calc( Y );
        fy1 = ySplines[y1].calc( X );
        fy2 = ySplines[y2].calc( X );
        if ( x1 != x2 ) {
            f1 = fx1 +
                ( fx2 - fx1 ) * ( X - gridX[x1] ) / ( gridX[x2] - gridX[x1] );
        } else
            f1 = fx1;
        if ( y1 != y2 ) {
            f2 = fy1 +
                ( fy2 - fy1 ) * ( Y - gridY[y1] ) / ( gridY[y2] - gridY[y1] );
        } else
            f2 = fy1;
        return ( f1 + f2 ) / 2.0;

    }

}
