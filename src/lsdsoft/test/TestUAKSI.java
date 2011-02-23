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
//import java.util.*;

/**
 * <p>Title: Проверка работы УАК-СИ</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class TestUAKSI extends JFrame implements ChannelDataEventListener,
RespondEventListener{
  private DigitalDisplay display1 = new DigitalDisplay(7, 3);
  private DigitalDisplay display2 = new DigitalDisplay(7, 3);
  private ImageIcon connOff = createImageIcon("images/conn_off2.png");
  private ImageIcon connOn = createImageIcon("images/conn_on2.png");
  private ImageIcon iconPC = createImageIcon("images/pc.png");
  private ImageIcon iconGear = createImageIcon("images/oh.png");
  private JImage imgPC = new JImage(TestUAKSI.class, "images/PC.png");
  private JImage imgGear = new JImage(iconGear.getImage());
  private JImage imgDig1 = new JImage(display1.getImage());
  private JImage imgDig2 = new JImage(display2.getImage());

  private UAKSICheckUnit cunit;
  private String portName = "COM1";
  int counter = 0;
  javax.swing.Timer timer;
  JTextField jTextField1 = new JTextField();
  JButton bConn = new JButton();
  JLabel jLabel1 = new JLabel();
  JSlider jSlider1 = new JSlider();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JPanel jPanel1 = new JPanel();
  Border border1;
  JButton bMotorX = new JButton();
  JButton bMotorZ = new JButton();
  //JTextField tSteps = new JTextField();
  WholeNumberField tSteps = new WholeNumberField(0,1);
  JButton bMotorY = new JButton();
  JLabel jLabel5 = new JLabel();
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

  protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TestUAKSI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
  public TestUAKSI() {
    cunit = new UAKSICheckUnit(portName);
    display1.renderClear();
    display2.renderClear();
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
      this.setSize(600,600);
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }
  public static void main(String[] args) {
    TestUAKSI testUAKSI1 = new TestUAKSI();
    testUAKSI1.setVisible(true);
  }
  public void channelEvent(ChannelDataEvent ev) {
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
    border1 = new TitledBorder(BorderFactory.createLineBorder(Color.gray,1),"Поворот");
    border2 = BorderFactory.createLineBorder(Color.gray,1);
    titledBorder1 = new TitledBorder(border2,"Поворот");
    border3 = BorderFactory.createLineBorder(Color.black,1);
    titledBorder2 = new TitledBorder(BorderFactory.createLineBorder(Color.gray,1),"Подключение по...");
    border4 = BorderFactory.createLineBorder(Color.white,1);
    titledBorder3 = new TitledBorder("");
    border5 = BorderFactory.createLineBorder(SystemColor.controlText,2);
    bConn.setBounds(new Rectangle(149, 350, 57, 33));
    bConn.setFont(new java.awt.Font("Dialog", 0, 12));
    bConn.setBorder(null);
    bConn.setRolloverEnabled(true);
    bConn.setIcon(connOff);
    bConn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bConn_actionPerformed(e);
      }
    });
    jTextField1.setText("jTextField1");
    this.getContentPane().setLayout(null);
    jLabel1.setFont(new java.awt.Font("SansSerif", 1, 24));
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setText("УАК-СИ");
    jLabel1.setVerticalAlignment(SwingConstants.CENTER);
    jLabel1.setBounds(new Rectangle(27, 0, 145, 62));
    jSlider1.setMajorTickSpacing(25);
    jSlider1.setMinorTickSpacing(5);
    jSlider1.setPaintLabels(true);
    jSlider1.setPaintTicks(true);
    jSlider1.setPaintTrack(true);
    jSlider1.setBackground(Color.lightGray);
    jSlider1.setOpaque(false);
    jSlider1.setLabelTable(jSlider1.createStandardLabels(25));
    jSlider1.setToolTipText("кк");
    jSlider1.setBounds(new Rectangle(23, 127, 268, 46));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setTitle("Проверка УАК-СИ");
    jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12));
    jLabel2.setText("Скорость");
    jLabel2.setBounds(new Rectangle(21, 110, 54, 15));
    jLabel3.setText("Датчик X");
    jLabel3.setBounds(new Rectangle(33, 58, 62, 16));
    jLabel4.setBounds(new Rectangle(266, 58, 62, 16));
    jLabel4.setText("Датчик Y");
    jPanel1.setEnabled(false);
    jPanel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jPanel1.setBorder(titledBorder1);
    jPanel1.setOpaque(false);
    jPanel1.setToolTipText("");
    jPanel1.setBounds(new Rectangle(22, 144, 337, 189));
    jPanel1.setLayout(null);
    bMotorX.setBounds(new Rectangle(169, 18, 107, 25));
    bMotorX.setFont(new java.awt.Font("SansSerif", 0, 12));
    bMotorX.setText("Двигатель X");
    bMotorX.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bMotorX_actionPerformed(e);
      }
    });
    bMotorZ.setText("Двигатель Z");
    bMotorZ.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bMotorZ_actionPerformed(e);
      }
    });
    bMotorZ.setFont(new java.awt.Font("SansSerif", 0, 12));
    bMotorZ.setBounds(new Rectangle(169, 76, 107, 25));
    tSteps.setText("");
    tSteps.setBounds(new Rectangle(61, 20, 87, 20));
    bMotorY.setText("Двигатель Y");
    bMotorY.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bMotorY_actionPerformed(e);
      }
    });
    bMotorY.setFont(new java.awt.Font("SansSerif", 0, 12));
    bMotorY.setBounds(new Rectangle(169, 47, 107, 25));
    jLabel5.setFont(new java.awt.Font("SansSerif", 0, 11));
    jLabel5.setText("Шагов:");
    jLabel5.setBounds(new Rectangle(12, 23, 43, 15));
    rgCW.setFont(new java.awt.Font("SansSerif", 0, 12));
    rgCW.setOpaque(false);
    rgCW.setSelected(true);
    rgCW.setText("По часовой");
    rgCW.setBounds(new Rectangle(34, 49, 104, 24));
    rgCCW.setFont(new java.awt.Font("SansSerif", 0, 12));
    rgCCW.setOpaque(false);
    rgCCW.setText("Против часовой");
    rgCCW.setBounds(new Rectangle(34, 72, 116, 24));
    cbPorts.setBorder(border3);
    cbPorts.setBounds(new Rectangle(84, 356, 64, 21));
    this.getContentPane().add(jLabel3, null);
    this.getContentPane().add(jLabel4, null);
    this.getContentPane().add(jPanel1, null);
    ButtonGroup group = new ButtonGroup();
    //group.add
    jPanel1.add(bMotorX, null);
    jPanel1.add(jLabel5, null);
    jPanel1.add(tSteps, null);
    jPanel1.add(bMotorY, null);
    jPanel1.add(bMotorZ, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(rgCW, null);
    jPanel1.add(rgCCW, null);
    jPanel1.add(jSlider1, null);
    this.getContentPane().add(jLabel1, null);
    group.add(rgCW);
    group.add(rgCCW);
    imgPC.setLocation(30, 340);
    imgGear.setLocation(210, 350);
    imgDig1.setLocation(20, 80);
    imgDig2.setLocation(240, 80);
    this.getContentPane().add(imgPC, null);
    this.getContentPane().add(imgGear, null);
    this.getContentPane().add(imgDig2, null);
    this.getContentPane().add(cbPorts, null);
    this.getContentPane().add(imgDig1, null);
    this.getContentPane().add(bConn, null);
    initCom();
  }
  private void goToOffLine() {
    cunit.disconnect();
    bConn.setIcon(connOff);
    display1.renderClear();
    display2.renderClear();
    imgDig1.repaint();
    imgDig2.repaint();
    timer.stop();
  }

  private void initTimer() {
    timer = new Timer(100, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
            timer_actionPerformed(e);
          }
        }
        );

  }
  void bConn_actionPerformed(ActionEvent e) {
    try {
      if(!cunit.isConnected()) {
        cunit.connect();
        initTimer();
        timer.start();
        cunit.notifyOnNoRespond(true);
        cunit.addRespondEventListener(this);
        bConn.setIcon(connOn);
      }
      else {
        goToOffLine();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  void initCom() {
    //JRadioButton rb;
    //ButtonGroup grp = new ButtonGroup();
    java.util.ArrayList list = CommConnection.getPortNames();
    for(int i = 0; i < list.size(); i++) {
      //rb = new JRadioButton((String)list.get(i));
      //rb.setBounds(new Rectangle(16, 20 + 20 * i, 80, 20));
      //rb.addActionListener(new PortSelect());
      //listCom.add(rb);
      cbPorts.add(new JLabel((String)list.get(i)));
      cbPorts.addItem((String)list.get(i));
      //grp.add(rb);
    }
  }

  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }
  public void respond(RespondEvent ev){
    if(ev.getRespond() == RespondEvent.EVENT_NO_RESPOND) {
      JOptionPane.showMessageDialog(this,
                                    "Нет связи с установкой УАК-СИ",
                                    "Ошибка связи",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  void timer_actionPerformed(ActionEvent e) {
    timer.stop();
    //labelX.repaint();
    //labelY.repaint();
    //labelY.setText(Integer.toString(counter));
    Image img = display1.getImage();
    display1.render(cunit.getAzimut().getValue());
    imgDig1.repaint();
    //this.getGraphics().drawImage(img, 20, 100, null);
    display2.render(cunit.getZenit().getValue());
    imgDig2.repaint();
    //this.getGraphics().drawImage(display2.getImage(), 220, 100, null);
    timer.start();
  }

  void rotateMotor(char ID) {
    int steps = tSteps.getValue();
    if(rgCCW.isSelected())
      steps = -steps;
    cunit.rotate(ID, steps, 255);
  }

  void bMotorX_actionPerformed(ActionEvent e) {
    rotateMotor('x');
  }

  void bMotorY_actionPerformed(ActionEvent e) {
    rotateMotor('y');
  }

  void bMotorZ_actionPerformed(ActionEvent e) {
    rotateMotor('z');
  }
  class PortSelect implements ActionListener {
    public void actionPerformed(ActionEvent e) {
          JRadioButton src = (JRadioButton)e.getSource();
          portName = src.getText();
          //lPort.setText(portName);
        }
  }

}

