package lsdsoft.test;

import lsdsoft.metrolog.unit.*;
import javax.swing.*;
import java.awt.*;
import lsdsoft.metrolog.*;
import com.lsdsoft.comm.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.awt.image.*;
import lsdsoft.zeus.ui.*;
import lsdsoft.units.*;
import javax.swing.event.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.*;
import lsdsoft.util.*;

/**
 * <p>Title: Проверка работы УАК-СИ</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */
public class TestUAKSI2
    extends AbstractMethodsViewer
    implements ChannelDataEventListener,
    SignalEventListener,
    RespondEventListener {
    private DigitalDisplay display1 = new DigitalDisplay( 7, 3 );
    private DigitalDisplay display2 = new DigitalDisplay( 7, 3 );
    private DigitalDisplay display3 = new DigitalDisplay( 7, 3 );
    private ImageIcon connOff = Zeus.createImageIcon( "images/conn_off2.png" );
    private ImageIcon connOn = Zeus.createImageIcon( "images/conn_on2.png" );
    private ImageIcon iconPC = Zeus.createImageIcon( "images/pc.png" );
    private ImageIcon iconGear = Zeus.createImageIcon( "images/oh.png" );
    private ImageIcon iconUaksi = Zeus.createImageIcon( "images/uaksi2.png" );
    private ImageIcon iconGear1 = Zeus.createImageIcon( "images/gear_1.png" );
    private ImageIcon iconGear2 = Zeus.createImageIcon( "images/gear_2.png" );
    private ImageIcon iconGear3 = Zeus.createImageIcon( "images/gear_3.png" );
    private ImageIcon iconGear4 = Zeus.createImageIcon( "images/gear_4.png" );
    private ImageIcon gears[] = { iconGear1, iconGear2, iconGear3, iconGear4 };
    private ImageIcon iconGB_ON = Zeus.createImageIcon( "images/green_but_on.png" );
    private ImageIcon iconGB_OFF = Zeus.createImageIcon( "images/green_but_off.png" );
    private ImageIcon iconRB_ON = Zeus.createImageIcon( "images/red_but_on.png" );
    private ImageIcon iconRB_OFF = Zeus.createImageIcon( "images/red_but_off.png" );
    private ImageIcon iconYB_ON = Zeus.createImageIcon( "images/yellow_but_on.png" );
    private JLabel imgPC = new JLabel( iconPC );
    private JLabel imgUaksi = new JLabel( iconUaksi );
    private JImage imgGear = new JImage( iconGear1.getImage() );
    private JImage imgDig1 = new JImage( display1.getImage() );
    private JImage imgDig2 = new JImage( display2.getImage() );
    private JImage imgDig3 = new JImage( display3.getImage() );
    private JImage imgButSX = new JImage( iconGB_OFF.getImage() );
    private JImage imgButSY = new JImage( iconGB_OFF.getImage() );
    private JImage imgButSZ = new JImage( iconGB_OFF.getImage() );
    private JImage imgButMX = new JImage( iconGB_OFF.getImage() );
    private JImage imgButMY = new JImage( iconGB_OFF.getImage() );
    private JImage imgButMZ = new JImage( iconGB_OFF.getImage() );
    private JLabel lpostUaksi = new JLabel();
    private JLabel lpostWt = new JLabel();
    private UAKSI2CheckUnit cunit;
    private String portName = "COM1";
    int counter = 0;
    int gearStage = 0;
    javax.swing.Timer timer;
    JTextField jTextField1 = new JTextField();
    JButton bConn = new JButton();
    JSlider sSpeed = new JSlider();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JPanel jPanel1 = new JPanel();
    Border border1;
    JButton bMotorX = new JButton();
    JButton bMotorZ = new JButton();
    //JTextField tSteps = new JTextField();
    JButton bMotorY = new JButton();
    Border border2;
    TitledBorder titledBorder1;
    JRadioButton rgCW = new JRadioButton();
    JRadioButton rgCCW = new JRadioButton();
    Border border3;
    TitledBorder titledBorder2;
    private Border border4;
    private JComboBox cbPorts = new JComboBox();
    private TitledBorder titledBorder3;
    private Border border5;
    JLabel jLabel6 = new JLabel();

    char usedMotor = 'x';
    JButton bFindMarker = new JButton();
    JPanel jPanel2 = new JPanel();
    Border border6;
    TitledBorder titledBorder4;
    JFormattedTextField tGotoX = new JFormattedTextField();
    JLabel jLabel5 = new JLabel();
    JButton bGotoX = new JButton();
    JFormattedTextField tGotoY = new JFormattedTextField();
    JButton bGotoY = new JButton();
    JLabel jLabel7 = new JLabel();
    JFormattedTextField tGotoZ = new JFormattedTextField();
    JButton bGotoZ = new JButton();
    JLabel jLabel8 = new JLabel();

    ION1CorrectionTable table = new ION1CorrectionTable( "211103" );
    JButton bFindMarker1 = new JButton();
    JButton bFindMarker2 = new JButton();
    JButton bStop = new JButton();
    JCheckBox cbMakeCorrection = new JCheckBox();

    protected static ImageIcon createImageIcon( String path ) {
        java.net.URL imgURL = TestUAKSI.class.getResource( path );
        if ( imgURL != null ) {
            return new ImageIcon( imgURL );
        } else {
            System.err.println( "Couldn't find file: " + path );
            return null;
        }
    }

    public TestUAKSI2() {
        cunit = (UAKSI2CheckUnit)DataFactory.createCheckUnit(UAKSI2CheckUnit.class.getName());
        //cunit.setPortName( portName );
        cunit.addSignalListener( this );
        cunit.setNumber(Zeus.getInstance().getToolNumber());
        cunit.init();

        display1.renderClear();
        display2.renderClear();
        display3.renderClear();
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            //table.load();
            jbInit();

        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public void start() {
        boolean quit = false;
        UiUtils.toScreenCenter(this);

        this.setVisible( true );
    }

    public static void main( String[] args ) {
        TestUAKSI2 testUAKSI2 = new TestUAKSI2();
        testUAKSI2.setVisible( true );
    }
    private void rotateGear() {
        gearStage++;
        if(gearStage >= gears.length ) {
            gearStage = 0;
        }
        imgGear.setImage( gears[gearStage].getImage() );
        imgGear.repaint();
    }
    private void refreshIndicators() {
        Channel chan = cunit.getChannel( "sensors" );
        if( chan != null ) {
            lightIndicator( imgButSX,
                            chan.getValue( 9 ).getAsInteger() );
            lightIndicator( imgButSY,
                            chan.getValue( 10 ).getAsInteger() );
            lightIndicator( imgButSZ,
                            chan.getValue( 11 ).getAsInteger() );
            lightIndicator( imgButMX,
                            chan.getValue( 12 ).getAsInteger() );
            lightIndicator( imgButMY,
                            chan.getValue( 13 ).getAsInteger() );
            lightIndicator( imgButMZ,
                            chan.getValue( 14 ).getAsInteger() );
        }
    }

    private void lightIndicator( JImage img, int state ) {
        Image image = null;
        if(!cunit.isConnected() ) {
            image = iconGB_OFF.getImage();
        } else {
            switch ( state ) {
                case UAKSI2CheckUnit.DEVICE_STATE_BAD:
                    image = iconRB_ON.getImage();
                    break;
                case UAKSI2CheckUnit.DEVICE_STATE_ABSENT:
                    image = iconYB_ON.getImage();
                    break;
                case UAKSI2CheckUnit.DEVICE_STATE_OK:
                    image = iconGB_ON.getImage();
                    break;
                case UAKSI2CheckUnit.DEVICE_STATE_ERROR:
                    image = iconRB_ON.getImage();
                    break;
                default:
                    image = iconRB_ON.getImage();
            }
        }
        img.setImage( image );
        img.repaint();
    }
    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        lpostWt.setText(cunit.postWt);
        if ( src.equals( cunit ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                lpostUaksi.setText(cunit.postUaksi);

                rotateGear();
                refreshIndicators();
                Channel chan = cunit.getChannel( "angles" );
                if ( chan != null ) {
                    //accAngles.azimut = chan.getValue( 0 ).angle;
                    //accAngles.zenit = chan.getValue( 1 ).angle;
                    //accAngles.rotate = chan.getValue( 2 ).angle;
                    redrawAccurateValues();
                }

            }
        }
    }

    public void channelEvent( ChannelDataEvent ev ) {

        /*
             if(ev.getChannel() == 0) {
          labelX.setText(String.valueOf(ev.getValue().angle.toString()));
             }
             if(ev.getChannel() == 1) {
          labelY.setText(String.valueOf(ev.getValue().angle.toString()));
             }
         */
    }

    private void jbInit() throws Exception {
        lpostUaksi.setBounds(3, 420, 700, 20 );
        lpostWt.setBounds(3, 440, 700, 20 );

        border1 = new TitledBorder( BorderFactory.createLineBorder( Color.black,
            1 ), "Поворот" );
        border2 = BorderFactory.createLineBorder( Color.black, 1 );
        titledBorder1 = new TitledBorder( border2, "Поворот" );
        border3 = BorderFactory.createLineBorder( Color.black, 1 );
        titledBorder2 = new TitledBorder( BorderFactory.createLineBorder( Color.
            gray, 1 ), "Подключение по..." );
        border4 = BorderFactory.createLineBorder( Color.white, 1 );
        titledBorder3 = new TitledBorder( "" );
        border5 = BorderFactory.createLineBorder( SystemColor.controlText, 2 );
        border6 = BorderFactory.createLineBorder( SystemColor.controlText, 1 );
        titledBorder4 = new TitledBorder( border6, "Выход на точку" );
        bConn.setBounds(new Rectangle(157, 372, 57, 33) );
        bConn.setFont( new java.awt.Font( "Dialog", 0, 12 ) );
        bConn.setBorder( null );
        bConn.setRolloverEnabled( true );
        bConn.setIcon( connOff );
        bConn.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bConn_actionPerformed( e );
            }
        } );
        jTextField1.setText( "jTextField1" );
        this.getContentPane().setLayout( null );
        sSpeed.setMajorTickSpacing( 25 );
        sSpeed.setMinorTickSpacing( 5 );
        sSpeed.setPaintLabels( true );
        sSpeed.setPaintTicks( true );
        sSpeed.setPaintTrack( true );
        sSpeed.setBackground( Color.lightGray );
        sSpeed.setOpaque( false );
        sSpeed.setLabelTable( sSpeed.createStandardLabels( 25 ) );
        sSpeed.setBounds( new Rectangle( 23, 127, 268, 46 ) );
        sSpeed.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                sSpeed_stateChanged( e );
            }
        } );
        sSpeed.addInputMethodListener( new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged( InputMethodEvent e ) {
            }

            public void caretPositionChanged( InputMethodEvent e ) {
                sSpeed_caretPositionChanged( e );
            }
        } );
        jLabel2.setFont( new java.awt.Font( "SansSerif", 0, 12 ) );
        jLabel2.setText( "Скорость (в % от макс.)" );
        jLabel2.setBounds( new Rectangle( 21, 110, 154, 15 ) );
        jLabel3.setText( "Датчик 'Азимут'" );
        jLabel3.setBounds(new Rectangle(40, 81, 120, 16) );
        jLabel4.setBounds(new Rectangle(260, 81, 100, 16) );
        jLabel4.setText( "Датчик 'Зенит'" );
        jPanel1.setEnabled( false );
        jPanel1.setFont( new java.awt.Font( "Dialog", 1, 12 ) );
        jPanel1.setBorder( titledBorder1 );
        jPanel1.setOpaque( false );
        jPanel1.setBounds(new Rectangle(20, 170, 337, 189) );
        jPanel1.setLayout( null );
        bMotorX.setBounds( new Rectangle( 169, 18, 107, 25 ) );
        bMotorX.setFont( new java.awt.Font( "SansSerif", 0, 12 ) );
        bMotorX.setText( "Двиг. 'Ау'" );
        bMotorX.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bMotorX_actionPerformed( e );
            }
        } );
        bMotorZ.setText( "Двиг. 'Ву'" );
        bMotorZ.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bMotorZ_actionPerformed( e );
            }
        } );
        bMotorZ.setFont( new java.awt.Font( "SansSerif", 0, 12 ) );
        bMotorZ.setBounds( new Rectangle( 169, 76, 107, 25 ) );
        bMotorY.setText( "Двиг. 'Зу'" );
        bMotorY.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bMotorY_actionPerformed( e );
            }
        } );
        bMotorY.setFont( new java.awt.Font( "SansSerif", 0, 12 ) );
        bMotorY.setBounds( new Rectangle( 169, 47, 107, 25 ) );
        rgCW.setFont( new java.awt.Font( "SansSerif", 0, 12 ) );
        rgCW.setOpaque( false );
        rgCW.setSelected( true );
        rgCW.setText( "По часовой" );
        rgCW.setBounds(new Rectangle(23, 28, 104, 24) );
        rgCW.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rgCW_actionPerformed( e );
            }
        } );
        rgCCW.setFont( new java.awt.Font( "SansSerif", 0, 12 ) );
        rgCCW.setOpaque( false );
        rgCCW.setText( "Против часовой" );
        rgCCW.setBounds(new Rectangle(23, 61, 116, 24) );
        rgCCW.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rgCCW_actionPerformed( e );
            }
        } );
        cbPorts.setBorder( border3 );
        cbPorts.setBounds(new Rectangle(83, 379, 73, 21) );
        jLabel6.setText( "Датчик 'Визир'" );
        jLabel6.setBounds(new Rectangle(480, 81, 120, 16) );
        bFindMarker.setBounds(new Rectangle(20, 142, 188, 26) );
        bFindMarker.setText( "Поиск маркера" );
        bFindMarker.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bFindMarker_actionPerformed( e );
            }
        } );
        jPanel2.setBorder( titledBorder4 );
        jPanel2.setBounds(new Rectangle(375, 170, 278, 186) );
        jPanel2.setLayout( null );

        tGotoX.setText( "0" );
        tGotoX.setBounds( new Rectangle( 50, 28, 68, 19 ) );
        tGotoX.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                bGotoX_actionPerformed( e );
            }
        });
        jLabel5.setText( "Ау:" );
        jLabel5.setBounds( new Rectangle( 11, 29, 25, 15 ) );
        bGotoX.setBounds( new Rectangle( 141, 23, 83, 25 ) );
        bGotoX.setText( "Пуск" );
        bGotoX.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bGotoX_actionPerformed( e );
            }
        } );

        tGotoY.setBounds(new Rectangle(50, 67, 68, 19) );
        tGotoY.setText( "0" );
        tGotoY.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                bGotoY_actionPerformed( e );
            }
        });
        bGotoY.setText( "Пуск" );
        bGotoY.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bGotoY_actionPerformed( e );
            }
        } );
        bGotoY.setBounds(new Rectangle(141, 63, 83, 25) );
        jLabel7.setBounds(new Rectangle(11, 67, 25, 15) );
        jLabel7.setText( "Зу:" );

        tGotoZ.setBounds(new Rectangle(50, 105, 68, 19) );
        tGotoZ.setText( "0" );
        tGotoZ.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                bGotoZ_actionPerformed( e );
            }
        });
        bGotoZ.setText( "Пуск" );
        bGotoZ.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bGotoZ_actionPerformed( e );
            }
        } );
        bGotoZ.setBounds(new Rectangle(141, 103, 83, 25) );
        jLabel8.setBounds(new Rectangle(11, 105, 40, 15) );
        jLabel8.setText( "Ву:" );
        bFindMarker1.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bFindMarker1_actionPerformed( e );
            }
        } );
        bFindMarker1.setText( "Поиск маркера" );
        bFindMarker1.setBounds(new Rectangle(240, 142, 188, 26) );
        bFindMarker2.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bFindMarker2_actionPerformed( e );
            }
        } );
        bFindMarker2.setText( "Сброс в '0'" );
        bFindMarker2.setBounds(new Rectangle(459, 142, 188, 26) );
        imgUaksi.setHorizontalTextPosition( SwingConstants.LEFT );
        imgDig1.setBounds(new Rectangle(19, 123, 1, 1));
        imgDig2.setBounds(new Rectangle(239, 103, 1, 1));
        imgDig3.setBounds(new Rectangle(459, 103, 1, 1));
        imgButSX.setBounds(new Rectangle(20, 85, 16, 16));
        imgButSY.setBounds(new Rectangle(240, 85, 16, 16));
        imgButSZ.setBounds(new Rectangle(460, 85, 16, 16));
        imgButMX.setBounds(new Rectangle(285, 22, 16, 16));
        imgButMY.setBounds(new Rectangle(285, 52, 16, 16));
        imgButMZ.setBounds(new Rectangle(285, 82, 16, 16));
        imgGear.setBounds(new Rectangle(210, 400, 32, 32));
        imgPC.setBounds(new Rectangle(100, 400, 48, 48));

        ButtonGroup group = new ButtonGroup();
        bStop.setBounds(new Rectangle(141, 142, 83, 25));
        bStop.setText("Стоп");
        bStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bStop_actionPerformed(e);
            }
        });
        cbMakeCorrection.setText( "Поправка за магнитную неоднородность" );
        cbMakeCorrection.setBounds( new Rectangle( 279, 375, 354, 23 ) );
        cbMakeCorrection.setSelected( cunit.getMakeMagneticCorrection() );
        cbMakeCorrection.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                cbMakeCorrection_actionPerformed( e );
            }
        } );
        jPanel1.add( bMotorX, null );
        jPanel1.add( bMotorY, null );
        jPanel1.add( bMotorZ, null );
        jPanel1.add( jLabel2, null );
        jPanel1.add( sSpeed, null );
        jPanel1.add( rgCW, null );
        jPanel1.add( rgCCW, null );
        jPanel1.add( imgButMX, null );
        jPanel1.add( imgButMY, null );
        jPanel1.add( imgButMZ, null );

        this.setSize( 680, 500 );
        this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        this.setResizable( false );
        this.setTitle( "Проверка УАК-СИ" );
        this.getContentPane().add( bFindMarker2, null );
        this.getContentPane().add( cbMakeCorrection, null );
        this.getContentPane().add( jLabel4, null );
        this.getContentPane().add( imgDig1, null );
        this.getContentPane().add( imgDig2, null );
        this.getContentPane().add( imgDig3, null );
        this.getContentPane().add( bFindMarker, null );
        this.getContentPane().add( imgGear, null );
        this.getContentPane().add( imgPC, null );
        this.getContentPane().add( jLabel6, null );
        this.getContentPane().add( bFindMarker1, null );
        this.getContentPane().add( cbPorts, null );
        this.getContentPane().add( jPanel2, null );
        this.getContentPane().add( jLabel3, null );
        this.getContentPane().add( bConn, null );
        this.getContentPane().add( jPanel1, null );
        this.getContentPane().add( imgButSX, null );
        this.getContentPane().add( imgButSY, null );
        this.getContentPane().add( imgButSZ, null );
        this.getContentPane().add( imgUaksi, null );
        this.getContentPane().add( lpostUaksi, null );
        this.getContentPane().add( lpostWt, null );
        group.add( rgCW );
        group.add( rgCCW );
        imgUaksi.setLocation( 0, 0 );
        imgUaksi.setSize( 700, 60 );
        imgPC.setLocation( 30, 365 );
        imgGear.setLocation( 220, 370 );
        imgDig1.setLocation( 20, 100 );
        imgDig2.setLocation( 240, 100 );
        imgDig3.setLocation( 460, 100 );
        jPanel2.add( tGotoX, null );
        jPanel2.add( bGotoX, null );
        jPanel2.add( jLabel5, null );
        jPanel2.add( bGotoY, null );
        jPanel2.add( jLabel7, null );
        jPanel2.add( tGotoY, null );
        jPanel2.add( bGotoZ, null );
        jPanel2.add( tGotoZ, null );
        jPanel2.add( jLabel8, null );
        jPanel2.add( bStop, null );
        initCom();
    }
    private void goToOnLine() {
        bConn.setIcon( connOn );
    }

    private void goToOffLine() {
        cunit.disconnect();
        bConn.setIcon( connOff );
        display1.renderClear();
        display2.renderClear();
        display3.renderClear();
        imgDig1.repaint();
        imgDig2.repaint();
        imgDig3.repaint();
        //timer.stop();
    }

    private void initTimer() {
        timer = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timer_actionPerformed( e );
            }
        }
        );

    }

    void bConn_actionPerformed( ActionEvent e ) {
        try {
            if ( !cunit.isConnected() ) {
                cunit.setPortName( cbPorts.getSelectedItem().toString() );
                cunit.connect();
                goToOnLine();
                //initTimer();
                //timer.start();
                //cunit.notifyOnNoRespond(true);
                //cunit.addRespondEventListener(this);

            } else {
                goToOffLine();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    void initCom() {
        //JRadioButton rb;
        //ButtonGroup grp = new ButtonGroup();
        java.util.ArrayList list = CommConnection.getPortNames();
        for ( int i = 0; i < list.size(); i++ ) {
            //rb = new JRadioButton((String)list.get(i));
            //rb.setBounds(new Rectangle(16, 20 + 20 * i, 80, 20));
            //rb.addActionListener(new PortSelect());
            //listCom.add(rb);
            cbPorts.add( new JLabel( ( String ) list.get( i ) ) );
            cbPorts.addItem( ( String ) list.get( i ) );
            //grp.add(rb);
        }
    }

    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            goToOffLine();
            //System.exit( 0 );
        }
    }

    public void respond( RespondEvent ev ) {
        /*
             if(ev.getRespond() == RespondEvent.EVENT_NO_RESPOND) {
          JOptionPane.showMessageDialog(this,
                                        "Нет связи с установкой УАК-СИ",
                                        "Ошибка связи",
                                        JOptionPane.ERROR_MESSAGE);
             }
         */
    }

    private void redrawAccurateValues() {
        /** @todo optimize!*/

        try {
            Angle ang = new Angle();
            display1.render( cunit.getAngle( 'x', ang ).getValue() );
            imgDig1.repaint();
            display2.render( cunit.getAngle( 'y', ang ).getValue() );
            imgDig2.repaint();
            display3.render( cunit.getAngle( 'z', ang ).getValue() );
            imgDig3.repaint();
        } catch ( Exception ex ) {
        }
    }

    void timer_actionPerformed( ActionEvent e ) {
        timer.stop();
        //labelX.repaint();
        //labelY.repaint();
        //labelY.setText(Integer.toString(counter));
        try {
            cunit.readAngles();
            Angle ang = new Angle();
            Image img = display1.getImage();
            display1.render( cunit.getAzimut().getValue() );
            imgDig1.repaint();
            //this.getGraphics().drawImage(img, 20, 100, null);
            display2.render( cunit.getZenit().getValue() );
            imgDig2.repaint();
            display3.render( cunit.getAngle( 'z', ang ).getValue() );
            imgDig3.repaint();
            //cunit.readWellToolValues();
            //lToolX.setText( cunit.getToolAngle( 'x' ).toString() );
            //lToolY.setText( cunit.getToolAngle( 'y' ).toString() );
            //lToolZ.setText( cunit.getToolAngle( 'z' ).toString() );

            //this.getGraphics().drawImage(display2.getImage(), 220, 100, null);

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        timer.start();
    }

    void rotateMotor( char ID ) {
        // int steps = tSteps.getValue();

        //if(rgCCW.isSelected())
        //   steps = -steps;
        try {
            //cunit.rotate(usedMotor, 255);
            setSpeed();
        } catch ( Exception ex ) {}
    }

    void findMarker( char plane ) {
        try {
            cunit.findMarker( plane );
        } catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
        }
    }
    void reset( char plane ) {
        try {
            cunit.reset( plane );
        } catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
        }
    }

    void setSpeed() {
        try {
            cunit.rotate( usedMotor,
                          sSpeed.getValue() * 255 / sSpeed.getMaximum() );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
    }

    void setDirection() {
        try {
            char d = 'c';
            if ( rgCCW.isSelected() ) {
                d = 'C';
            }
            cunit.setRotateDirection( d );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }

    }

    void bMotorX_actionPerformed( ActionEvent e ) {
        usedMotor = 'x';
        rotateMotor( 'x' );
    }

    void bMotorY_actionPerformed( ActionEvent e ) {
        usedMotor = 'y';
        rotateMotor( 'y' );
    }

    void bMotorZ_actionPerformed( ActionEvent e ) {
        usedMotor = 'z';
        rotateMotor( 'z' );
    }

    class PortSelect
        implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton src = ( JRadioButton ) e.getSource();
            portName = src.getText();
            //lPort.setText(portName);
        }
    }


    void bFindMarker_actionPerformed( ActionEvent e ) {
        findMarker( 'x' );
    }

    void sSpeed_caretPositionChanged( InputMethodEvent e ) {
        setSpeed();
    }

