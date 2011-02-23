package lsdsoft.zeus.methods;


import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.Timer;
import javax.swing.border.*;
//import javax.swing.event.*;
//import javax.swing.table.*;

import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.util.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.report.*;
import lsdsoft.zeus.ui.*;
//import bsh.*;
//import java.net.*;
import java.text.*;


/**
 * <p>Title: Обозреватель методики калибровки для прибора ИОН-1</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Ural-Geo 2004-2010</p>
 * @author lsdsoft, de-nos
 * @version 0.9
 */

public class UaksiMethodsViewer
    extends BaseMethodsViewer
    implements SignalEventListener {


    protected Command commandMeasure = new Command( this, "doMeasure", "замер в точке",
                                      "" );

    protected Command[] commands = {
        new Command( this, "doCalibrationFull", "калибровка полностью", "" ),
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
    Action changeAction = new ChangeAction();
    protected BaseMethodsViewer viewer = this;
    protected TableContainer tables[] = {
        new TableContainer(), new TableContainer(), new TableContainer()};
    protected TableContainer selectedContainer;
    //protected CommandExecuter executer;
    protected int rotateTableIndex = 0;
    protected int zenithTableIndex = 1;
    protected int azimuthTableIndex = 2;


    protected int selectedCommand = 1;

    protected InclinometerValues toolValues = new InclinometerValues();
    protected InclinometerValues accValues = new InclinometerValues();

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
    ImageIcon iconOff = Zeus.createImageIcon( "images/conn_off.png" );
    ImageIcon iconOn = Zeus.createImageIcon( "images/conn_on.png" );

    JComboBox cbZenRotates = new JComboBox();

    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JComboBox cbAzZeniths = new JComboBox();
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
    JPanel panelTool = new JPanel();
    JLabel jLabel4 = new JLabel();
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
    //JPopupMenu tablePopup = new JPopupMenu();
    //JMenuItem miChange = new JMenuItem();
    //JMenuItem miMeasure = new JMenuItem();
    protected DecimalFormat df;
    protected JButton bProtocol = new JButton();
    protected JButton bSertificate = new JButton();
    protected JLabel operatorLabel = new JLabel("Калибровщик");
    protected JTextField operatorTextField = new JTextField();
    protected JLabel temperatureLabel = new JLabel("Темп-ра воздуха:");
    protected JTextField temperatureTextField = new JTextField();


    public UaksiMethodsViewer() {
        try {
            df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
            df.applyPattern("#0.00");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    protected void selectTables() {
        Vector tables = datas.selectTables( "type", "azimuth" );
        if ( tables.size() > 0 ) {
            this.tables[azimuthTableIndex].measureTable = ( MeasureTable )tables.get( 0 );
        }
        tables = datas.selectTables( "type", "zenith" );
        if ( tables.size() > 0 ) {
            this.tables[zenithTableIndex].measureTable = ( MeasureTable )tables.get( 0 );
        }
        tables = datas.selectTables( "type", "rotate" );
        if ( tables.size() > 0 ) {
            this.tables[rotateTableIndex].measureTable = ( MeasureTable )tables.get( 0 );
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
            if(tables[i].table != null) {
                tables[i].init();
                tables[i].table.addMouseListener( mouseAdapter );
                //tables[i].table.setCellSelectionEnabled(false);
                tables[i].table.setSelectionMode( ListSelectionModel.
                                                  SINGLE_SELECTION );
                tables[i].table.add( tablePopup );
            }
        }

    }

    protected void changeAzimuthTable() {
        String value = ( String )cbAzZeniths.getSelectedItem();
        Vector tables = datas.selectTypedTables( "azimuth", "zenith", value );
        if ( tables.size() > 0 ) {
            this.tables[azimuthTableIndex].measureTable = ( MeasureTable )tables.get( 0 );
        }
        this.tables[azimuthTableIndex].init();
    }

    protected void changeZenithTable() {
        String value = ( String )cbZenRotates.getSelectedItem();
        Vector tables = datas.selectTypedTables( "zenith", "rotate", value );
        if ( tables.size() > 0 ) {
            this.tables[zenithTableIndex].measureTable = ( MeasureTable )tables.get( 0 );
        }
        this.tables[zenithTableIndex].init();
    }

    protected void redrawToolValues() {
        if( toolSource != null) {
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
    }

    protected void redrawAccurateValues() {
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

    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = toolSource.getChannel( "angles" );
                if ( chan == null ) {
                    chan = toolSource.getChannel( "values" );
                }
                if ( chan != null ) {
                    System.out.println("### tool");
                    Value val = chan.getValue( 0 ).getAsValue();
                    System.out.println("Az=" + formatValue(val)+';');
                    val = chan.getValue( 1 ).getAsValue();
                    System.out.println("Zn=" + formatValue(val)+';');
                    val = chan.getValue( 2 ).getAsValue();
                    System.out.println("Rt=" + formatValue(val)+';');

                    toolValues.azimuth = chan.getValue( 0 ).getAsValue();
                    toolValues.zenith = chan.getValue( 1 ).getAsValue();
                    toolValues.rotate = chan.getValue( 2 ).getAsValue();

                    redrawToolValues();
                }
            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Нет связи с прибором" );
            }

        } else
        if ( src.equals( unit ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = unit.getChannel( "angles" );
                if ( chan != null ) {
                    accValues.azimuth = chan.getValue( 0 ).getAsValue();
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
            boolean b = Boolean.valueOf( zeus.getProperty( Zeus.PROP_WORK_NEW ) ).
                booleanValue();
            if ( b ) {
                datas = DataFactory.createMeasureDatas( zeus.getToolType(),
                    zeus.getWorkMode().getName()  );
                datas.setWorkState( workState );
            } else {
                String id = zeus.getProperty( Zeus.PROP_WORK_ID );
                datas = DataFactory.loadMeasureDatas( workState.getToolType(),
                                                      workState.getToolNumber(),
                                                      id );
                workState = datas.getWorkState();

            }
            datas.ensurePointsCount( 4 );
            datas.calc();
            selectTables();
            buildTables();
            initZenithTab();
            initAzimuthTab();
            //zeus.setToolDataSourceID(zeus.getToolType());
            try {
                toolSource = DataFactory.createToolDataSource( zeus.getToolType() );
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

            uiInit();
            UiUtils.toScreenCenter( this );
            selectedContainer = tables[0];
            tabbedTables.setSelectedIndex( 0 );
            tables[zenithTableIndex].init();
            tables[azimuthTableIndex].init();
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
            UiUtils.showError( this, ex.getLocalizedMessage() );
            //ex.printStackTrace();
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

    protected void updateTitle() {
        String title = "Калибровка прибора " + workState.getToolName() + " № " +
                       workState.getToolNumber();
        if(workState.getToolType() == "ion1") {
            title += " | " + toolSource.getProperty( "table.filename" ) +
                " от " + toolSource.getProperty( "table.date" );
        }
        this.setTitle( title );

    }
    protected void uiInit() throws Exception {
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
        panelZ.setLayout( null );
        //table.setMinimumSize( new Dimension( , 0 ) );
        //table.setPreferredSize( new Dimension( 0, 0 ) );
        this.getContentPane().setLayout( null );
        this.setSize( 790, 480 );
        this.setResizable(false);
        this.setState( Frame.NORMAL );
        updateTitle();
        //this.setExtendedState( 6 );
        tabbedTables.setBounds( new Rectangle( 9, 41, 478, 244 ) );
        //tabbedTables.addTab("Угол поворота", table);
        //Font font = new Font( "Dialog", 0, 14 );
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
                changeZenithTable();
            }
        } );

        jLabel2.setText( "Зенитный угол:" );
        jLabel2.setBounds( new Rectangle( 7, 9, 134, 15 ) );
        cbAzZeniths.setBounds( new Rectangle( 112, 7, 115, 24 ) );
        cbAzZeniths.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                changeAzimuthTable();
            }
        } );
        jToolBar1.setEnabled( true );
        //jToolBar1.setAlignmentY((float) 0.5);
        jToolBar1.setBorder( null );
        jToolBar1.setFloatable( false );
        jToolBar1.setBounds( new Rectangle( 1, 1, 780, 41 ) );
        bSave.setText( "Сохранить" );
        bSave.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                DataFactory.saveMeasureDatas( datas );
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
        miMeasure.setAction( measureAction );
        bProtocol.setText( "Протокол" );
        bSertificate.setText("Сертификат");
        bProtocol.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                buildProtocol();
            }
        } );
        bSertificate.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                buildSertificate();
            }
        } );
        operatorTextField.setText(Zeus.getInstance().getProperty("operator"));
        operatorTextField.setMaximumSize(new Dimension(160, 28));
        //operatorTextField.deletesetCaretPosition();
        operatorTextField.addKeyListener(new KeyListener(){
            public void keyTyped( KeyEvent e ) {}
            public void keyPressed( KeyEvent e ) {}
            public void keyReleased( KeyEvent e ) {
                Zeus.getInstance().setProperty("operator_edited", operatorTextField.getText());
            }
        });
        temperatureTextField.setText(Zeus.getInstance().getProperty("temperature"));
        temperatureTextField.setMaximumSize(new Dimension(40, 28));
        temperatureTextField.addKeyListener(new KeyListener(){
            public void keyTyped( KeyEvent e ) {}
            public void keyPressed( KeyEvent e ) {}
            public void keyReleased( KeyEvent e ) {
                Zeus.getInstance().setProperty("temperature_edited", temperatureTextField.getText());
            }
        });
        Zeus.getInstance().setProperty("operator_edited", Zeus.getInstance().getProperty("operator",""));
        Zeus.getInstance().setProperty("temperature_edited", Zeus.getInstance().getProperty("temperature",""));
        if(rotateTableIndex >= 0) {
            tabbedTables.add( panelZ, "Угол поворота" );
            panelZ.add( tables[rotateTableIndex].scrollPane, null );
            tables[rotateTableIndex].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );
            tables[rotateTableIndex].init();
        }
        tables[azimuthTableIndex].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );
        panelX.add( tables[azimuthTableIndex].scrollPane, null );tables[zenithTableIndex].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );


        panelY.add( tables[zenithTableIndex].scrollPane, null );
        tabbedTables.add( panelY, "Зенитный угол" );
        panelY.add( jLabel1, null );
        panelY.add( cbZenRotates, null );
        tabbedTables.add( panelX, "Азимутальный угол" );

        //scrollPaneY.getViewport().add( tables[1].table, null );

        panelX.add( jLabel2, null );
        panelX.add( cbAzZeniths, null );
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

        this.getContentPane().add( jToolBar1, null );
        jToolBar1.add( bSave, null );
        jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        jToolBar1.add( bConnect, null );
        jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        jToolBar1.add( bProtocol, null );
        jToolBar1.add( bSertificate, null );
        jToolBar1.addSeparator( new Dimension( 20, 10 ) );
        jToolBar1.add( operatorLabel, null );
        jToolBar1.addSeparator( new Dimension( 5, 10 ) );
        jToolBar1.add( operatorTextField, null );
        jToolBar1.addSeparator( new Dimension( 10, 10 ) );
        jToolBar1.add( temperatureLabel, null );
        jToolBar1.addSeparator( new Dimension( 5, 10 ) );
        jToolBar1.add( temperatureTextField, null );
        this.getContentPane().add( tabbedTables, null );
        jPanel1.add( lMessage2, null );
        jPanel1.add( lMessage3, null );
        jPanel1.add( lMessage1, null );
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

    /**
     * Задание ном погрешностей эталонных значений от УАКСИ.
     * Заполняются только не нулевые данные.
     */
    protected void assignAccDelta() {
        int tableCount = datas.size();
        for ( int i = 0; i < tableCount; i++ ) {
            MeasureTable table = datas.getTable( i );
            String type = table.getProperty( "type" );
            for ( int rr = 0; rr < table.size(); rr++ ) {
                MeasureChain mc = table.getChain( rr );
                int points = mc.size();
                for ( int pnt = 0; pnt < points; pnt++ ) {
                    MeasurePoint mp = mc.getPoint( pnt );
                    // изменяем значение только для не пустого замера
                    if ( !mp.isEmpty() ) {
                        mp.accurate.delta = unit.getErrorLimitFor(channelNameToPlane(type), null);
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
                ex.printStackTrace();
            }
        }
    }

    void doMeasure( MeasureTable table, int row, char plane ) {
        //Channel achan = unit.getChannel( "angles" );
        //Channel tchan = toolSource.getChannel( "angles" );
        //if ( achan == null || tchan == null ) {
        //    System.err.println( "Not available channel for measure" );
        //    return;
        //}
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
            Value val = doMeasureAccurate( plane );
            System.out.print( "Etalon: " + val.toString(2) );
            point.setAccurateValue( doMeasureAccurate( plane ) );
            System.out.print( ',' );
            Value value = doMeasureTool( plane );
            value.value = nearestValueTo(value.value, val.value);
            System.out.print( "Tool: " + value.toString(2) );
            point.setToolValue( value );
            lMessage3.setText( "" );
        }
        chain.calc();

    }

    /**
     * Приведение кругового значения ближайшего к заданному
     * @param tool double приводимое знаечение
     * @param to double относительно чего рассматривается
     * @return double
     */
    public static double nearestValueTo(double tool, double to) {
        double ret = tool;
        double v1 = Math.abs(to - tool);
        double v2 = Math.abs(to - tool + 360.0);
        if( v1 > v2) {
            ret = tool - 360.0;
        }
        return ret;
    }

    protected int planeToIndex( char plane ) {
        int subchan = 0;
        if ( plane == 'y' ) {
            subchan = 1;
        } else if ( plane == 'z' ) {
            subchan = 2;
        }
        return subchan;
    }
    /**
     * Возвращает имя плоскости для указанного канала
     * azimuth = x, zenith = y, rotate = z
     * @param channel String
     * @return char
     */
    public static char channelNameToPlane( String channel ) {
        char plane = 'z';
        if ( channel.equals("azimuth") ) {
            plane = 'x';
        } else if ( channel.equals("zenith") ) {
            plane = 'y';
        }
        return plane;
    }

    Value doMeasureAccurate( char plane ) {
        double val = 0;
        double err = 0;
        int subchan = planeToIndex( plane );
        Channel chan = unit.getChannel( "angles" );
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
    }

    Value doMeasureTool( char plane ) {
        double value = 0;
        double err = 0;
        int subchan = planeToIndex( plane );
        String name = planeToName( plane );
        //Channel chan = toolSource.getChannel( "angles" );
        //Channel chan2 = toolSource.getChannel( "errnorm" );
        //if ( chan == null ) {
            /** @todo throw exce */
            //return new Value();
        //}
        for ( int i = 0; i < 4; i++ ) {
            toolSource.waitNewData();
            //System.out.print("# getting tool value for: "+name);
            ChannelValue cv = toolSource.getValue(name);
            if(cv != null) {
                Value v1 = cv.getAsValue();
                if ( v1.value > 350 ) {
                    v1.value -= 360;
                }
                value += v1.value;
                err += v1.delta;
            }
        }
        value /= 4.0;
        err /= 4.0;
        return new Value(value, err);
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
            ex.printStackTrace();
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

    String planeToName( char plane ) {
        String name = "unknown";
        if ( plane == 'x' ) {
            name = "azimuth";
        } else
        if ( plane == 'y' ) {
            name = "zenith";
        }else
        if ( plane == 'z' ) {
            name = "rotate";
        }
        return name;
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
        selectedContainer = tables[rotateTableIndex];
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
        doMeasure( workTable, row, plane );
        table.repaint();
    }

    /**
     * Поиск максимального отклонения азимута в зависимости от поворота
     */
    public void doFindMaxDeviation() {
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

    }

    protected void startProcess() {
        execCommand( ( Command )cbDo.getSelectedItem() );
        System.out.println( "Process started" );
    }
/*
    protected void execCommand( Command command ) {
        if ( commandExecuter != null ) {
            if ( commandExecuter.isAlive() ) {
                return;
            }
        }
        commandExecuter = new CommandExecuter( command );
        //executer.setDaemon(true);
        //System.out.println( "Process started" );
        commandExecuter.start();

    }

*/

    protected void updateTime() {
        lTimeStart.setText( TextUtil.dateToString( startTime, "HH:mm:ss" ) );
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

    protected void switchOff() {
        if ( unit != null ) {
            //unit.removeAllListeners();
            unit.removeSignalListeners();
            unit.disconnect();
        }
    }

    protected void quit() {
        if ( JOptionPane.showConfirmDialog( this, "Выйти?",
                                            "Выход из модуля",
                                            JOptionPane.YES_NO_OPTION ) == 0 ) {
            switchOff();
            dispose();
        }

    }


    protected void buildToolLabel() {
        StringBuffer str = new StringBuffer(
            "<html><table width=200 border=1><tr><th>" );
        str.append( toolValues.azimuth.toString() );
        str.append( "</th><th>" );
        str.append( toolValues.zenith.toString() );
        str.append( "</th><th>" );
        str.append( toolValues.rotate.toString() );
        str.append( "</th></tr></table>" );
    }

    protected void switchConnect() {
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


    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            quit();
            //System.exit( 0 );
        }
    }


    protected void buildProtocol() {
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
            HtmlViewer htmlViewer = new HtmlViewer();
            htmlViewer.setText( out.toString() );
            htmlViewer.view();
            out.close();
        } catch ( Exception ex ) {
            System.out.println(ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    protected void buildSertificate() {
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
        } catch ( Exception ex ) {
            ex.printStackTrace();
            ex.printStackTrace();
        }
  }

}
