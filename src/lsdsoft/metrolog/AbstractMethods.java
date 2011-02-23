package lsdsoft.metrolog;


import java.util.*;

import org.w3c.dom.*;
import lsdsoft.util.*;


/**
 * <p>Абстрактная методика. Содержит точки, в которых нужно производить измерения</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class AbstractMethods
    implements XMLStorable {
    private static final String TAG_POINTS = "points";
    private static final String TAG_USETABLES = "usetables";

    protected Vector pointsList = new Vector( 16, 16 );
    protected Vector useTables = new Vector( 4 );

    public AbstractMethods() {
    }

    public Vector getPointsList() {
        return pointsList;
    }

    public Vector getUseTables() {
        return useTables;
    }

    public MethodsPoints getPoints( String name ) {
        MethodsPoints points;
        int size = pointsList.size();
        for ( int i = 0; i < size; i++ ) {
            points = ( MethodsPoints ) pointsList.get( i );
            if ( name.equals( points.attrs.getProperty( "name" ) ) ) {
                return points;
            }
        }
        // not found - return null
        return null;
    }

    public void load( Node parentNode ) {
        if ( ! ( parentNode instanceof Element ) ) {
            return;
        }
        pointsList.clear();
        useTables.clear();
        Element parentElem = ( Element ) parentNode;
        NodeList list = parentElem.getElementsByTagName( TAG_POINTS );
        int len = list.getLength();
        for ( int i = 0; i < len; i++ ) {
            MethodsPoints points = new MethodsPoints();
            points.load( list.item( i ) );
            pointsList.add( points );
        }
        list = parentElem.getElementsByTagName( TAG_USETABLES );
        if ( list.getLength() > 0 ) {
            String[] strs = list.item( 0 ).getFirstChild().getNodeValue().split(
                "," );
            for ( int i = 0; i < strs.length; i++ ) {
                useTables.add( strs[i] );
            }
        }

    }

    public void save( Node parentNode ) {
    }
}