//    void sSpeed_ancestorMoved( AncestorEvent e ) {
//        setSpeed();
//    }

    void sSpeed_stateChanged( ChangeEvent e ) {
        setSpeed();
    }

    void rgCW_actionPerformed( ActionEvent e ) {
        setDirection();
    }

    void rgCCW_actionPerformed( ActionEvent e ) {
        setDirection();
    }

    void bGotoX_actionPerformed( ActionEvent e ) {
        try {
            cunit.goTo( 'x', Double.parseDouble( tGotoX.getText() ) );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
    }

    void bGotoY_actionPerformed( ActionEvent e ) {
        try {
            cunit.goTo( 'y', Double.parseDouble( tGotoY.getText() ) );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }

    }

    void bGotoZ_actionPerformed( ActionEvent e ) {
        try {
            cunit.goTo( 'z', Double.parseDouble( tGotoZ.getText() ) );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }

    }

    void bFindMarker1_actionPerformed( ActionEvent e ) {
        findMarker( 'y' );

    }

    void bFindMarker2_actionPerformed( ActionEvent e ) {
        reset( 'z' );

    }

    void bStop_actionPerformed(ActionEvent e) {
        try {
            cunit.stop();
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
    }

    void cbMakeCorrection_actionPerformed(ActionEvent e) {
        cunit.setMakeMagneticCorrection( cbMakeCorrection.isSelected());
    }

}
