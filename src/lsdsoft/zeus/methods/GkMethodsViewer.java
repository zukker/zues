package lsdsoft.zeus.methods;


import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

//import com.borland.jbcl.layout.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.ui.*;
import javax.swing.event.*;
import java.lang.reflect.*;
import javax.swing.Timer;
import lsdsoft.util.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.report.*;
import java.io.*;
import com.lsdsoft.math.*;


/**
 * <p>Title: Обозреватель методики калибровки для приборов ИММН-36, -60, -73А</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class GkMethodsViewer
    extends BaseMethodsViewer
    implements SignalEventListener {

    // время интегрирования в секундах
    protected int TIME_INTEGRATION = 120;

    Command commandMeasure = new Command( this, "doMeasure", "замер в точке", "" );

    Command[] commands = {
        new Command( this, "doCalibrationFull", "калибровка полностью", "" ),
        new Command( this, "doFindMarker",   "поиск метки", "" ),
        new Command( this, "doMeasureBack",  "измерение фона", "" ),
        new Command( this, "doCalibration1", "калибровка в точке 1", "" ),
        new Command( this, "doCalibration2", "калибровка в точке 2", "" ),
        new Command( this, "doCalibration3", "калибровка в точке 3", "" ),
        new Command( this, "doCalibration4", "калибровка в точке 4", "" ),
        new Command( this, "doCalibration5", "калибровка в точке 5", "" ),
        new Command( this, "doCalibration6", "калибровка в точке 6", "" ),
        new Command( this, "doGoToSafePoint", "выход на безопасную точку", "" ),

    };
    ///////////////////////////////////////////////////////////////////////////
    Command[] commandsGrad = {
        new Command( this, "doCalibrationFull", "градуировка полностью", "" ),
        new Command( this, "doFindMarker", "поиск метки", "" ),
        new Command( this, "doMeasureBack", "измерение фона", "" ),
        new Command( this, "doCalibration1", "измерение в точке 1", "" ),
        new Command( this, "doCalibration2", "измерение в точке 2", "" ),
        new Command( this, "doCalibration3", "измерение в точке 3", "" ),
        new Command( this, "doCalibration4", "измерение в точке 4", "" ),
        new Command( this, "doCalibration5", "измерение в точке 5", "" ),
        new Command( this, "doCalibration6", "измерение в точке 6", "" ),
        new Command( this, "doGoToSafePoint", "выход на безопасную точку", "" ),
        new Command( this, "doBuildGrad", "расчет коэффициента", "" ),
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
    //ION1MethodsViewer viewer = this;

    int selectedCommand = 1;
    //InklinometerAngles toolAngles = new InklinometerAngles();
    //InklinometerAngles accAngles = new InklinometerAngles();
    JTabbedPane tabbedTables = new JTabbedPane();
    JPanel panelZ = new JPanel();
    // цифровые индикаторы
    DigitalDisplay displayAX = new DigitalDisplay( 5, 1 );
    DigitalDisplay displayAY = new DigitalDisplay( 5, 1 );
    //DigitalDisplay displayAZ = new DigitalDisplay( 6, 1 );
    DigitalDisplay displayTX = new DigitalDisplay( 5, 0 );
    DigitalDisplay displayTY = new DigitalDisplay( 5, 1 );
    //DigitalDisplay displayTZ = new DigitalDisplay( 6, 0 );
    ImageIcon iconOff = Zeus.createImageIcon( "images/conn_off.png" );
    ImageIcon iconOn = Zeus.createImageIcon( "images/conn_on.png" );

    //JTable tableX;
    //JTable tableY;
    //JTable tableZ;
    //JComboBox cbZenRotates = new JComboBox();
    //JLabel jLabel1 = new JLabel();
    //JLabel jLabel2 = new JLabel();
    //JComboBox cbAzZeniths = new JComboBox();
    private TableContainer tables[] = {
        new TableContainer(), new TableContainer(), new TableContainer()};
    private CommandExecuter executer;
    JToolBar jToolBar1 = new JToolBar();
    JButton bSave = new JButton();
    JPanel panelDisplay = new JPanel();
    JPanel panelHeader = new JPanel();
    JLabel lBackValue = new JLabel("0");
    JLabel lCoef = new JLabel("K=0");
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JLabel jLabel8 = new JLabel();
    JPanel panelAcc = new JPanel();
    JLabel jLabel3 = new JLabel();
    JImage imgDigAX = new JImage( displayAX.getImage() );
    JImage imgDigAY = new JImage( displayAY.getImage() );
    //JImage imgDigAZ = new JImage( displayAZ.getImage() );
    JPanel panelTool = new JPanel();
    JImage imgDigTX = new JImage( displayTX.getImage() );
    JLabel jLabel4 = new JLabel();
    JImage imgDigTY = new JImage( displayTY.getImage() );
    //JImage imgDigTZ = new JImage( displayTZ.getImage() );
    //VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    //BoxLayout2 boxLayout21 = new BoxLayout2();
    //BoxLayout2 boxLayout22 = new BoxLayout2();
    TitledBorder titledBorder1;
    Border border1;
    TitledBorder titledBorder2;
    JButton bConnect = new JButton();
    Border border2;
    TitledBorder titledBorder3;
    private ButtonGroup bgWhat = new ButtonGroup();
    private TitledBorder titledBorder4;
    private JLabel jLabel9 = new JLabel();
    private JComboBox cbDo = new JComboBox();
    private JButton bStart = new JButton();
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel10 = new JLabel();
    private JLabel jLabel11 = new JLabel();
    JLabel lTimeStart = new JLabel();
    JLabel lTimeElapsed = new JLabel();
    JLabel lMessage1 = new JLabel();
    JLabel lMessage2 = new JLabel();
    JLabel lMessage3 = new JLabel();
    JButton bStop = new JButton();
    TableContainer selectedContainer;
    //JPopupMenu tablePopup = new JPopupMenu();
    //JMenuItem miChange = new JMenuItem();
    //JMenuItem miMeasure = new JMenuItem();
    JButton bProtocol = new JButton();
    // среднее значение выходного сигнала прибора
    double toolCode = 0;
    // текущее значение физической величины по прибору
    Value toolValue = new Value();
    // значение выходного сигнала при измерении фона
    double toolBack;
    // коэффициент преобразования (градуировочная характеристика)
    double gradCoef = 0;
    // текущие эталонные значения МЭД
    Value accValue = new Value();
    // номер текущей секции барабана в десятых долях
    double accSection = 0;
    int workMode = WorkMode.MODE_CALIB;
    String toolChannel;


    public GkMethodsViewer() {
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
        Vector tables = datas.selectTables( "type", "gk" );
        this.tables[0].measureTable = ( MeasureTable ) tables.get( 0 );
    }

    protected void initZenithTab() {
        Vector tables = datas.selectTables( "type", "zenith" );
        int size = tables.size();
        //cbZenRotates.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "rotate" );
            if ( value != null ) {
        //        cbZenRotates.addItem( value );
            }
        }

    }


    protected void buildTables() {
        for ( int i = 0; i < 1; i++ ) {
            tables[i].init();
            tables[i].setTableModel(new GkMeasureTableModel(tables[i].measureTable));
            tables[i].table.addMouseListener( mouseAdapter );
            //tables[i].table.setCellSelectionEnabled(false);
            tables[i].table.setSelectionMode( ListSelectionModel.
                                              SINGLE_SELECTION );
            tables[i].table.add( tablePopup );
            //tables[i].table.getTableHeader().setSize(100, 100);
            tables[i].table.getColumnModel().getColumn(0).setPreferredWidth(24);

        }

    }


    private void redrawToolValues() {
        if ( toolSource.isConnected() ) {
            displayTX.render( toolCode );
            displayTY.render( toolValue.value * 60.0 );
            //displayTZ.render( toolAngles.rotate.getValue() );
        } else {
            displayTX.renderClear();
            displayTY.renderClear();
            //displayTZ.renderClear();
        }
        imgDigTX.repaint();
        imgDigTY.repaint();
        //imgDigTZ.repaint();
    }

    private void redrawAccurateValues() {
        if ( unit.isConnected() ) {
            displayAX.render( accSection );
            displayAY.render( accValue.value );
            //displayAZ.render( accAngles.rotate.getValue() );
        } else {
            displayAX.renderClear();
            displayAY.renderClear();
            //displayAZ.renderClear();
        }
        imgDigAX.repaint();
        imgDigAY.repaint();
        //imgDigAZ.repaint();
    }

    protected void errorAccValues() {
        //displayAX.
        displayAX.renderClear();
        displayAY.renderClear();
        imgDigAX.repaint();
        imgDigAY.repaint();
    }

    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = toolSource.getChannel( "sensors" );
                //ChannelValue val; // = toolSource.getValue("azimuth");
                if ( chan != null ) {
                    //toolValue = chan.getValue( 8 ).getAsInteger();
                    toolCode = toolSource.getValue( "gk" ).getAsInteger();
                    toolValue = doCalcFunc( toolCode );
                }
                redrawToolValues();
                displayCoef();

            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Нет связи с прибором" );
            }

        } else
        if ( src.equals( unit ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = unit.getChannel( "sensors" );
                if ( chan != null ) {
                    accSection = chan.getValue( 14 ).getAsDouble();
                }
                chan = unit.getChannel( "values" );
                if ( chan != null ) {
                    accValue = chan.getValue( 0 ).getAsValue();
                }
                redrawAccurateValues();

            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                errorAccValues();
                //UiUtils.showError( this, "Нет связи с установкой" );
            }

        }

    }

    public void start() {
        boolean quit = false;
        try {
            Zeus zeus = Zeus.getInstance();
            zeus.getConfig().setProperty( Zeus.PROP_CHECKUNIT_CLASS,
                                         "lsdsoft.metrolog.unit.UAKGKCheckUnit" );
            TIME_INTEGRATION = Integer.parseInt(
                zeus.getConfig().getProperty("gk.measuretime","120"));
            unit = DataFactory.createCheckUnit();
            unit.addSignalListener( this );
            boolean b = Boolean.valueOf( zeus.getProperty( Zeus.PROP_WORK_NEW ) ).
                booleanValue();
            if ( b ) {
                //datas = DataFactory.createMeasureDatas( DataFactory.getMethods(
                //    "ion1_test.xml" ) );
                datas = DataFactory.createMeasureDatas( DataFactory.getMethods(
                    workState.getToolType()+'.'+workState.getWorkMode().getName()+".xml" ) );
                datas.setWorkState( workState );
            } else {
                String id = zeus.getProperty( Zeus.PROP_WORK_ID );
                datas = DataFactory.loadMeasureDatas( workState.getToolType(),
                    workState.getToolNumber(),
                    zeus.getProperty( Zeus.PROP_WORK_ID ) );
                workState = datas.getWorkState();

            }
            workMode = workState.getWorkMode().getWorkMode();
            if(workState.getToolChannel() == null) {
                workState.setToolChannel("gk");
            }
            toolChannel = workState.getToolChannel();
            //setErrorLimits();
            datas.ensurePointsCount(4);
            datas.calc();
            selectTables();
            buildTables();
            //initZenithTab();
            //initAzimuthTab();

            try {
                zeus.setProperty(Zeus.PROP_TOOL_DATASOURCE_CLASS, "lsdsoft.welltools.DataSourceViaUAKGK");
                toolSource = DataFactory.createToolDataSource();
            } catch ( Exception ex ) {
                ex.printStackTrace(System.err);
                UiUtils.showError( this,
                                   "Error at toolsource:" + ex.toString() );
            }
            if ( toolSource == null ) {
                int res = UiUtils.showConfirmError( this,
                    "<html><center>Не удалось создать источник данных прибора" +
                    "<br>Продолжить?" );
                quit = res != 0;
            } else {
                toolSource.addSignalListener( this );
            }
            try {
                toolSource.setAliases( DataFactory.createChannelAlias(
                    workState.getToolType() ) );
            } catch ( Exception ex1 ) {
                UiUtils.showError( this,
                                   "<html><center>Ошибка при создании псевдонимов:<br>" +
                                   ex1.getLocalizedMessage() );
            }

            try {
                switchOn();
            } catch ( Exception ex2 ) {
                UiUtils.showError( this,
                                   "<html><center>Ошибка при подключении:<br>" +
                                   ex2.getLocalizedMessage() );
            }
            jbInit();
            displayBack();
            UiUtils.toScreenCenter( this );
            selectedContainer = tables[0];
            initMED();
            tabbedTables.setSelectedIndex( 0 );
            redrawAccurateValues();
            redrawToolValues();
            doCalcFunc(toolCode);
            displayCoef();
        } catch ( Exception ex ) {
            ex.printStackTrace();
            UiUtils.showError( this, ex.getLocalizedMessage() );
        }
        if ( !quit ) {
            this.setVisible( true );
        }
    }

    private void initMED() {
        UAKGKCheckUnit u =(UAKGKCheckUnit ) unit;
        for(int i = 0; i < 6; i++) {
            Value med = u.getMED(i+1);
            tables[0].measureTable.getChain(i).getPoint(0).accurate = med;
        }
        tables[0].measureTable.calc();
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
        //panelX.setLayout( null );
        panelZ.setLayout( null );
        //table.setMinimumSize( new Dimension( , 0 ) );
        //table.setPreferredSize( new Dimension( 0, 0 ) );
        this.getContentPane().setLayout( null );
        this.setSize( 800, 600 );
        this.setState( Frame.NORMAL );
        String s =workState.getToolName() + " № " + workState.getToolNumber();
        if(workState.getToolChannel() == null) {
            workState.setToolChannel("gk");
        }
        s += " по каналу " + (workState.getToolChannel().equals("gk")?"ГК":"НГК");
        if(workState.getWorkMode().getWorkMode() == WorkMode.MODE_CALIB )
            this.setTitle( "Калибровка прибора " + s);
        else
            this.setTitle( "Градуировка прибора " + s);

        //this.setExtendedState( 6 );
        tabbedTables.setBounds( new Rectangle( 9, 41, 478, 244 ) );
        //tabbedTables.addTab("Угол поворота", table);
        Font font = new Font( "Dialog", 0, 14 );
        Font fontB = new Font( "Dialog", Font.BOLD , 14 );
        // init tableX
        //panelY.setMaximumSize( new Dimension( 500, 500 ) );
        //panelY.setLayout( null );
        //scrollPaneY.setAutoscrolls( true );
        //scrollPaneY.setDebugGraphicsOptions( 0 );
        JLabel labelChar = new JLabel("Градуировочная хар-ка:");
        labelChar.setBounds( 3, 9,184,15 );
        labelChar.setFont(font);
        panelZ.add(labelChar);

        JLabel labelChar2 = new JLabel("МЭД=N/K");
        labelChar2.setBounds( 190, 9,80,15 );
        labelChar2.setFont(fontB);
        panelZ.add(labelChar2);

        lCoef.setBounds( 300, 9,224,15 );
        //lCoef.setFont(fontB);
        panelZ.add(lCoef);

        JLabel labelBack = new JLabel("Фон:");
        labelBack.setBounds( 3, 29,124,15 );
        labelBack.setFont(font);
        panelZ.add(labelBack);

        lBackValue.setBounds( 55, 29,124,15 );
        lBackValue.setFont(fontB);
        panelZ.add(lBackValue);




        //jLabel1.setText( "Угол поворота:" );
        //jLabel1.setBounds( new Rectangle( 3, 9, 124, 15 ) );
        //cbZenRotates.setBounds( new Rectangle( 110, 5, 115, 24 ) );
        //cbZenRotates.addActionListener( new java.awt.event.ActionListener() {
        //    public void actionPerformed( ActionEvent e ) {
        //    }
        //} );

        //jLabel2.setText( "Зенитный угол:" );
        //jLabel2.setBounds( new Rectangle( 7, 9, 134, 15 ) );
        //cbAzZeniths.setBounds( new Rectangle( 112, 7, 115, 24 ) );
        //cbAzZeniths.addActionListener( new java.awt.event.ActionListener() {
        //    public void actionPerformed( ActionEvent e ) {
        //        cbAzZeniths_actionPerformed( e );
        //    }
        //} );
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
        //jLabel5.setHorizontalAlignment( SwingConstants.CENTER );
        //jLabel5.setText( "АЗИМУТ" );
        //jLabel5.setBounds( new Rectangle( 58, 7, 159, 15 ) );
        //jLabel6.setHorizontalAlignment( SwingConstants.CENTER );
        //jLabel6.setText( "ЗЕНИТ" );
        //jLabel6.setBounds( new Rectangle( 243, 7, 162, 15 ) );
        //jLabel7.setHorizontalAlignment( SwingConstants.CENTER );
        //jLabel7.setText( "ПОВОРОТ" );
        //jLabel7.setBounds( new Rectangle( 431, 7, 163, 15 ) );
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
        //imgDigAZ.setLocation( new java.awt.Point( 380, 380 ) );
        //imgDigAZ.setBounds( new Rectangle( 466, 2, 162, 35 ) );
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
        //imgDigTZ.setLocation( new java.awt.Point( 460, 420 ) );
        //imgDigTZ.setBounds( new Rectangle( 466, 3, 162, 35 ) );
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
        jLabel9.setBounds( new Rectangle( 502, 44, 94, 15 ) );
        cbDo.setBounds( new Rectangle( 502, 64, 264, 24 ) );
        bStart.setBounds( new Rectangle( 504, 95, 80, 29 ) );
        bStart.setPreferredSize( new Dimension( 80, 30 ) );
        bStart.setText( "Пуск" );
        bStart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                startProcess();
            }
        } );
        jPanel1.setBorder( BorderFactory.createLineBorder( Color.black ) );
        jPanel1.setBounds( new Rectangle( 504, 133, 270, 153 ) );
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
        bStop.setBounds( new Rectangle( 590, 95, 94, 29 ) );
        bStop.setPreferredSize( new Dimension( 84, 30 ) );
        bStop.setText( "Останов" );
        bStop.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stopProcess( e );
            }
        } );
        //miMeasure.setText( "Замер" );
        miChange.setAction( changeAction );
        miMeasure.setAction(measureAction);
        bProtocol.setText("Протокол");
        bProtocol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bProtocol_actionPerformed(e);
            }
        });
        if(toolChannel.equals("gk"))
            tabbedTables.add( panelZ, "Канал ГК" );
        if(toolChannel.equals("ngk"))
            tabbedTables.add( panelZ, "Канал НГК" );

        //tables[2].scrollPane.setBounds( new Rectangle( 1, 41, 473, 169 ) );
        //tables[1].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );

        //panelX.add( tables[2].scrollPane, null );
        //panelY.add( tables[1].scrollPane, null );
        //panelZ.add( tables[0].scrollPane, BorderLayout.CENTER );

        //tabbedTables.add( panelY, "Зенитный угол" );
        //panelY.add( jLabel1, null );
        //panelY.add( cbZenRotates, null );
        //tabbedTables.add( panelX, "Азимутальный угол" );
        //scrollPaneY.getViewport().add( tables[1].table, null );

        //panelX.add( jLabel2, null );
        //panelX.add( cbAzZeniths, null );
        this.getContentPane().add( jPanel1, null );
        jPanel1.add( jLabel10, null );
        jPanel1.add( jLabel11, null );
        jPanel1.add( lTimeStart, null );
        jPanel1.add( lTimeElapsed, null );
        this.getContentPane().add( bStart, null );
        this.getContentPane().add( cbDo, null );
        this.getContentPane().add( jLabel9, null );
        this.getContentPane().add( panelDisplay, null );
        panelHeader.add( jLabel8, null );
        panelHeader.add( jLabel7, null );
        panelHeader.add( jLabel6, null );
        panelHeader.add( jLabel5, null );
        panelDisplay.add( panelHeader, null );
        panelDisplay.add( panelAcc, null );
        panelAcc.add( imgDigAY, null );
        panelAcc.add( imgDigAX, null );
        //panelAcc.add( imgDigAZ, null );
        panelAcc.add( jLabel3, null );
        panelDisplay.add( panelTool, null );
        panelTool.add( imgDigTX, null );
        panelTool.add( imgDigTY, null );
        //panelTool.add( imgDigTZ, null );
        panelTool.add( jLabel4, null );

        displayAX.renderClear();
        displayAY.renderClear();
        //displayAZ.renderClear();

        this.getContentPane().add( jToolBar1, null );
        jToolBar1.add( bSave, null );
        jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        jToolBar1.add( bConnect, null );
        jToolBar1.add(bProtocol, null);
        this.getContentPane().add( tabbedTables, null );
        jPanel1.add( lMessage2, null );
        jPanel1.add( lMessage3, null );
        jPanel1.add( lMessage1, null );
        this.getContentPane().add( bStop, null );
        tablePopup.add( miChange );
        tablePopup.add( miMeasure );

        //scrollPaneX.getViewport().add( tables[0].table, null );
        //scrollPaneZ.getViewport().add( tables[2].table, null );
        tabbedTables.setSelectedComponent( panelZ );
        buildToolLabel();
        Command[] com;
        com = (workMode == WorkMode.MODE_CALIB)?commands:commandsGrad;

        for ( int i = 0; i < com.length; i++ ) {
            cbDo.addItem( com[i] );
        }
        clearMessages();
        this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        tables[0].table.getTableHeader().setPreferredSize(new Dimension(300, 48));
        panelZ.add( tables[0].scrollPane, null  );
        int w = panelZ.getBounds().width;
        w = tabbedTables.getBounds().width;
        tables[0].scrollPane.setBounds(0,50, w-4, 200);
    }



    void doMeasure( MeasureTable table, int row, char plane ) {
        String alias = planeToName( plane );
        Channel achan = unit.getChannel( "angles" );
        Channel tchan = toolSource.getChannel( "angles" );
        //if ( achan == null || tchan == null ) {
        //    System.err.println( "Not available channel for measure" );
        //    return;
       // }
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
            double value = doMeasureTool( alias );
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

    double doMeasureTool( String alias ) {
        double value = 0;
        //int subchan = planeToIndex( plane );
        //Channel chan = toolSource.getChannel( "angles" );
        ChannelValue cvalue = toolSource.getValue( alias );
        if ( cvalue == null ) {
            /** @todo throw exce */
            return 0;
        }
        for ( int i = 0; i < 4; i++ ) {
            toolSource.waitNewData();
            double v1 = cvalue.getAsDouble();
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

    String planeToName( char plane ) {
        if( plane == 'x' )
            return "azimuth";
        if( plane == 'y' )
            return "zenith";
        if( plane == 'z' )
            return "rotate";
        return "";
    }

/*
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
*/
/*
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
*/

    public void doFindMarker() throws Exception {
        UAKGKCheckUnit u =(UAKGKCheckUnit)unit;
        lMessage1.setText( "Поиск флажка..." );
        lMessage2.setText( "" );
        lMessage3.setText( "" );
        u.findMarker();
        Thread.sleep(1000);
        int state;
        do {
            Thread.sleep(500);
            state = u.getDataSource().getChannel( "sensors" ).getValue( 7 ).
                getAsInteger();
        } while( state == 0 );
        lMessage2.setText( "Done!" );
    }
    public void doCalibrationFull() throws Exception {
        clearMessages();
        // поиск флажка (метка начала)
        doFindMarker();
        // измерение фона
        doMeasureBack();
        // измерение в первой точке
        JOptionPane.showMessageDialog( this, "<html><center>Перед измерением в точках<br>" +
                                       "необходимо установить источник<br>" +
                                       "радиоактивного излучения",
                                    "Предупреждение", JOptionPane.INFORMATION_MESSAGE
                                     );

        //UiUtils.showConfirmError(this, "Установите источник");
        doCalibration(1);
        doCalibration(2);
        doCalibration(3);
        doCalibration(4);
        doCalibration(5);
        doCalibration(6);
        doGoToSafePoint();
        if(workMode == WorkMode.MODE_GRAD ) {
            doBuildGrad();
            lMessage3.setText( "Градуировка выполнена" );
        } else {
            lMessage3.setText( "Калибровка выполнена" );
        }
    }
    public void doCalibration1() throws Exception {
        doCalibration( 1 );
    }
    public void doCalibration2() throws Exception {
        doCalibration( 2 );
    }
    public void doCalibration3() throws Exception {
        doCalibration( 3 );
    }
    public void doCalibration4() throws Exception {
        doCalibration( 4 );
    }
    public void doCalibration5() throws Exception {
        doCalibration( 5 );
    }
    public void doCalibration6() throws Exception {
        doCalibration( 6 );
    }
    public void doGoToSafePoint() throws Exception {
        UAKGKCheckUnit gkUnit =(UAKGKCheckUnit)unit;
        gkUnit.goToLocation(UAKGKCheckUnit.sections[gkUnit.getCurrentSection()-1]-100);
    }

    public void doCalibration(int section) throws Exception {
        lMessage2.setText( "" );
        UAKGKCheckUnit u =(UAKGKCheckUnit)unit;
        Value med = u.getMED(section);
        if(toolChannel.equals("ngk")) {
            if(med.value < 5.0) {
                System.out.println("Point skiped");
                return;
            }
        }
        lMessage1.setText( "Поворот в сектор " + section );
        u.initSpeed();
        u.goToLocation(u.sections[section-1]);

        int left;
        // wait until tube arrived to req position
        do {
            Thread.sleep(1000);
            Channel c = u.getDataSource().getChannel( "sensors" );
            left = c.getValue( 2 ).getAsInteger();
        } while( left != 0 );
        lMessage1.setText( "Измерение..." );
        Channel chan = toolSource.getChannel( "sensors" );
        if ( chan == null ) {
            /** @todo throw exce */
            lMessage2.setText( "Error: tool source not found" );
            return;
        }
        double sum = doMeasureTool();
        MeasurePoint point = tables[0].measureTable.getChain(section - 1).getPoint(0);
        point.toolCode = (int)(sum - toolBack);
        point.calc();
        //point.setToolValue(sum/100.0);
        point.setAccurateValue(med.value);
        point.accurate = med;
        if(workMode == WorkMode.MODE_GRAD) {
            doBuildGrad();
            recalcToolValues();
        }
        point.tool = doCalcFunc(point.toolCode);
        if(med.value <=2.0 ) {
            point.tool.delta = point.tool.value;
        }
        tables[0].measureTable.calc();
        selectedContainer.table.repaint();
        lMessage2.setText( "Done!" );
    }

    public void doMeasureBack() throws Exception {
        JOptionPane.
            showMessageDialog( this,
                               "<html><center>Для измерения естественного<br>" +
                               "радиоактивного фона убедитесь в отсутствии<br>" +
                               "радиоактивного источника в установке!",
                               "Предупреждение", JOptionPane.INFORMATION_MESSAGE
                               );

        MeasureTable table = datas.selectTable("name", "back");
        lMessage2.setText( "" );
        lMessage1.setText( "Измерение фона" );
        table.ensurePointsCount(1);
        double v = doMeasureTool();
        toolBack = v;
        table.getChain(0).getPoint(0).toolCode = (int)(v);
        // set non zero value
        table.getChain(0).getPoint(0).setToolValue( v + 0.1 );
        //lBackValue.setText(String.valueOf(toolValue));
        displayBack();
        //tables[0].measureTable.setProperty("back", String.valueOf(doMeasureTool()));
        lMessage2.setText( "Выполнено" );
    }
    public void doBuildGrad() {
        if( toolSource instanceof ToolDataSource) {
            ToolDataSource toolS = ( ToolDataSource )toolSource;
            Graduation grad = toolS.getGraduation( "gk" );
            // get conversion const
            if ( grad != null ) {
                //double K = Double.parseDouble(grad.getParameter("K"));
                //double deltac = Double.parseDouble(grad.getParameter("deltacoef"));
                gradCoef = buildGradCoef();
                grad.setParameter("K", String.valueOf(gradCoef));
            }
        }
        recalcToolValues();
        datas.getProperties().setProperty("K", String.valueOf((int)gradCoef));
        selectedContainer.table.repaint();
        displayCoef();
    }
    /**
     * Расчет градуировочной характеристики для канала ГК
     * Используется линейная регрессия, в качестве коэффициента
     * преобразования берется коэффициент линейной функции,
     * свободный член опускается. %)
     * @return Коеэфициент градуировочной характеристики.
     */
    protected double buildGradCoef() {
        double coef = 0;
        //MeasureTable table = datas.selectTable("name", "gk");
        MeasureTable table = tables[0].measureTable;
        int N = table.size();
        double X[] = new double[N];
        double Y[] = new double[N];
        double KK[] = new double[N];
        double Y2[] = new double[N];
        double B[] = new double[2];
        double sum = 0;
        int v = 0;
        for(int i = 0; i < N; i++) {
            //X[v] = table.getChain(i).getCodeAverage();
            double acc = table.getChain(i).getAccurateValue().value;
            if(acc < 2.0 ) {
                continue;
            }
            X[v] = acc;
            if(X[v] == 0)
                continue;
            KK[v] = table.getChain(i).getCodeAverage()/ X[v];
            sum += KK[v];
            v++;
        }
        regress.RegressLine(v, X, KK, B);
        // среднее арифметическое
        sum /= v;
        return sum;
        //return B[0];
        /*
        for(int i = 0; i < N; i++) {
            X[i] = table.getChain(i).getCodeAverage();
            Y[i] = table.getChain(i).getAccurateValue().value;
        }
        regress.RegressLine(N, X, Y, B);
        double k = B[1];
        int K = 0;
        if(k != 0 )
            for(int i = 0; i < N; i++) {
                MeasureChain c = table.getChain(i);
                if(c.getAccurateValue().value != 0) {
                    X[K] = Y[i];
                    //X[i] = table.getChain(i).getToolValue().value;
                    Y2[K] = c.getCodeAverage() * k;
                    Y2[K] = ( Y2[K] -
                              c.getAccurateValue().value ) /
                        c.getAccurateValue().value;
                    K++;
                }
            }
        regress.RegressLine(K, X, Y2, B);
        k = k *(1+B[1]) + B[0];
        return 1/k;
*/
    }
    protected void recalcToolValues() {
        MeasureTable table = tables[0].measureTable;
        int N = table.size();
        for(int i =0 ; i < N; i++) {
            MeasurePoint point = table.getChain( i ).getPoint( 0 );
            //point.toolCode = ( int ) ( sum - toolBack );
            //point.calc();
//point.setToolValue(sum/100.0);
            //Value med = u.getMED( section );
            //point.setAccurateValue( med.value );
            //point.accurate = med;
            point.tool = doCalcFunc( point.toolCode );
            if(point.accurate.value <= 2.0 ) {
                point.tool.delta = 2.0;

            }
        }
        table.calc();

    }
    /**
     * Измерение по прибору, среднее значение выходного сигнала
     * @return усредненное значение выходного сигнала за промежуток времени
     * @throws Exception
     */
    public double doMeasureTool() throws Exception {
        double sum = 0;
        int count = 0;
        //String id = "";
        for ( int i = 0; i < TIME_INTEGRATION; i++ ) {
            Thread.sleep(500);

            while( !toolSource.hasNewData  ) {
                Thread.sleep( 100 );
            }
            System.out.print("!");
            toolSource.hasNewData = false;
            //id = toolSource.id;
            //Thread.sleep(500);
            //toolSource.waitNewData();
            lMessage2.setText( String.valueOf(i*100/TIME_INTEGRATION) + "%" );
            double v1 = toolCode;
            sum += v1;
            count++;

        }
        sum /= count;
        return sum* 60.0;
    }
    /**
     * Расчет значения физической величины по коду прибора, используя
     * градуировочную характеристику
     * @param toolCode значение выходного сигнала
     * @return значение физической величины
     */
    public Value doCalcFunc(double toolCode) {
        Value val = new Value();
        if( toolSource instanceof ToolDataSource) {
            ToolDataSource toolS = (ToolDataSource)toolSource;
            Graduation grad = toolS.getGraduation("gk");
            // get conversion const
            if( grad != null ) {
                double K = Double.parseDouble(grad.getParameter("K"));
                double deltac = Double.parseDouble(grad.getParameter("deltacoef"));
                gradCoef = K;
                datas.getProperties().setProperty("K", String.valueOf(K));
                displayCoef();
                if( K != 0 ) {
                    // for impulses per min need mul on 60
                    val.value = toolCode / K;
                    val.delta = val.value * deltac ;
                } else {
                    System.err.println("Grad coef is zero!");
                }
            }
        }
        return val;
    }
    public void doChange() {
        selectedContainer = tables[tabbedTables.getSelectedIndex()];
        JTable table = selectedContainer.table;
        int row = table.getSelectedRow();
        MeasureChainEditor.edit( selectedContainer.measureTable.
                                 getChain( row ), this );
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

    protected void displayCoef() {
        String a = String.valueOf(Math.floor(gradCoef*1000.0)/1000.0);
        lCoef.setText("<html><b>K=" + a +"</b> (имп/мин)/(мкР/час)");
    }
    protected void displayBack() {
        if(workMode == WorkMode.MODE_GRAD) {
            lBackValue.setText( String.valueOf( toolBack ) );
        } else {
            lBackValue.setText( String.valueOf( toolValue ) );
        }
    }
    protected void setGradCoef(double coef) {
        if ( toolSource instanceof ToolDataSource ) {
            ToolDataSource toolS = ( ToolDataSource )toolSource;
            Graduation grad = toolS.getGraduation( "gk" );
            //grad.setParameter("gk", Integer.toString((int)toolValue));
            // get conversion const
            if ( grad != null ) {
                grad.setParameter("K", String.valueOf(coef));
            }

        }

    }

    protected void startProcess() {
        execCommand(( Command ) cbDo.getSelectedItem() );
        System.out.println( "Process started" );
    }


    protected void updateTime() {
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

    void switchOn() throws Exception {
        if ( unit != null ) {
            if( !unit.isConnected() ) {
                unit.connect();
            }
            toolSource.connect();
        }
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

    protected Vector buildCommandList() {
        char plane = 'z';
        MeasureTable workTable = tables[2].measureTable;
        int pointsCount = workTable.size();
        Vector commands = new Vector( 16 );
        /*
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
            */
        return commands;
    }

    void buildToolLabel() {
        //StringBuffer str = new StringBuffer(
        //    "<html><table width=200 border=1><tr><th>" );
        //str.append( toolAngles.azimut.toString() );
        //str.append( "</th><th>" );
        //str.append( toolAngles.zenit.toString() );
        //str.append( "</th><th>" );
        //str.append( toolAngles.rotate.toString() );
        //str.append( "</th></tr></table>" );
    }

    void switchConnect() {
        try {
            if ( !unit.isConnected() ) {
                unit.connect();
            } else {
                unit.disconnect();
            }
        } catch ( Exception ex ) {
            UiUtils.showError( this,
                               "Не удалось подключиться: " + ex.getMessage() );
            //System.err.println( ex.getMessage() );
        }
        try {
            if ( unit.isConnected() ) {
                toolSource.connect();
            } else {
                toolSource.disconnect();
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



    void bSave_actionPerformed( ActionEvent e ) {
        DataFactory.saveMeasureDatas( datas );
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
            //doJoinRotates();
        } catch ( Exception ex ) {
        }
    }

    void cbAzZeniths_actionPerformed( ActionEvent e ) {
    }

    void bProtocol_actionPerformed(ActionEvent e) {
        try {
            String path = Zeus.getInstance().getProperty("report.path");
            String filename =  path + "/" + datas.getToolType() + "_" + datas.getToolNumber()+ ".html";
            //FileWriter file = new FileWriter( filename );
            //StringWriter out = new StringWriter(8192);
            FileOutputStream outs = new FileOutputStream( filename );
            OutputStreamWriter osw = new OutputStreamWriter( outs, "Windows-1251" );
            GKProtocol prot = new GKProtocol( osw );
            prot.generate(datas);
            outs.flush();
            //outs.close();
            //file.write(out.toString());
            osw.close();

            HtmlViewer viewer = new HtmlViewer();
            //viewer.setText(outs.toString());
            viewer.loadText( filename );
            viewer.view();
            outs.close();
        } catch ( Exception ex ) {
            System.out.println(ex.getMessage());
        }

    }

}
