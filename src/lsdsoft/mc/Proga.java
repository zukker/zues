package lsdsoft.mc;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.*;
import java.util.regex.*;
import com.lsdsoft.comm.*;
import javax.comm.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class Proga
    extends JFrame {
    ObjectFile hex = new IntelHexFile();
    UralProgrammer proger = new UralProgrammer();
    //CommConnection conn = new CommConnection();
    JFileChooser fc = new JFileChooser();
    JLabel jLabel1 = new JLabel();
    JTextField tfFile = new JTextField();
    JButton bSelect = new JButton();
    JLabel jLabel2 = new JLabel();
    JRadioButton rbMaster = new JRadioButton();
    JRadioButton rbSlave = new JRadioButton();
    JComboBox cbSlaves = new JComboBox();
    ButtonGroup bgTarget = new ButtonGroup();
    JButton bUpload = new JButton();
    JButton bCancel = new JButton();
    JLabel jLabel3 = new JLabel();
    JComboBox cbConnection = new JComboBox();
    JButton bSetup = new JButton();
    JButton bView = new JButton();
    JCheckBox cbOnOff = new JCheckBox();
    JLabel lID = new JLabel();
    JTextArea log = new JTextArea();
    JButton bChange = new JButton();

    class HexFilter
        extends javax.swing.filechooser.FileFilter {
        public final static String mask = ".hex";
        public boolean accept( File f ) {
            // ########## TODO ############
            return Pattern.matches( mask, f.getName() );
        }

        public String getDescription() {
            return "Intel Hex files (*.hex)";
        }
    }


    public Proga() throws HeadlessException {
        try {
            jbInit();
            initCom();
            fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
            ExtFileFilter ff1 = new ExtFileFilter("hex", "*.hex - микрокод в Intel Hex");
            fc.addChoosableFileFilter(ff1);

            //fc.setFileFilter(new HexFilter());
            this.setSize( 400, 400 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        jLabel1.setText( "Прошивка" );
        jLabel1.setBounds( new Rectangle( 12, 9, 123, 16 ) );
        this.getContentPane().setLayout( null );
        tfFile.setSelectionStart( 11 );
        tfFile.setText( "" );
        tfFile.setBounds( new Rectangle( 9, 26, 306, 25 ) );
        bSelect.setBounds( new Rectangle( 321, 25, 36, 26 ) );
        bSelect.setText( "..." );
        bSelect.addActionListener( new Proga_bSelect_actionAdapter( this ) );
        jLabel2.setText( "Микроконтроллер" );
        jLabel2.setBounds( new Rectangle( 12, 112, 122, 16 ) );
        rbMaster.setSelected( true );
        rbMaster.setText( "Мастер" );
        rbMaster.setBounds( new Rectangle( 9, 134, 96, 24 ) );
        rbSlave.setText( "Подчиненный" );
        rbSlave.setBounds( new Rectangle( 8, 160, 130, 24 ) );
        cbSlaves.setBounds( new Rectangle( 139, 160, 84, 25 ) );
        bUpload.setBounds( new Rectangle( 266, 218, 99, 26 ) );
        bUpload.setText( "Загрузить" );
        bUpload.addActionListener( new Proga_bUpload_actionAdapter( this ) );
        bCancel.setBounds( new Rectangle( 21, 218, 99, 26 ) );
        bCancel.setText( "Отмена" );
        jLabel3.setText( "Подключение" );
        jLabel3.setBounds( new Rectangle( 14, 59, 130, 16 ) );
        cbConnection.setBounds( new Rectangle( 40, 79, 128, 25 ) );
        bSetup.setBounds( new Rectangle( 178, 78, 103, 26 ) );
        bSetup.setText( "Настройка" );
        bView.setBounds( new Rectangle( 141, 218, 99, 26 ) );
        bView.setText( "Просмор" );
        this.setResizable( false );
        this.setState( Frame.NORMAL );
        this.setTitle( "Программатор Урал-1" );
        cbOnOff.setText( "" );
        cbOnOff.setBounds( new Rectangle( 13, 79, 22, 24 ) );
        cbOnOff.addItemListener( new Proga_cbOnOff_itemAdapter( this ) );
        cbOnOff.addActionListener( new Proga_cbOnOff_actionAdapter( this ) );
        lID.setText( "avr" );
        lID.setBounds( new Rectangle( 135, 137, 117, 16 ) );
        log.setBorder( BorderFactory.createLineBorder( Color.black ) );
        log.setEditable( false );
        log.setText( "" );
        log.setBounds( new Rectangle( 16, 259, 357, 97 ) );
        bChange.setBounds( new Rectangle( 232, 160, 93, 26 ) );
        bChange.setToolTipText( "Изменть i2c адрес устройства" );
        bChange.setText( "Сменить" );
        bChange.addActionListener( new Proga_bChange_actionAdapter( this ) );
        this.getContentPane().add( jLabel1, null );
        this.getContentPane().add( tfFile, null );
        this.getContentPane().add( bSelect, null );
        bgTarget.add( rbMaster );
        bgTarget.add( rbSlave );
        this.getContentPane().add( jLabel3, null );
        this.getContentPane().add( cbConnection, null );
        this.getContentPane().add( bSetup, null );
        this.getContentPane().add( rbMaster, null );
        this.getContentPane().add( jLabel2, null );
        this.getContentPane().add( rbSlave, null );
        this.getContentPane().add( cbSlaves, null );
        this.getContentPane().add( bView, null );
        this.getContentPane().add( bCancel, null );
        this.getContentPane().add( bUpload, null );
        this.getContentPane().add( cbOnOff, null );
        this.getContentPane().add( lID, null );
        this.getContentPane().add( log, null );
        this.getContentPane().add( bChange, null );
    }

    public static void main( String[] args ) {
        Proga proga = new Proga();
        proga.setVisible( true );
    }

    void bSelect_actionPerformed( ActionEvent e ) {
        int returnVal = fc.showOpenDialog( this );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File file = fc.getSelectedFile();
            try {
                hex.load( new FileInputStream( file.getAbsoluteFile() ) );
                hex.combineRecords();
                tfFile.setText( file.getPath() );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
            //This is where a real application would open the file.
        }

    }

    void initCom() {
        //JRadioButton rb;
        //ButtonGroup grp = new ButtonGroup();
        java.util.ArrayList list = CommConnection.getPortNames();
        for ( int i = 0; i < list.size(); i++ ) {
            //cbConnection.add(new JLabel((String)list.get(i)));
            cbConnection.addItem( ( String ) list.get( i ) );
            //grp.add(rb);
        }
    }

    void changeAddressDialog() {
        if ( cbSlaves.getSelectedItem() != null ) {
            int slave = ( ( I2CAddress ) cbSlaves.getSelectedItem() ).
                getAddress();
            String addr = JOptionPane.showInputDialog(
                "Введите новый i2c адрес" );
            try {
                proger.setNewAddress( slave, Integer.parseInt( addr, 16 ) );
            } catch ( Exception ex ) {
                log.append( ex.getMessage() );
            }

        }
    }

    /*
       void setComm() throws Exception{
      if(conn.connected()) {
        SerialPort port = (SerialPort)conn.getPort();
        port.setSerialPortParams(9600,
                               SerialPort.DATABITS_8,
                               SerialPort.STOPBITS_1,
                               SerialPort.PARITY_NONE);
      }
       }*/
    void cbOnOff_actionPerformed( ActionEvent e ) {
        proger.setPortName( cbConnection.getSelectedItem().toString() );
        try {
            //if(cbOnOff.getsta
            proger.connect();
            lID.setText( proger.getSoftwareID() );
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
        }
    }

    void rescanDevices() throws Exception {
        cbSlaves.removeAllItems();
        for ( int i = 0; i < 127; i++ ) {
            proger.selectDevice( i << 1 );
            if ( proger.checkSlave() ) {
                cbSlaves.addItem( new I2CAddress( i << 1 ) );
            }
        }

    }

    int getSelectedSlave() {
        if ( cbSlaves.getSelectedItem() == null ) {
            return -1;
        }
        return ( ( I2CAddress ) cbSlaves.getSelectedItem() ).getAddress();
    }

    void cbOnOff_itemStateChanged( ItemEvent e ) {
        proger.setPortName( cbConnection.getSelectedItem().toString() );
        try {
            if ( e.getStateChange() == e.SELECTED ) {
                log.append( "\r\nПодключение к программатору..." );
                proger.connect();
                proger.enterProgrammingMode();
                log.append( "OK" );
                // scan devices on i2c bus
                rescanDevices();
            } else {
                proger.disconnect();
            }
        } catch ( Exception ex ) {
            log.append( "ОШИБКА" );
            log.append( "\r\n  -- " + ex.getMessage() );
            //System.err.println(ex.getMessage());
        }
        cbOnOff.setSelected( proger.isConnected() );
    }

    void bUpload_actionPerformed( ActionEvent e ) {
        //proger.enterProgrammingMode();
        lID.setText( proger.getSoftwareID() );
        try {
            if ( rbMaster.isSelected() ) {
                proger.selectDevice( 0xff );
            } else {
                proger.selectDevice( getSelectedSlave() );
            }
            proger.programRecord( ( ObjectRecord ) hex.records.get( 1 ) );
            FileOutputStream fs = new FileOutputStream( "d:/test.bin" );
            fs.write( ( ( ObjectRecord ) hex.records.get( 1 ) ).getData() );
        } catch ( Exception ex ) {
            log.append( "\r\n" + ex.getMessage() );
        }
    }

    void bChange_actionPerformed( ActionEvent e ) {
        changeAddressDialog();
        try {
            rescanDevices();
        } catch ( Exception ex ) {}
    }

    class ExtFileFilter extends javax.swing.filechooser.FileFilter {

      String ext;
      String description;

      ExtFileFilter(String ext, String descr) {
          this.ext = ext;
          description = descr;
      }

      public boolean accept(File f) {
          if(f != null) {
              if(f.isDirectory()) {
                  return true;
              }
              String extension = getExtension(f);
              if( extension == null )
                  return (ext.length() == 0);
              return ext.equals(extension);
          }
          return false;
      }

      public String getExtension(File f) {
          if(f != null) {
              String filename = f.getName();
              int i = filename.lastIndexOf('.');
              if(i>0 && i<filename.length()-1) {
                  return filename.substring(i+1).toLowerCase();
              };
          }
          return null;
      }

      public String getDescription() {
          return description;
      }

}

}


class Proga_bSelect_actionAdapter
    implements java.awt.event.ActionListener {
    Proga adaptee;

    Proga_bSelect_actionAdapter( Proga adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.bSelect_actionPerformed( e );
    }
}


class Proga_cbOnOff_actionAdapter
    implements java.awt.event.ActionListener {
    Proga adaptee;

    Proga_cbOnOff_actionAdapter( Proga adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.cbOnOff_actionPerformed( e );
    }
}


class Proga_cbOnOff_itemAdapter
    implements java.awt.event.ItemListener {
    Proga adaptee;

    Proga_cbOnOff_itemAdapter( Proga adaptee ) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged( ItemEvent e ) {
        adaptee.cbOnOff_itemStateChanged( e );
    }
}


class Proga_bUpload_actionAdapter
    implements java.awt.event.ActionListener {
    Proga adaptee;

    Proga_bUpload_actionAdapter( Proga adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.bUpload_actionPerformed( e );
    }
}


class Proga_bChange_actionAdapter
    implements java.awt.event.ActionListener {
    Proga adaptee;

    Proga_bChange_actionAdapter( Proga adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.bChange_actionPerformed( e );
    }
}
