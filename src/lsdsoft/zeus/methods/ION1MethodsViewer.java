package lsdsoft.zeus.methods;


import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

//import com.borland.jbcl.layout.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.util.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.report.*;
import lsdsoft.zeus.ui.*;
//import bsh.*;
import java.net.*;
import java.text.*;
import com.lsdsoft.math.*;
import com.google.gson.*;


/**
 * <p>Title: Обозреватель методики калибровки для прибора ИОН-1</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Ural-Geo 2004-2010</p>
 * @author lsdsoft, de-nos
 * @version 0.9
 */

public class ION1MethodsViewer
    extends AbstractMethodsViewer
    implements SignalEventListener {

    class Command {
        protected Class[] params = {};

        String description;
        String name;
        String method;
        ION1MethodsViewer viewer;
        public Command( ION1MethodsViewer viewer, String method, String name,
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


    Command commandMeasure = new Command( this, "doMeasure", "замер в точке",
                                          "" );

    Command[] commands = {
        new Command( this, "doCalibrationFull", "калибровка полностью", "" ),
        new Command( this, "doCalibrationRotate", "калибровка угла поворота",
                     "" ),
        new Command( this, "doCalibrationZenith", "калибровка зенитного угла",
                     "" ),
        new Command( this, "doCalibrationZenithPart",
                     "калибровка зенитного угла частично", "" ),
        new Command( this, "doCalibrationAzimuth",
                     "калибровка азимутального угла", "" ),
        new Command( this, "doCalibrationAzimuthPart",
                     "калибровка азимутального угла частично", "" ),
        new Command( this, "doJoinRotates", "совмещение углов поворота", "" ),
    };
    ///////////////////////////////////////////////////////////////////////////
    class ChangeAction
        extends AbstractAction {
        public ChangeAction() {
            putValue( Action.NAME, "Изменить..." );
        }

        public void actionPerformed( ActionEvent e ) {
            doChange();
        }
    }


    Action changeAction = new ChangeAction();
    class MeasureAction
        extends AbstractAction {
        public MeasureAction() {
            putValue( Action.NAME, "Замер" );
        }

        public void actionPerformed( ActionEvent e ) {
            execCommand( commandMeasure );
        }
    }


    Action measureAction = new MeasureAction();
    ION1MethodsViewer viewer = this;
    MouseAdapter madapter = new MouseAdapter() {
        public void mouseClicked( MouseEvent e ) {
            JTable table = ( JTable )e.getSource();

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

    MeasureDatas datas = null;
    AbstractCheckUnit unit = null;
    ChannelDataSource toolSource = null;
    InclinometerValues toolValues = new InclinometerValues();
    InclinometerValues accValues = new InclinometerValues();
    private boolean toCheckToolNumber = false;

    JTabbedPane tabbedTables = new JTabbedPane();
    JPanel panelZ = new JPanel();
    JPanel panelY = new JPanel();
    JPanel panelX = new JPanel();
    // цифровые индикаторы
    DigitalDisplay displayAX = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayAY = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayAZ = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayTX = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayTY = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayTZ = new DigitalDisplay( 6, 2 );
    ImageIcon iconOff = Zeus.createImageIcon( "images/disconnect.png" );
    ImageIcon iconOn = Zeus.createImageIcon( "images/connect.png" );
    ImageIcon iconDisk = Zeus.createImageIcon( "images/disk.32.png" );
    ImageIcon iconNote = Zeus.createImageIcon( "images/document.32.png" );
    ImageIcon iconCert = Zeus.createImageIcon( "images/certificate.32.png" );
    protected static final ImageIcon ICON_GEARS[] = {
        Zeus.createImageIcon( "images/off.png" ),
        Zeus.createImageIcon( "images/on.png" ),
        //Zeus.createImageIcon( "images/gear_3.png" ),
        //Zeus.createImageIcon( "images/gear_4.png" ),
    };
    protected int gearIndex = 0;

    JComboBox cbZenRotates = new JComboBox();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JComboBox cbAzZeniths = new JComboBox();
    TableContainer tables[] = {
        new TableContainer(), new TableContainer(), new TableContainer()};
    CommandExecuter executer;
    protected JToolBar toolBar = new JToolBar();
    private ToolButton bSave = new ToolButton("Сохранить", iconDisk);
    private ToolButton bConnect = new ToolButton("Подключение", ICON_GEARS[0]);
    private ToolButton bProtocol = new ToolButton("Протокол", iconNote);
    private ToolButton bSertificate = new ToolButton("Сертификат", iconCert);
    JPanel panelDisplay = new JPanel();
    JPanel panelHeader = new JPanel();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JLabel jLabel8 = new JLabel();
    JPanel panelAcc = new JPanel();
    JLabel jLabel3 = new JLabel();
    JPanel panelTool = new JPanel();
    JLabel jLabel4 = new JLabel();
    TitledBorder titledBorder1;
    Border border1;
    TitledBorder titledBorder2;
    Border border2;
    TitledBorder titledBorder3;
    ButtonGroup bgWhat = new ButtonGroup();
    TitledBorder titledBorder4;
    JLabel lExecute = new JLabel();
    JComboBox cbDo = new JComboBox();
    JButton bStart = new JButton();
    JPanel panelTask = new JPanel();
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
    DecimalFormat df;
    final char PLUS_MINUS = '\u0177';
    private JLabel operatorLabel = new JLabel("Калибровщик");
    private JTextField operatorTextField = new JTextField();
    private JLabel temperatureLabel = new JLabel("Темп-ра воздуха:");
    private JTextField temperatureTextField = new JTextField();
    public class ToolButton extends JButton {
        ToolButton(String title, Icon icon) {
            super(title, icon);
            setVerticalTextPosition(AbstractButton.BOTTOM);
            setHorizontalTextPosition(AbstractButton.CENTER);
            setIconTextGap(0);
            setPreferredSize(new Dimension (96,54));
        }
    };

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
            table.getTableHeader().setPreferredSize(new Dimension(700,40));
            table.addMouseListener( madapter );
            //tables[i].table.setCellSelectionEnabled(false);
            table.setSelectionMode( ListSelectionModel.
                                    SINGLE_SELECTION );
            table.add( tablePopup );
            scrollPane.setAutoscrolls( true );
            scrollPane.getViewport().add( table, null );

        }
    }


    public ION1MethodsViewer() {
        try {
            df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
            df.applyPattern("#0.00");
            System.out.print("Az="+PLUS_MINUS);
            //jbInit();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void setProperties( Properties props ) {
        properties = ( Properties )props.clone();
    }

    public void setMeasureDatas( MeasureDatas datas ) {
        this.datas = datas;
        datas.calc();
    }

    protected void selectTables() {
        Vector tables = datas.selectTables( "type", "azimuth" );
        if ( tables.size() > 0 ) {
            this.tables[2].measureTable = ( MeasureTable )tables.get( 0 );
        }
        tables = datas.selectTables( "type", "zenith" );
        if ( tables.size() > 0 ) {
            this.tables[1].measureTable = ( MeasureTable )tables.get( 0 );
        }
        tables = datas.selectTables( "type", "rotate" );
        if ( tables.size() > 0 ) {
            this.tables[0].measureTable = ( MeasureTable )tables.get( 0 );
        }
    }

    protected void initZenithTab() {
        Vector tables = datas.selectTables( "type", "zenith" );
        int size = tables.size();
        cbZenRotates.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable )tables.get( i );
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
            MeasureTable table = ( MeasureTable )tables.get( i );
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
        String value = ( String )cbAzZeniths.getSelectedItem();
        Vector tables = datas.selectTypedTables( "azimuth", "zenith", value );
        if ( tables.size() > 0 ) {
            this.tables[2].measureTable = ( MeasureTable )tables.get( 0 );
        }
        this.tables[2].init();
    }

    protected void changeZenithTable() {
        String value = ( String )cbZenRotates.getSelectedItem();
        Vector tables = datas.selectTypedTables( "zenith", "rotate", value );
        if ( tables.size() > 0 ) {
            this.tables[1].measureTable = ( MeasureTable )tables.get( 0 );
        }
        this.tables[1].init();
    }

    private void redrawToolValues() {
        if ( toolSource.isConnected() ) {
            displayTX.render( toolValues.azimuth.value );
            displayTY.render( toolValues.zenith.value );
            displayTZ.render( toolValues.rotate.value );
        } else {
            displayTX.renderClear();
            displayTY.renderClear();
            displayTZ.renderClear();
        }
        displayTX.repaint();
        displayTY.repaint();
        displayTZ.repaint();
    }

    private void redrawAccurateValues() {
        if ( unit.isConnected() ) {
            displayAX.render( accValues.azimuth.value );
            displayAY.render( accValues.zenith.value );
            displayAZ.render( accValues.rotate.value );
        } else {
            displayAX.renderClear();
            displayAY.renderClear();
            displayAZ.renderClear();
        }
        displayAX.repaint();
        displayAY.repaint();
        displayAZ.repaint();
    }
    public String formatValue(Value val) {
        String s;
        s = df.format(val.value) + " @ " + df.format( val.delta );
        return s;
    }
    private int counter = 0;
    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            //System.out.print( "T");
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = toolSource.getChannel( "angles" );
                if ( chan == null ) {
                    chan = toolSource.getChannel( "values" );
                }
                if ( chan != null ) {
                    String id = toolSource.getProperty("id");
                    //System.out.println("# ID = " + id);
                    //System.out.println("### tool"+ counter++);
                    System.out.print("### tool"+ id);
                    Value val = chan.getValue( 0 ).getAsValue();
                    System.out.print("Az=" + formatValue(val)+"; ");
                    val = chan.getValue( 1 ).getAsValue();
                    System.out.print("Zn=" + formatValue(val)+"; ");
                    val = chan.getValue( 2 ).getAsValue();
                    System.out.println("Rt=" + formatValue(val)+"; ");


                    //toolAngles.zenit = chan.getValue( 1 ).angle;
                    //toolAngles.rotate = chan.getValue( 2 ).angle;
                    toolValues.azimuth = chan.getValue( 0 ).getAsValue();
                    toolValues.zenith = chan.getValue( 1 ).getAsValue();
                    toolValues.rotate = chan.getValue( 2 ).getAsValue();

                    redrawToolValues();
                }
                if(toCheckToolNumber) {
                    chan = toolSource.getChannel( "sensors" );
                    if ( chan != null ) {
                        // берем из канала датчиков 4-й и 5-й элемент содержащие номер прибора
                        // 4-й - старший байт, 5-й - младший
                        int number = chan.getValue( 4 ).getAsInteger() *
                            0x10000 +
                            chan.getValue( 5 ).getAsInteger();
                        System.out.println( "#" + number );
                        String strNum = "" + number;
                        if( !strNum.equals(datas.getToolNumber()) ) {
                            System.out.println( "invalid tool number!!!" );
                            toolSource.setProperty("tool.number", strNum);
                            datas.getWorkState().setToolNumber(strNum);
                            //datas.getProperties().setProperty("tool.number", strNum);
                            updateTitle();
                        }
                    }
                }
            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Нет связи с прибором" );
            }
            //System.out.print( "t");

        } else
        if ( src.equals( unit ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                gearIndex = (gearIndex+1)%ICON_GEARS.length;
                bConnect.setIcon(ICON_GEARS[gearIndex]);
                Channel chan = unit.getChannel( "angles" );
                if ( chan != null ) {
                    accValues.azimuth = chan.getValue( 0 ).getAsValue();
                    //System.out.println(df.format( chan.getValue( 0 ).getAsDouble()));
                    accValues.zenith = chan.getValue( 1 ).getAsValue();
                    accValues.rotate = chan.getValue( 2 ).getAsValue();
                    redrawAccurateValues();
                }

            } else
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Нет связи с установкой" );
            }

        }

    }

    public void start() {
        boolean quit = false;
        try {
            Zeus zeus = Zeus.getInstance();
            unit = DataFactory.createCalibrationRigForChannel( "angles" );
            unit.addSignalListener( this );
            boolean isNewWork = Boolean.valueOf( zeus.getProperty( Zeus.PROP_WORK_NEW ) ).
                booleanValue();
            if ( isNewWork ) {
                datas = DataFactory.createMeasureDatas( DataFactory.getMethods(
                    "ion1_test.xml" ) );
                datas.setWorkState( workState );
            } else {
                String id = zeus.getProperty( Zeus.PROP_WORK_ID );
                datas = DataFactory.loadMeasureDatas( workState.getToolType(),
                                                      workState.getToolNumber(),
                                                      zeus.getProperty( Zeus.
                    PROP_WORK_ID ) );
                workState = datas.getWorkState();

            }
            setErrorLimits();
            datas.ensurePointsCount( 4 );
            datas.calc();
            selectTables();
            buildTables();
            initZenithTab();
            initAzimuthTab();

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
            String toolType = workState.getToolType();
            if ( toolType.equals( "ion2" ) ||
                 toolType.equals( "ion3" ) ) {
                // если прибор ИОН-2, надо проверить правильность номера прибора
                toCheckToolNumber = true;
            }
            if ( toolType.equals( "ion1") && isNewWork ) {
                String td = toolSource.getProperty( "table.date" );
                datas.getProperties().setProperty( "table.date",
                    td );
            }
            jbInit();
            UiUtils.toScreenCenter( this );
            selectedContainer = tables[0];
            tabbedTables.setSelectedIndex( 0 );
            redrawAccurateValues();
            redrawToolValues();
            /*
                         // init shell vars
                         shell.set( "zeus", zeus );
                         shell.set( "unit", unit );
                         //shell.eval( "importCommands(\"/data/commands/\")" );
                         //ClassManager.
                         //shell.getClassManager().addClassPath(new URL("file", "", "d:/zeus/data"));
                         //URL url = shell.getClassManager().getResource( "test.bsh" );
                         shell.getNameSpace().importCommands( "/commands" );
                         //shell.eval("debug()");
                         shell.source("data/commands/Test1.bsh");
                         shell.eval( "Test1.print1()" );
                         Object o = shell.get( "coms" );
                         if ( o instanceof String ) {

                         }
             */

        } catch ( Exception ex ) {
            StackTraceElement[] st = ex.getStackTrace();
            String location = st[0].getClassName()+"."+st[0].getLineNumber();
            UiUtils.showError( this, ex.getClass().getName() +": "+ ex.getLocalizedMessage() +"\r\n"+location);
            ex.printStackTrace();
        }
        if ( !quit ) {
            this.setVisible( true );
        }
        if ( Zeus.getInstance().getProperty( "debug", "off" ).equals( "on" ) ) {
            JOptionPane.showMessageDialog( this, "Включен режим отладки.",
                                           "Информация",
                                           JOptionPane.INFORMATION_MESSAGE );
        }
    }

    private void updateTitle() {
        String title = "Калибровка прибора " + workState.getToolName() + " № " +
                       workState.getToolNumber();
        if(workState.getToolType().equals("ion1")) {
            String fn = toolSource.getProperty( "table.filename" );
            if(fn != null ) {
            title += " | " + fn +
                " от " + toolSource.getProperty( "table.date" );
            } else  {
                title += " | Без таблицы!";
            }
        }
        this.setTitle( title );

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
        this.setSize( 800, 560 );
        this.setResizable(false);
        this.setState( Frame.NORMAL );
        updateTitle();
        //this.setExtendedState( 6 );
        tabbedTables.setBounds( new Rectangle( 9, 64, 478, 274 ) );
        //tabbedTables.addTab("Угол поворота", table);
        Font font = new Font( "Dialog", 0, 14 );
        // init tableX
        panelY.setMaximumSize( new Dimension( 500, 500 ) );
        panelY.setLayout( null );
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
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.LEFT);
        fl.setHgap(2);
        toolBar.setEnabled( true );
        //jToolBar1.setAlignmentY((float) 0.5);
        toolBar.setBorder( null );
        toolBar.setFloatable( false );
        toolBar.setLayout(fl);
        toolBar.setBounds( new Rectangle( 1, 1, 780, 64 ) );
        bSave.setVerticalTextPosition(AbstractButton.BOTTOM);
        bSave.setHorizontalTextPosition(AbstractButton.CENTER);
        //b1.setMnemonic(KeyEvent.VK_D);
        bSave.setActionCommand("save");
        bSave.setToolTipText("Сохранить данные");
        bSave.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bSave_actionPerformed( e );
            }
        } );
        // bConnect
        bConnect.setToolTipText( "Подключиться к установке" );
        bConnect.setActionCommand( "connect" );
        bConnect.setContentAreaFilled( true );
        bConnect.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                switchConnect();
            }
        } );



        panelDisplay.setBorder( titledBorder2 );
        panelDisplay.setBounds( new Rectangle( 10, 346, 662, 150 ) );
        jLabel5.setHorizontalAlignment( SwingConstants.CENTER );
        jLabel5.setText( "АЗИМУТ" );
        jLabel5.setBounds( new Rectangle( 58, 7, 159, 15 ) );
        jLabel6.setHorizontalAlignment( SwingConstants.CENTER );
        jLabel6.setText( "ЗЕНИТ" );
        jLabel6.setBounds( new Rectangle( 243, 7, 162, 15 ) );
        jLabel7.setHorizontalAlignment( SwingConstants.CENTER );
        jLabel7.setText( "ВИЗИР" );
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
        displayAX.setLocation( new java.awt.Point( 100, 310 ) );
        displayAX.setBounds( new Rectangle( 79, 1, 162, 35 ) );
        displayAY.setLocation( new java.awt.Point( 200, 380 ) );
        displayAY.setBounds( new Rectangle( 273, 1, 162, 35 ) );
        displayAZ.setLocation( new java.awt.Point( 380, 380 ) );
        displayAZ.setBounds( new Rectangle( 466, 2, 162, 35 ) );
        panelTool.setBorder( null );
        panelTool.setMinimumSize( new Dimension( 270, 30 ) );
        panelTool.setPreferredSize( new Dimension( 640, 40 ) );
        panelTool.setLayout( null );
        //panelTool.setLayout(boxLayout22);
        displayTX.setLocation( new java.awt.Point( 20, 420 ) );
        displayTX.setBounds( new Rectangle( 80, 3, 162, 35 ) );
        jLabel4.setPreferredSize( new Dimension( 90, 15 ) );
        jLabel4.setHorizontalAlignment( SwingConstants.LEFT );
        jLabel4.setHorizontalTextPosition( SwingConstants.LEFT );
        jLabel4.setIconTextGap( 4 );
        jLabel4.setText( "По прибору" );
        jLabel4.setBounds( new Rectangle( 2, 13, 84, 15 ) );
        displayTY.setLocation( new java.awt.Point( 240, 420 ) );
        displayTY.setBounds( new Rectangle( 273, 3, 162, 35 ) );
        displayTZ.setLocation( new java.awt.Point( 460, 420 ) );
        displayTZ.setBounds( new Rectangle( 466, 3, 162, 35 ) );
        panelHeader.setPreferredSize( new Dimension( 600, 30 ) );
        jLabel8.setBounds( new Rectangle( 0, 14, 0, 0 ) );
        //bConnect.setBorder( BorderFactory.createRaisedBevelBorder() );
        lExecute.setText( "Выполнить:" );
        lExecute.setBounds( new Rectangle( 502, 70, 94, 15 ) );
        cbDo.setBounds( new Rectangle( 502, 92, 275, 24 ) );
        bStart.setText( "Пуск" );
        bStart.setBounds( new Rectangle( 502, 127, 130, 30 ) );
        //bStart.setPreferredSize( new Dimension( 100, 30 ) );
        bStart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                startProcess();
            }
        } );
        bStop.setBounds( new Rectangle( 647, 127, 130, 30 ) );
        //bStop.setPreferredSize( new Dimension( 84, 30 ) );
        bStop.setText( "Останов" );
        bStop.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stopProcess( e );
            }
        } );
        panelTask.setBorder( BorderFactory.createLineBorder( Color.black ) );
        panelTask.setBounds( new Rectangle( 502, 170, 275, 168 ) );
        panelTask.setLayout( null );
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
        //miMeasure.setText( "Замер" );
        miChange.setAction( changeAction );
        miMeasure.setAction( measureAction );
        bProtocol.setText( "Протокол" );
        bSertificate.setText("Сертификат");
        bProtocol.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bProtocol_actionPerformed( e );
            }
        } );
        bSertificate.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bSertificate_actionPerformed( e );
            }
        } );
        operatorTextField.setText(Zeus.getInstance().getProperty("operator"));
        operatorTextField.setPreferredSize(new Dimension(160, 28));
        //operatorTextField.deletesetCaretPosition();
        operatorTextField.addKeyListener(new KeyListener(){
            public void keyTyped( KeyEvent e ) {}
            public void keyPressed( KeyEvent e ) {}
            public void keyReleased( KeyEvent e ) {
                Zeus.getInstance().setProperty("operator_edited", operatorTextField.getText());
            }
        });
        temperatureTextField.setText(Zeus.getInstance().getProperty("temperature"));
        temperatureTextField.setPreferredSize(new Dimension(40, 28));
        temperatureTextField.addKeyListener(new KeyListener(){
            public void keyTyped( KeyEvent e ) {}
            public void keyPressed( KeyEvent e ) {}
            public void keyReleased( KeyEvent e ) {
                Zeus.getInstance().setProperty("temperature_edited", temperatureTextField.getText());
            }
        });
        Zeus.getInstance().setProperty("operator_edited", Zeus.getInstance().getProperty("operator",""));
        Zeus.getInstance().setProperty("temperature_edited", Zeus.getInstance().getProperty("temperature",""));
        tabbedTables.add( panelZ, "Угол поворота" );

        tables[2].scrollPane.setBounds( new Rectangle( 0, 41, 473, 199 ) );
        tables[1].scrollPane.setBounds( new Rectangle( 0, 41, 473, 179 ) );

        panelX.add( tables[2].scrollPane, null );
        panelY.add( tables[1].scrollPane, null );
        panelZ.add( tables[0].scrollPane, BorderLayout.CENTER );

        tabbedTables.add( panelY, "Зенитный угол" );
        panelY.add( jLabel1, null );
        panelY.add( cbZenRotates, null );
        tabbedTables.add( panelX, "Азимутальный угол" );
        //scrollPaneY.getViewport().add( tables[1].table, null );

        panelX.add( jLabel2, null );
        panelX.add( cbAzZeniths, null );
        this.getContentPane().add( panelTask, null );
        panelTask.add( jLabel10, null );
        panelTask.add( jLabel11, null );
        panelTask.add( lTimeStart, null );
        panelTask.add( lTimeElapsed, null );
        this.getContentPane().add( bStart, null );
        this.getContentPane().add( cbDo, null );
        this.getContentPane().add( lExecute, null );
        this.getContentPane().add( panelDisplay, null );
        panelHeader.add( jLabel8, null );
        panelHeader.add( jLabel7, null );
        panelHeader.add( jLabel6, null );
        panelHeader.add( jLabel5, null );
        panelDisplay.add( panelHeader, null );
        panelDisplay.add( panelAcc, null );
        panelAcc.add( displayAY, null );
        panelAcc.add( displayAX, null );
        panelAcc.add( displayAZ, null );
        panelAcc.add( jLabel3, null );
        panelDisplay.add( panelTool, null );
        panelTool.add( displayTX, null );
        panelTool.add( displayTY, null );
        panelTool.add( displayTZ, null );
        panelTool.add( jLabel4, null );

        displayAX.renderClear();
        displayAY.renderClear();
        displayAZ.renderClear();

        this.getContentPane().add( toolBar, null );
        toolBar.add( bSave, null );
        //jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        toolBar.add( bConnect, null );
        //jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        toolBar.add( bProtocol, null );
        toolBar.add( bSertificate, null );
        //jToolBar1.addSeparator( new Dimension( 20, 10 ) );
        toolBar.add( operatorLabel, null );
        //jToolBar1.addSeparator( new Dimension( 5, 10 ) );
        toolBar.add( operatorTextField, null );
        //jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        toolBar.add( temperatureLabel, null );
        //jToolBar1.addSeparator( new Dimension( 5, 10 ) );
        toolBar.add( temperatureTextField, null );
        this.getContentPane().add( tabbedTables, null );
        panelTask.add( lMessage2, null );
        panelTask.add( lMessage3, null );
        panelTask.add( lMessage1, null );
        this.getContentPane().add( bStop, null );
        tablePopup.add( miChange );
        tablePopup.add( miMeasure );

        //scrollPaneX.getViewport().add( tables[0].table, null );
        //scrollPaneZ.getViewport().add( tables[2].table, null );
        tabbedTables.setSelectedComponent( panelX );
        buildToolLabel();
        for ( int i = 0; i < commands.length; i++ ) {
            cbDo.addItem( commands[i] );
        }
        clearMessages();
        this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    }

    void setErrorLimits() {
        System.out.println("Setting limits ");
        //ION1Informer informer = new ION1Informer();
        int size = datas.size();
        Channel chan = new Channel( "angles", 0, 3 );
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = datas.getTable( i );
            char plane = nameToPlane( table.getProperty( "type" ) );
            int index = planeToIndex( plane );
            String s = table.getProperty( "zenith" );
            if ( s != null ) {
                try {
                    chan.getValue( 1 ).setAsDouble( Double.parseDouble( s ) );
                } catch ( Exception ex ) {
                }
            }
            for ( int ti = 0; ti < table.size(); ti++ ) {
                MeasureChain chain = table.getChain( ti );
                chan.getValue( index ).setAsDouble( chain.
                    getReproductionValue() );
                int points = chain.size();
                for(int pi = 0; pi < points; pi++) {
                    MeasurePoint point = chain.getPoint(pi);
                    if ( !point.isEmpty() ) {
                        point.tool.delta = ION1Informer.getErrorLimits( chan )[
                            index];
                        System.out.println( "Setting tool error " +
                                            point.tool.delta );
                        if ( point.accurate.delta == 0 ) {
                            point.accurate.delta = ( ( UAKSI2CheckUnit )unit ).
                                getErrorLimit( plane, null );
                            System.out.println( "Setting accurate error " +
                                                point.accurate.delta );
                        }
                    }
                }
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
            Value value = doMeasureTool( plane );
            if ( value.value > 350 ) {
                value.value -= 360;
            }
            System.out.print( '-' );
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

    Value doMeasureAccurate( char plane ) {
        //double val = 0;
        //double err = 0;
        int subchan = planeToIndex( plane );
        Channel chan = unit.getChannel( "angles" );
        System.out.print('_');
        unit.waitNewData();
        Value val = chan.getValue(subchan).getAsValue();
        Value ret = new Value(val.value, val.delta);
        return ret;
        /*
        Channel chan2 = unit.getChannel( "errnorm" );
        for ( int i = 0; i < 4; i++ ) {
            unit.waitNewData();
            val += chan.getValue( subchan ).getAsDouble();
        }
        if(chan2 != null) {
            err = chan2.getValue(subchan).getAsDouble();
        }
        val /= 4.0;
        Value value = new Value(val, err);
        return value;
        */
    }

    Value doMeasureTool( char plane ) {
        double value = 0;
        double err = 0;
        int subchan = planeToIndex( plane );
        Channel chan = toolSource.getChannel( "angles" );
        Channel chan2 = toolSource.getChannel( "errnorm" );
        if ( chan == null ) {
            /** @todo throw exce */
            return new Value();
        }
        // ожидание показаний с минимальным среднеквадратичным отклонением
        MedianFilter mmf = new MedianFilter(4);
        double sko = 0;
        do {
            System.out.print( ':' );
            //toolSource.waitNewData();
            synchronized (toolSource) {
            try {
                toolSource.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            }
            double v1 = chan.getValue( subchan ).getAsDouble();
            if ( v1 > 350 ) {
                v1 -= 360;
            }
            mmf.add(v1);
            sko = mmf.calcSko();
            System.out.println("#sko " + v1 + ":"+sko);
            if( chan2 != null) {
                err = chan2.getValue( subchan ).getAsDouble();
            }
        }while((sko>err/4) || mmf.getSize()<4);
        return new Value(mmf.average(), err);
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
        ang.rotate.setAngle( doMeasureAccurate( 'z' ).value - doMeasureTool( 'z' ).value );
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
        Value current = doMeasureAccurate( plane );
        if ( Math.abs( current.value - firstPoint.doubleValue() ) <
             Math.abs( current.value - lastPoint.doubleValue() ) ) {
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
                Thread.sleep( 10000 );
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
        doCalibrationRotate();
        doCalibrationZenith();
        doCalibrationAzimuth();
    }

    public void doCalibrationRotate() {
        selectedContainer = tables[0];
        lMessage1.setText( "Калибровка угла поворота" );
        doGoTo( 'y', new Double( 4.0 ) );
        MeasureTable workTable = selectedContainer.measureTable;
        doCalibrationTable( workTable );
        lMessage2.setText( "Калибровка выполнена" );
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
        doGoTo( 'x', new Double( 0 ) );
        double rotate = doFindMaxDeviation();
        System.out.println("Max deviation at "+rotate);
        //@todo:
        workTable.setProperty("rotate", Double.toString(rotate));
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
        doMeasure( workTable, row, plane );
        table.repaint();
    }

    /**
     * Поиск максимального отклонения азимута в зависимости от поворота
     */
    public double doFindMaxDeviation() {
        double max = 0;
        double rotate = 0;
        for ( double r = 0; r < 332; r += 30 ) {
            doGoTo( 'z', new Double( r ) );
            Util.delay( 8000 );
            double acc = doMeasureAccurate( 'x' ).value;
            double tool = doMeasureTool( 'x' ).value;
            double d = Math.abs( acc - tool );
            if ( d > max ) {
                max = d;
                rotate = r;
            }
        }
        doGoTo( 'z', new Double( rotate ) );
        return rotate;
    }

    protected void startProcess() {
        execCommand( ( Command )cbDo.getSelectedItem() );
        System.out.println( "Process started" );
    }

    protected void execCommand( Command command ) {
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
            //unit.removeAllListeners();
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
        str.append( toolValues.azimuth.toString() );
        str.append( "</th><th>" );
        str.append( toolValues.zenith.toString() );
        str.append( "</th><th>" );
        str.append( toolValues.rotate.toString() );
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
                "Не удалось подключиться к источку данных прибора: " +
                ex.getMessage() );
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
        Gson gs = new Gson();
        String s = gs.toJson(datas);
        System.out.println(s);
        datas.getWorkState().start();
        MeasureDatas md = gs.fromJson(s, MeasureDatas.class );
        DataFactory.saveMeasureDatas( md );
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

    void bProtocol_actionPerformed( ActionEvent e ) {
        try {
            String path = Zeus.getInstance().getProperty( "report.path" );
            FileWriter file = new FileWriter( path + "/" + datas.getToolType() +
                                              "_" + datas.getToolNumber() +
                                              ".html" );
            StringWriter out = new StringWriter( 8192 );
            IMProtocol prot = new IMProtocol( out );
            prot.generate( datas );
            out.flush();
            file.write( out.toString() );
            file.close();
            HtmlViewer viewer = new HtmlViewer();
            viewer.setText( out.toString() );
            viewer.view();
            out.close();
        } catch ( Exception ex ) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    void bSertificate_actionPerformed(ActionEvent e) {
        try {
            String path = Zeus.getInstance().getProperty("report.path");
            String filename =  path + "/" + datas.getToolType() + "_" + datas.getToolNumber()+ "s.html";
            FileOutputStream outs = new FileOutputStream( filename );
            OutputStreamWriter osw = new OutputStreamWriter( outs, "Windows-1251" );
            IMSertificate sert = new IMSertificate( osw );
            sert.generate(datas);
            outs.flush();
            osw.close();

            HtmlViewer viewer = new HtmlViewer();
            viewer.loadText( filename );
            viewer.view();
            outs.close();
        } catch ( Exception ex ) { }
  }

}
