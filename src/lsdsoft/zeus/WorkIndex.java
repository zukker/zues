package lsdsoft.zeus;

import java.util.*;
import lsdsoft.util.*;
import org.w3c.dom.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class WorkIndex
    implements XMLStorable {

    private static final String TAG_ITEM = "item";
    private static final String TAG_WORKINDEX = "workindex";

    protected Vector items = new Vector(16, 16);

    public WorkIndex() {
    }
    public void add(WorkIndexItem item) {
        items.add(item);
    }
    public WorkIndexItem get(int index) {
        return (WorkIndexItem) items.get(index);
    }
    /**
     * Поиск элемента в списке работ с указанным идентификатиором.
     * @param id идентификатор работы
     * @return null - если ничего не найдено
     */
    public WorkIndexItem get(String id) {
        for(int i = 0; i < items.size(); i++) {
            WorkIndexItem item = get(i);
            if(item.getID().equals(id)) {
                return item;
            }
        }
        return null;
    }
    public void load(Node parentNode) {
        Element elem = (Element) parentNode;
        NodeList list = elem.getElementsByTagName(TAG_ITEM);
        int size = list.getLength();
        items.clear();
        for(int i = 0; i < size; i++) {
            WorkIndexItem item = new WorkIndexItem();
            item.load(list.item(i));
            items.add(item);
        }
    }
    public void save(Node parentNode) {
       Document doc = parentNode.getOwnerDocument();
       Element elem;
       elem = (Element) parentNode;
       //elem = doc.createElement( TAG_WORKINDEX );
       //parentNode.appendChild( elem );
       int size = items.size();
       for ( int i = 0; i < size; i++ ) {
           Element e = doc.createElement( TAG_ITEM );
           WorkIndexItem item = (WorkIndexItem) items.get( i );
           item.save( e );
           elem.appendChild( e );
       }
   }

}