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

public class PanelSelectWorkMode2 extends JPanel {
  private static String zeusNextStepClass = "zeus.wizard.nextstep";
  private Properties zeusProps = Zeus.getInstance().getConfig();
  private JLabel lMode = new JLabel();
//  private JRadioButton rbCalib = new JRadioButton();
//  private JRadioButton rbGrad = new JRadioButton();
//  private JRadioButton rbView = new JRadioButton();
//  private JRadioButton rbSetup = new JRadioButton();
  private ButtonGroup bgMode = new ButtonGroup();
  private ModeSelectAdapter adapter = new ModeSelectAdapter();
  private Zeus zeus = Zeus.getInstance();
//  JToggleButton bVar = new JToggleButton();

  public PanelSelectWorkMode2() {
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
    lMode.setText("Выберите режим работы:");
    lMode.setBounds(new Rectangle(36, 15, 227, 17));
    this.setLayout(null);
    this.add(lMode, null);
    buildModeControls();
  }
  /**
   * Создает radioButtons для выбоа режима работы
   */
  private void buildModeControls() {
      String[] syns = WorkMode.getWorkModeDescriptions();
      String[] names = WorkMode.getWorkModeNames();
      for(int i = 0; i < 2; i++) {
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

