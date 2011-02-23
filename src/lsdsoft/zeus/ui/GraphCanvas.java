package lsdsoft.zeus.ui;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.lsdsoft.math.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class GraphCanvas extends Surface {
    protected ArrayList graphs = new ArrayList(2);
    public Point2D.Float clipZero = new Point2D.Float(0.0f, 0.0f);
    public Point2D.Float clipSize = new Point2D.Float(20.0f, 10.0f);
    public Point areaLB = new Point(20, 20);
    public Point areaRT = new Point(5, 5);
    protected Color backgroundColor = Color.white;
    private SplineInterpolation spline = new SplineInterpolation();

    public GraphCanvas() {
    }
    public void addGraph(Graph graph) {
        graphs.add(graph);
    }
    public Graph get(int index ) {
        return (Graph)graphs.get(index);
    }
    private void renderGraph(int w, int h, Graphics2D g2, Graph graph) {
        if(graph == null )
            return;
        if( !graph.visible )
            return;
        int xr = areaLB.x;
        int yr = areaRT.y;
        int wr = w - areaLB.x - areaRT.x;
        int hr = h - areaLB.y - areaRT.y;
        //int size = function.size();
        double xscale = ( double )wr / ( double )clipSize.x;
        double yscale = ( double )hr / ( double )clipSize.y;
        TableFunction function = graph.function;
        int size = function.size();
        g2.setStroke(new BasicStroke( graph.lineWidth ));
        g2.setColor( graph.lineColor );
        if(function != null) {
            // кусочнолинейна€ интерпол€ци€
            if ( (graph.interpolation == Graph.INTERPOLATION_LINE) || (size < 3)) {
                for ( int i = 1; i < function.size(); i++ ) {
                    double xx = ( function.get( i - 1 ).x -
                                  ( double )clipZero.x ) * xscale;
                    double yy = ( function.get( i - 1 ).y -
                                  ( double )clipZero.y ) * yscale;
                    int x1 = xr + 1 + ( int ) ( xx );
                    int y1 = yr + hr - 1 - ( int ) ( yy );
                    xx = ( function.get( i ).x - ( double )clipZero.x ) *
                        xscale;
                    yy = ( function.get( i ).y - ( double )clipZero.y ) *
                        yscale;
                    int x2 = xr + 1 + ( int ) ( xx );
                    int y2 = yr + hr - 1 - ( int ) ( yy );
                    g2.drawLine( x1, y1, x2, y2 );
                }
            } else {
                // spline interpolation
                double xstep, ystep;
                spline.buildByTable(function);
                //function.sortByX();
                double xstart = function.get(0).x;
                double xstop = function.get(size - 1).x;
                //spline.setDimention( size );
                try {
                    spline.preCalc();
                } catch ( Exception ex ) { }
                xstep = clipSize.x * 1.0 / (double)wr;
                double xx = ( function.get(0).x - clipZero.x ) * xscale;
                double yy = ( spline.calc(function.get(0).x) - clipZero.y ) * yscale;
                int x1,y1,x2,y2;
                x1 = xr + 1 + ( int ) ( xx );
                y1 = yr + hr - 1 - ( int ) ( yy );
                for(double x = xstart; x < xstop; x+=xstep) {
                    xx = ( x - clipZero.x ) * xscale;
                    yy = ( spline.calc(x) - clipZero.y ) * yscale;
                    x2 = xr + 1 + ( int ) ( xx );
                    y2 = yr + hr - 1 - ( int ) ( yy );
                    g2.drawLine(x1, y1, x2, y2);
                    x1 = x2;
                    y1= y2;
                }
            }
        }
    }

    public void render( int w, int h, Graphics2D g2 ) {
        int y = 0;
        int xr = areaLB.x;
        int yr = areaRT.y;
        int wr = w - areaLB.x - areaRT.x;
        int hr = h - areaLB.y - areaRT.y;
        //int size = function.size();
        double xscale = ( double )wr / ( double )clipSize.x;
        double yscale = ( double )hr / ( double )clipSize.y;
        //g2.drawLine(0, 0, w, h);
        // draw background
        g2.setColor( backgroundColor );
        g2.fillRect( xr, yr, wr, hr );
        g2.setColor( Color.darkGray );
        g2.setStroke( new BasicStroke( 1.0f ) );
        g2.drawRect( xr, yr, wr, hr );
        // draw axis
        g2.drawLine( 2, h - 2, w - 2, h - 2 );
        g2.drawLine( 2, h - 2, w - 2, h - 2 );
        // draw graph
        g2.clipRect( xr, yr, wr, hr );
        for ( int i = 0; i < graphs.size(); i++ ) {
            renderGraph( w, h, g2, ( Graph )graphs.get( i ) );
        }
    }

}