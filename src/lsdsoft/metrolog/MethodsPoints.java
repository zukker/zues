package lsdsoft.metrolog;

import lsdsoft.util.XMLStorable;
import org.w3c.dom.Node;
import java.util.*;
import org.w3c.dom.*;
import lsdsoft.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class MethodsPoints
    implements XMLStorable {
    private static final String TAG_ATTR = "attr";
    private static final String TAG_POINT = "point";

    protected Vector points = new Vector( 16, 16 );
    protected Properties attrs = new Properties();
    public MethodsPoints() {
    }
    public Properties getAttributes() {
        return attrs;
    }
    public Vector getPoints() {
        return points;
    }
    public String getName() {
        return attrs.getProperty("name");
    }
    public String getType() {
        return attrs.getProperty("type");
    }
    public String getCross() {
        return attrs.getProperty("cross");
    }
    /**
     * Load data from <points> tag
     * @param parentNode
     */
    public void load( Node parentNode ) {
        if ( ! ( parentNode instanceof Element ) )
            return;

        // loading attributes
        XMLUtil.loadAttributesFromNode(parentNode, attrs);
        /*
        NodeList list = parentElem.getElementsByTagName( TAG_ATTR );
        int len = list.getLength();
        for ( int i = 0; i < len; i++ ) {
            Element element = ( Element ) list.item( i );
            String[] strs = element.getFirstChild().getNodeValue().split( "=" );
            if ( strs.length > 1 ) {
                attrs.setProperty( strs[0], strs[1] );
            }
        }*/

        // loading points values
        Element parentElem = ( Element ) parentNode;
        points.clear();
        NodeList list = parentElem.getElementsByTagName( TAG_POINT );
        int len = list.getLength();
        for ( int i = 0; i < len; i++ ) {
            //Element element = ( Element ) list.item( i );
            points.add( XMLUtil.getTextTag(list.item(i)) );
        }
    }

    public void save( Node parentNode ) {
        /**@todo Implement this lsdsoft.util.XMLStorable method*/
        throw new java.lang.UnsupportedOperationException(
            "Method save() not yet implemented." );
    }

}