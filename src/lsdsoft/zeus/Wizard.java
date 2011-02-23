package lsdsoft.zeus;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.net.*;
//import com.borland.jbcl.layout.*;
import lsdsoft.zeus.ui.*;
import lsdsoft.util.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class Wizard
    extends JFrame {
    private int step = 0;
    private final int maxSteps = 3;
    private Zeus zeus = Zeus.getInstance();
    private String[] panelClassNames = {
        "lsdsoft.zeus.PanelSelectWorkMode",
        "lsdsoft.zeus.PanelSelectTool",
        "lsdsoft.zeus.PanelSelectDatas",
    };
    private String[] panelClassNames2 = {
        "lsdsoft.zeus.PanelSelectWorkMode",
        "lsdsoft.zeus.PanelSelectSetupMode",
    };
    private JButton bPrev = new JButton();
    private JButton bNext = new JButton();
    private JButton bExit = new JButton();
    private ImageIcon zeusLogo = Zeus.createImageIcon( "images/zeus.png" );
    private Border border1;
    private TitledBorder titledBorder1;
    private String zeusNextStepClass = "zeus.wizard.nextstep";

    private JPanel steps = new JPanel();
    JLabel lImage = new JLabel();
    JButton bBegin = new JButton();
    JLabel lMain = new JLabel();
    //PaneLayout paneLayout1 = new PaneLayout();

    public
        Wizard() throws HeadlessException {
        try {
            jbInit();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public String getStepPropertyName() {
        return zeusNextStepClass;
    }

    public void start() {
        goToStep( 0 );
    }

    private void setButtonsState( int step ) {
        bPrev.setEnabled( step > 0 );
        bNext.setEnabled( step + 1 < maxSteps );
        //bNext.setEnabled(step < maxSteps);
        //String next = zeus.getConfig().getProperty(zeusNextStepClass);
        //bNext.setText( (next != "") ? "����� >" : "������");
    }

    private void clearPanel() {
        steps.removeAll();
    }

    private void setNewPanel( String className ) {
        clearPanel();
        try {
            Class cls = Class.forName( className );
            JPanel panel = ( JPanel ) ( cls.newInstance() );
            Dimension dim = steps.getSize();
            dim.height -= 2;
            dim.width -= 2;
            panel.setSize( dim );
            panel.setMaximumSize( dim );
            panel.setMinimumSize( dim );
            panel.setPreferredSize( dim );
            panel.setLocation( 1, 1 );
            steps.add( panel, null );
            panel.setVisible( true );
            //this.getContentPane().add(panel, null);

            steps.repaint();
            //panel.repaint();
            repaint();
        } catch ( ClassNotFoundException ex ) {
            UiUtils.showSystemError( this, "�� ������ ����� " + className );
        } catch ( Exception ex ) {
            UiUtils.showSystemError( this, ex.getMessage() );
        }

    }

    private void doNextStep() {
        goToStep( step + 1 );
        //step++;
        setButtonsState( step );
        //setNewPanel(zeus.getConfig().getProperty(zeusNextStepClass));
        //lStep.setText("��� " + step);
    }

    private void goToStep( int step ) {
        buildMainLabel();
        setButtonsState( step );
        this.step = step;
        int wm = zeus.getWorkMode().getWorkMode();
        if( wm == WorkMode.MODE_SETUP ) {

            String classN = "";
            switch(step) {
                default:
                    classN = panelClassNames2[step];
            }
              setNewPanel( classN );

        } else {
            setNewPanel( panelClassNames[step] );
        }
        //lStep.setText("��� " + (step + 1));
        updateTitle();
    }

    private void jbInit() throws Exception {
        border1 = new EtchedBorder( EtchedBorder.RAISED, Color.white,
                                    new Color( 134, 134, 134 ) );
        titledBorder1 = new TitledBorder( border1, "��������������� �������" );
        bPrev.setBounds( new Rectangle( 161, 343, 98, 32 ) );
        bPrev.setText( "<< ����� " );
        bPrev.setEnabled( false );
        bPrev.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bPrev_actionPerformed( e );
            }
        } );
        bNext.setBounds( new Rectangle( 258, 343, 101, 32 ) );
        bNext.setText( "����� >>" );
        bNext.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bNext_actionPerformed( e );
            }
        } );
        this.setDefaultCloseOperation( EXIT_ON_CLOSE );
        this.setFont( new java.awt.Font( "Dialog", 0, 14 ) );
        this.setResizable( false );
        this.getContentPane().setLayout( null );
        updateTitle();
        bExit.setText( "�����" );
        bExit.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                exit();
            }
        } );
        bExit.setBounds( new Rectangle( 21, 343, 101, 32 ) );
        bExit.setToolTipText( "" );
        steps.setFont( new java.awt.Font( "Dialog", 0, 14 ) );
        steps.setBorder( BorderFactory.createLineBorder( Color.black ) );
        steps.setOpaque( false );
        steps.setBounds( new Rectangle( 161, 106, 429, 228 ) );
        steps.setLayout( null );
        this.setTitle( "���" );
        lImage.setIcon( zeusLogo );
        lImage.setBounds( new Rectangle( 10, 10, 151, 331 ) );
        bBegin.setToolTipText( "" );
        bBegin.setBounds( new Rectangle( 488, 344, 101, 32 ) );
        bBegin.setText( "������" );
        bBegin.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                bBegin_actionPerformed( e );
            }
        } );
        lMain.setOpaque( false );
        buildMainLabel();
        //lMain.setText("<html>");
        lMain.setVerticalAlignment( SwingConstants.TOP );
        lMain.setVerticalTextPosition( SwingConstants.TOP );
        lMain.setBounds( new Rectangle( 159, 10, 348, 73 ) );
        this.getContentPane().add( steps, null );
        this.getContentPane().add( lImage, null );
        this.getContentPane().add( bBegin, null );
        this.getContentPane().add( lMain, null );
        this.getContentPane().add( bExit, null );
        this.getContentPane().add( bNext, null );
        this.getContentPane().add( bPrev, null );
        this.setSize( 600, 410 );
        UiUtils.toScreenCenter( this );
    }

    public void buildMainLabel() {
        StringBuffer str = new StringBuffer( 32 );
        str.append( "<html>�����: <b>" );
        str.append( zeus.getWorkMode().getShortDescription() );
        str.append( "</b><br>���: <b>" );
        str.append( zeus.getToolName() );
        str.append( "</b><br>�����: <b>" );
        str.append( zeus.getToolNumber() );
        lMain.setText( str.toString() );
    }

    private void updateTitle() {
        this.setTitle( "��������������� �������: ��� " + ( step + 1 ) );
        this.getContentPane().add( lMain, null );
        this.getContentPane().add( bBegin, null );
        this.getContentPane().add( bExit, null );
        this.getContentPane().add( bPrev, null );
        this.getContentPane().add( bNext, null );
    }

    void bPrev_actionPerformed( ActionEvent e ) {
        if ( step > 0 ) {
            goToStep( step - 1 );
        } else {
            bPrev.setEnabled( false );
        }
    }

    public void exit() {
        int option = JOptionPane.showConfirmDialog( this,
            "�� ������������� ������ ��������� ������?",
            "�������������",
            JOptionPane.YES_NO_OPTION );
        if ( option == JOptionPane.OK_OPTION ) {
            System.exit( 0 );
        }
    }

    void bNext_actionPerformed( ActionEvent e ) {
        doNextStep();
//    if(step < maxSteps ) {
//      goToStep(step + 1);
//    }

    }

    void bBegin_actionPerformed( ActionEvent e ) {
        zeus.getConfig().setProperty( Zeus.PROP_VIEWER_CLASS,
                                      "lsdsoft.zeus.methods.ION1MethodsViewer" );
        zeus.startViewer();
    }

}
