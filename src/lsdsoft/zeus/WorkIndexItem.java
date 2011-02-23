package lsdsoft.zeus;


import org.w3c.dom.*;
import lsdsoft.util.*;
import java.util.*;
import java.text.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class WorkIndexItem
    implements XMLStorable {
    private static final String TAG_ID = "id";
    private static final String TAG_DATE = "date";
    private static final String TAG_MODE = "mode";
    private static final String TAG_FINISHED = "finished";

    protected String id;
    protected WorkMode workMode = new WorkMode(WorkMode.MODE_CALIB);
    protected Date date;
    protected boolean isFinished;
    public WorkIndexItem() {
        id = "";
        date = new Date();
        isFinished = false;
    }

    public String getID() {
        return id;
    }

    public void setID( String id ) {
        this.id = id;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode( WorkMode workMode ) {
        this.workMode = workMode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }

    public void load( Node parentNode ) {
        id = XMLUtil.getTextTag( parentNode, TAG_ID );
        try {
            String str = XMLUtil.getTextTag( parentNode, TAG_DATE );
            date = DataFactory.getDataStorage().stringToDate(str);
        } catch ( Exception ex ) {
            System.err.println( ex );
        }
        workMode = new WorkMode(XMLUtil.getTextTag( parentNode, TAG_MODE ));
        isFinished = Boolean.getBoolean( XMLUtil.getTextTag( parentNode,
            TAG_FINISHED ) );

    }

    public void save( Node parentNode ) {
        XMLUtil.addTextTag( parentNode, TAG_ID, id );
        String dateStr = null;
        try {
            dateStr = DataFactory.getDataStorage().dateToString( date );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }
        XMLUtil.addTextTag( parentNode, TAG_DATE, dateStr );
        XMLUtil.addTextTag( parentNode, TAG_MODE, workMode.getName() );
        XMLUtil.addTextTag( parentNode, TAG_FINISHED,
                            Boolean.toString( isFinished ) );
    }

}