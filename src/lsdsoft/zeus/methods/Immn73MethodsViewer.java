package lsdsoft.zeus.methods;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.ui.*;
import javax.swing.event.*;
import lsdsoft.util.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.report.*;
import java.io.*;

/**
 * <p>Title: Обозреватель методики калибровки для приборов ИММН-73</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Ural-Geo 2004-2010</p>
 * @author lsdsoft, de-nos
 * @version 1.0
 */
public class Immn73MethodsViewer extends BaseMethodsViewer implements SignalEventListener {
    Command commandMeasure = new Command( this, "doMeasure", "замер в точке", "" );

    Command[] commands = {
        new Command( this, "doCalibrationFull", "калибровка полностью", "" ),
        new Command( this, "doCalibrationZenith", "калибровка зенитного угла", "" ),
        new Command( this, "doCalibrationAzimuth", "калибровка азимутального угла", "" ),
        new Command( this, "doCalibrationAzimuthPart", "калибровка азимутального угла частично", "" ),
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
    InclinometerValues toolValues = new InclinometerValues();
    InclinometerValues accValues = new InclinometerValues();
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
    //CommandExecuter executer;
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
    //JPopupMenu tablePopup = new JPopupMenu();
    //JMenuItem miChange = new JMenuItem();
    //JMenuItem miMeasure = new JMenuItem();
    JButton bProtocol = new JButton();
    private JButton bSertificate = new JButton();
    private JLabel operatorLabel = new JLabel("Калибровщик");
    private JTextField operatorTextField = new JTextField();
    private JLabel temperatureLabel = new JLabel("Темп-ра воздуха:");
    private JTextField temperatureTextField = new JTextField();

    public Immn73MethodsViewer() {
        try {
            //jbInit();

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void setProperties( Properties props ) {
        properties = ( Properties ) props.clone();
    }

    protected void selectTables() {
        Vector tables = datas.selectTables( "type", "azimuth" );
        if ( tables.size() > 0 ) {
            this.tables[1].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        tables = datas.selectTables( "type", "zenith" );
        if ( tables.size() > 0 ) {
            this.tables[0].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        /*
        tables = datas.selectTables( "type", "rotate" );
        if ( tables.size() > 0 ) {
            this.tables[0].measureTable = ( MeasureTable ) tables.get( 0 );
        }
*/
    }

    protected void initZenithTab() {
        Vector tables = datas.selectTables( "type", "zenith" );
        int size = tables.size();
        this.tables[0].init();
        //cbZenRotates.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            //MeasureTable table = ( MeasureTable ) tables.get( i );
            //String value = table.getProperty( "rotate" );
            //if ( value != null ) {
            //    cbZenRotates.addItem( value );
           // }
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
            if(tables[i].measureTable == null )
                continue;
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
        Vector tables = datas.selectTypedTables( "azimuth", "zenith", value );
        if ( tables.size() > 0 ) {
            this.tables[1].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        this.tables[1].init();
    }

    protected void changeZenithTable() {
        //String value = ( String ) cbZenRotates.getSelectedItem();
        //Vector tables = datas.selectTypedTables( "zenith", "rotate", value );
        //if ( tables.size() > 0 ) {
        //    this.tables[1].measureTable = ( MeasureTable ) tables.get( 0 );
        //}
        this.tables[0].init();
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
        imgDigTX.repaint();
        imgDigTY.repaint();
        imgDigTZ.repaint();
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
        imgDigAX.repaint();
        imgDigAY.repaint();
        imgDigAZ.repaint();
    }

    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = toolSource.getChannel( "angles" );
                if ( chan == null ) {
                    chan = toolSource.getChannel( "values" );
                }
                ChannelValue val = toolSource.getValue("azimuth");
                //if ( chan != null ) {
                //toolAngles.azimut.setAngle(chan.getValue( 0 ).doubleValue);
                if( val != null )
                    toolValues.azimuth = val.getAsValue();

                val = toolSource.getValue("zenith");
                if( val != null )
                    toolValues.zenith = val.getAsValue();

                val = toolSource.getValue("rotate");
                if( val != null )
                    toolValues.rotate = val.getAsValue();

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
                    accValues.azimuth = chan.getValue( 0 ).getAsValue();
                    accValues.zenith = chan.getValue( 1 ).getAsValue();
                    accValues.rotate = chan.getValue( 2 ).getAsValue();
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
            zeus.log.info("IMMN73 calibration starts");
            unit = DataFactory.createCheckUnit();
            unit.addSignalListener( this );
            boolean b = Boolean.valueOf( zeus.getProperty( Zeus.PROP_WORK_NEW ) ).
                booleanValue();
            if ( b ) {
                datas = DataFactory.createMeasureDatas( DataFactory.getMethods(
                    "immn73.calib.xml" ) );
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
            UiUtils.showError( this, ex.getMessage() );
            //ex.printStackTrace();
        }
        if ( !quit ) {
            this.setVisible( true );
        }
        if (Zeus.getInstance().getProperty("debug", "off").equals("on")) {
            JOptionPane.showMessageDialog( this, "Включен режим отладки.", "Информация",
                                           JOptionPane.INFORMATION_MESSAGE );
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
        this.setSize( 790, 480 );
        this.setResizable(false);
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
        jToolBar1.setBounds( new Rectangle( 1, 1, 780, 41 ) );
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
        operatorTextField.setMaximumSize(new Dimension(160, 28));
        //operatorTextField.deletesetCaretPosition();
        operatorTextField.addKeyListener(new KeyListener(){
            public void keyTyped( KeyEvent e ) {}
            public void keyPressed( KeyEvent e ) {}
            public void keyReleased( KeyEvent e ) {
                /** @todo херня какая-то.... :(   В JTextField не работают кнопки delete и backspace */
                Zeus.getInstance().setProperty("operator_edited", operatorTextField.getText());
                //operatorLabel.setText(Zeus.getInstance().getProperty("operator_edited"));
            }
        });
        temperatureTextField.setText(Zeus.getInstance().getProperty("temperature"));
        temperatureTextField.setMaximumSize(new Dimension(40, 28));
        temperatureTextField.addKeyListener(new KeyListener(){
            public void keyTyped( KeyEvent e ) {}
            public void keyPressed( KeyEvent e ) {}
            public void keyReleased( KeyEvent e ) {
                /** @todo херня какая-то.... :(   В JTextField не работают кнопки delete и backspace */
                Zeus.getInstance().setProperty("temperature_edited", temperatureTextField.getText());
                //operatorLabel.setText(Zeus.getInstance().getProperty("operator_edited"));
            }
        });
        Zeus.getInstance().setProperty("operator_edited", Zeus.getInstance().getProperty("operator",""));
        Zeus.getInstance().setProperty("temperature_edited", Zeus.getInstance().getProperty("temperature",""));

        //tabbedTables.add( panelZ, "Угол поворота" );

        tables[0].scrollPane.setBounds( new Rectangle( 1, 41, 473, 169 ) );
        tables[1].scrollPane.setBounds( new Rectangle( 0, 38, 473, 179 ) );

        panelX.add( tables[1].scrollPane, null );
        panelY.add( tables[0].scrollPane, null );
        //panelZ.add( tables[0].scrollPane, BorderLayout.CENTER );

        tabbedTables.add( panelY, "Зенитный угол" );
        //panelY.add( jLabel1, null );
        //panelY.add( cbZenRotates, null );
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
        String alias = planeToName( plane );
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
    String planeToName( char plane ) {
        if( plane == 'x' ) {
            return "azimuth";
        }
        if( plane == 'y' ) {
            return "zenith";
        }
        if( plane == 'z' ) {
            return "rotate";
        }
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
        //doJoinRotates();
        //doCalibrationRotate();
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
        changeZenithTable();
        doCalibrationZenithPart();
//        int size = cbZenRotates.getItemCount();
//        for ( int i = 0; i < size; i++ ) {
//            cbZenRotates.setSelectedIndex( i );
//            changeZenithTable();
//        }

    }

    public void doCalibrationZenithPart() {
        clearMessages();
        selectedContainer = tables[0];
        lMessage1.setText( "Калибровка зенитного угла" );
        MeasureTable workTable = selectedContainer.measureTable;
        //Double rotate = new Double( workTable.getProperty( "rotate" ) );
        //doGoTo( 'z', rotate );
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
        selectedContainer = tables[1];
        lMessage1.setText( "Калибровка азимутального угла" );
        MeasureTable workTable = selectedContainer.measureTable;
        Double zenith = new Double( workTable.getProperty( "zenith" ) );
        doGoTo( 'y', zenith );
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
            //if( !unit.isConnected() ) {
            //    unit.connect();
            //}
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



    void bSave_actionPerformed( ActionEvent ev ) {
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
    void bSertificate_actionPerformed(ActionEvent e) {
        try {
            String path = Zeus.getInstance().getProperty("report.path");
            //System.out.println("CP: " + System.getProperty("file.encoding"));
            //System.out.println(org);
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
        }

    }

}
