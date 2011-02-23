package lsdsoft.zeus;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class PanelSelectSetupMode extends JPanel {
  private static String zeusNextStepClass = "zeus.wizard.nextstep";
  private Properties zeusProps = Zeus.getInstance().getConfig();
  private JLabel lMode = new JLabel();
  private JRadioButton rbTools = new JRadioButton();
  private JRadioButton rbProperties = new JRadioButton();
//  private JRadioButton rbView = new JRadioButton();
//  private JRadioButton rbSetup = new JRadioButton();
  private ButtonGroup bgMode = new ButtonGroup();
  private ModeSelectAdapter adapter = new ModeSelectAdapter();
  private Zeus zeus = Zeus.getInstance();
//  JToggleButton bVar = new JToggleButton();

  public PanelSelectSetupMode() {
    try {
      jbInit();
      selectButton(zeus.getWorkMode().getName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    zeusProps.setProperty(zeusNextStepClass, "");
  }
  private void selectButton(String command) {
    if(command == null) {
        return;
    }
    Enumeration buttons = bgMode.getElements();
    while(buttons.hasMoreElements()) {
      JToggleButton button = (JToggleButton)(buttons.nextElement());
      if( (button.getActionCommand().compareTo(command) == 0)) {
        button.setSelected(true);
        break;
      }
    }
  }
  private void jbInit() throws Exception {
    this.setSize(new Dimension(338, 200));
    //this.setFont(new java.awt.Font("Dialog", 0, 14));
    this.setVisible(true);
    //lMode.setFont(new java.awt.Font("Dialog", 1, 14));
    lMode.setText("Выберите режим настройки:");
    lMode.setBounds(new Rectangle(36, 15, 227, 17));
    this.setLayout(null);
    rbTools.setActionCommand("tools");
    rbTools.setSelected(true);
    rbTools.setText("настройка типов аппаратуры");
    rbTools.setBounds(new Rectangle(55, 33, 176, 25));
    //rbGrad.setActionCommand("grad");
    //rbGrad.setText("градуировка приборов");
    //rbGrad.setBounds(new Rectangle(55, 53, 176, 25));
    //rbView.setActionCommand("view");
    //rbView.setText("просмотр данных");
    //rbView.setBounds(new Rectangle(55, 73, 176, 25));
    //rbSetup.setActionCommand("setup");
    //rbSetup.setText("настройка системы");
    //rbSetup.setBounds(new Rectangle(55, 93, 176, 25));
    //bVar.setText("Тоже вариант");
    //bVar.setBounds(new Rectangle(62, 121, 119, 26));
    //bVar.setActionCommand("variant");
    // assigning listner
    //rbGrad.addActionListener(adapter);
    //rbView.addActionListener(adapter);
    //rbCalib.addActionListener(adapter);
    //rbSetup.addActionListener(adapter);
    //bVar.addActionListener(adapter);


    this.add(lMode, null);
    //this.add(rbCalib, null);
    //this.add(rbGrad, null);
    //this.add(rbView, null);
    //this.add(rbSetup, null);
    //this.add(bVar, null);
    //bgMode.add(rbCalib);
    //bgMode.add(rbGrad);
    //bgMode.add(rbView);
    //bgMode.add(rbSetup);
    //bgMode.add(bVar);
    buildModeControls();
  }
  /**
   * Создает radioButtons для выбоа режима работы
   */
  private void buildModeControls() {
      String[] syns = WorkMode.getWorkModeDescriptions();
      String[] names = WorkMode.getWorkModeNames();
      for(int i = 0; i < names.length; i++) {
          JRadioButton button = new JRadioButton();
          button.setText(syns[i]);
          button.setBounds(new Rectangle(55, 33 + i * 20, 200, 25));
          button.setActionCommand(names[i]);
          button.addActionListener(adapter);
          this.add(button, null);
          bgMode.add(button);


      }

  }
  void mode_actionPerformed(ActionEvent e) {
    String com = e.getActionCommand();
    //JRadioButton button = (JRadioButton) e.getSource();
    //lb.setText(com);
    zeus.setWorkMode(com);
    Zeus.log.info("Select mode '" + com + "'" );
    //zeusProps.setProperty(Zeus.WORK_MODE, com);
  }

//  void rbCalib_actionPerformed(ActionEvent e) {
//  }
  class ModeSelectAdapter implements java.awt.event.ActionListener {
    public void actionPerformed(ActionEvent e) {
      mode_actionPerformed(e);
    }
  }
}

