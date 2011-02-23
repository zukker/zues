package lsdsoft.welltools;

import java.util.*;

import org.w3c.dom.*;
import lsdsoft.util.*;
import java.lang.reflect.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class ToolIndex
    implements XMLStorable {
    public class ToolIndexItem implements Comparable {
        public String number;
        public String dir;
        public String toString() {
            return number;
        }
        public int compareTo(Object o) {
            return number.compareTo(o.toString());
        }
    }
    private static final String TAG_TOOL = "tool";
    private static final String ATTR_NUMBER = "num";
    private static final String ATTR_DIR = "dir";


    private Vector data = new Vector(20, 20);

    public ToolIndex() {
    }
    public Vector getVector() {
        return data;
    }
    public String[] getData() {
        int size = data.size();
        String[] nums = new String[size];
        for(int i = 0; i < size; i++ ){
            ToolIndexItem item = (ToolIndexItem) data.get(i);
            nums[i] = item.number;
        }
        return nums;
    }

    public ToolIndexItem find(String number) {
        int size = data.size();
        for(int i = 0; i < size; i++ ){
            ToolIndexItem item = (ToolIndexItem) data.get(i);
            if(item.number.equals(number)) {
                return item;
            }

        }
        return null;
    }
    public void add(String number) {
        if(find(number) == null ) {
            ToolIndexItem item = new ToolIndexItem();
            item.number = number;
            item.dir = number;
            data.add(item);
        }
    }
    public void load(Node parentNode) {

        if(! (parentNode instanceof Element)) return;
        Element parentElem = (Element)parentNode;
        Element elem;
        NodeList list = parentElem.getElementsByTagName(TAG_TOOL);
        data.clear();
        //if(list.getLength() < 1) return;
        //elem = (Element)list.item(0);
        //System.out.println(elem.getNodeName());
        //list = parentElem.getElementsByTagName(tagWellToolType);
        //list = elem.getElementsByTagName(tagCoef);
        for (int i = 0; i < list.getLength(); i++) {
            Element cElem = (Element) list.item(i);
            ToolIndexItem item = new ToolIndexItem();
            item.number = cElem.getAttribute(ATTR_NUMBER);
            item.dir = cElem.getAttribute(ATTR_DIR);
            data.add(item);
        }
        sort();
    }
    public void save(Node parentNode) {
        sort();
        Document doc = parentNode.getOwnerDocument();
        Element elem;
        elem = (Element) parentNode;
        //elem = doc.createElement( TAG_WORKINDEX );
        //parentNode.appendChild( elem );
        int size = data.size();
        for ( int i = 0; i < size; i++ ) {
            Element e = doc.createElement( TAG_TOOL );
            ToolIndexItem item = (ToolIndexItem) data.get( i );
            e.setAttribute(ATTR_NUMBER, item.number);
            e.setAttribute(ATTR_DIR, item.dir);
            elem.appendChild( e );
        }
    }

    /**
     * sort
     */
    private void sort() {
        Collections.sort( data );
    }

}
