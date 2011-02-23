package lsdsoft.metrolog;


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

public class MeasureTable
    implements XMLStorable {
    private static final String TAG_ATTR = "attr";
    private static final String TAG_CHAIN = "chain";

    protected Vector items = new Vector( 16, 16 );
    protected Properties attrs = new Properties();
    protected int defaultPointsCount = 4;
    public MeasureTable() {
    }

    public MeasureTable( int rowsCount, int pointsCount ) {
        defaultPointsCount = pointsCount;
        items.ensureCapacity( rowsCount );
        for ( int i = 0; i < rowsCount; i++ ) {
            items.add( new MeasureChain( pointsCount ) );
        }
    }

    public MeasureChain getChain( int index ) {
        return ( MeasureChain ) items.get( index );
    }

    public MeasureChain addChain( int pointsCount ) {
        MeasureChain chain = new MeasureChain( pointsCount );
        items.add( chain );
        return chain;
    }

    public MeasureChain addChain() {
        MeasureChain chain = new MeasureChain( defaultPointsCount );
        items.add( chain );
        return chain;
    }
    public void ensurePointsCount(int count) {
        for(int i = 0; i < items.size(); i++ ) {
            getChain(i).ensureSize(count);
        }
    }
    public void calc() {
        for(int i = 0; i < items.size(); i++) {
            getChain(i).calc();
        }
    }
    public void clear() {
        items.clear();
    }
    public boolean isEmpty() {
        boolean ret = true;
        for(int i = 0; i < items.size(); i++) {
            ret = ret && getChain(i).isEmpty();
        }
        return ret;
    }
    public int size() {
        return items.size();
    }

    // Возвращает Замер (MeasureChain) с максимальной оценкой
    // абс. погрешности.
    public MeasureChain getMaxDelta() {
        double cur_delta, max_delta;
        MeasureChain cur_chain, max_chain;

        max_chain = (MeasureChain)items.get(0);
        max_delta = max_chain.getDelta();
        for(int i = 0; i < items.size(); i++) {
            cur_chain = (MeasureChain)items.get(i);
            cur_delta = cur_chain.getDelta();
            if (Math.abs(cur_delta) > Math.abs(max_delta)) {
                max_chain = cur_chain;
                max_delta = cur_delta;
            }
        }
        return max_chain;
    }

    public String getProperty( String key ) {
        return attrs.getProperty( key, "" );
    }
    public String getProperty( String key, String def_key ) {
        return attrs.getProperty( key, def_key );
    }

    public void setProperty( String key, String value ) {
        attrs.setProperty( key, value );
    }

    public void load( Node parentNode ) {
        XMLUtil.loadAttributesFromNode(parentNode, attrs);
        NodeList list = ((Element)parentNode).getElementsByTagName( TAG_CHAIN);
        int size = list.getLength();
        items.clear();
        for(int i = 0; i < size; i++) {
            MeasureChain chain = new MeasureChain();
            chain.load(list.item(i));
            items.add(chain);
        }
    }

    /** Сохранение значений в родительский узел
     * @param parentNode родительский узел
     */
    public void save( Node parentNode ) {
        XMLUtil.saveAttributesToNode( parentNode, attrs );
        Document doc = parentNode.getOwnerDocument();
        Element elem;
        int size = items.size();
        for ( int i = 0; i < size; i++ ) {
            elem = doc.createElement( TAG_CHAIN );
            MeasureChain chain = getChain( i );
            chain.save( elem );
            parentNode.appendChild( elem );
        }
    }

}
