package lsdsoft.zeus;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

import java.util.*;

import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.welltools.*;


//import org.hsqldb.*;

public class DataFactory {
    private static DataStorage dataStorage = new DataStorage();
    private static Zeus zeus = Zeus.getInstance();
    private static Vector checkUnits = new Vector( 16 );
    //private ToolInfo[] toolInfos = null;
    public DataFactory() {
    }

    public static void addToolNumber( String toolType, String number ) {
        try {
            // add number to list, save, make dir
            dataStorage.addToolNumber(toolType,number);
            // make default grads for all channels
            Graduation[] graduations = createDefaultGraduations(toolType, number);
            dataStorage.saveGraduations(graduations);
        } catch ( Exception ex ) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * createDefaultGraduations
     *
     * @param toolType String
     * @param number String
     */
    public static Graduation[] createDefaultGraduations( String toolType, String number ) throws
        Exception {
        Graduation[] graduations = null;
        //ToolInfoList toolInfoList = dataStorage.getToolInfoList();
        ToolTypeInfo toolInfo = dataStorage.findToolInfo(toolType);
        if(toolInfo != null) {
            ToolChannels channels = toolInfo.getChannels();
            int size = channels.size();
            graduations = new Graduation[size];
            for(int i = 0; i < size; i++) {
                ToolChannel channel = (ToolChannel) channels.get(i);
                String cname = channel.getID();
                Graduation grad = createDefaultGraduation(toolType, number, cname);
                graduations[i] = grad;
            }
        }
        return graduations;
    }

    /**
     * createDefaultGraduation
     *
     * @param toolType String
     * @param number String
     * @param cname String
     * @return Graduation
     */
    public static Graduation createDefaultGraduation( String toolType, String number,
                                                String cname ) {
        BaseGraduation grad = new BaseGraduation();
        grad.setParameter("type", toolType);
        grad.setParameter("number", number);
        /** @todo: set default graduation from templates */
        if( "gk".equals(cname) ) {
            grad.setParameter("channel", cname);
            grad.setParameter("unit", "uR");
            grad.setParameter("func", "gk/K");
            grad.setParameter("deltafunc", "deltacoef*func");
            grad.setParameter("deltacoef", "0.15");
            grad.setParameter("K", "100");

        } else
        if( "nnk".equals(cname) ) {
            grad.setParameter("channel", cname);
            grad.setParameter("unit", "uR");
            grad.setParameter("func", "gk/K");
            grad.setParameter("deltafunc", "deltacoef*func");
            grad.setParameter("deltacoef", "0.15");
            grad.setParameter("K", "100");
        } else
        if( "temp".equals(cname) ) {
        }
        return grad;
    }

    public static DataStorage getDataStorage() {
        return dataStorage;
    }

    /**
     * ������� ������ ��������, ������������������ � ���������
     * @return ������ �����, ���������� �������������� �������� (companyID)
     */
    public static Vector getCompanies() {
        Vector companies = new Vector( 5, 5 );
        // TODO: read from
        companies.add( "ural" );
        return companies;
    }

    /**
     * ������� ������ ����� ��������, ��������� (����������) � �����������
     * ����������� ������ � ��������� ��� ������ ������ ��������������� �����������
     * @param orgID ������������� �����������, ��� ������� �������� ������, ���� ��� ����
     * �����������, �� ����� ������� "*" ��� ������ ������
     * @return ������ �����, ���������� ID ����� �������� (toolID)
     */
    public static Vector getStoredToolID( String orgID ) throws Exception {
        Vector tools = new Vector( 20 );
        dataStorage.getToolInfo();
        return tools;
    }

    public static ToolTypeInfo getToolInfo(String toolType) throws Exception {
        Object[] infos = getToolsInfo();
        for(int i = 0; i < infos.length; i++) {
            ToolTypeInfo info = (ToolTypeInfo) infos[i];
            if(info.getID().equals(toolType)) {
                return info;
            }
        }
        return null;
    }
    public static ToolTypeInfo[] getToolsInfo() throws Exception {
        return dataStorage.getToolInfo();
    }

    public static String[] getToolNumbers() throws Exception {
        return dataStorage.getToolNumbers();
    }

    public static String[] getToolNumbers(String toolType) throws Exception {
        return dataStorage.getToolNumbers(toolType);
    }

    public static AbstractMethods getMethods( String fileName ) throws
        Exception {
        return dataStorage.loadMethods( fileName );
    }

    /**
     * �������� �� ���� ����� ��������, �� ��� ������� �������� �������������
     * ��������� ����������� (����������, ����������� � ��).
     * @param capability ��������������� �������� �����������, ��� ������� �����������
     * ��� ������ ������ ������ � �������� ("calib", "grad", "setup" ...)
     * @param company ������������� ��������, ��� ������� ����� ������������ �����,
     * ���� �� ������, �� ����� ����� ���������� �� ���� ��������� ������� ����� ��������
     * @return ������ ����� toolID (��������������� ��������)
     */
    public static Vector selectToolsWithCapabilities( String capability,
        String company ) {
        Vector tools = new Vector( 20 );
        return tools;
    }

    /**
     * ������� ������ ��������� ��� ������������� �� ��������� ����������� ��������
     * @param methods ��������
     * @return ��������� ������ ������
     */
    public static MeasureDatas createMeasureDatas( AbstractMethods methods ) {
        MeasureDatas datas = new MeasureDatas();
        Vector useTables = methods.getUseTables();
        Vector pointsList = methods.getPointsList();
        int size = useTables.size();
        for ( int i = 0; i < size; i++ ) {
            String name = ( String ) useTables.get( i );
            MethodsPoints mpoints = methods.getPoints( name );
            int crossIndex = 0;
            if ( mpoints != null ) {
                Vector points = mpoints.getPoints();
                int psize = points.size();
                // checking cross table
                String crossName = mpoints.getCross();
                String tableType = mpoints.getType();
                String tableName = mpoints.getName();
                MethodsPoints crossTable = null;
                String crossType = null;
                Vector crossPoints = null;
                int crossSize = 0;

                MethodsPoints crossTable2 = null;
                String crossName2 = null;
                String crossType2 = null;
                Vector crossPoints2 = null;
                int crossSize2 = 0;

                if(crossName != null ) {
                    crossTable = methods.getPoints(crossName);
                    if(crossTable != null) {
                        crossPoints = crossTable.getPoints();
                        crossType = crossTable.getType();
                        crossSize = crossPoints.size();

                        crossName2 = crossTable.getCross();
                        // ���� ����� � ������
                        if(crossName2 != null ) {
                            crossTable2 = methods.getPoints(crossName2);
                            if(crossTable2 != null) {
                                crossPoints2 = crossTable2.getPoints();
                                crossType2 = crossTable2.getType();
                                crossSize2 = crossPoints2.size();
                            }
                        }
                    }

                }
                // ���� ������������ ������������ �������, ��
                // ��������� �������� ������ �������, �������
                // ����� � ������������ �������. � �������� �����������
                // �������� type=value
                // ��������, zenith=30..90
                int crossIndex2 = 0;
                do {
                    crossIndex = 0;

                    do {
                        MeasureTable table = new MeasureTable( psize, 4 );
                        table.setProperty( "name", tableName );
                        table.setProperty( "type", tableType );
                        Properties attrs = mpoints.getAttributes();
                        Enumeration keys = attrs.keys();
                        for ( int j = 0; j < attrs.size(); j++ ) {
                            String key = ( String )keys.nextElement();
                            table.setProperty( key, attrs.getProperty( key ) );
                        }
                        for ( int j = 0; j < psize; j++ ) {
                            double value;
                            value = Double.parseDouble( ( String )points.get( j ) );
                            table.getChain( j ).setReproductionValue( value );
                        }
                        datas.addTable( table );
                        //boolean done = true;
                        if ( crossPoints != null ) {
                            //done = false;
                            table.setProperty( crossType,
                                               ( String )crossPoints.
                                               get( crossIndex ) );
                            crossIndex++;
                        }
                        if ( crossPoints2 != null ) {
                            table.setProperty( crossType2,
                                               ( String )crossPoints2.
                                               get( crossIndex2 ) );
                        }

                        //if(crossIndex >= crossSize)
                        //    break;
                    }
                    while ( crossIndex < crossSize );
                    crossIndex2++;
                }while (crossIndex2 < crossSize2);
            }
        }
        return datas;
    }

    /**
     *
     * @param type String
     * @param mode String
     * @return MeasureDatas
     * @throws Exception
     */
    public static MeasureDatas createMeasureDatas( String type, String mode ) throws
        Exception {
        return createMeasureDatas( getMethods( type + "." + mode + ".xml" ) );
    }


    public static MeasureDatas
        loadMeasureDatas( String toolType,
                          String toolNumber,
                          String id ) throws Exception {
        return dataStorage.loadMeasureDatas(toolType, toolNumber, id);

    }
    /**
     * ������� ����������� ��� ������. ��������� ��������� �� ����� �������.
     * ��� ����� ����������� �� ������������� ����� � �������� ".alias"
     * @param name ��� ����������� (�� ����)
     * @return ���������
     * @throws Exception ���������� �������� � ������ �� �����
     */
    public static ChannelAlias createChannelAlias( String name ) throws AliasException, Exception {
        Properties props = new Properties();
        ChannelAlias alias = new ChannelAlias();
        try {
            zeus.loadProperties( props, name + ".alias" );
            alias.buildFromProperties( props );
        } catch ( Exception ex ) {
        }
        return alias;
    }
    /**
     * ������� ��������� ������������� ��������� � ����������� � ������ ������
     * ���������� � �������� Zeus "checkunit.class".
     * @return null ���� ��������� ������, ����� CheckUnit ��������
     */
    public static AbstractCheckUnit createCheckUnit() {

        String className = zeus.getConfig().getProperty( Zeus.
            PROP_CHECKUNIT_CLASS );
        return createCheckUnit( className );
    }

    public static AbstractCheckUnit createCalibrationRigForChannel( String
        channel ) throws Exception {

        String className = null;
        Zeus.log.info( "Creating calibration rig for channel'" + channel + "'" );
        ToolItemInfo ti = zeus.getCalibrationRigForChannel( channel );
        if ( ti == null ) {
            throw new Exception( "No defined rig for channel '" + channel + "'" );
        }
        Properties props = new Properties();
        zeus.loadProperties( props,
                             ti.type + "_" + ti.number + ".properties" );
        className = props.getProperty( Zeus.PROP_CHECKUNIT_CLASS );
        AbstractCheckUnit unit = createCheckUnit( className );
        unit.setNumber(ti.number);
        return unit;
    }

    /** @todo Comment this method */
    public static AbstractCheckUnit createCheckUnit( String className ) {
        AbstractCheckUnit unit = null;
        Zeus.log.info("Attempting create calibration rig '" + className + "'");
        try {
            // for one type of unit one instance of class
            int unitsCount = checkUnits.size();
            for ( int i = 0; i < unitsCount; i++ ) {
                Object obj = checkUnits.get( i );
                if ( className.equals( obj.getClass().getName() ) ) {
                    return ( AbstractCheckUnit ) obj;
                }
            }
            Class cls = Class.forName( className );
            unit = ( AbstractCheckUnit ) ( cls.newInstance() );
            Zeus.log.info("Creating calibration rig '" + className + "'");
            checkUnits.add( unit );
        } catch ( Exception ex ) {
            Zeus.logex( ex );
            ex.printStackTrace();
        }
        return unit;

    }

    public static ChannelDataSource createToolDataSource(String toolType) throws Exception {
        Zeus zeus = Zeus.getInstance();
        ToolTypeInfo info = getToolInfo(toolType);
        if(info != null) {
            String dataSourceID = info.getSourceName();
            zeus.setToolDataSourceID(dataSourceID);
        }
        return createToolDataSource();
    }

    public static ChannelDataSource createToolDataSource() throws Exception {
        ChannelDataSource source = null;
        //try {
        String className = zeus.getProperty( Zeus.
            PROP_TOOL_DATASOURCE_CLASS );
        Zeus.log.info("Creating tool source '" + className + "'");
        Class cls = Class.forName( className );
        try {
            source = ( ChannelDataSource ) ( cls.newInstance() );
        //} catch ( IllegalAccessException ex ) {
        //    System.err.println(ex);
        //} catch ( InstantiationException ex ) {
        //    System.err.println(ex);
        } catch( Exception ex) {
            System.err.println(ex);
            throw ex;
        }
        source.setProperty( Zeus.PROP_TOOL_TYPE,
                            zeus.getProperty( Zeus.PROP_TOOL_TYPE));
        source.setProperty( Zeus.PROP_TOOL_NUMBER,
                            zeus.getProperty( Zeus.PROP_TOOL_NUMBER));
        source.setProperty( Zeus.PROP_TOOL_NAME,
                            zeus.getProperty( Zeus.PROP_TOOL_NAME));

        //}catch (Exception ex) {
        //  System.err.println(ex);
        //}
        return source;
    }

    public static void saveMeasureDatas( MeasureDatas datas ) {
        try {
            dataStorage.saveMeasureDatas( datas );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

    }
/*
    public static void addMeasureDataToIndex( MeasureDatas datas ) {
        try {
            //WorkIndex index = getWorkIndex();
            //WorkIndexItem item = index.get( datas.getIdentifer() );
            //if ( item == null ) {
            //    item = new WorkIndexItem();
                //item.
            //}
            dataStorage.saveMeasureDatas( datas );
            //datas.
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

    }
*/
    public static WorkIndex getWorkIndex() {
        WorkIndex index = null;
        try {
            index = dataStorage.getWorkIndex( zeus.getToolType(),
                                              zeus.getToolNumber() );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }
        return index;
    }
}
