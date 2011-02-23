package lsdsoft.zeus;


import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import com.sun.java.swing.plaf.mac.*;
import lsdsoft.zeus.ui.*;
import lsdsoft.util.*;
import org.grlea.log.*;
import lsdsoft.welltools.im.immn73.*;
import com.lsdsoft.math.*;
import lsdsoft.welltools.*;
import lsdsoft.metrolog.ToolItemInfo;
import lsdsoft.metrolog.AbstractMethods;
import javax.swing.plaf.metal.MetalLookAndFeel;
import com.amper.gui.ExMetalLookAndFeel;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.basic.BasicLookAndFeel;
import com.amper.io.AProperties;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class Zeus {
    public static final SimpleLogger log = new SimpleLogger(Zeus.class);
    // current work mode
    public static final String PROP_WORK_MODE = "workmode";
    public static final String DEFAULT_WORK_MODE = "calib";

    public static final String PROP_WORK_DIR = "zeus.workdir";
    public static final String PROP_DATASTORAGE_PROPERTIES =
        "datastorage.properties";
    public static final String PROP_DATASOURCES_PROPERTIES =
        "datasources.properties";
    public static final String PROP_VIEWERS_PROPERTIES =
        "viewers.properties";
    public static final String PROP_RIGS_PROPERTIES =
        "rigs.properties";
    public static final String PROP_TOOL_TYPE = "tool.type";
    public static final String PROP_TOOL_NAME = "tool.name";
    public static final String PROP_TOOL_NUMBER = "tool.number";
    public static final String PROP_TOOL_CHANNEL = "tool.channel";
    public static final String PROP_VIEWER_CLASS = "viewer.class";
    public static final String PROP_CHECKUNIT_CLASS = "checkunit.class";
    public static final String PROP_WORK_ID = "workdatas.id";
    public static final String PROP_WORK_NEW = "workdatas.new";
    public static final String PROP_COMMANDS_DIR = "commands.dir";



    public static final String PROP_TOOL_DATASOURCE_ID =
        "tool.datasource.id";
    public static final String PROP_TOOL_DATASOURCE_CLASS =
        "tool.datasource.class";

    // property for current selected company id
    public static final String PROP_COMPANY = "zeus.company";
    // general properties for Zeus
    private static Zeus instance = null;
    private static AProperties config = new AProperties();
    private Properties viewers = new Properties();
    private Properties datasources = new Properties();
    private Properties logproperties = new Properties();
    private Properties rigs = new Properties();

    private String workDir = "/zeus/";
    private static String zeusPropertiesFile = "zeus.properties";
    private Wizard wizard;
    private WorkMode workMode = new WorkMode( WorkMode.MODE_CALIB );
    /**
     * Возвращает настройки системы Zeus.config.
     * @return config
     */
    public Properties getConfig() {
        return config;
    }

    /**
     * Возвращает экземпляр Zeus. Он может быть только один.
     * Если в текщий момент нет рабочего экземпляра, то создается новый.
     * @return экземпляр Zeus
     */
    public static Zeus getInstance() {
        if ( instance == null ) {
            instance = new Zeus();
        }
        return instance;
    }

    public ToolItemInfo getCalibrationRigForChannel(String channel) {
        String rig = rigs.getProperty(channel);
        String[] strs = rig.split("\\x2E");
        ToolItemInfo ti = null;
        if(strs.length >= 2)  {
            ti = new ToolItemInfo();
            ti.type = strs[0];
            ti.number = strs[1];
        }
        return ti;
    }
    /**
     * Задать текущий режим работы.
     * Задает значение ключа {@link #PROP_WORK_MODE }.
     * @param workMode Строковое значение режима работы
     */
    public void setWorkMode( int workMode ) {
        this.workMode = new WorkMode( workMode );
        config.setProperty( PROP_WORK_MODE, this.workMode.getName() );
    }

    public void setWorkMode( String workModeName ) {
        this.workMode = new WorkMode( workModeName );
        config.setProperty( PROP_WORK_MODE, this.workMode.getName() );
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public String getToolType() {
        return config.getProperty( PROP_TOOL_TYPE );
    }

    public void setToolType( String type ) {
        ToolTypeInfo info = null;
        try {
            info = DataFactory.getToolInfo( type );
        } catch ( Exception ex ) {
        }
        if( info != null ) {
            config.setProperty( PROP_TOOL_TYPE, type );
            setToolName();
        }
    }
    public void setToolName() {
        try {
            String type = getToolType();

            ToolTypeInfo info = DataFactory.getToolInfo( type );
            if(info != null) {
                config.setProperty( PROP_TOOL_NAME, info.getName());
            }
        } catch ( Exception ex ) {
            System.err.println(ex);
        }

    }
    public void setToolDataSourceID( String datasourceID ) {
        config.setProperty( PROP_TOOL_DATASOURCE_ID, datasourceID );
        setToolDataSourceClass();
    }

    public void setToolDataSourceClass() {
        String s = config.getProperty( PROP_TOOL_DATASOURCE_ID );
        s = datasources.getProperty( s, "" );
        config.setProperty( PROP_TOOL_DATASOURCE_CLASS, s );
    }
    public void setToolChannel(String channel) {
        try {
            //String type = getToolType();
            config.setProperty( PROP_TOOL_CHANNEL, channel );
        } catch ( Exception ex ) {
            System.err.println(ex);
        }

    }

    public String getToolNumber() {
        return config.getProperty( PROP_TOOL_NUMBER );
    }
    public String getToolChannel() {
        return config.getProperty( PROP_TOOL_CHANNEL );
    }

    public void setToolNumber( String number ) {
        config.setProperty( PROP_TOOL_NUMBER, number );
    }

    public String getToolName() {
        return config.getProperty( PROP_TOOL_NAME );
    }

    public String getRootDir() {
        return workDir;
    }
    public String getProperty(String key) {
        return config.getProperty(key);
    }
    public String getProperty(String key, String def_key) {
        return config.getProperty(key, def_key);
    }
    public void setProperty(String key, String value) {
        config.setProperty(key, value);
    }

    public void setNewWork(boolean isNewWork) {
        config.setProperty(PROP_WORK_NEW, Boolean.toString(isNewWork));
    }
    public void setWorkID(String id) {
        config.setProperty(PROP_WORK_ID, id);
    }

    public void loadProperties( Properties props, String filename ) throws Exception {
        /** @todo load props relative jars
        */
        //URL url = ClassLoader.getSystemClassLoader().getResource("lsdsoft.jar");
        //url = new URL( url, "/zeus/etc/" + filename );
        //InputStream ins = new FileInputStream( url.getFile() );
        String fileName = "/zeus/etc/" + filename;
        //try {
            InputStream ins = new FileInputStream( fileName );
            log.info( "Loading properties '" + fileName + "'" );
            props.load( ins );
            ins.close();
        //} catch ( FileNotFoundException ex ) {
        //    System.out.println("Не найден файл настроек: " + fileName);
        //    ex.printStackTrace();
        //} catch ( IOException ex ) {
        //    System.out.println("Ошибка чтения из файла: " + fileName);
        //}
    }

    public void loadProperties( Properties props, String filename, String charsetName ) throws Exception {
        String fileName = "/zeus/etc/" + filename;
        InputStream ins = new FileInputStream( fileName );
        log.info( "Loading properties '" + fileName + "'" );
        if (props instanceof AProperties) {
            ((AProperties)props).load(ins, charsetName);
        } else {
            props.load( ins );
        }
        ins.close();
    }

    /**
     * Загрузка кофигурационного файла. Обычно это файл zeus.properties.
     * @param url Полное имя файла кофигурации
     */
    public void loadConfig( URL url ) {
        try {
            InputStream ins = new FileInputStream( url.getFile() );
            config.load( ins );
        } catch ( FileNotFoundException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Не найден файл конфигурации: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        } catch ( IOException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Ошибка чтения из файла: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        }

    }
    public void loadViewers( URL url ) {
        try {
            InputStream ins = new FileInputStream( url.getFile() );
            viewers.load( ins );
        } catch ( FileNotFoundException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Не найден файл обозревателей: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        } catch ( IOException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Ошибка чтения из файла: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        }
    }
    public void loadRigs( URL url ) {
        try {
            InputStream ins = new FileInputStream( url.getFile() );
            rigs.load( ins );
        } catch ( FileNotFoundException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Не найден список калибровочного оборудования: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        } catch ( IOException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Ошибка чтения из файла: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        }
    }
    /**
     * Загрузка кофигурационного файла. Обычно это файл zeus.properties.
     * @param url Полное имя файла кофигурации
     */
    public void loadDatasources( URL url ) {
        try {
            InputStream ins = new FileInputStream( url.getFile() );
            datasources.load( ins );
        } catch ( FileNotFoundException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Не найден файл: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        } catch ( IOException ex ) {
            JOptionPane.showMessageDialog( wizard,
                                           "Ошибка чтения из файла: " +
                                           url.getFile(),
                                           "Ошибка инициализации",
                                           JOptionPane.ERROR_MESSAGE );

        }
    }

    public void startViewer() {
        try {
            System.gc();
            //AbstractMethods methods = DataFactory.getMethods( "ion1_test.xml" );
            WorkState workState = new WorkState(getToolType(), getToolNumber());
            workState.toolChannel = getToolChannel();
            workState.setWorkMode(workMode);
            String toolMode = getToolType() + "." + workMode.getName();
            String className = viewers.getProperty( toolMode );
            if(workMode.getWorkMode() == WorkMode.MODE_SETUP ) {
                className = viewers.getProperty( workMode.getName() );
            }
            if(className == null) {
                UiUtils.showSystemError(
                    wizard,
                    "Не наден обозреватель для прибора " +
                    getToolName() + " для режима '" +
                    workMode.getShortDescription() +"'" );
            } else {
                Class cls = Class.forName( className );
                MethodsViewer viewer = ( MethodsViewer ) ( cls.newInstance() );
                viewer.setWorkState( workState );
                viewer.setProperties( config );
                //MeasureDatas datas = DataFactory.createIMMeasureDatas( methods );
                //datas.setWorkState(workState);
                //datas.setProperties( config );
                //datas.generateDataIdentifer();

                //viewer.setMeasureDatas( datas );
                //DataFactory.saveMeasureDatas( datas );
                //WorkIndex index = DataFactory.getWorkIndex();
                viewer.start();
            }
        } catch ( Exception ex ) {
            UiUtils.showSystemError(
                wizard,
                "Ошибка при запуске обозревателя.\n" +
                ex.getClass() + "\n" + ex.getMessage());
            //           System.err.println(ex);
            ex.printStackTrace();
        }
    }

    public void start() {
        initUI();
        wizard = new Wizard();
        wizard.setVisible( true );
        wizard.start();
    }

    public Zeus() {
        if ( instance == null ) {
            instance = this;
        }
        try {
            //log = new SimpleLogger(new SimpleLog(logproperties), Zeus.class);
            log.info("Start Zeus");
            log.setTracing(false);
            //Logger logger = Logger.;
            // loading properties, located at up dir relative lsdsoft.jar archive
            //URL url = ClassLoader.getSystemClassLoader().getResource("lsdsoft.jar");
            //loadConfig( new URL( url, "../" + defaultPropertyName ) );
            //log.info("Config file:" + workDir + zeusPropertiesFile);
            loadProperties( config, zeusPropertiesFile, "CP125" );
            //loadProperties( config, config.getProperty("userconfig"), "Cp1251" );
            //loadViewers( new URL(config.getProperty(PROP_VIEWERS_PROPERTIES)));
            loadProperties( viewers, config.getProperty( PROP_VIEWERS_PROPERTIES ) );
            loadProperties( datasources, config.getProperty( PROP_DATASOURCES_PROPERTIES ) );
            loadProperties( rigs, config.getProperty( PROP_RIGS_PROPERTIES, PROP_RIGS_PROPERTIES ) );
            config.setProperty( PROP_WORK_DIR, workDir );
            workMode = new WorkMode( config.getProperty( PROP_WORK_MODE ) );
            setToolName();
        } catch ( Exception ex ) {
            log.error( ex.getMessage());
            System.err.println( ex.getMessage() );
        }
    }

    private void initUI() {
        try {
            //UIManager.setLookAndFeel(new ExMetalLookAndFeel());

            //MacLookAndFeel laf = new MacLookAndFeel();
            //UIManager.installLookAndFeel( "Macintosh",
            //    "com.sun.java.swing.plaf.mac.MacLookAndFeel" );
            //UIManager.setLookAndFeel( laf );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
    }

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ClassLoader.getSystemResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static void logex( Exception ex ) {
        log.db( DebugLevel.L5_DEBUG , ex.getMessage() );
    }

    public static void main( String[] args ) {
        UIManager.put( "OptionPane.yesButtonText", "Да" );
        UIManager.put( "OptionPane.noButtonText", "Нет" );
        UIManager.put( "OptionPane.cancelButtonText", "Отменить" );
/*
        LasFile las = new LasFile();
        try {
            las.load( new File( "/zeus/1.las" ) );
        } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());
        }
        Tracker tracker = new Tracker(las);
        tracker.selectTrackForSearch(0);
        int row = tracker.findRow(555);
        System.out.println(tracker.getRow(row)[0]);
*/
        Zeus zeus = Zeus.getInstance();
        zeus.start();
    }




}
