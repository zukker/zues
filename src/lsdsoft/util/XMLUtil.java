package lsdsoft.util;


/**
 * <p>Title: interface for XML DOM read/write</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CMI Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */
import org.w3c.dom.*;
import java.text.DecimalFormat;
import java.util.*;
import java.text.*;
import lsdsoft.metrolog.*;


public class XMLUtil {
    public static final int DONE = 0;
    public static final int ERROR = 1;
    public static int FRACTION_DIGITS = 4;
    private static final String TAG_ATTR = "attr";
    private static DecimalFormat form = new DecimalFormat( "#0.###" );
    private static int lastState;

    public XMLUtil() {

    }

    public static int getState() {
        return lastState;
    }

    /** get value of attribute as integer number
     * @param node node contains attribute
     * @param attr attribute name
     */
    public static int getIntegerAttribute( Element node, String attr ) {
        int retValue = 0;
        lastState = DONE;
        try {
            retValue = Integer.parseInt( node.getAttribute( attr ) );
        } catch ( NumberFormatException ex ) {
            lastState = ERROR;
        }
        return retValue;
    }

    public static double getDoubleAttribute( Element node, String attr ) {
        double retValue = 0;
        try {
            String s = node.getAttribute( attr );
            s = s.replace(',', '.' );
            //retValue = Double.parseDouble( s );
            retValue = Double.valueOf( s ).doubleValue();
        } catch ( NumberFormatException ex ) {
            System.err.println("Parse error:"+ex.getMessage()+ " while reading attr: "+attr);
            lastState = ERROR;
        }
        return retValue;
    }
    /**
     * ѕолучить значение атрибута, представленного в виде числа: число +- дельта.
     * ‘орма записи в виде строки: число@дельта
     * »спользуетс€ дл€ предоставлени€ измеренных величин с нормированными
     * погрешност€ми.
     * ѕример: "23.91@0.02" означает 23.91 +- 0.02
     * @param node
     * @param attr
     * @return
     */
    public static Value getValueAttribute( Element node, String attr ) {
        // по умолчанию нулевое значение
        Value retValue = new Value(0,0);
        try {
            String s = node.getAttribute( attr );
            // на вс€кий случай приводим зап€тую в точку
            s = s.replace(',', '.' );
            // place of 'plus/minus' tag
            int pm = s.indexOf('@');
            if(pm < 0) { // no tag, as simple decimal value
                retValue.value = Double.parseDouble( s );
                retValue.delta = 0;
            } else {
                retValue.value = Double.parseDouble( s.substring( 0, pm ) );
                retValue.delta = Double.parseDouble( s.substring( pm + 1 ) );
            }
        } catch ( NumberFormatException ex ) {
            System.err.println("Parse error:"+ex.getMessage()+ " while reading attr: "+attr);
            lastState = ERROR;
        }
        return retValue;
    }

    public static String getStringAttribute( Element node, String attr ) {
        String retValue = "";
        try {
            retValue = node.getAttribute( attr );
        } catch ( Exception ex ) {
            lastState = ERROR;
        }
        return retValue;
    }

    public static void setIntegerAttribute( Element node, String attr,
                                            int value ) {
        lastState = DONE;
        try {
            node.setAttribute( attr, String.valueOf( value ) );
        } catch ( NumberFormatException ex ) {
            lastState = ERROR;
        }
    }
    public static String doubleToString( double value ) {
        return Double.toString( Math.floor(value * 10000.0 + 0.5) / 10000.0 );
    }
    public static void setDoubleAttribute( Element node, String attr,
                                           double value ) {
        //form.setMaximumFractionDigits( FRACTION_DIGITS );
        //form.applyPattern("######0.#####");
        //DecimalFormatSymbols formsymb = new DecimalFormatSymbols(Locale.getDefault());
        //char c = form.getDecimalFormatSymbols().getDecimalSeparator();
        //form.getDecimalFormatSymbols().setDecimalSeparator('.');
        try {
            //String s = form.format( value );
            //String s = Double.toString( Math.floor(value * 10000.0 + 0.5) / 10000.0 );
            node.setAttribute( attr, doubleToString( value ) );
            //node.setAttribute( attr, Double.toString( value ) );
        } catch ( NumberFormatException ex ) {
            lastState = ERROR;
        }
    }
    public static void setValueAttribute( Element node, String attr,
                                           Value value ) {
        try {
            node.setAttribute( attr, value.toString(3));
        } catch ( NumberFormatException ex ) {
            lastState = ERROR;
        }
    }
    public static void setStringAttribute( Element node, String attr,
                                           String value ) {
        try {
            node.setAttribute( attr, value);
        } catch ( Exception ex ) {
            lastState = ERROR;
        }
    }

    public static void saveAttributesToNode( Node node, Properties props ) {
        Document doc = node.getOwnerDocument();
        Element elem;
        Enumeration keys = props.keys();
        Enumeration elems = props.elements();
        while ( keys.hasMoreElements() ) {
            String s;
            s = keys.nextElement().toString() + "=" +
                elems.nextElement().toString();
            addTextTag(node, TAG_ATTR, s);
            //elem = doc.createElement( TAG_ATTR );
            //Text text = doc.createTextNode( s );
            //elem.appendChild( text );
            //node.appendChild( elem );
        }

    }

    public static void loadAttributesFromNode( Node node, Properties attrs ) {
        attrs.clear();
        Element elem = (Element) node;
        NodeList list = elem.getElementsByTagName(TAG_ATTR);
        for(int i = 0; i < list.getLength(); i++) {
            String text = getTextTag(list.item(i));
            String[] s = text.split("=");
            if(s.length >= 2) {
                attrs.setProperty(s[0], s[1]);
            }
        }
    }

    /**
     * ƒобавл€ет к узлу новый узел с именем 'tagName', содержащий только
     * текстовую часть.
     * @param node ”зел, к которому нужно добавить новый узел
     * @param tagName им€ тэга узла
     * @param text текстовое содержание
     */
    public static void addTextTag( Node node, String tagName, String text ) {
        Document doc = node.getOwnerDocument();
        Element elem = doc.createElement( tagName );
        elem.appendChild( doc.createTextNode( text ) );
        node.appendChild( elem );
    }
    public static void addTextTagAsDouble( Node node, String tagName, double value ) {
        addTextTag( node, tagName, doubleToString( value ) );
    }
    public static void addTextTagAsValue( Node node, String tagName, Value value ) {
        addTextTag( node, tagName, value.toString(3));
    }
    /**
     * ¬озвращает текстовую чать простого тага <tag>text</tag>.
     * @param node ”казывает узел, содержащий текст
     * @return первый текст тэга, null если нет текста.
     */
    public static String getTextTag( Node node ) {
        Node child = node.getFirstChild();
        if(child.getNodeType() == Node.TEXT_NODE)
            return child.getNodeValue();
        return null;
    }

    public static String getTextTag( Node node, String tagName ) {
        Element elem = (Element) node;
        NodeList list = elem.getElementsByTagName(tagName);
        if(list.getLength() == 0)
            return null;
        Node child = list.item(0).getFirstChild();
        if(child.getNodeType() == Node.TEXT_NODE)
            return child.getNodeValue();
        return null;
    }

}
