package lsdsoft.zeus;


import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.xerces.dom.*;
import org.apache.xerces.parsers.*;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import lsdsoft.metrolog.*;
import lsdsoft.welltools.*;
import lsdsoft.welltools.ToolIndex.*;


/**
 * <p>’ранилище данных дл€ Zeus </p>
 * <p>—одержит информацию об организаци€х, скважинных приборах, установках, —ќ,
 * проводимых работах.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class DataStorage {
    public static final String PROP_TOOLINDEX_PATH = "path.toolindex";
    public static final String PROP_METHODS_PATH = "path.methods";
    public static final String PROP_DATE_FORMAT = "date.format";
    public static final String DEFAULT_DATE_FORMAT = "yyyy.MM.dd G HH:mm:ss z";
    private Zeus zeus = null;
    private Properties config = new Properties();
    private ToolInfoList toolInfoList = new ToolInfoList();
    private ToolIndex toolIndex = new ToolIndex();
    private boolean toolInfoLoaded = false;
    private DOMParser parser = new DOMParser();
    private Document doc = null;

    public DataStorage() {
        zeus = Zeus.getInstance();
        try {
            //Properties prop = zeus.getConfig();
            zeus.loadProperties( config, zeus.getConfig().getProperty( Zeus.
                PROP_DATASTORAGE_PROPERTIES ));
            //String u = zeus.getConfig().getProperty( Zeus.
            //    PROP_DATASTORAGE_PROPERTIES );
            //URL url = new URL( u );
            //InputStream ins = new FileInputStream( url.getFile() );
            //config.load( ins );
            //ins.close();
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }

    }

    public void formatXML( Document doc, String fileName ) throws Exception {
        OutputStream os1 = new FileOutputStream(fileName);
        OutputFormat of = new OutputFormat( doc );
        of.setIndenting( true );
        of.setIndent( 2 );
        of.setEncoding( "Windows-1251" );
        of.setLineWidth( 78 );
        of.setVersion( "1.0" );
        XMLSerializer ser = new XMLSerializer( os1, of );
        ser.serialize( doc );

    }
    /**
     * ќткрывает XML файл индекса работ дл€ указанного типа приборов и заводского номера
     * @param toolType тип прибора
     * @param number заводской номер
     * @return  орневой узел XML документа, null - если документ не найден
     */

    public Node openWorkIndex(String toolType, String number) throws Exception {
        getToolInfo();
        loadToolNumbers(toolType);
        ToolIndex.ToolIndexItem item = toolIndex.find(number);
        if(item != null) {
            String fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
                "/" + findToolInfo(toolType).getDir() + "/" +
                item.dir +
                "/workindex.xml";
            File file = new File(fileName);
            if( file.exists() ) {
                parser.parse( new InputSource( new FileInputStream( fileName ) ) );
                doc = parser.getDocument();
            } else {
                return null;
            }
            return doc.getDocumentElement();
        }
        return null;
    }
    /**
     * —оздает полное им€ файла индекса работ дл€ указанного прибора.
     * @param toolType тип прибора
     * @param number заводской номер
     * @return полное им€ файла индекса
     * @throws Exception фигн€ кака€-то
     */
    protected String getWorkIndexFileName(String toolType, String number) throws Exception {
        String fileName = null;
        getToolInfo();
        loadToolNumbers( toolType );
        ToolIndex.ToolIndexItem item = toolIndex.find( number );
        if ( item != null ) {
            fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
                "/" + findToolInfo( toolType ).getDir() + "/" +
                item.dir +
                "/workindex.xml";
        }
        return fileName;
    }

    protected Document createWorkIndexDocument() throws Exception {
        //String fileName = getWorkIndexFileName(toolType, number);
        doc = new DocumentImpl();
        Element elem = doc.createElement("workindex");
        doc.appendChild(elem);
        //datas.save(elem);
        //formatXML(doc, "d:/zeus/"+ datas.getIdentifer() + ".xml");
        return doc;

    }
    protected Document createToolIndexDocument() throws Exception {
        doc = new DocumentImpl();
        Element elem = doc.createElement("toolindex");
        doc.appendChild(elem);
        return doc;
    }

    public Document createXMLDocument() {
        Document doc = new DocumentImpl();
        doc.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"UTF-8\"");
        return doc;
    }

    public WorkIndex getWorkIndex( String toolType, String number ) throws
        Exception {
        Node node = openWorkIndex( toolType, number );
        WorkIndex workIndex = new WorkIndex();
        if(node != null) {
            workIndex.load( node );
        }
        return workIndex;
    }

    public void saveWorkIndex(String toolType, String number, WorkIndex workIndex) throws Exception {
        String fileName = getWorkIndexFileName(toolType, number);
        if(fileName == null)
            throw new Exception("Not found work index for tool " + toolType + "_" + number);
        Document doc = createWorkIndexDocument();
        workIndex.save(doc.getDocumentElement());
        formatXML(doc, fileName);
    }

    /**
     * buildToolIndexFileName
     *
     * @param toolType String
     */
    protected String buildToolIndexFileName( String toolType ) {
        ToolTypeInfo info = findToolInfo( toolType );
        String fileName = null;
        if ( info != null ) {
             fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
                "/" + info.getDir() + "/toolindex.xml";
        }
        return fileName;
    }

    /**
     * saveToolIndex
     */
    public void saveToolIndex() throws Exception {
        String fn = buildToolIndexFileName(zeus.getToolType());
        if ( fn != null ) {
            doc = createToolIndexDocument();
            toolIndex.save( doc.getDocumentElement() );
            formatXML( doc, fn );
        }
    }

    public void saveScript( Document doc, String filename ) throws Exception {
        String path = zeus.getProperty(Zeus.PROP_COMMANDS_DIR);
        path = path + "/" + filename;
        formatXML( doc, path );
    }

    public Document loadXML( String fileName ) throws Exception {
        parser.parse( new InputSource( new FileInputStream( fileName ) ) );
        return parser.getDocument();
    }

    private void loadToolInfo() throws Exception {
        String fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
            "/tooltypeindex.xml";
        parser.parse( new InputSource( new FileInputStream( fileName ) ) );
        doc = parser.getDocument();
        toolInfoList.load( doc.getDocumentElement() );
        toolInfoLoaded = true;
    }

    public AbstractMethods loadMethods( String fileName ) throws Exception {
        AbstractMethods methods = new AbstractMethods();
        fileName = config.getProperty( PROP_METHODS_PATH ) +
            "/" + fileName;
        parser.parse( new InputSource( new FileInputStream( fileName ) ) );
        doc = parser.getDocument();
        methods.load( doc.getDocumentElement() );
        return methods;
    }

    public ToolTypeInfo[] getToolInfo() throws Exception {
        if ( !toolInfoLoaded ) {
            loadToolInfo();
        }
        return toolInfoList.getData();
    }
    public ToolInfoList getToolInfoList() throws Exception {
        if ( !toolInfoLoaded ) {
            loadToolInfo();
        }
        return toolInfoList;
    }
    /**
     * —оздает список идентификаторов типов приборов
     * @return String[]
     * @throws Exception
     */
    public String[] getToolsList() throws Exception {
        if ( !toolInfoLoaded ) {
            loadToolInfo();
        }
        Object[] infoList = toolInfoList.getData();
        final int length = infoList.length;
        String[] tools = new String[length];
        for(int i = 0; i < length; i++ ) {
            tools[i] = ((ToolTypeInfo)infoList[i]).getID();
        }
        return tools;
    }
    /**
     * —оздает список имен приборов
     * @return String[]
     * @throws Exception
     */
    public String[] getToolsNamesList() throws Exception {
        if ( !toolInfoLoaded ) {
            loadToolInfo();
        }
        Object[] infoList = toolInfoList.getData();
        final int length = infoList.length;
        String[] tools = new String[length];
        for(int i = 0; i < length; i++ ) {
            tools[i] = ((ToolTypeInfo)infoList[i]).getName();
        }
        return tools;
    }
    /**
     * addToolNumber
     *
     * @param toolType String
     * @param number String
     */
    public void addToolNumber( String toolType, String number ) {
        try {
            // is number exists for reffered type
            String[] nums = getToolNumbers(toolType);
            loadToolNumbers(toolType);
            ToolIndexItem item = toolIndex.find(number);
            if(item == null) {
                // make new directory
                File file = new File(createFileName(toolType, number, "" ));
                boolean mayAdd = file.exists();
                mayAdd |= file.mkdirs();
                if(mayAdd) {
                    // add new number to list
                    toolIndex.add(number);
                    saveToolIndex();
                } else {
                    // else error message
                }
                // make default grads for all channels
            }
        } catch ( Exception ex ) {
        }
    }

    /**
     * createFileName
     *
     * @param toolType String
     * @param number String
     * @param suffix String
     * @return String
     */
    public String createFileName( String toolType, String number,
                                   String suffix ) {
        String fileName = null;
        fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
            "/" + toolType + "/" + number + "/" + suffix;
        return fileName;
    }

    public ToolTypeInfo findToolInfo( String id ) {
        ToolTypeInfo info = null;
        Vector vect = toolInfoList.getVector();
        for ( int i = 0; i < vect.size(); i++ ) {
            ToolTypeInfo w = ( ToolTypeInfo ) ( vect.get( i ) );
            if ( w.getID().equals(id) ) {
                info = w;
                break;
            }
        }
        return info;
    }

    private void loadToolNumbers() throws Exception {
        loadToolNumbers( zeus.getToolType() );
    }
    /**
     * —оздает пустой документ дл€ номеров приборов
     * @param toolType тип прибора дл€ которого нужно создать документ
     * @throws Exception
     */
    private void createToolNumbers(String toolType) throws Exception {
        Document doc = createXMLDocument();
        toolIndex.getVector().clear();
        toolIndex.save(doc.getDocumentElement());
        //formatXML(doc, fileName);


    }

    public void loadToolNumbers(String toolType) throws Exception {
        ToolTypeInfo info = findToolInfo( toolType );
        if ( info != null ) {
            String fileName = buildToolIndexFileName(toolType);
            // checking existance of file
            if(!new File(fileName).exists()) {
                toolIndex.getVector().clear();
                saveToolIndex();
            }
            parser.parse( new InputSource( new FileInputStream( fileName ) ) );
            doc = parser.getDocument();
            toolIndex.load( doc.getDocumentElement() );
        }
    }

    /**
     * —оздает список заводских номеров типа прибора, указанного в свойстве
     * Zeus.config "welltool.type"
     * @return ћассив строк, содержащий имеющиес€ номера приборов
     */
    public String[] getToolNumbers() throws Exception {
        loadToolNumbers();
        return toolIndex.getData();
    }
    public String[] getToolNumbers(String toolType) throws Exception {
        zeus.setToolType(toolType);
        loadToolNumbers();
        return toolIndex.getData();
    }
    protected void addMeasureDatasToIndex(MeasureDatas datas) throws Exception {
        WorkIndex index = getWorkIndex(datas.getToolType(), datas.getToolNumber());
        WorkIndexItem item = index.get(datas.getIdentifer());
        if(item == null) {
            item = new WorkIndexItem();
            item.id = datas.getIdentifer();
            item.date = datas.getStartDate();
            item.setWorkMode(datas.getWorkMode());
            index.add(item);
            saveWorkIndex(datas.getToolType(), datas.getToolNumber(), index);
        }

    }

    protected String getMeasureDatasFileName(String toolType, String toolNumber, String id) throws Exception {
        String fileName = null;
        getToolInfo();
        loadToolNumbers( toolType );
        ToolIndex.ToolIndexItem item = toolIndex.find( toolNumber );
        if ( item != null ) {
            fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
                "/" + findToolInfo( toolType ).getDir() + "/" +
                item.dir + "/" + id + ".xml";
        }
        return fileName;
    }

    public String getFileName(String toolType, String toolNumber, String suffix) throws Exception {
        String fileName = null;
        getToolInfo();
        loadToolNumbers( toolType );
        ToolIndex.ToolIndexItem item = toolIndex.find( toolNumber );
        if ( item != null ) {
            fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
                "/" + findToolInfo( toolType ).getDir() + "/" +
                item.dir + "/" + suffix;
        }
        return fileName;
    }

    /**
     * getFileNameForGraduation
     *
     * @param graduation Graduation
     */
    public String getFileNameForGraduation( Graduation graduation ) {
        String fileName = "";
        try {
            String toolType = graduation.getParameter( "type" );
            String number = graduation.getParameter( "number" );
            String channel = graduation.getParameter( "channel" );
            fileName = getFileName( toolType, number,
                                    getShortFileNameForGraduation(graduation) );
        } catch(Exception ex) {
            System.err.println(ex.getMessage());
        }
        return fileName;
    }

    protected String getMeasureDatasFileName(MeasureDatas datas) throws Exception {
        String fileName = null;
        String toolType = datas.getToolType();
        getToolInfo();
        loadToolNumbers( toolType );
        ToolIndex.ToolIndexItem item = toolIndex.find( datas.getToolNumber() );
        if ( item != null ) {
            fileName = config.getProperty( PROP_TOOLINDEX_PATH ) +
                "/" + findToolInfo( toolType ).getDir() + "/" +
                item.dir + "/" + datas.getIdentifer() + ".xml";
        }
        return fileName;
    }
    protected Document createMeasureDatasDocument() {
        doc = new DocumentImpl();
        doc.appendChild(doc.createElement("work"));
        return doc;
    }
    public void saveMeasureDatas(MeasureDatas datas) throws Exception {
        addMeasureDatasToIndex(datas);
        doc = createMeasureDatasDocument();
        datas.save(doc.getDocumentElement());
        formatXML(doc, getMeasureDatasFileName(datas));
    }
    public MeasureDatas
        loadMeasureDatas( String toolType,
                          String toolNumber,
                          String id ) throws Exception {
        WorkIndex index = getWorkIndex(toolType, toolNumber);
        index.get(id);
        MeasureDatas datas = new MeasureDatas(toolType, toolNumber);
        String fileName = getMeasureDatasFileName(toolType, toolNumber, id);
        parser.parse( new InputSource( new FileInputStream( fileName ) ) );
        doc = parser.getDocument();
        datas.load( doc.getDocumentElement() );
        return datas;

    }

    public String getDateFormat() {
        return config.getProperty(PROP_DATE_FORMAT, DEFAULT_DATE_FORMAT);
    }
    public String dateToString(Date date) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat(getDateFormat());
        String s = df.format( date );
        return s;
    }
    public Date stringToDate(String dateStr) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat(getDateFormat());
        df.setLenient(true);
        Date date = df.parse( dateStr );
        return date;

    }

    /**
     * saveGraduation
     *
     * @param toolType String
     * @param number String
     * @param graduation Graduation
     */
    public void saveGraduation( BaseGraduation graduation ) throws Exception {
        String fileName = getFileNameForGraduation(graduation);
        File file = new File(fileName);
        FileOutputStream outs = new FileOutputStream(file);
        Properties props = graduation.getParameters();
        props.store(outs, null);
        outs.close();
        addToGraduationIndex(graduation);
    }

    /**
     * addToGraduationIndex
     *
     * @param graduation Graduation
     */
    private void addToGraduationIndex( Graduation graduation ) {
        String toolType = graduation.getParameter( "type" );
        String number = graduation.getParameter( "number" );
        String channel = graduation.getParameter( "channel" );
        try {
            String indexFileName = getFileName( toolType, number,
                                           toolType + "." + number + ".grad" );
            Properties props = new Properties();

            String fngrad = getShortFileNameForGraduation(graduation);
            String fngradFull = getFileNameForGraduation(graduation);
            // skip any errors with opening file
            File fIndex = new File(indexFileName);
            if(fIndex.exists()) {
                InputStream ins = new FileInputStream( indexFileName );
                // load list of graduations
                props.load( ins );
                ins.close();
            }
            String pr = props.getProperty(fngrad, "");
            // checking for existance of graduation item
            if(pr.equals("")) {
                Enumeration list = props.propertyNames();
                int lastIndex = props.size();
                props.setProperty( "grad." + String.valueOf( lastIndex + 1 ),
                                   fngrad );
                try {
                    OutputStream outs = new FileOutputStream( indexFileName );
                    props.store( outs, "graduation list of '" + toolType + "' N " + number);
                    outs.close();
                } catch ( Exception ex ) {
                    System.err.println( "Unable to stor graduation index: " +
                                        indexFileName );
                }
            }
        } catch ( Exception ex ) {
        }

    }

    /**
     * getShortFileNameForGraduation
     *
     * @param graduation BaseGraduation
     * @return String
     */
    private static String getShortFileNameForGraduation( Graduation graduation ) {
        String toolType = graduation.getParameter( "type" );
        String number = graduation.getParameter( "number" );
        String channel = graduation.getParameter( "channel" );
        String fileName = toolType + "." + number + "." + channel + ".grad";
        return fileName;
    }
    /**
     * saveGraduations
     *
     * @param graduations Graduation[]
     */
    public void saveGraduations( Graduation[] graduations ) throws Exception {
        int size = graduations.length;
        for(int i = 0; i < size; i++) {
            if(graduations[i] instanceof BaseGraduation) {
                saveGraduation((BaseGraduation)graduations[i]);
            }
        }
    }
}
