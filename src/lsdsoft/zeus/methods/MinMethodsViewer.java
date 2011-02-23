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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.*;


/**
 * <p>Title: Обозреватель методики калибровки для приборов МИН, МИН+ГК</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class MinMethodsViewer
    extends BaseMethodsViewer
    implements SignalEventListener {

    Command commandMeasure = new Command( this, "doMeasure", "замер в точке", "" );

    Command[] commands = {
        new Command( this, "doCalibrationFull", "калибровка полностью", "" ),
        new Command( this, "doCalibrationRotatePart",
                     "калибровка угла поворота частично", "" ),
        new Command( this, "doCalibrationRotate", "калибровка угла поворота",
                     "" ),
        new Command( this, "doCalibrationZenith", "калибровка зенитного угла",
                     "" ),
        new Command( this, "doCalibrationZenithPart",
                     "калибровка зенитного угла частично", "" ),
        new Command( this, "doCalibrationAzimuth",
                     "калибровка азимутального угла", "" ),
        new Command( this, "doCalibrationAzimuth",
                     "калибровка азимутального угла при З", "" ),
        new Command( this, "doCalibrationAzimuthAtZenith",
                     "калибровка азимутального угла частично", "" ),
        new Command( this, "doJoinRotates", "совмещение углов поворота", "" ),
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
    DecimalFormat df;

    int selectedCommand = 1;
    InclinometerAngles toolAngles = new InclinometerAngles();
    InclinometerAngles accAngles = new InclinometerAngles();
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
    JComboBox cbRotateZens = new JComboBox();
    JComboBox cbZenRotates = new JComboBox();
    JComboBox cbAzZeniths = new JComboBox();
    JComboBox cbAzRotates = new JComboBox();
    JLabel lAzRotates = new JLabel();

    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
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
    JLabel lRotateZens = new JLabel();

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
    //JPopupMenu tablePopup = new JPopupMenu();
    //JMenuItem miChange = new JMenuItem();
    //JMenuItem miMeasure = new JMenuItem();
    JButton bProtocol = new JButton();



    public MinMethodsViewer() {
        try {
            df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
            df.applyPattern("#0.00");
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

    protected void initRotateTab() {
        Vector tables = datas.selectTables( "type", "rotate" );
        int size = tables.size();
        cbRotateZens.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "zenith" );
            if ( value != null ) {
                cbRotateZens.addItem( value );
            }
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
        Vector tbz = new Vector();
        int size = tables.size();
        cbAzZeniths.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "zenith" );
            if ( value != null ) {
                //cbAzZeniths.get
                //String str = (String)tbz.get(0);
                if( !tbz.contains(value)) {
                    cbAzZeniths.addItem( value );
                    tbz.add(value);
                }
            }
        }
        cbAzRotates.removeAllItems();
        tbz.clear();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "rotate" );
            if ( value != null ) {
                if( !tbz.contains(value)) {
                    cbAzRotates.addItem( value );
                    tbz.add(value);
                }
            }
        }
        tbz.clear();
    }

    protected void buildTables() {
        for ( int i = 0; i < 3; i++ ) {
            tables[i].init();
            tables[i].table.addMouseListener( mouseAdapter );
            //tables[i].table.setCellSelectionEnabled(false);
            tables[i].table.setSelectionMode( ListSelectionModel.
                                              SINGLE_SELECTION );
            tables[i].table.add( tablePopup );
        }

    }

    protected void changeAzimuthTable() {
        String value = ( String ) cbAzZeniths.getSelectedItem();
        String valRotate = ( String ) cbAzRotates.getSelectedItem();

        Vector tables = datas.selectTypedTables( "azimuth", "zenith", value );
        if ( tables.size() > 0 ) {
            // выборка из списка таблицы с указанным углом поворота
            for(int i = 0; i < tables.size(); i++) {
                MeasureTable table = ( MeasureTable )tables.get( i );
                if(table.getProperty("rotate").equals(valRotate)) {
                    //System.out.println("sel table " + value + " " + valRotate);
                    this.tables[2].measureTable = table;
                    break;
                }
            }
        }
        // TODO
        this.tables[2].init();
    }

    protected void changeRotateTable() {
        String value = ( String ) cbRotateZens.getSelectedItem();
        Vector tables = datas.selectTypedTables( "rotate", "zenith", value );
        if ( tables.size() > 0 ) {
            this.tables[0].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        this.tables[0].init();
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

    public String formatValue(Value val) {
         String s;
         s = df.format(val.value) + " @ " + df.format( val.delta );
         return s;
     }

    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                //Channel chan = toolSource.getChannel( "angles" );
                //if ( chan == null ) {
                //    chan = toolSource.getChannel( "values" );
               // }
                ChannelValue val;// = toolSource.getValue("azimuth");
                Channel chan = toolSource.getChannel( "angles" );
                if ( chan == null ) {
                    chan = toolSource.getChannel( "values" );
                }

                val = toolSource.getValue("zenith");
                double zenith = toolAngles.zenith.getValue();
                if( val != null ) {
                    val.getAsValue().delta = 0.2;
                    zenith = val.getAsDouble();
                    toolAngles.zenith.setAngle( zenith );
                }
                val = toolSource.getValue("azimuth");
                if( val != null ) {
                    toolAngles.azimuth.setAngle( val.getAsDouble() );
                }
                val = toolSource.getValue("rotate");
                if( val != null ) {
                    toolAngles.rotate.setAngle( val.getAsDouble() );
                }
                if ( chan != null ) {
                    System.out.println( "### tool" );
                    {
                        Value val1;
                        //------ process zenith degree
                        val1 = chan.getValue( 0 ).getAsValue();
                        double zen = val1.value;
                        double err = 100;
                        System.out.println( "Zn=" + formatValue( val1 ) + ';' );
                        //------ process azimuth degree
                        val1 = chan.getValue( 1 ).getAsValue();
                        if(zen < 6.5) {
                            err = 0.125/Math.sin(zen/180.0*Math.PI) + 0.4;
                        } else {
                            err = 1.5;
                        }
                        val1.delta = err;
                        System.out.println( "Az=" + formatValue( val1 ) + ';' );
                        //------ process vizir (apsida)
                        val1 = chan.getValue( 2 ).getAsValue();
                        if(zen < 6.5) {
                            err = 0.125/Math.sin(zen/180.0*Math.PI)+ 0.4;
                        } else {
                            err = 1.5;
                        }
                        val1.delta = err;
                        System.out.println( "Rt=" + formatValue( val1 ) + ';' );
                        //------ process zenith (magnet)
                        val1 = chan.getValue( 3 ).getAsValue();
                        if ( (zen >= 0) && (zen <= 3) ) {
                            err = 2;
                        } else {
                            err = 4*Math.sin(zen/180.0*Math.PI)+1.8;
                        }
                        val1.delta = err;
                        System.out.println( "Vz=" + formatValue( val1 ) + ';' );
                    }
                }
                    //toolAngles.zenit = chan.getValue( 1 ).angle;
                    //toolAngles.rotate = chan.getValue( 2 ).angle;
                    //toolAngles.zenit.setAngle(chan.getValue( 1 ).doubleValue );
                    //toolAngles.rotate.setAngle( chan.getValue( 2 ).doubleValue );

                    redrawToolValues();
                //}

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
            setErrorLimits();
            datas.ensurePointsCount(4);
            datas.calc();
            selectTables();
            buildTables();
            initRotateTab();
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
            UiUtils.toScreenCenter( this );
            selectedContainer = tables[0];
            tabbedTables.setSelectedIndex( 0 );
            redrawAccurateValues();
            redrawToolValues();
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

        //table.setMinimumSize( new Dimension( , 0 ) );
        //table.setPreferredSize( new Dimension( 0, 0 ) );
        this.getContentPane().setLayout( null );
        this.setSize( 800, 600 );
        this.setState( Frame.NORMAL );
        this.setTitle( "Калибровка прибора " + workState.getToolName() + " № " + workState.getToolNumber());
        //this.setExtendedState( 6 );
        tabbedTables.setBounds( new Rectangle( 9, 41, 478, 244 ) );
        //tabbedTables.addTab("Угол поворота", table);
        Font font = new Font( "Dialog", 0, 14 );
        // init tableX
        panelY.setMaximumSize( new Dimension( 500, 500 ) );
        panelY.setLayout( null );
        //scrollPaneY.setAutoscrolls( true );
        //scrollPaneY.setDebugGraphicsOptions( 0 );



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
        bProtocol.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bProtocol_actionPerformed( e );
            }
        } );
        tables[2].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );
        tables[1].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );
        tables[0].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );

        // --- закладка "угол поворота"
        tabbedTables.add( panelZ, "Угол поворота" );

        lRotateZens.setText("Зенитный угол:");
        lRotateZens.setBounds(7, 11, 124, 15);
        cbRotateZens.setBounds( new Rectangle( 110, 7, 115, 24 ) );
        cbRotateZens.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                changeRotateTable();
            }
        } );
        panelZ.setLayout( null );
        panelZ.add( lRotateZens, null );
        panelZ.add( cbRotateZens, null );
        panelZ.add( tables[0].scrollPane, null );


        // --- закладка "Зенитный угол"
        tabbedTables.add( panelY, "Зенитный угол" );
        panelY.add( tables[1].scrollPane, null );
        panelY.add( jLabel1, null );
        panelY.add( cbZenRotates, null );
        jLabel1.setText( "Угол поворота:" );
        jLabel1.setBounds( new Rectangle( 7, 11, 124, 15 ) );
        cbZenRotates.setBounds( new Rectangle( 110, 7, 115, 24 ) );
        cbZenRotates.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                changeZenithTable();
            }
        } );

        // --- закладка "Азимутальный угол"
        tabbedTables.add( panelX, "Азимутальный угол" );
        panelX.setLayout( null );
        panelX.add( tables[2].scrollPane, null );
        panelX.add( jLabel2, null );
        panelX.add( cbAzZeniths, null );
        jLabel2.setText( "Зенитный угол:" );
        jLabel2.setBounds( new Rectangle( 7, 11, 134, 15 ) );
        lAzRotates.setText( "Угол поворота:" );
        lAzRotates.setBounds( new Rectangle( 252, 11, 134, 15 ) );
        cbAzZeniths.setBounds( new Rectangle( 110, 7, 115, 24 ) );
        cbAzZeniths.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                changeAzimuthTable();
            }
        } );
        cbAzRotates.setBounds( new Rectangle( 350, 7, 115, 24 ) );
        cbAzRotates.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                changeAzimuthTable();
            }
        } );
        panelX.add( cbAzRotates, null );
        panelX.add( lAzRotates, null );


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
                point.setAccurateValue( doMeasureAccurate( plane ) );
                System.out.print( ',' );
                Value value = doMeasureTool( plane );
                if ( value.value > 350 ) {
                    value.value -= 360;
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

    Value doMeasureTool( char plane ) {
        Value val = new Value();
            double value = 0;
            double err = 0;
            int subchan = planeToIndex( plane );
            Channel chan = toolSource.getChannel( "angles" );
            Channel chan2 = toolSource.getChannel( "errnorm" );
            //if ( chan == null ) {
                /** @todo throw exce */
            //    return val;
           // }
            for ( int i = 0; i < 4; i++ ) {
                toolSource.waitNewData();
                //double v1 = chan.getValue( subchan ).getAsDouble();
                Value vv = toolSource.getValue(planeToName(plane)).getAsValue();
                if ( vv.value > 350 ) {
                    vv.value -= 360;
                }
                val.add(vv);
            }
            val.value /= 4.0;
            val.delta /= 4.0;
            return val;
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
        ang.rotate.setAngle( doMeasureAccurate( 'z' ) - doMeasureTool( "rotate" ) );
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

    private String planeToName( char plane ) {
        if( plane == 'x' )
            return "azimuth";
        if( plane == 'y' )
            return "zenith";
        if( plane == 'z' )
            return "rotate";
        if( plane == 'v' )
            return "vizir";
        return "";
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
                Thread.sleep( delayBeforeMeasure );
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

    public void doCalibrationRotatePart() {
        clearMessages();
        selectedContainer = tables[0];
        lMessage1.setText( "Калибровка угла поворота" );
        MeasureTable workTable = selectedContainer.measureTable;
        Number dd = null;
        try {
            dd = df.parse( workTable.getProperty( "zenith" ) );
        } catch ( ParseException ex ) {
            System.err.println("error parsing input at"+ex.getErrorOffset());
        }
        Double zenith = new Double( dd.doubleValue() );
        doGoTo( 'y', zenith );
        doCalibrationTable( workTable );
        lMessage2.setText( "Калибровка выполнена" );

    }
    public void doCalibrationRotate() {
        int size = cbRotateZens.getItemCount();
        for ( int i = 0; i < size; i++ ) {
            cbRotateZens.setSelectedIndex( i );
            changeRotateTable();
            doCalibrationRotatePart();
        }

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
        int zens = cbAzZeniths.getItemCount();
        //int rots = cbAzRotates.getItemCount();
        for ( int i = 0; i < zens; i++ ) {
            cbAzZeniths.setSelectedIndex( i );
            doCalibrationAzimuthAtZenith();
        }
    }

    public void doCalibrationAzimuthAtZenith() {
        int rots = cbAzRotates.getItemCount();
        for ( int rot = 0; rot < rots; rot++ ) {
            cbAzRotates.setSelectedIndex( rot );
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
        Double rotate = new Double( workTable.getProperty( "rotate" ) );
        doGoTo( 'y', zenith );
        doGoTo( 'z', rotate );
        //lMessage2.setText( "Поиск максимального отклонения" );
        // перед выборкой угла поворота выставляем прибор на север
        //doGoTo('x', new Double(0) );
        //doFindMaxDeviation();
        doCalibrationTable( workTable );
        lMessage2.setText( "Калибровка выполнена" );
    }

    public void doChange() {
        selectedContainer = tables[tabbedTables.getSelectedIndex()];
        JTable table = selectedContainer.table;
        int row = table.getSelectedRow();
        if(row >= 0) {
            MeasureChainEditor.edit( selectedContainer.measureTable.
                                     getChain( row ), this );
            selectedContainer.measureTable.calc();
        }
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
            Value tool = doMeasureTool('x');
            double d = Math.abs(acc - tool.value);
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
            String filename =  path + "/" + datas.getToolType() + "_" + datas.getToolNumber()+ ".html";
            //FileWriter file = new FileWriter( filename );
            //StringWriter out = new StringWriter(8192);
            FileOutputStream outs = new FileOutputStream( filename );
            OutputStreamWriter osw = new OutputStreamWriter( outs, "Windows-1251" );
            IMProtocol prot = new IMProtocol( osw );
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
        }

    }

}
