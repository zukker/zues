package lsdsoft.zeus;


import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.*;

import javax.swing.*;
//import com.sun.java.swing.plaf.mac.*;
//import lsdsoft.zeus.ui.*;
import lsdsoft.util.*;
import org.grlea.log.*;
import lsdsoft.welltools.*;
import lsdsoft.welltools.im.ion1.TprvFile;
import lsdsoft.metrolog.ToolItemInfo;
//import com.amper.gui.ExMetalLookAndFeel;
import com.amper.io.AProperties;
//import edu.emory.mathcs.jtransforms.dst.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class Zeus {
    public static SimpleLogger log;
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

    private static final String zeusName = "lsdsoft/zeus/Zeus.class";
    private static String workDir;
    private static URL wdUrl = null;
    private static String classFile;  // url corresponded to Zeus loaded class
    private static String startPath; // url of start dir of zeus relative loaded class
    
    
    private static String zeusPropertiesFile = "zeus.properties";
    private JFrame wizard;
    private Class cWizard = Wizard.class;
    private WorkMode workMode = new WorkMode( WorkMode.MODE_CALIB );
    
    {
    	classFile = makeClassFile();
    	startPath = makeStartPath();
    	if(workDir == null) {
    		workDir = startPath;
    	}
    	workDir = normalizePathName(workDir);
    	info("Start path: " + startPath);
    	info("Work dir: " + workDir);
    	addLibraryPathURL(workDir + "lib");
    	loadJARs();
    	
    }
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

    public String getWorkDir() {
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
    	loadProperties(props, filename, "CP1251");
    }

	public void loadProperties(Properties props, String filename,
			String charsetName) throws Exception {
		String fileName = workDir + "etc" +File.separatorChar + filename;
		File file = new File(fileName);
		if (file.exists()) {
			FileInputStream ins = new FileInputStream(file);
			if (log != null) {
				log.info("Loading properties '" + file.getCanonicalPath() + "'");
			} else {
			}
			info("loading properties: "
					+ file.getCanonicalPath());
			if (props instanceof AProperties) {
				((AProperties) props).load(ins, charsetName);
			} else {
				props.load(ins);
			}
			ins.close();
		} else {
			error("file doesn't exist: "
					+ file.getCanonicalPath());
		}
	}

    /*
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
*/
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
    public void startViewer(String toolType, String toolNumber, String mode) {
        try {
            String className;
            if(mode.equals("setup") ) {
                className = viewers.getProperty( mode );
            }else {
            	String workMode = toolType + "." + mode;
                className = viewers.getProperty( workMode );
            }
            if(className == null) {
                UiUtils.showSystemError(
                    wizard,
                    "Не наден обозреватель для прибора " +
                    toolType + " для режима '" +
                    workMode.getShortDescription() +"'" );
            } else {
                Class cls = Class.forName( className );
                MethodsViewer viewer = ( MethodsViewer ) ( cls.newInstance() );
                WorkState workState = new WorkState(toolType, toolNumber);
                viewer.setWorkState( workState );
                viewer.setProperties( config );
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
        startWizard();
        
    }

    @SuppressWarnings("rawtypes")
    public void startWizard() {
        String className = config.getProperty("wizard",Wizard.class.getName());
        Class cls = cWizard;
        try {
            cls = Class.forName( className );
        } catch (ClassNotFoundException e1) {
            // error getting class for name. leave default wizard class
            info("Не найден указанный мастер, выбран по умолчанию...");
        }
        try {
            // wizard must be instance of JFrame
            wizard = (JFrame)cls.newInstance();
            Method method = cls.getMethod( "start", null );
            method.invoke( wizard, (Object[])null );
            wizard.setVisible(true);
            // TODO text antialiasing!???
            Graphics2D gc = (Graphics2D)wizard.getContentPane().getGraphics();
            gc.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

        } catch (Exception e) {
            error(e.getLocalizedMessage());
        }
    }

    private Zeus() {
        if ( instance == null ) {
            instance = this;
        }
        try {
        	wdUrl = new URL("file", "", workDir);
        	loadProperties( logproperties, "simplelog.properties" );
        	log = new SimpleLogger( new SimpleLog(logproperties) , Zeus.class);
            log.info("Start Zeus");
            log.setTracing(false);
            
            loadProperties( config, zeusPropertiesFile, "CP125" );
            loadProperties( viewers, config.getProperty( PROP_VIEWERS_PROPERTIES ) );
            loadProperties( datasources, config.getProperty( PROP_DATASOURCES_PROPERTIES ) );
            loadProperties( rigs, config.getProperty( PROP_RIGS_PROPERTIES, PROP_RIGS_PROPERTIES ) );
            config.setProperty( PROP_WORK_DIR, workDir );
            workMode = new WorkMode( config.getProperty( PROP_WORK_MODE ) );
            setToolName();
        } catch ( Exception ex ) {
            log.error( ex.getMessage());
            error( ex.getMessage() );
        }
    }
    private void loadJARs() {
    	File file = new File(workDir + "lib");
    	try {
			addClassPathURL(file.toURI().toURL());
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	File[] files = file.listFiles();
    	int count = 0;
    	for(int i=0; i < files.length; i++) {
    		//ClassLoader.getSystemClassLoader(). files[i]
    		String fileName = files[i].getAbsolutePath();
    		if(fileName.endsWith(".jar")){
    			//System.out.println(fileName);
    			try {
					addClassPathURL(files[i].toURI().toURL());
					count++;
				} catch (Throwable e) {
	    			error(e.getMessage());
				}
    		}
    	}
		info("loaded JARs[" + count + "] from " + file.getPath() );
    }
    
    private static void addClassPathURL(URL path) throws Throwable {
        // URL файла для добавления к classpath
        // достаем системный загрузчик классов
        URLClassLoader urlClassLoader =
            (URLClassLoader) ClassLoader.getSystemClassLoader();
        // используя механизм отражения,
        // достаем метод для добавления URL к classpath
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod(
                "addURL",
                new Class[]{ URL.class });
        // делаем метод доступным для вызова
        method.setAccessible(true);
        // вызываем метод системного загрузчика,
        // передавая в качестве параметра
        // URL файла для добавления к classpath
        method.invoke(urlClassLoader, new Object[]{ path });
    }
    /**
     * 
     * @param path
     */
    private static void addLibraryPathURL(String path) {
    	Class urlClass = ClassLoader.class;
        
        try {
            // достаем поле sys_paths из ClassLoader
			Field fss = urlClass.getDeclaredField("sys_paths");
			// делаем его доступным
			fss.setAccessible(true);
			// обнуляем значение. теперь при следущем вылове loadLibrary
			// загрузчик обновит sys_paths из java.library.path
			fss.set(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		// извлекаем значение путей 
        String lib = System.getProperty("java.library.path","");
    	//System.out.println(lib);
        // добавляем свой путь в начало списка
    	lib = path + File.pathSeparator + lib;
		// устанавливаем новое значение
    	System.setProperty("java.library.path", lib);
    	//System.out.println(lib);
    	//System.loadLibrary("rxtxSerial");
    }
    	   
    private void initUI() {
        try {
            LookAndFeel laf;
        	String className = config.getProperty("laf", "");
        	try {
				Object cls = Class.forName(className).newInstance();
				if(cls instanceof LookAndFeel) {
					laf = (LookAndFeel)cls;
					UIManager.setLookAndFeel( laf );
				}
			} catch (Exception e) {
				error("can't set laf : " + className);
			}
			UIManager.put( "OptionPane.yesButtonText", "Да" );
			UIManager.put( "OptionPane.noButtonText", "Нет" );
			UIManager.put( "OptionPane.cancelButtonText", "Отменить" );
			info("laf: " + UIManager.getLookAndFeel().getName());
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
    }

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ClassLoader.getSystemResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            error("Couldn't find file: " + path);
            return null;
        }
    }
    public static String getClassFile() {
        return classFile;
    }

    private static String makeClassFile() {
    	URL url = ClassLoader.getSystemClassLoader().getResource(
				zeusName);
		String file = null;
        if (url != null) {
        	file = url.toString();
        	if(file.startsWith("jar:file:")){
        		file = file.substring(9);
        	} else {
        		file = file.substring(5);
        	}
        }
        return normalizeFileName(file);
    }
    
    public static String getStartPath() {
    	return startPath;
    }
    public static String normalizeFileName(String fileName) {
    	File file = new File(fileName);
    	String name = fileName;
    	try {
			name = file.getCanonicalPath();
		} catch (IOException e) {
		}
    	return name;
    }

    public static String normalizePathName(String pathName) {
    	return normalizeFileName(pathName) + File.separatorChar;
    }
    
    private static String makeStartPath() {
    	String path = classFile;
    	char separator = File.separatorChar;
    	path = path.substring(0, path.length()-zeusName.length()-1);
    	// if class in jar file then remove name of jar
    	if(path.endsWith("!")) {
        	path = path.substring(0, path.lastIndexOf(separator));
    	}
    	int index = path.lastIndexOf(separator);
    	if(index > 0) {
    		path = path.substring(0, index);
    	}
    	path = normalizePathName(path);
    	return path;
    }
    
    public static void info(String message) {
        System.out.println("#INFO: " + message);
    }

    public static void error(String message) {
        System.err.println("#ERROR: " + message);
    }
    
    public static void logex( Exception ex ) {
        log.db( DebugLevel.L5_DEBUG , ex.getMessage() );
    }
	private static void parseArguments(String[] args){
		for(int i=0; i < args.length; i++){
			String arg = args[i];
			String val = null;
			if(arg.startsWith("-wd=")){
				val = arg.substring(4);
				info("setting work dir: " + val);
				workDir = val;
				
			} else
			if(arg.startsWith("-help")){
				System.out.println("command line options:\r\n" +
						"  -wd=<work_dir>  Setting work directory\r\n" +
						"  -help  print this help\r\n" +
						"  -wt=<tool>  settnig tool ID for work within\r\n"+
						"  -mode=<mode>  setting work mode: calib, grad, tune\r\n"+
						"  -number=<num>  setting tool number"
				);
				
				
			}
					
			
		}
	}
	/**
	 * 
	 * @param args
	 * -wd=<workdir> установка рабочего каталога, например d:\zeus\
	 */
    public static void main( String[] args ) {
    	parseArguments(args);
    	
    	//TprvFile tp = new TprvFile();
    	//tp.load("E:/Projects/toolsoft/inclinometers/ИОН Тарировка AVS/Предварительные/021103.tprv");
    	//Properties prop = System.getProperties();
    	//prop.save(System.out, "");
        Zeus zeus = Zeus.getInstance();
        zeus.start();
    }

    


}
