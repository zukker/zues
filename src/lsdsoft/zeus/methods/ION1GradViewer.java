package lsdsoft.zeus.methods;


import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.lsdsoft.math.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.util.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.report.*;
import lsdsoft.zeus.ui.*;


/**
 * <p>Title: Градуировка приборов ИОН-1</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 * @todo: split methods viewer on MethodsPerformer and Viewer
 */

public class ION1GradViewer
    extends AbstractMethodsViewer
    implements SignalEventListener {
    class Command {
        protected Class[] params = {};

        String description;
        String name;
        String method;
        AbstractMethodsViewer viewer;
        public Command( AbstractMethodsViewer viewer, String method, String name,
                        String desc ) {
            this.viewer = viewer;
            this.method = method;
            this.name = name;
            this.description = desc;
        }

        void execute() throws Exception {
            Method[] meths = viewer.getClass().getMethods();
            Method method = viewer.getClass().getMethod( this.method, params );
            if ( method != null ) {
                method.invoke( viewer, null );
            }

        }

        public String toString() {
            return name;
        }
    }

    Command commandMeasure = new Command( this, "doMeasure", "замер в точке", "" );

    Command[] commands = {
        new Command( this, "doCalibrationFull", "градуировка полностью", "" ),
        new Command( this, "doFindAlgo", "поиск алгоритмических углов", "" ),
        new Command( this, "doAzHxy", "градуировка датчиков Az и Hxy", "" ),
        new Command( this, "doAzHxyF", "градуировка датчиков Az и Hxy прямой ход", "" ),
        new Command( this, "doAzHxyR", "градуировка датчиков Az и Hxy обратный", "" ),
        new Command( this, "doAxHyz", "градуировка датчиков Ax и Hyz", "" ),
        new Command( this, "doAyHzx", "градуировка датчиков Ay и Hzx", "" ),
        //new Command( this, "doCalibrationAzimuthPart",
        //             "калибровка азимутального угла частично", "" ),
        //new Command( this, "doJoinRotates", "совмещение углов поворота", "" ),
    };
    ///////////////////////////////////////////////////////////////////////////
    class ChangeAction extends AbstractAction {
        public ChangeAction() {
            putValue(Action.NAME, "Изменить...");
        }
        public void actionPerformed( ActionEvent e ) {
            doChange();
        }
    }
    Action changeAction = new ChangeAction();
    class MeasureAction extends AbstractAction {
        public MeasureAction() {
            putValue(Action.NAME, "Замер");
        }
        public void actionPerformed( ActionEvent e ) {
            execCommand(commandMeasure);
        }
    }
    Action measureAction = new MeasureAction();
    ION1GradViewer viewer = this;
    MouseAdapter madapter = new MouseAdapter() {
        public void mouseClicked( MouseEvent e ) {
            JTable table = ( JTable ) e.getSource();

            if ( e.getButton() == MouseEvent.BUTTON3 ) {
                int row = table.rowAtPoint( e.getPoint() );
                table.setRowSelectionInterval( row, row );
                tablePopup.show( e.getComponent(), e.getX(), e.getY() );
            }
            /*
                         if( e.getButton() != MouseEvent.BUTTON2 ) {
                tablePopup.show(e.getComponent(), e.getX(), e.getY());
                return;
                         }
                         if ( e.getButton() != MouseEvent.BUTTON3 ) {
                return;
                         }
                         int row = table.getSelectedRow();
                 selectedContainer = tables[tabbedTables.getSelectedIndex()];
                         if ( row >= 0 ) {
                MeasureChainEditor.edit( selectedContainer.measureTable.
                                         getChain( row ), viewer );
                selectedContainer.measureTable.calc();
                table.repaint();
                         }
             */
        }
    };

    int selectedCommand = 1;
    Date startTime;
    Timer timeUpdater = new Timer( 1000, new ActionListener() {
        public void actionPerformed( ActionEvent evt ) {
            updateTime();
        }
    } );
    // выполнять ли обратный проход градуировки датчиков
    protected boolean doReverse = true;
    DecimalFormat angleFormat = new DecimalFormat();
    MeasureDatas datas = null;
    ION1GraduationData graduation = new ION1GraduationData();
    static int FILTER_SIZE = 9;
    MedianFilter mfilter1 = new MedianFilter( FILTER_SIZE );
    MedianFilter mfilter2 = new MedianFilter( FILTER_SIZE );
    AbstractCheckUnit unit = null;
    ChannelDataSource toolSource = null;
    InclinometerAngles toolAngles = new InclinometerAngles();
    InclinometerAngles accAngles = new InclinometerAngles();
    Graph graphAz = new Graph();
    Graph graphAzrev = new Graph();
    Graph graphAx = new Graph();
    Graph graphAxrev = new Graph();
    Graph graphAy = new Graph();
    Graph graphAyrev = new Graph();
    Graph graphHxy = new Graph();
    Graph graphHxyrev = new Graph();
    Graph graphHyz = new Graph();
    Graph graphHyzrev = new Graph();
    Graph graphHzx = new Graph();
    Graph graphHzxrev = new Graph();
    GraphCanvas grAz = new GraphCanvas();
    GraphCanvas grAx = new GraphCanvas();
    GraphCanvas grAy = new GraphCanvas();
    GraphCanvas grHxy = new GraphCanvas();
    GraphCanvas grHyz = new GraphCanvas();
    GraphCanvas grHzx = new GraphCanvas();
    JTabbedPane tabbedTables = new JTabbedPane();
    JPanel panelZ = new JPanel();
    JPanel panelY = new JPanel();
    JPanel panelX = new JPanel();
    //JScrollPane scrollPaneX = new JScrollPane();
    //JScrollPane scrollPaneY = new JScrollPane();
    //JScrollPane scrollPaneZ = new JScrollPane();
    // цифровые индикаторы
    DigitalDisplay displayAX = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayAY = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayAZ = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayTX = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayTY = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayTZ = new DigitalDisplay( 6, 2 );
    ImageIcon iconOff = Zeus.createImageIcon( "images/conn_off.png" );
    ImageIcon iconOn = Zeus.createImageIcon( "images/conn_on.png" );

    //JTable tableX;
    //JTable tableY;
    //JTable tableZ;
    JComboBox cbZenRotates = new JComboBox();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JComboBox cbAzZeniths = new JComboBox();
    TableContainer tables[] = {
        new TableContainer(), new TableContainer(), new TableContainer()};
    CommandExecuter executer;
    JToolBar jToolBar1 = new JToolBar();
    JButton bSave = new JButton();
    JPanel panelDisplay = new JPanel();
    JPanel panelHeader = new JPanel();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JLabel jLabel8 = new JLabel();
    JPanel panelAcc = new JPanel();
    JLabel jLabel3 = new JLabel();
    JImage imgDigAX = new JImage( displayAX.getImage() );
    JImage imgDigAY = new JImage( displayAY.getImage() );
    JImage imgDigAZ = new JImage( displayAZ.getImage() );
    JPanel panelTool = new JPanel();
    JImage imgDigTX = new JImage( displayTX.getImage() );
    JLabel jLabel4 = new JLabel();
    JImage imgDigTY = new JImage( displayTY.getImage() );
    JImage imgDigTZ = new JImage( displayTZ.getImage() );
    //VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    //BoxLayout2 boxLayout21 = new BoxLayout2();
    //BoxLayout2 boxLayout22 = new BoxLayout2();
    TitledBorder titledBorder1;
    Border border1;
    TitledBorder titledBorder2;
    JButton bConnect = new JButton();
    Border border2;
    TitledBorder titledBorder3;
    ButtonGroup bgWhat = new ButtonGroup();
    TitledBorder titledBorder4;
    JLabel jLabel9 = new JLabel();
    JComboBox cbDo = new JComboBox();
    JButton bStart = new JButton();
    JPanel jPanel1 = new JPanel();
    JLabel jLabel10 = new JLabel();
    JLabel jLabel11 = new JLabel();
    JLabel lTimeStart = new JLabel();
    JLabel lTimeElapsed = new JLabel();
    JLabel lMessage1 = new JLabel();
    JLabel lMessage2 = new JLabel();
    JLabel lMessage3 = new JLabel();
    JButton bStop = new JButton();
    TableContainer selectedContainer;
    JPopupMenu tablePopup = new JPopupMenu();
    JMenuItem miChange = new JMenuItem();
    JMenuItem miMeasure = new JMenuItem();
    JButton bProtocol = new JButton();
    JLabel jLabel12 = new JLabel();
    JLabel jLabel13 = new JLabel();
    JLabel jLabel14 = new JLabel();
    JLabel jLabel15 = new JLabel();
    JLabel jLabel16 = new JLabel();
    JLabel jLabel17 = new JLabel();
    JLabel jLabel18 = new JLabel();
    JLabel jLabel19 = new JLabel();
    JLabel jLabel110 = new JLabel();
    JLabel jLabel111 = new JLabel();
    JLabel jLabel112 = new JLabel();
    JLabel jLabel113 = new JLabel();
    JLabel jLabel114 = new JLabel();
    JLabel jLabel115 = new JLabel();
    JLabel jLabel116 = new JLabel();
    JLabel jLabel117 = new JLabel();
    JLabel jLabel118 = new JLabel();
    JLabel jLabel119 = new JLabel();
    JLabel jLabel1110 = new JLabel();
    JLabel jLabel120 = new JLabel();
    JLabel jLabel1111 = new JLabel();
    JLabel jLabel121 = new JLabel();
    JLabel jLabel122 = new JLabel();
    JLabel jLabel123 = new JLabel();
    JLabel lAx1 = new JLabel();
    JLabel lAy1 = new JLabel();
    JLabel lAz1 = new JLabel();
    JLabel lAx2 = new JLabel();
    JLabel lAy2 = new JLabel();
    JLabel lAz2 = new JLabel();
    JLabel lAx = new JLabel();
    JLabel lAy = new JLabel();
    JLabel lAz = new JLabel();
    JLabel lAxn = new JLabel();
    JLabel lAyn = new JLabel();
    JLabel lAzn = new JLabel();
    JLabel lHxy1 = new JLabel();
    JLabel lHyz1 = new JLabel();
    JLabel lHzx1 = new JLabel();
    JLabel lHxy2 = new JLabel();
    JLabel lHyz2 = new JLabel();
    JLabel lHzx2 = new JLabel();
    JLabel lHxy = new JLabel();
    JLabel lHyz = new JLabel();
    JLabel lHzx = new JLabel();
    JLabel lHxyn = new JLabel();
    JLabel lHyzn = new JLabel();
    JLabel lHzxn = new JLabel();

    class TableContainer {
        JTable table;
        MeasureTable measureTable;
        JScrollPane scrollPane = new JScrollPane();
        public void init() {
            table = new JTable( new MeasureTableModel( measureTable ) );
            Font font = new Font( "Dialog", 0, 14 );
            // init tableX
            table.setFont( font );
            table.setBorder( BorderFactory.createLineBorder( Color.black ) );
            table.setMinimumSize( new Dimension( 300, 200 ) );
            table.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
            table.setRowHeight( 16 );
            table.addMouseListener( madapter );
            //tables[i].table.setCellSelectionEnabled(false);
            table.setSelectionMode( ListSelectionModel.
                                    SINGLE_SELECTION );
            table.add( tablePopup );
            scrollPane.setAutoscrolls( true );
            scrollPane.getViewport().add( table, null );

        }
    }


    public ION1GradViewer() {
        // график поправки Az прямой ход
        graphAz.function = graduation.tableAz;
        graphAz.lineColor = new Color(255, 0, 0);
        graphAz.lineWidth = 1;
        // график поправки Az обратный ход
        graphAzrev.function = graduation.tableAzrev;
        graphAzrev.lineColor = new Color(100, 60, 60);
        graphAzrev.lineWidth = 1;
        // график поправки Hxy прямой ход
        graphHxy.function = graduation.tableHxy;
        graphHxy.lineColor = new Color(0, 160, 160);
        graphHxy.lineWidth = 1;
        // график поправки Hxy обратный ход
        graphHxyrev.function = graduation.tableHxyrev;
        graphHxyrev.lineColor = new Color(0, 200, 200);
        graphHxyrev.lineWidth = 1;
        // Az
        grAz.addGraph(graphAz);
        grAz.addGraph(graphAzrev);
        grAz.clipSize.x = 360.0f;
        grAz.clipSize.y = 2.0f;
        grAz.clipZero.y = -1f;
        // Hxy
        grHxy.addGraph(graphHxy);
        grHxy.addGraph(graphHxyrev);
        grHxy.clipSize.x = 360.0f;
        grHxy.clipSize.y = 2.0f;
        grHxy.clipZero.y = -1f;

        try {
            //jbInit();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void setProperties( Properties props ) {
        properties = ( Properties ) props.clone();
    }

    public void setMeasureDatas( MeasureDatas datas ) {
        this.datas = datas;
        datas.calc();
    }

    protected void selectTables() {
        Vector tables = datas.selectTables( "type", "azimuth" );
        if ( tables.size() > 0 ) {
            this.tables[2].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        tables = datas.selectTables( "type", "zenith" );
        if ( tables.size() > 0 ) {
            this.tables[1].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        tables = datas.selectTables( "type", "rotate" );
        if ( tables.size() > 0 ) {
            this.tables[0].measureTable = ( MeasureTable ) tables.get( 0 );
        }
    }

    protected void initZenithTab() {
        Vector tables = datas.selectTables( "type", "zenith" );
        int size = tables.size();
        cbZenRotates.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "rotate" );
            if ( value != null ) {
                cbZenRotates.addItem( value );
            }
        }

    }

    protected void initAzimuthTab() {
        Vector tables = datas.selectTables( "type", "azimuth" );
        int size = tables.size();
        cbAzZeniths.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "zenith" );
            if ( value != null ) {
                cbAzZeniths.addItem( value );
            }
        }
    }

    protected void buildTables() {
        for ( int i = 0; i < 3; i++ ) {
            tables[i].init();
            tables[i].table.addMouseListener( madapter );
            //tables[i].table.setCellSelectionEnabled(false);
            tables[i].table.setSelectionMode( ListSelectionModel.
                                              SINGLE_SELECTION );
            tables[i].table.add( tablePopup );
        }

    }

    protected void changeAzimuthTable() {
        String value = ( String ) cbAzZeniths.getSelectedItem();
        Vector tables = datas.selectTypedTables( "azimuth", "zenith", value );
        if ( tables.size() > 0 ) {
            this.tables[2].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        this.tables[2].init();
    }

    protected void changeZenithTable() {
        String value = ( String ) cbZenRotates.getSelectedItem();
        Vector tables = datas.selectTypedTables( "zenith", "rotate", value );
        if ( tables.size() > 0 ) {
            this.tables[1].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        this.tables[1].init();
    }

    private void redrawToolValues() {
        if ( toolSource.isConnected() ) {
            displayTX.render( toolAngles.azimuth.getValue() );
            displayTY.render( toolAngles.zenith.getValue() );
            displayTZ.render( toolAngles.rotate.getValue() );
        } else {
            displayTX.renderClear();
            displayTY.renderClear();
            displayTZ.renderClear();
        }
        imgDigTX.repaint();
        imgDigTY.repaint();
        imgDigTZ.repaint();
    }

    private void redrawAccurateValues() {
        if ( unit.isConnected() ) {
            displayAX.render( accAngles.azimuth.getValue() );
            displayAY.render( accAngles.zenith.getValue() );
            displayAZ.render( accAngles.rotate.getValue() );
        } else {
            displayAX.renderClear();
            displayAY.renderClear();
            displayAZ.renderClear();
        }
        imgDigAX.repaint();
        imgDigAY.repaint();
        imgDigAZ.repaint();
    }
    /**
     *
     */
    protected void redrawGraphs() {
        grAz.repaint();
        grAx.repaint();
        grAy.repaint();
        grHxy.repaint();
        grHyz.repaint();
        grHzx.repaint();
    }

    public String formatAngle( double value ) {
        String str = "";
        angleFormat.setMaximumFractionDigits( 2 );
        angleFormat.applyPattern("0.00");
        str = angleFormat.format(value);
        return str;
    }
    public void refreshToolValues() {
        Channel chan = toolSource.getChannel( "angles" );
        if ( chan == null ) {
            chan = toolSource.getChannel( "values" );
        }
        if ( chan != null ) {
            toolAngles.azimuth.setAngle(chan.getValue( 0 ).getAsDouble());
            //toolAngles.zenit = chan.getValue( 1 ).angle;
            //toolAngles.rotate = chan.getValue( 2 ).angle;
            toolAngles.zenith.setAngle(chan.getValue( 1 ).getAsDouble() );
            toolAngles.rotate.setAngle( chan.getValue( 2 ).getAsDouble() );


            redrawToolValues();
        }

        chan = toolSource.getChannel( "sensors" );
        if( chan != null ) {
            lAx1.setText( Integer.toString(chan.getValue(0).getAsInteger()));
            lAx2.setText( Integer.toString(chan.getValue(1).getAsInteger()));
            lAy1.setText( Integer.toString(chan.getValue(2).getAsInteger()));
            lAy2.setText( Integer.toString(chan.getValue(3).getAsInteger()));
            lAz1.setText( Integer.toString(chan.getValue(4).getAsInteger()));
            lAz2.setText( Integer.toString(chan.getValue(5).getAsInteger()));
            lHxy1.setText( Integer.toString(chan.getValue(6).getAsInteger()));
            lHxy2.setText( Integer.toString(chan.getValue(7).getAsInteger()));
            lHyz1.setText( Integer.toString(chan.getValue(8).getAsInteger()));
            lHyz2.setText( Integer.toString(chan.getValue(9).getAsInteger()));
            lHzx1.setText( Integer.toString(chan.getValue(10).getAsInteger()));
            lHzx2.setText( Integer.toString(chan.getValue(11).getAsInteger()));
            //lAx1.setText( Integer.toString(chan.getValue(0).getAsInteger()));
            //lAx1.setText( Integer.toString(chan.getValue(0).getAsInteger()));
        }

        chan = toolSource.getChannel( "angles2" );
        if( chan != null ) {
            lAx.setText( formatAngle(chan.getValue(12).getAsDouble()));
            lAy.setText( formatAngle(chan.getValue(13).getAsDouble()));
            lAz.setText( formatAngle(chan.getValue(14).getAsDouble()));
            lAxn.setText( formatAngle(chan.getValue(6).getAsDouble()));
            lAyn.setText( formatAngle(chan.getValue(7).getAsDouble()));
            lAzn.setText( formatAngle(chan.getValue(8).getAsDouble()));
            lHxy.setText( formatAngle(chan.getValue(15).getAsDouble()));
            lHyz.setText( formatAngle(chan.getValue(16).getAsDouble()));
            lHzx.setText( formatAngle(chan.getValue(17).getAsDouble()));
            lHxyn.setText( formatAngle(chan.getValue(9).getAsDouble()));
            lHyzn.setText( formatAngle(chan.getValue(10).getAsDouble()));
            lHzxn.setText( formatAngle(chan.getValue(11).getAsDouble()));

        }

    }
    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                refreshToolValues();
            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Нет связи с прибором" );
            }

        } else
        if ( src.equals( unit ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = unit.getChannel( "angles" );
                if ( chan != null ) {
                    accAngles.azimuth.setAngle( chan.getValue( 0 ).getAsDouble() );
                    accAngles.zenith.setAngle( chan.getValue( 1 ).getAsDouble() );
                    accAngles.rotate.setAngle( chan.getValue( 2 ).getAsDouble() );
                    redrawAccurateValues();
                }

            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Нет связи с установкой" );
            }

        }

    }

    public void start() {
        boolean quit = false;
        try {
            Zeus zeus = Zeus.getInstance();
            workState.setWorkMode( zeus.getWorkMode() );
            unit = DataFactory.createCheckUnit();
            unit.addSignalListener( this );
            boolean b = Boolean.valueOf( zeus.getProperty( Zeus.PROP_WORK_NEW ) ).
                booleanValue();
            if ( b ) {
                datas = new MeasureDatas( workState );
                graduation.exportData(datas);
                /*
                datas = DataFactory.createMeasureDatas( DataFactory.getMethods(
                    "ion1_test.xml" ) );
                 */
                //datas.setWorkState( workState );
            } else {
                String id = zeus.getProperty( Zeus.PROP_WORK_ID );
                datas = DataFactory.loadMeasureDatas( workState.getToolType(),
                    workState.getToolNumber(),
                    zeus.getProperty( Zeus.PROP_WORK_ID ) );
                workState = datas.getWorkState();

            }
            //setErrorLimits();
            //datas.ensurePointsCount(4);
            //datas.calc();
            //selectTables();
            //buildTables();
            //initZenithTab();
            //initAzimuthTab();
            // import loaded data to graduation
            graduation.importData(datas);
            graduation.calcAfterImport();
            try {
                toolSource = DataFactory.createToolDataSource();
            } catch ( Exception ex ) {
                UiUtils.showError( this,
                                   ex.getLocalizedMessage() );
            }
            if ( toolSource == null ) {
                int res = UiUtils.showConfirmError( this,
                    "<html><center>Не удалось создать источник данных прибора" +
                    "<br>Продолжить?" );
                quit = res != 0;
            } else {
                toolSource.addSignalListener( this );
            }
            jbInit();

            //graph.function.add( 0, 0);
            //graph.function.add( 2, 5);
            //graph.function.add( 7, 3);
            //graph.function.add( 18, 18);

            UiUtils.toScreenCenter( this );
            selectedContainer = tables[0];
            tabbedTables.setSelectedIndex( 0 );
            redrawAccurateValues();
            redrawToolValues();
            redrawGraphs();
        } catch ( Exception ex ) {
            UiUtils.showError( this, ex.getLocalizedMessage() );
            //ex.printStackTrace();
        }
        if ( !quit ) {
            this.setVisible( true );
        }
    }

    private void jbInit() throws Exception {
        //tables[0].init();
        titledBorder1 = new TitledBorder( "" );
        border1 = BorderFactory.createLineBorder( Color.black, 2 );
        titledBorder2 = new TitledBorder( BorderFactory.createLineBorder( Color.
            black, 1 ), "Показания " );
        border2 = BorderFactory.createLineBorder( SystemColor.controlText, 1 );
        titledBorder3 = new TitledBorder( BorderFactory.createLineBorder(
            SystemColor.controlText, 1 ), "Выполнить калибровку..." );
        titledBorder4 = new TitledBorder( "" );
        panelX.setLayout( null );
        panelZ.setLayout( new BorderLayout() );
        //table.setMinimumSize( new Dimension( , 0 ) );
        //table.setPreferredSize( new Dimension( 0, 0 ) );
        this.getContentPane().setLayout( null );
        this.setSize( 800, 600 );
        this.setState( Frame.NORMAL );
        this.setTitle( "Градуировка прибора " + workState.getToolName() + " № " +
                       workState.getToolNumber() + " | " +
                       toolSource.getProperty( "table.filename" ) + " от " +
                       toolSource.getProperty( "table.date" ));

        //this.setExtendedState( 6 );
        tabbedTables.setBounds(new Rectangle(9, 90, 781, 195) );
        //tabbedTables.addTab("Угол поворота", table);
        Font font = new Font( "Dialog", 0, 14 );
        // init tableX
        //scrollPaneY.setAutoscrolls( true );
        //scrollPaneY.setDebugGraphicsOptions( 0 );

        jLabel1.setText( "Угол поворота:" );
        jLabel1.setBounds( new Rectangle( 3, 9, 124, 15 ) );
        cbZenRotates.setBounds( new Rectangle( 110, 5, 115, 24 ) );
        cbZenRotates.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                cbZenRotates_actionPerformed( e );
            }
        } );

        jLabel2.setText( "Зенитный угол:" );
        jLabel2.setBounds( new Rectangle( 7, 9, 134, 15 ) );
        cbAzZeniths.setBounds( new Rectangle( 112, 7, 115, 24 ) );
        cbAzZeniths.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                cbAzZeniths_actionPerformed( e );
            }
        } );
        jToolBar1.setEnabled( true );
        //jToolBar1.setAlignmentY((float) 0.5);
        jToolBar1.setBorder( null );
        jToolBar1.setFloatable( false );
        jToolBar1.setBounds( new Rectangle( 1, 1, 480, 41 ) );
        bSave.setText( "Сохранить" );
        bSave.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bSave_actionPerformed( e );
            }
        } );
        panelDisplay.setBorder( titledBorder2 );
        panelDisplay.setBounds( new Rectangle( 10, 286, 662, 150 ) );
        jLabel5.setHorizontalAlignment( SwingConstants.CENTER );
        jLabel5.setText( "АЗИМУТ" );
        jLabel5.setBounds( new Rectangle( 58, 7, 159, 15 ) );
        jLabel6.setHorizontalAlignment( SwingConstants.CENTER );
        jLabel6.setText( "ЗЕНИТ" );
        jLabel6.setBounds( new Rectangle( 243, 7, 162, 15 ) );
        jLabel7.setHorizontalAlignment( SwingConstants.CENTER );
        jLabel7.setText( "ПОВОРОТ" );
        jLabel7.setBounds( new Rectangle( 431, 7, 163, 15 ) );
        panelHeader.setLayout( null );
        panelAcc.setBorder( null );
        panelAcc.setDebugGraphicsOptions( 0 );
        panelAcc.setMinimumSize( new Dimension( 600, 17 ) );
        panelAcc.setPreferredSize( new Dimension( 640, 40 ) );
        panelAcc.setLayout( null );
        //panelAcc.setLayout(boxLayout21);
        jLabel3.setPreferredSize( new Dimension( 90, 15 ) );
        jLabel3.setHorizontalAlignment( SwingConstants.LEFT );
        jLabel3.setHorizontalTextPosition( SwingConstants.LEFT );
        jLabel3.setIconTextGap( 4 );
        jLabel3.setText( "По эталону" );
        jLabel3.setBounds( new Rectangle( 0, 4, 81, 31 ) );
        imgDigAX.setLocation( new java.awt.Point( 100, 310 ) );
        imgDigAX.setBounds( new Rectangle( 79, 1, 162, 35 ) );
        imgDigAY.setLocation( new java.awt.Point( 200, 380 ) );
        imgDigAY.setBounds( new Rectangle( 273, 1, 162, 35 ) );
        imgDigAZ.setLocation( new java.awt.Point( 380, 380 ) );
        imgDigAZ.setBounds( new Rectangle( 466, 2, 162, 35 ) );
        panelTool.setBorder( null );
        panelTool.setMinimumSize( new Dimension( 270, 30 ) );
        panelTool.setPreferredSize( new Dimension( 640, 40 ) );
        panelTool.setLayout( null );
        //panelTool.setLayout(boxLayout22);
        imgDigTX.setLocation( new java.awt.Point( 20, 420 ) );
        imgDigTX.setBounds( new Rectangle( 80, 3, 162, 35 ) );
        jLabel4.setPreferredSize( new Dimension( 90, 15 ) );
        jLabel4.setHorizontalAlignment( SwingConstants.LEFT );
        jLabel4.setHorizontalTextPosition( SwingConstants.LEFT );
        jLabel4.setIconTextGap( 4 );
        jLabel4.setText( "По прибору" );
        jLabel4.setBounds( new Rectangle( 2, 13, 84, 15 ) );
        imgDigTY.setLocation( new java.awt.Point( 240, 420 ) );
        imgDigTY.setBounds( new Rectangle( 273, 3, 162, 35 ) );
        imgDigTZ.setLocation( new java.awt.Point( 460, 420 ) );
        imgDigTZ.setBounds( new Rectangle( 466, 3, 162, 35 ) );
        panelHeader.setPreferredSize( new Dimension( 600, 30 ) );
        jLabel8.setBounds( new Rectangle( 0, 14, 0, 0 ) );
        bConnect.setBorder( BorderFactory.createRaisedBevelBorder() );
        bConnect.setMinimumSize( new Dimension( 82, 24 ) );
        bConnect.setPreferredSize( new Dimension( 82, 24 ) );
        bConnect.setToolTipText( "Подключиться к установке" );
        //bConnect.setActionCommand("Подключение");
        //bConnect.setBorderPainted(false);
        bConnect.setContentAreaFilled( true );
        //bConnect.setFocusPainted(true);
        bConnect.setIcon( iconOff );
        bConnect.setMargin( new Insets( 2, 14, 2, 14 ) );
        //bConnect.setMnemonic('0');
        //bConnect.setRolloverEnabled(true);
        bConnect.setText( "Подключение" );
        bConnect.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                switchConnect();
            }
        } );
        jLabel9.setText( "Выполнить:" );
        jLabel9.setBounds(new Rectangle(12, 55, 94, 15) );
        cbDo.setBounds(new Rectangle(90, 52, 264, 24) );
        bStart.setBounds(new Rectangle(366, 52, 80, 29) );
        bStart.setPreferredSize( new Dimension( 80, 30 ) );
        bStart.setText( "Пуск" );
        bStart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                startProcess();
            }
        } );
        jPanel1.setBorder( BorderFactory.createLineBorder( Color.black ) );
        jPanel1.setBounds(new Rectangle(544, 8, 248, 97) );
        jPanel1.setLayout( null );
        jLabel10.setText( "Начало" );
        jLabel10.setBounds( new Rectangle( 7, 7, 53, 15 ) );
        jLabel11.setText( "Прошло" );
        jLabel11.setBounds( new Rectangle( 143, 8, 51, 15 ) );
        lTimeStart.setFont( new java.awt.Font( "Dialog", 1, 24 ) );
        lTimeStart.setText( "00:00:00" );
        lTimeStart.setBounds( new Rectangle( 5, 22, 117, 32 ) );
        lTimeElapsed.setBounds( new Rectangle( 138, 24, 117, 32 ) );
        lTimeElapsed.setText( "00:00:00" );
        lTimeElapsed.setFont( new java.awt.Font( "Dialog", 1, 24 ) );
        lMessage1.setText( "M1" );
        lMessage1.setBounds( new Rectangle( 7, 63, 253, 22 ) );
        lMessage2.setBounds( new Rectangle( 7, 83, 253, 22 ) );
        lMessage2.setText( "M2" );
        lMessage3.setBounds( new Rectangle( 7, 103, 253, 22 ) );
        lMessage3.setText( "M3" );
        bStop.setBounds(new Rectangle(451, 53, 88, 29) );
        bStop.setPreferredSize( new Dimension( 84, 30 ) );
        bStop.setText( "Останов" );
        bStop.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stopProcess( e );
            }
        } );
        //miMeasure.setText( "Замер" );
        miChange.setAction( changeAction );
        miMeasure.setAction( measureAction );
        bProtocol.setText( "Протокол" );
        bProtocol.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bProtocol_actionPerformed( e );
            }
        } );
        jLabel12.setBounds( new Rectangle( 30, 445, 25, 15 ) );
        jLabel12.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel12.setText( "Ax\'" );
        jLabel14.setBounds(new Rectangle(30, 465, 25, 15));
        jLabel14.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel14.setText("Ay\'");
        jLabel16.setBounds(new Rectangle(30, 485, 25, 15));
        jLabel16.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel16.setText("Az\'");
        jLabel13.setBounds( new Rectangle( 130, 445, 25, 15 ) );
        jLabel13.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel13.setText("Ax\'\'");
        jLabel15.setBounds(new Rectangle(130, 465, 25, 15));
        jLabel15.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel15.setText("Ay\'\'");
        jLabel17.setBounds(new Rectangle(130, 485, 25, 15));
        jLabel17.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel17.setText("Az\'\'");
        jLabel18.setBounds(new Rectangle(230, 445, 25, 15));
        jLabel18.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel18.setText("Ax");
        jLabel19.setBounds(new Rectangle(230, 465, 25, 15));
        jLabel19.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel19.setText("Ay");
        jLabel110.setBounds(new Rectangle(230, 485, 25, 15));
        jLabel110.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel110.setText("Az");
        jLabel111.setBounds(new Rectangle(350, 445, 25, 15));
        jLabel111.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel111.setText("Axn");
        jLabel112.setBounds(new Rectangle(350, 465, 25, 15));
        jLabel112.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel112.setText("Ayn");
        jLabel113.setBounds(new Rectangle(350, 485, 25, 15));
        jLabel113.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel113.setText("Azn");
        jLabel116.setBounds(new Rectangle(30, 505, 25, 15));
        jLabel116.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel116.setText("Hxy\'");
        jLabel118.setBounds(new Rectangle(30, 525, 25, 15));
        jLabel118.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel118.setText("Hyz\'");
        jLabel119.setBounds(new Rectangle(30, 545, 25, 15));
        jLabel119.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel119.setText("Hzx\'");
        jLabel120.setBounds(new Rectangle(130, 505, 37, 15));
        jLabel120.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel120.setText("Hxy\'\'");
        jLabel123.setBounds(new Rectangle(130, 525, 41, 15));
        jLabel123.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel123.setText("Hyz\'\'");
        jLabel122.setBounds(new Rectangle(130, 545, 38, 15));
        jLabel122.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel122.setText("Hzx\'\'");
        jLabel121.setBounds(new Rectangle(230, 505, 25, 15));
        jLabel121.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel121.setText("Hxy");
        jLabel115.setBounds(new Rectangle(230, 525, 25, 15));
        jLabel115.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel115.setText("Hyz");
        jLabel1110.setBounds(new Rectangle(230, 545, 25, 15));
        jLabel1110.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel1110.setText("Hzx");
        jLabel117.setBounds(new Rectangle(350, 505, 36, 15));
        jLabel117.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel117.setText("Hxyn");
        jLabel1111.setBounds(new Rectangle(350, 525, 50, 15));
        jLabel1111.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel1111.setText("Hyzn");
        jLabel114.setBounds(new Rectangle(350, 545, 39, 15));
        jLabel114.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        jLabel114.setText("Hzxn");

        lAx1.setBounds(new Rectangle(60, 445, 38, 15));
        lAx1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAx1.setText("0000");
        lAy1.setBounds(new Rectangle(60, 465, 38, 15));
        lAy1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAy1.setText("0000");
        lAz1.setBounds(new Rectangle(60, 485, 38, 15));
        lAz1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAz1.setText("0000");
        lAx2.setBounds(new Rectangle(160, 445, 38, 15));
        lAx2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAx2.setText("0000");
        lAy2.setBounds(new Rectangle(160, 465, 38, 15));
        lAy2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAy2.setText("0000");
        lAz2.setBounds(new Rectangle(160, 485, 38, 15));
        lAz2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAz2.setText("0000");
        lAx.setBounds(new Rectangle(260, 445, 60, 15));
        lAx.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAx.setHorizontalAlignment(SwingConstants.RIGHT);
        lAx.setText("0000");
        lAy.setBounds(new Rectangle(260, 465, 60, 15));
        lAy.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAy.setHorizontalAlignment(SwingConstants.RIGHT);
        lAy.setText("0000");
        lAz.setBounds(new Rectangle(260, 485, 60, 15));
        lAz.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAz.setHorizontalAlignment(SwingConstants.RIGHT);
        lAz.setText("0000");
        lAxn.setBounds(new Rectangle(380, 445, 60, 15));
        lAxn.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAxn.setHorizontalAlignment(SwingConstants.RIGHT);
        lAxn.setText("0000");
        lAyn.setBounds(new Rectangle(380, 465, 60, 15));
        lAyn.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAyn.setHorizontalAlignment(SwingConstants.RIGHT);
        lAyn.setText("0000");
        lAzn.setBounds(new Rectangle(380, 485, 60, 15));
        lAzn.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAzn.setHorizontalAlignment(SwingConstants.RIGHT);
        lAzn.setText("0000");

        lHxy1.setBounds(new Rectangle(60, 505, 38, 15));
        lHxy1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHxy1.setText("0000");
        lHyz1.setBounds(new Rectangle(60, 525, 38, 15));
        lHyz1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHyz1.setText("0000");
        lHzx1.setBounds(new Rectangle(60, 545, 38, 15));
        lHzx1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHzx1.setText("0000");
        lHxy2.setBounds(new Rectangle(160, 505, 38, 15));
        lHxy2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHxy2.setText("0000");
        lHyz2.setBounds(new Rectangle(160, 525, 38, 15));
        lHyz2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHyz2.setText("0000");
        lHzx2.setBounds(new Rectangle(160, 545, 38, 15));
        lHzx2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHzx2.setText("0000");
        lHxy.setBounds(new Rectangle(260, 505, 60, 15));
        lHxy.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHxy.setHorizontalAlignment(SwingConstants.RIGHT);
        lHxy.setText("0000");
        lHyz.setBounds(new Rectangle(260, 525, 60, 15));
        lHyz.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHyz.setHorizontalAlignment(SwingConstants.RIGHT);
        lHyz.setText("0000");
        lHzx.setBounds(new Rectangle(260, 545, 60, 15));
        lHzx.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHzx.setHorizontalAlignment(SwingConstants.RIGHT);
        lHzx.setText("0000");
        lHxyn.setBounds(new Rectangle(380, 505, 60, 15));
        lHxyn.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHxyn.setHorizontalAlignment(SwingConstants.RIGHT);
        lHxyn.setText("0000");
        lHyzn.setBounds(new Rectangle(380, 525, 60, 15));
        lHyzn.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHyzn.setHorizontalAlignment(SwingConstants.RIGHT);
        lHyzn.setText("0000");
        lHzxn.setBounds(new Rectangle(380, 545, 60, 15));
        lHzxn.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHzxn.setHorizontalAlignment(SwingConstants.RIGHT);
        lHzxn.setText("0000");

        panelY.setMaximumSize( new Dimension( 500, 500 ) );
        panelY.setLayout( null );
        grAz.setMaximumSize( new Dimension( 500, 500 ) );
        grAz.setBounds( 400, 400, 200, 200 );
        grAz.setLayout(null);
        graphAz.lineWidth = 1;
        graphAzrev.lineWidth = 1;
        //graphHxy.setMaximumSize( new Dimension( 500, 500 ) );
        //graphHxy.setBounds( 400, 400, 200, 200 );
        //graphHxy.setLayout(null);
        //graphHxy.lineWidth = 1;
        //graphHxyrev.setMaximumSize( new Dimension( 500, 500 ) );
        //graphHxyrev.setBounds( 400, 400, 200, 200 );
        //graphHxyrev.setLayout(null);
        //graphHxyrev.lineWidth = 1;
        //tabbedTables.add( panelZ, "Угол поворота" );
        tabbedTables.add( grAx, "График Ax" );
        tabbedTables.add( grAy, "График Ay" );
        tabbedTables.add( grAz, "График Az" );
        tabbedTables.add( grHxy, "График Hxy" );
        tabbedTables.add( grHyz, "График Hyz" );
        tabbedTables.add( grHzx, "График Hzx" );

        tables[2].scrollPane.setBounds( new Rectangle( 1, 41, 473, 169 ) );
        tables[1].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );

        //panelX.add( tables[2].scrollPane, null );
        //panelY.add( tables[1].scrollPane, null );
        //panelZ.add( tables[0].scrollPane, BorderLayout.CENTER );

        //tabbedTables.add( panelY, "Зенитный угол" );
        panelY.add( jLabel1, null );
        panelY.add( cbZenRotates, null );
        //tabbedTables.add( panelX, "Азимутальный угол" );
        //scrollPaneY.getiewport().add( tables[1].table, null );

        panelX.add( jLabel2, null );
        panelX.add( cbAzZeniths, null );
        jPanel1.add( jLabel10, null );
        jPanel1.add( jLabel11, null );
        jPanel1.add( lTimeStart, null );
        jPanel1.add( lTimeElapsed, null );
        jPanel1.add(lMessage2, null);
        jPanel1.add(lMessage3, null);
        jPanel1.add(lMessage1, null);
        this.getContentPane().add(bStop, null);
        this.getContentPane().add(bStart, null);
        this.getContentPane().add(jPanel1, null);
        this.getContentPane().add( panelDisplay, null );
        panelHeader.add( jLabel8, null );
        panelHeader.add( jLabel7, null );
        panelHeader.add( jLabel6, null );
        panelHeader.add( jLabel5, null );
        panelDisplay.add( panelHeader, null );
        panelDisplay.add( panelAcc, null );
        panelAcc.add( imgDigAY, null );
        panelAcc.add( imgDigAX, null );
        panelAcc.add( imgDigAZ, null );
        panelAcc.add( jLabel3, null );
        panelDisplay.add( panelTool, null );
        panelTool.add( imgDigTX, null );
        panelTool.add( imgDigTY, null );
        panelTool.add( imgDigTZ, null );
        panelTool.add( jLabel4, null );

        displayAX.renderClear();
        displayAY.renderClear();
        displayAZ.renderClear();

        jToolBar1.add( bSave, null );
        jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        jToolBar1.add( bConnect, null );
        jToolBar1.add(bProtocol, null);
        this.getContentPane().add( jToolBar1, null );
        this.getContentPane().add( tabbedTables, null );
        //this.getContentPane().add( graph, null );
        this.getContentPane().add(jLabel12, null);
        this.getContentPane().add(jLabel14, null);
        this.getContentPane().add(jLabel16, null);
        this.getContentPane().add(jLabel116, null);
        this.getContentPane().add(jLabel118, null);
        this.getContentPane().add(jLabel119, null);
        this.getContentPane().add(jLabel111, null);
        this.getContentPane().add(jLabel112, null);
        this.getContentPane().add(jLabel113, null);
        this.getContentPane().add(jLabel117, null);
        this.getContentPane().add(jLabel114, null);
        this.getContentPane().add( jLabel1111, null );
        this.getContentPane().add( jLabel19, null );
        this.getContentPane().add( jLabel18, null );
        this.getContentPane().add( jLabel110, null );
        this.getContentPane().add( jLabel115, null );
        this.getContentPane().add( jLabel1110, null );
        this.getContentPane().add( jLabel121, null );
        this.getContentPane().add( jLabel17, null );
        this.getContentPane().add( jLabel13, null );
        this.getContentPane().add( jLabel15, null );
        this.getContentPane().add( jLabel120, null );
        this.getContentPane().add( jLabel122, null );
        this.getContentPane().add( jLabel123, null );
        this.getContentPane().add( lAx1, null );
        this.getContentPane().add( lAy1, null );
        this.getContentPane().add( lAz1, null );
        this.getContentPane().add( lAx2, null );
        this.getContentPane().add( lAy2, null );
        this.getContentPane().add( lAz2, null );
        this.getContentPane().add( lAx, null );
        this.getContentPane().add( lAy, null );
        this.getContentPane().add( lAz, null );
        this.getContentPane().add( lAxn, null );
        this.getContentPane().add( lAyn, null );
        this.getContentPane().add( lAzn, null );
        this.getContentPane().add( lHxy1, null );
        this.getContentPane().add( lHyz1, null );
        this.getContentPane().add( lHzx1, null );
        this.getContentPane().add( lHxy2, null );
        this.getContentPane().add( lHyz2, null );
        this.getContentPane().add( lHzx2, null );
        this.getContentPane().add( lHxy, null );
        this.getContentPane().add( lHyz, null );
        this.getContentPane().add( lHzx, null );
        this.getContentPane().add( lHxyn, null );
        this.getContentPane().add( lHyzn, null );
        this.getContentPane().add( lHzxn, null );
        this.getContentPane().add(jLabel9, null);
        this.getContentPane().add(cbDo, null);
        tablePopup.add( miChange );
        tablePopup.add( miMeasure );

        //scrollPaneX.getViewport().add( tables[0].table, null );
        //scrollPaneZ.getViewport().add( tables[2].table, null );
        //tabbedTables.setSelectedComponent( panelX );
        buildToolLabel();
        for ( int i = 0; i < commands.length; i++ ) {
            cbDo.addItem( commands[i] );
        }
        clearMessages();
        this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    }

    void setErrorLimits() {
        ION1Informer informer = new ION1Informer();
        int size = datas.size();
        Channel chan = new Channel( "angles", 0, 3 );
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = datas.getTable( i );
            int index = planeToIndex( nameToPlane( table.getProperty( "type" ) ) );
            String s = table.getProperty( "zenith" );
            if ( s != null ) {
                try {
                    chan.getValue( 1 ).setAsDouble( Double.parseDouble( s ) );
                } catch ( Exception ex ) {
                }
            }
            for ( int j = 0; j < table.size(); j++ ) {
                MeasureChain chain = table.getChain( j );
                chan.getValue( index ).setAsDouble( chain.
                    getReproductionValue() );
                chain.setToolErrorLimit( informer.getErrorLimits( chan )[index] );
            }

        }
    }

    void waitForValue( double value, char plane ) {
        for ( ; ; ) {

            try {
                double val = Double.parseDouble( unit.getValue( plane ).
                                                 toString() );
                if ( Math.abs( val - value ) < 0.5 ) {
                    break;
                }
                Thread.currentThread().sleep( 500 );
            } catch ( Exception ex ) {
                System.err.println( ex );
            }
        }
    }

    void doMeasure( MeasureTable table, int row, char plane ) {
        Channel achan = unit.getChannel( "angles" );
        Channel tchan = toolSource.getChannel( "angles" );
        if ( achan == null || tchan == null ) {
            System.err.println( "Not available channel for measure" );
            return;
        }
        int subchan = planeToIndex( plane );
        MeasureChain chain = table.getChain( row );
        if ( chain == null ) {
            System.err.println( "No chain for row " + row );
            return;
        }
        int size = chain.size();
        if ( size < 4 ) {
            do {
                chain.addPoint();
                size = chain.size();
            }
            while ( size < 4 );
        }

        //size = 1;
        for ( int i = 0; i < size; i++ ) {
            lMessage3.setText( "Замер " + ( i + 1 ) );
            //System.out.print( "\nMeasure " + ( i + 1 ) );
            MeasurePoint point = chain.getPoint( i );
            System.out.print( '.' );
            point.setAccurateValue( doMeasureAccurate( plane ) );
            System.out.print( ',' );
            double value = doMeasureTool( plane );
            if ( value > 350 ) {
                value -= 360;
            }
            point.setToolValue( value );
            lMessage3.setText( "" );
        }
        chain.calc();

    }

    private int planeToIndex( char plane ) {
        int subchan = 0;
        if ( plane == 'y' ) {
            subchan = 1;
        } else if ( plane == 'z' ) {
            subchan = 2;
        }
        return subchan;
    }

    double doMeasureAccurate( char plane ) {
        double value = 0;
        int subchan = planeToIndex( plane );
        Channel chan = unit.getChannel( "angles" );
        for ( int i = 0; i < 4; i++ ) {
            unit.waitNewData();
            value += chan.getValue( subchan ).getAsDouble();
        }
        value /= 4.0;
        return value;
    }

    double doMeasureTool( char plane ) {
        double value = 0;
        int subchan = planeToIndex( plane );
        Channel chan = toolSource.getChannel( "angles" );
        if ( chan == null ) {
            /** @todo throw exce */
            return 0;
        }
        for ( int i = 0; i < 4; i++ ) {
            toolSource.waitNewData();
            double v1 = chan.getValue( subchan ).getAsDouble();
            if(v1 > 350 )
                v1 -= 360;
            value += v1;
        }
        value /= 4.0;
        return value;
    }

    void doWaitComplete( char plane ) {
        unit.waitNewData();
        unit.waitNewData();
        unit.waitNewData();
        unit.waitNewData();

        try {
            while ( !unit.isComplete( plane ) ) {
                Thread.sleep( 500 );
            }
        } catch ( InterruptedException ex ) {
        }
    }

    /**
     * Совмещение показаний угла поворота установки и прибора.
     * Совмещение производтся при зените 90 градусов.
     */
    public void doJoinRotates() throws Exception {
        clearMessages();
        lMessage1.setText( "Совмещение углов поворота" );
        UAKSI2CheckUnit unit = ( UAKSI2CheckUnit )this.unit;
        // сбрасываем поправку по повороту
        InclinometerAngles ang = unit.getAccurateDelta();
        ang.rotate.setAngle( 0 );
        // выходим на 90 градусов по зениту
        lMessage2.setText( "Выход на зенит 90 град" );
        unit.goToPoint( 'y', 90 );
        doWaitComplete( 'y' );
        lMessage2.setText( "Совмещение углов" );
        ang.rotate.setAngle( doMeasureAccurate( 'z' ) - doMeasureTool( 'z' ) );
        lMessage2.setText( "Совмещение выполнено" );

    }

    char nameToPlane( String tableName ) {
        char plane = 'z';
        if ( tableName.equals( "azimuth" ) ) {
            plane = 'x';
        } else
        if ( tableName.equals( "zenith" ) ) {
            plane = 'y';
        }
        return plane;
    }

    void doGoTo( char plane, Double value ) {
        try {
            String mess = "Задание азимутального угла ";
            if ( plane == 'y' ) {
                mess = "Задание зенитного угла ";
            }
            if ( plane == 'z' ) {
                mess = "Задание угла поворота ";
            }
            lMessage2.setText( mess + value );
            unit.goToPoint( plane, value );
            doWaitComplete( plane );
            lMessage2.setText( "" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }

    void doCalibrationTable( MeasureTable workTable ) {
        String name = workTable.getProperty( "name" );
        int pointsCount = workTable.size();
        char plane = nameToPlane( name );

        int first = 0;
        int inc = 1;
        Double firstPoint = new Double( workTable.getChain( 0 ).
                                        getReproductionValue() );
        Double lastPoint = new Double( workTable.getChain( pointsCount - 1 ).
                                       getReproductionValue() );
        double current = doMeasureAccurate( plane );
        if ( Math.abs( current - firstPoint.doubleValue() ) <
             Math.abs( current - lastPoint.doubleValue() ) ) {
            first = 0;
            inc = 1;
        } else {
            first = pointsCount - 1;
            inc = -1;
        }
        for ( int i = 0; i < pointsCount; i++, first += inc ) {
            try {
                Double value = new Double( workTable.getChain( first ).
                                           getReproductionValue() );
                doGoTo( plane, value );
                Thread.sleep( 4000 );
                doMeasure( workTable, first, plane );
                selectedContainer.table.repaint();
                //tables[2].table.repaint();
            } catch ( Exception ex ) {
                System.err.println( ex.getMessage() );
                ex.printStackTrace();
            }
        }

    }

    public void doCalibrationFull() throws Exception {
        doJoinRotates();
        doAzHxy();
        doCalibrationZenith();
        doCalibrationAzimuth();
    }

    /**
     * Выполнение градуировки датчиков Az и Hxy.
     * Методика: БД устанавливается по зениту 90+-2', по азимуту 90+-2'
     * БД вращается вокруг собственной оси. В качестве поправки записывается
     * отклонение значений датчиков в угловых градусах от эталонного значения
     * поворота. Перед началом измерений совмещается ноль эталонного датчика
     * поворота с приборным, также поправка для датчика Hxy записывается
     * относительно показаний в нуле поворота
     */
    public void doAzHxy() {
        /** @todo ActionProgress, Action, SubAction */
        grAz.clipSize.x = 360.0f;
        grAz.clipSize.y = 2.0f;
        grAz.clipZero.y = -1f;
        grHxy.clipSize.x = 360.0f;
        grHxy.clipSize.y = 2.0f;
        grHxy.clipZero.y = -1f;
        MeasureTable tAz = datas.selectTable("name", "Az");
        MeasureTable tHxy = datas.selectTable("name", "Hxy");
        tAz.clear();
        tHxy.clear();
        graduation.tableAz.clear();
        graduation.tableHxy.clear();
        Channel chan1 = toolSource.getChannel( "angles2" );
        Channel achan = unit.getChannel( "angles" );
        lMessage1.setText( "градуировка датчиков Az и Hxy" );
        doGoTo( 'x', new Double( 90.0 ) );
        doGoTo( 'y', new Double( 90.0 ) );
        UAKSI2CheckUnit unit = ( UAKSI2CheckUnit )this.unit;
        InclinometerAngles ang = unit.getAccurateDelta();
        ang.rotate.setAngle( 0 );
        doGoTo( 'z', new Double( 0 ) );
        lMessage2.setText( "стабилизация");
        Util.delay( 4000 );
        lMessage2.setText( "совмещение углов");
        for ( int i = 0; i < FILTER_SIZE; i++ ) {
            lMessage2.setText( "замер  " + (i+1));
            toolSource.waitNewData();
            // фильтруем Az
            mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
            // Hxy
            mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
        }
        double az = Angle.norm(360.0 - mfilter1.cuttedAverage());
        double az0 = az;
        double hxy;
        double hxy0 = mfilter2.cuttedAverage();
        ang.rotate.setAngle( doMeasureAccurate( 'z' ) - az );
        hxy0 += ang.rotate.getValue();
        for ( double a = -1; a < 363.0; a += 3.0 ) {
            MeasureChain chainAz = tAz.addChain();
            chainAz.ensureSize(9);
            chainAz.setReproductionValue(a);
            MeasureChain chainHxy = tHxy.addChain();
            chainHxy.ensureSize(9);
            chainHxy.setReproductionValue(a);
            doGoTo( 'z', new Double( a ) );
            lMessage2.setText( "стабилизация");
            Util.delay( 4000 );
            for ( int i = 0; i < FILTER_SIZE; i++ ) {
                lMessage2.setText( "замер  " + (i+1));
                Util.delay( 1000 );
                toolSource.waitNewData();
                // фильтруем Az
                mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
                // Hxy
                mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
                chainAz.getPoint(i).setAccurateValue(achan.getValue( 2 ).getAsDouble());
                chainAz.getPoint(i).setToolValue( chan1.getValue( 14 ).getAsDouble());
                chainHxy.getPoint(i).setAccurateValue(achan.getValue( 2 ).getAsDouble());
                chainHxy.getPoint(i).setToolValue( chan1.getValue( 15 ).getAsDouble());
            }
            double rotate = achan.getValue( 2 ).getAsDouble();
            az = Angle.norm(360.0 - mfilter1.cuttedAverage());
            if(Math.abs(rotate) + 180.0 < az )
                az -= 360.0;
            hxy = Angle.norm(mfilter2.cuttedAverage() - hxy0 );
            double daz = Angle.norm( rotate ) - az;
            double dhxy = Angle.norm( rotate ) - hxy;
            lMessage3.setText( "Daz=" + daz );
            graduation.tableAz.add( rotate, daz );
            graduation.tableHxy.add( rotate, dhxy );
            grAz.repaint();
            grHxy.repaint();

        }
        //обратный ход
        graduation.tableAzrev.clear();
        graduation.tableHxyrev.clear();
        if( doReverse ) {
            MeasureTable tAzrev = datas.selectTable( "name", "Azrev" );
            MeasureTable tHxyrev = datas.selectTable( "name", "Hxyrev" );
            tAzrev.clear();
            tHxyrev.clear();

            for ( double a = 360.5; a >= -1.0; a -= 3.0 ) {
                MeasureChain chainAzrev = tAzrev.addChain();
                chainAzrev.ensureSize(9);
                chainAzrev.setReproductionValue(a);
                MeasureChain chainHxyrev = tHxyrev.addChain();
                chainHxyrev.ensureSize(9);
                chainHxyrev.setReproductionValue(a);
                doGoTo( 'z', new Double( a ) );
                lMessage2.setText( "стабилизация" );
                Util.delay( 4000 );
                for ( int i = 0; i < FILTER_SIZE; i++ ) {
                    lMessage2.setText( "замер  " + ( i + 1 ) );
                    Util.delay( 1000 );
                    toolSource.waitNewData();
                    // фильтруем Az
                    mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
                    // Hxy
                    mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
                    chainAzrev.getPoint(i).setAccurateValue(achan.getValue( 2 ).getAsDouble());
                    chainAzrev.getPoint(i).setToolValue( chan1.getValue( 14 ).getAsDouble());
                    chainHxyrev.getPoint(i).setAccurateValue(achan.getValue( 2 ).getAsDouble());
                    chainHxyrev.getPoint(i).setToolValue( chan1.getValue( 15 ).getAsDouble());
                }
                double rotate = achan.getValue( 2 ).getAsDouble();
                az = Angle.norm( 360.0 - mfilter1.cuttedAverage() );
                if ( Math.abs( rotate ) + 180.0 < az )
                    az -= 360.0;
                    //hxy = mfilter2.median();
                hxy = Angle.norm( mfilter2.cuttedAverage() - hxy0 );
                double daz = Angle.norm( rotate ) - az;
                double dhxy = Angle.norm( rotate ) - hxy;
                lMessage3.setText( "Daz=" + daz );
                graduation.tableAzrev.add( rotate, daz );
                graduation.tableHxyrev.add( rotate, dhxy );
                grAz.repaint();
                grHxy.repaint();
            }
            lMessage2.setText( "градуировка выполнена" );
        }
    }
    /**
     * Прямой ход градуировки датчиков Az Hxy
     */
    public void doAzHxyF() {
        MeasureTable tAz = datas.selectTable( "name", "Az" );
        MeasureTable tHxy = datas.selectTable( "name", "Hxy" );
        tAz.clear();
        tHxy.clear();
        graduation.tableAz.clear();
        graduation.tableHxy.clear();
        Channel chan1 = toolSource.getChannel( "angles2" );
        Channel achan = unit.getChannel( "angles" );
        lMessage1.setText( "градуировка датчиков Az и Hxy" );
        doGoTo( 'x', new Double( 90.0 ) );
        doGoTo( 'y', new Double( 90.0 ) );
        UAKSI2CheckUnit unit = ( UAKSI2CheckUnit )this.unit;
        InclinometerAngles ang = unit.getAccurateDelta();
        ang.rotate.setAngle( 0 );
        doGoTo( 'z', new Double( 0 ) );
        lMessage2.setText( "стабилизация" );
        Util.delay( 4000 );
        lMessage2.setText( "совмещение углов" );
        for ( int i = 0; i < FILTER_SIZE; i++ ) {
            lMessage2.setText( "замер  " + ( i + 1 ) );
            toolSource.waitNewData();
            // фильтруем Az
            mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
            // Hxy
            mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
        }
        double az = Angle.norm( 360.0 - mfilter1.cuttedAverage() );
        double az0 = az;
        double hxy;
        double hxy0 = mfilter2.cuttedAverage();
        ang.rotate.setAngle( doMeasureAccurate( 'z' ) - az );
        hxy0 += ang.rotate.getValue();
        for ( double a = -1; a < 363.0; a += 3.0 ) {
            MeasureChain chainAz = tAz.addChain();
            chainAz.ensureSize( 9 );
            chainAz.setReproductionValue( a );
            MeasureChain chainHxy = tHxy.addChain();
            chainHxy.ensureSize( 9 );
            chainHxy.setReproductionValue( a );
            doGoTo( 'z', new Double( a ) );
            lMessage2.setText( "стабилизация" );
            Util.delay( 4000 );
            for ( int i = 0; i < FILTER_SIZE; i++ ) {
                lMessage2.setText( "замер  " + ( i + 1 ) );
                Util.delay( 1000 );
                toolSource.waitNewData();
                // фильтруем Az
                mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
                // Hxy
                mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
                chainAz.getPoint( i ).setAccurateValue( achan.getValue( 2 ).
                    getAsDouble() );
                chainAz.getPoint( i ).setToolValue( chan1.getValue( 14 ).
                    getAsDouble() );
                chainHxy.getPoint( i ).setAccurateValue( achan.getValue( 2 ).
                    getAsDouble() );
                chainHxy.getPoint( i ).setToolValue( chan1.getValue( 15 ).
                    getAsDouble() );
            }
            double rotate = achan.getValue( 2 ).getAsDouble();
            az = Angle.norm( 360.0 - mfilter1.cuttedAverage() );
            if ( Math.abs( rotate ) + 180.0 < az )
                az -= 360.0;
            hxy = Angle.norm( mfilter2.cuttedAverage() - hxy0 );
            double daz = Angle.norm( rotate ) - az;
            double dhxy = Angle.norm( rotate ) - hxy;
            lMessage3.setText( "Daz=" + daz );
            //graduation.tableAz.add( rotate, daz );
            //graduation.tableHxy.add( rotate, dhxy );
            graduation.importData(datas);
            graduation.calcAfterImport();
            grAz.repaint();
            grHxy.repaint();

        }
    }
    /**
     * Обратный ход градуировки датчиков Az Hxy
     */
    public void doAzHxyR() {
        Channel chan1 = toolSource.getChannel( "angles2" );
        Channel achan = unit.getChannel( "angles" );
        lMessage1.setText( "градуировка датчиков Az и Hxy" );
        doGoTo( 'x', new Double( 90.0 ) );
        doGoTo( 'y', new Double( 90.0 ) );
        UAKSI2CheckUnit unit = ( UAKSI2CheckUnit )this.unit;
        InclinometerAngles ang = unit.getAccurateDelta();
        //double az0 = az;
        double hxy;
        //double hxy0 = mfilter2.cuttedAverage();
        double az = Angle.norm( 360.0 - mfilter1.cuttedAverage() );
        graduation.tableAzrev.clear();
        graduation.tableHxyrev.clear();
        if( doReverse ) {
            MeasureTable tAzrev = datas.selectTable( "name", "Azrev" );
            MeasureTable tHxyrev = datas.selectTable( "name", "Hxyrev" );
            tAzrev.clear();
            tHxyrev.clear();

            for ( double a = 360.5; a >= -1.0; a -= 3.0 ) {
                MeasureChain chainAzrev = tAzrev.addChain();
                chainAzrev.ensureSize(9);
                chainAzrev.setReproductionValue(a);
                MeasureChain chainHxyrev = tHxyrev.addChain();
                chainHxyrev.ensureSize(9);
                chainHxyrev.setReproductionValue(a);
                doGoTo( 'z', new Double( a ) );
                lMessage2.setText( "стабилизация" );
                Util.delay( 4000 );
                for ( int i = 0; i < FILTER_SIZE; i++ ) {
                    lMessage2.setText( "замер  " + ( i + 1 ) );
                    Util.delay( 1000 );
                    toolSource.waitNewData();
                    // фильтруем Az
                    mfilter1.add( chan1.getValue( 14 ).getAsDouble() );
                    // Hxy
                    mfilter2.add( chan1.getValue( 15 ).getAsDouble() );
                    chainAzrev.getPoint(i).setAccurateValue(achan.getValue( 2 ).getAsDouble());
                    chainAzrev.getPoint(i).setToolValue( chan1.getValue( 14 ).getAsDouble());
                    chainHxyrev.getPoint(i).setAccurateValue(achan.getValue( 2 ).getAsDouble());
                    chainHxyrev.getPoint(i).setToolValue( chan1.getValue( 15 ).getAsDouble());
                }
                double rotate = achan.getValue( 2 ).getAsDouble();
                az = Angle.norm( 360.0 - mfilter1.cuttedAverage() );
                if ( Math.abs( rotate ) + 180.0 < az )
                    az -= 360.0;
                    //hxy = mfilter2.median();
                //hxy = Angle.norm( mfilter2.cuttedAverage() - hxy0 );
                double daz = Angle.norm( rotate ) - az;
                double dhxy = Angle.norm( rotate );
                lMessage3.setText( "Daz=" + daz );
                graduation.tableAzrev.add( rotate, daz );
                graduation.tableHxyrev.add( rotate, dhxy );
                grAz.repaint();
                grHxy.repaint();
            }
            lMessage2.setText( "градуировка выполнена" );
        }
    }

    /**
     * Выполнение градуировки датчиков Ax, Hyz, Ay, Hzx
     * Методика: установить азимут 180+-1 град, зенит 90+-1 град
     * установить угол поворота по показаниям датчика Az = 0+-0.5 град.
     * Снять характеристику пары датчиков Ax Hyz вращая раму относительно
     * зенитной оси
     * Задать угол датчика Az = 270+-0,5 град.
     * Аналогично снять характеристику пары датчиков Ay Hzx
     */

    public void doAxHyz() {

    }
    public void doCalibrationZenith() {
        int size = cbZenRotates.getItemCount();
        for ( int i = 0; i < size; i++ ) {
            cbZenRotates.setSelectedIndex( i );
            changeZenithTable();
            doCalibrationZenithPart();
        }

    }

    public void doCalibrationZenithPart() {
        clearMessages();
        selectedContainer = tables[1];
        lMessage1.setText( "Калибровка зенитного угла" );
        MeasureTable workTable = selectedContainer.measureTable;
        Double rotate = new Double( workTable.getProperty( "rotate" ) );
        doGoTo( 'z', rotate );
        doCalibrationTable( workTable );
        lMessage2.setText( "Калибровка выполнена" );

    }

    public void doCalibrationAzimuth() {
        int size = cbAzZeniths.getItemCount();
        for ( int i = 0; i < size; i++ ) {
            cbAzZeniths.setSelectedIndex( i );
            changeAzimuthTable();
            doCalibrationAzimuthPart();
        }
    }

    public void doCalibrationAzimuthPart() {
        clearMessages();
        selectedContainer = tables[2];
        lMessage1.setText( "Калибровка азимутального угла" );
        MeasureTable workTable = selectedContainer.measureTable;
        Double zenith = new Double( workTable.getProperty( "zenith" ) );
        doGoTo( 'y', zenith );
        lMessage2.setText( "Поиск максимального отклонения" );
        // перед выборкой угла поворота выставляем прибор на север
        doGoTo('x', new Double(0) );
        doFindMaxDeviation();
        doCalibrationTable( workTable );
        lMessage2.setText( "Калибровка выполнена" );
    }

    public void doChange() {
        selectedContainer = tables[tabbedTables.getSelectedIndex()];
        JTable table = selectedContainer.table;
        int row = table.getSelectedRow();
        MeasureChainEditor.edit( selectedContainer.measureTable.
                                 getChain( row ), viewer );
        selectedContainer.measureTable.calc();
        table.repaint();

    }

    public void doMeasure() {
        selectedContainer = tables[tabbedTables.getSelectedIndex()];
        MeasureTable workTable = selectedContainer.measureTable;
        JTable table = selectedContainer.table;
        int row = table.getSelectedRow();
        String name = workTable.getProperty( "name" );
        char plane = nameToPlane( name );
        doMeasure(workTable, row, plane);
        table.repaint();
    }
    /**
     * Поиск максимального отклонения азимута в зависимости от поворота
     */
    public void doFindMaxDeviation() {
        double max = 0;
        double rotate = 0;
        for( double r = 0; r < 332; r += 30) {
            doGoTo( 'z', new Double( r ) );
            double acc = doMeasureAccurate( 'x' );
            double tool = doMeasureTool('x');
            double d = Math.abs(acc - tool);
            if( d > max) {
                max = d;
                rotate = r;
            }
        }
        doGoTo('z' , new Double(rotate));

    }
    protected void startProcess() {
        execCommand(( Command ) cbDo.getSelectedItem() );
        System.out.println( "Process started" );
    }
    protected void execCommand(Command command) {
        if ( executer != null ) {
            if ( executer.isAlive() ) {
                return;
            }
        }
        executer = new CommandExecuter( command );
        //executer.setDaemon(true);
        //System.out.println( "Process started" );
        executer.start();

    }
    void stopProcess( ActionEvent e ) {
        if ( executer != null ) {
            if ( executer.isAlive() ) {
                try {
                    unit.stop();
                } catch ( Exception ex ) {
                }
                timeUpdater.stop();
                clearMessages();
                //executer.interrupt();
                executer.stop();
                //executer.destroy();

            }
        }

    }

    protected void initTable( JTable table ) {
        TableColumn column = null;
        JTableHeader header = table.getTableHeader();
        header.setVisible( true );
        for ( int i = 0; i < 3; i++ ) {
            //table.getColumn
        }

    }

    void updateTime() {
        lTimeStart.setText( TextUtil.dateToString( startTime, "HH:mm:ss" ) );
        //ng mil = System.currentTimeMillis();
        Date elapsed = new Date();
        long mil = elapsed.getTime() - startTime.getTime();
        //elapsed.set(Calendar.ZONE_OFFSET, 0);
        //elapsed.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        //elapsed.setTimeInMillis(mil);
        lTimeElapsed.setText( TextUtil.millisToString( mil ) );

    }

    void clearMessages() {
        lMessage1.setText( "" );
        lMessage2.setText( "" );
        lMessage3.setText( "" );

    }

    void switchOff() {
        if ( unit != null ) {
            unit.removeSignalListeners();
            unit.disconnect();
        }
    }

    void quit() {
        if ( JOptionPane.showConfirmDialog( this, "Выйти?",
                                            "Выход из модуля",
                                            JOptionPane.YES_NO_OPTION ) == 0 ) {
            switchOff();
            dispose();
        }

    }
    /*
    protected Vector buildCommandList() {
        char plane = 'z';
        MeasureTable workTable = tables[2].measureTable;
        int pointsCount = workTable.size();
        Vector commands = new Vector( 16 );
        MethodsCommand command = new MethodsCommand( "selectPlane",
            "Выбор плоскости " + plane );
        command.setArgument( new Character( plane ) );
        commands.add( command );
        for ( int i = 0; i < pointsCount; i++ ) {
            double value = workTable.getChain( i ).getReproductionValue();
            command = new MethodsCommand( "goToPoint",
                                          "Выход на точку " + value );
            command.setArgument( new Double( value ) );
            commands.add( command );
        }
        return commands;
    }*/

    void buildToolLabel() {
        StringBuffer str = new StringBuffer(
            "<html><table width=200 border=1><tr><th>" );
        str.append( toolAngles.azimuth.toString() );
        str.append( "</th><th>" );
        str.append( toolAngles.zenith.toString() );
        str.append( "</th><th>" );
        str.append( toolAngles.rotate.toString() );
        str.append( "</th></tr></table>" );
    }

    void switchConnect() {
        try {
            if ( !unit.isConnected() ) {
                unit.connect();
            }
        } catch ( Exception ex ) {
            UiUtils.showError( this,
                               "Не удалось подключиться: " + ex.getMessage() );
            //System.err.println( ex.getMessage() );
        }
        try {
            if ( !toolSource.isConnected() ) {
                toolSource.connect();
            }
        } catch ( Exception ex ) {
            UiUtils.showError( this,
                               "Не удалось подключиться к источку данных прибора: " + ex.getMessage() );
            //System.err.println( ex.getMessage() );
        }
    }

    void bConnect_actionPerformed( ActionEvent e ) {
        //bConnect.setSelected( unit.isConnected() );

        if ( unit.isConnected() ) {
            bConnect.setIcon( iconOn );
        } else {
            bConnect.setIcon( iconOff );
        }
    }

    private class CommandExecuter
        extends Thread {
        private AbstractCheckUnit unit;
        private Vector commandList;
        private Command command;
        public CommandExecuter( Command command ) {
            this.command = command;
            //this.unit = unit;
            //this.commandList = commandList;
        }

        public void run() {

            try {
                startTime = new Date();
                updateTime();
                timeUpdater.start();
                timeUpdater.setRepeats( true );
                command.execute();
                timeUpdater.stop();
            } catch ( Exception ex ) {
                System.err.println( ex.getCause().getMessage() );
            }
            finally {
                timeUpdater.stop();
            }
            //doCalibrationRotate();
            /*
                                      int size = commandList.size();
                                      for ( int i = 0; i < size; i++ ) {
                 MethodsCommand command = ( MethodsCommand ) commandList.get( i );
                                          try {
                                              command.invoke( unit );
                                          } catch ( Exception ex ) {
                              System.err.println( ex.getCause().getMessage() );
                                          }
                                      }*/
        }
    }


    void bSave_actionPerformed( ActionEvent e ) {
        DataFactory.saveMeasureDatas( datas );
    }

    void cbZenRotates_actionPerformed( ActionEvent e ) {
        changeZenithTable();
    }

    void bStart_actionPerformed( ActionEvent e ) {

    }

    void bConnect_stateChanged( ChangeEvent e ) {
        switchConnect();
    }

    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            quit();
            //System.exit( 0 );
        }
    }

    void bJoin_actionPerformed( ActionEvent e ) {
        try {
            doJoinRotates();
        } catch ( Exception ex ) {
        }
    }

    void cbAzZeniths_actionPerformed( ActionEvent e ) {
        changeAzimuthTable();
    }

    void bProtocol_actionPerformed(ActionEvent e) {
        try {
            String path = Zeus.getInstance().getProperty("report.path");
            FileWriter file = new FileWriter( path + "/" + datas.getToolType() + "_" + datas.getToolNumber()+ ".html");
            StringWriter out = new StringWriter(8192);
            IMProtocol prot = new IMProtocol( out );
            prot.generate(datas);
            out.flush();
            file.write(out.toString());
            file.close();
            HtmlViewer viewer = new HtmlViewer();
            viewer.setText(out.toString());
            viewer.view();
            out.close();
        } catch ( Exception ex ) {
        }

    }

}
