package lsdsoft.metrolog;


import java.util.*;
import java.text.*;
import lsdsoft.zeus.*;
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

public class MeasureDatas
    implements XMLStorable {
    //private static final String PROP_DATE = "date";
    private static final String PROP_IDENTIFER = "identifier";
    //private static final String PROP_LOCALE = "locale";

    private static final String TAG_STATE = "state";
    private static final String TAG_OLD_GRAD = "oldgrad";
    private static final String TAG_NEW_GRAD = "newgrad";
    private static final String TAG_TABLES = "tables";
    private static final String TAG_TABLE = "table";

    protected Properties properties = new Properties();
    protected BaseGraduation newGrad = new BaseGraduation();
    protected BaseGraduation oldGrad = new BaseGraduation();
    protected Vector tables = new Vector( 16, 16 );
    protected WorkState state = new WorkState("toolType", "toolNumber");
    //protected Date startDate = new Date();
    //protected boolean finished = false;
    //protected WorkMode workMode = new WorkMode(WorkMode.MODE_CALIB);
    public MeasureDatas() {
    }
    public MeasureDatas(String toolType, String toolNumber) {
        state = new WorkState(toolType, toolNumber);
    }
    public MeasureDatas(WorkState workState) {
        this.state = workState;
    }
    public void addTable( MeasureTable table ) {
        tables.add( table );
    }
    public void clear() {
        tables.clear();
    }
    public void ensurePointsCount(int count) {
        for(int i = 0; i < tables.size(); i++ ){
            getTable(i).ensurePointsCount(count);
        }
    }
    public MeasureTable getTable( int index ) {
        return ( MeasureTable ) tables.get( index );
    }

    // Возвращает таблицу по её типу.
    public MeasureTable getTable(String type) {
        MeasureTable tbl;
        for(int i = 0; i < size(); i++) {
            tbl = getTable(i);
            if (tbl.getProperty("type").equals(type)) {
                return tbl;
            }
        }
        return null;
    }

    public void calc() {
        for(int i = 0; i < tables.size(); i++) {
            getTable(i).calc();
        }
    }
    public int size() {
        return tables.size();
    }

    public void setProperties( Properties properties ) {
        this.properties = new Properties( properties );
        initState();
    }
    protected void initState() {
        state.setWorkMode( new WorkMode(properties.getProperty( Zeus.PROP_WORK_MODE )));
        //properties.setProperty(Zeus.PROP_WORK_MODE, workMode.getName());
    }
    public WorkState getWorkState() {
        return state;
    }
    public void setWorkState(WorkState workState) {
        this.state = workState;
    }
    public Properties getProperties() {
        return properties;
    }
    public Date getStartDate() {
        return state.getStartDate();
    }
    public String getIdentifer() {
        return state.getIdentifier();
    }
    public String getToolType() {
        return state.getToolType();
    }
    public String getToolNumber() {
        return state.getToolNumber();
    }
    public WorkMode getWorkMode() {
        return state.getWorkMode();
    }
    /**
     * Select one measure table with key equals value
     * @param key searched key
     * @param value vakue of key which need to find
     * @return found table
     */
    public MeasureTable selectTable( String key, String value ) {
        MeasureTable foundTable = null;
        int size = tables.size();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            if ( value.equals( table.getProperty( key ) ) ) {
                foundTable = table;
            }
        }
        return foundTable;
    }
    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Vector selectTables( String key, String value ) {
        return selectTypedTables("*", key, value);
    }
    /**
     *
     * @param type
     * @param key
     * @param value
     * @return
     */
    public Vector selectTypedTables( String type, String key, String value) {
        Vector foundTables = new Vector( 16, 16 );
        int size = tables.size();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            if( type.equals(table.getProperty("type")) || type.equals("*")) {
                if ( value.equals( table.getProperty( key ) ) ||
                     value.equals( "*" ) ) {
                    foundTables.add( table );
                }
            }
        }
        return foundTables;
    }
    /**
     * Создает идентификатор данных, задает значения свойств: locale, date, identifer
     * Идентификатор имее вид type_number_datehash
     * Состоит из типа прибора, заводского номера и хэш-значения даты создания.
     * Идентификатор используется как уникальное имя файла для хранения данных.
     */
    public void generateDataIdentifer() {
        properties.setProperty( PROP_IDENTIFER, state.getIdentifier());
    }

    public void load( Node parentNode ) {
        Element parentElem = ( Element ) parentNode;
        Element elem;
        NodeList list = parentElem.getElementsByTagName( TAG_STATE );
        if(list.getLength() > 0) {
            state.load(list.item(0));
        }
        list = parentElem.getElementsByTagName( "properties" );
        if(list.getLength() > 0) {
            XMLUtil.loadAttributesFromNode(list.item(0), properties);
        }
        // load old graduation
        list = parentElem.getElementsByTagName( TAG_OLD_GRAD );
        if(list.getLength() > 0) {
            oldGrad.load(list.item(0));
        }
        // load new graduation
        list = parentElem.getElementsByTagName( TAG_NEW_GRAD );
        if(list.getLength() > 0) {
            newGrad.load(list.item(0));
        }
        // load tables
        list = parentElem.getElementsByTagName( TAG_TABLES );
        if(list.getLength() > 0 ) {
            elem = (Element) list.item(0);
            list = elem.getElementsByTagName( TAG_TABLE);
            int size = list.getLength();
            if(size>0) {
                tables.clear();
            }
            for(int i = 0; i < size; i++) {
                MeasureTable table = new MeasureTable();
                table.load(list.item(i));
                tables.add(table);
            }
        }
    }

    /** Сохранение значений в родительский узел
     * @param parentNode родительский узел
     */
    public void save( Node parentNode ) {
        //XMLUtil.saveAttributesToNode( parentNode, properties );
        Document doc = parentNode.getOwnerDocument();
        Element elem;
        int size = tables.size();
        // save STATE
        elem = doc.createElement( TAG_STATE );
        parentNode.appendChild( elem );
        state.save(elem);
        // save other properties
        elem = doc.createElement( "properties" );
        parentNode.appendChild( elem );
        XMLUtil.saveAttributesToNode(elem, properties);
        // save old graduation
        elem = doc.createElement( TAG_OLD_GRAD );
        parentNode.appendChild( elem );
        oldGrad.save(elem);
        // save new graduation
        elem = doc.createElement( TAG_NEW_GRAD );
        parentNode.appendChild( elem );
        newGrad.save(elem);
        // save tables
        elem = doc.createElement( TAG_TABLES );
        parentNode.appendChild( elem );
        for ( int i = 0; i < size; i++ ) {
            Element element = doc.createElement( TAG_TABLE );
            MeasureTable table = getTable( i );
            table.save( element );
            elem.appendChild( element );
        }

    }

}
