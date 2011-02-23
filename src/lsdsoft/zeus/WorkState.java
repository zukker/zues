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

public class WorkState
implements Cloneable, XMLStorable{
    private static final String PROP_STARTDATE = "startdate";
    private static final String PROP_FINISHDATE = "finishdate";
    private static final String PROP_WORKMODE = "workmode";
    private static final String PROP_IDENTIFER = "identifier";
    private static final String PROP_LOCALE = "locale";
    private static final String PROP_TOOL_TYPE = "tooltype";
    private static final String PROP_TOOL_NAME = "toolname";
    private static final String PROP_TOOL_CHANNEL = "toolchannel";
    private static final String PROP_TOOL_NUMBER = "toolnumber";
    private static final String PROP_FINISHED = "finished";
    private static final String PROP_OPERATOR = "operator";
    private static final String PROP_TEMPERATURE = "temperature";

    private static final String TAG_ID = "id";
    private static final String TAG_STARTDATE = "startdate";
    private static final String TAG_FINISHDATE = "finishdate";
    private static final String TAG_MODE = "mode";
    private static final String TAG_FINISHED = "finished";

    protected String toolType;
    protected String toolName;
    protected String toolNumber;
    protected String toolChannel = "";
    protected String identifier;
    protected String operator = "Noname"; // operator's name
    protected WorkMode workMode = new WorkMode(WorkMode.MODE_CALIB);
    protected double temperature = 20.0;  // environmet temperature
    protected Date startDate;
    protected Date finishDate = new Date();
    protected boolean finished = false;
    protected Locale locale = Locale.getDefault();

    public WorkState(String toolType, String toolNumber) {
        this.toolType = toolType;
        this.toolNumber = toolNumber;
        startDate = new Date();
        setName();
        generateIdentifier();
    }
    public WorkState(String toolType, String toolNumber, Date startDate) {
        this.toolType = toolType;
        this.toolNumber = toolNumber;
        this.startDate = startDate;
        setName();
        generateIdentifier();
    }
    protected void setName() {
        try {
            toolName = DataFactory.getToolInfo( toolType ).getName();
        } catch ( Exception ex ) {
        }
    }
    public void setToolNumber( String toolNumber) {
        this.toolNumber = toolNumber;
        generateIdentifier();
    }

    public String getToolChannel() {
        return toolChannel;
    }
    public String getToolType() {
        return toolType;
    }
    public String getToolNumber() {
        return toolNumber;
    }
    public String getToolName() {
        return toolName;
    }
    public String getIdentifier() {
        return identifier;
    }
    public Date getStartDate() {
        return startDate;
    }
    public WorkMode getWorkMode() {
        return workMode;
    }
    public void setToolChannel(String channel) {
        toolChannel = channel;
    }
    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }
    public void start() {
        startDate = new Date();
        finished = false;
        generateIdentifier();
    }
    public void finish() {
        finishDate = new Date();
        finished = true;
    }
    public boolean isFinished() {
        return finished;
    }
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        WorkState newState = new WorkState(new String(toolType), new String(toolNumber));
        newState.toolChannel = this.toolChannel;
        newState.locale = (Locale)locale.clone();
        newState.startDate = (Date)startDate.clone();
        newState.finishDate = (Date)finishDate.clone();
        newState.identifier = new String(identifier);
        newState.workMode = (WorkMode)workMode.clone();
        return newState;
    }
    public void generateIdentifier() {
        StringBuffer sb = new StringBuffer( 32 );
        String dateString = TextUtil.dateToString( startDate );
        //properties.setProperty( PROP_DATE, s );
        sb.append( toolType );
        sb.append( '_' );
        sb.append( toolNumber );
        sb.append( '_' );
        //sb.append( toolChannel );
        //sb.append( '_' );
        sb.append( Integer.toHexString( dateString.hashCode() ) );
        identifier = sb.toString();
        //properties.setProperty( PROP_IDENTIFER, sb.toString() );
        //properties.setProperty( PROP_LOCALE, Locale.getDefault().toString() );
    }
    public Properties generateProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_TOOL_TYPE, toolType);
        props.setProperty(PROP_TOOL_NUMBER, toolNumber);
        props.setProperty(PROP_TOOL_CHANNEL, toolChannel);
        props.setProperty(PROP_LOCALE, locale.toString());
        props.setProperty(PROP_IDENTIFER, identifier);
        props.setProperty(PROP_WORKMODE, workMode.getName());
        props.setProperty(PROP_STARTDATE, TextUtil.dateToString(startDate));
        props.setProperty(PROP_FINISHDATE, TextUtil.dateToString(finishDate));
        props.setProperty(PROP_FINISHED, Boolean.toString(finished));
        props.setProperty(PROP_OPERATOR, operator);
        props.setProperty(PROP_TEMPERATURE, Double.toString(temperature));
        return props;
    }
    public void load( Node parentNode ) {
        /** @todo checking for null strings */
        Properties props = new Properties();
        XMLUtil.loadAttributesFromNode(parentNode, props);
        toolType = props.getProperty(PROP_TOOL_TYPE);
        toolNumber = props.getProperty(PROP_TOOL_NUMBER);
        toolChannel = props.getProperty(PROP_TOOL_CHANNEL);
        locale = new Locale(props.getProperty(PROP_LOCALE));
        identifier = props.getProperty(PROP_IDENTIFER);
        workMode = new WorkMode(props.getProperty(PROP_WORKMODE));
        startDate = TextUtil.stringToDate(props.getProperty(PROP_STARTDATE));
        finishDate = TextUtil.stringToDate(props.getProperty(PROP_FINISHDATE));
        finished = Boolean.getBoolean(props.getProperty(PROP_FINISHED));
        operator = props.getProperty(PROP_OPERATOR, "");
        temperature = Double.parseDouble(props.getProperty(PROP_TEMPERATURE,"0"));
        setName();
        /*
        identifier = XMLUtil.getTextTag( parentNode, TAG_ID );
        try {
            String str = XMLUtil.getTextTag( parentNode, TAG_DATE );
            startDate = DataFactory.getDataStorage().stringToDate(str);
        } catch ( Exception ex ) {
            System.err.println( ex );
        }
        workMode = new WorkMode(XMLUtil.getTextTag( parentNode, TAG_MODE ));
        isFinished = Boolean.getBoolean( XMLUtil.getTextTag( parentNode,
            TAG_FINISHED ) );
*/
    }

    public void save( Node parentNode ) {
        XMLUtil.saveAttributesToNode(parentNode, generateProperties());
    }

}
