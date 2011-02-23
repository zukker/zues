package lsdsoft.metrolog.admin;

import lsdsoft.metrolog.mdb.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ManageOrgsDialog extends JDialog {
  private MetrologDB db = null;
  private JLabel jLabel1 = new JLabel();
  private JList jList1 = new JList();
  private JButton jbAdd = new JButton();
  private JButton jbChange = new JButton();
  private JButton jbRemove = new JButton();
  private JButton jbClose = new JButton();

  public ManageOrgsDialog() throws HeadlessException {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    jLabel1.setText("Органицации");
    jLabel1.setBounds(new Rectangle(20, 20, 104, 17));
    this.getContentPane().setLayout(null);
    jList1.setBounds(new Rectangle(22, 41, 348, 118));
    jbAdd.setBounds(new Rectangle(20, 173, 110, 30));
    jbAdd.setText("Добавить...");
    jbChange.setBounds(new Rectangle(143, 173, 110, 30));
    jbChange.setText("Изменить...");
    jbRemove.setBounds(new Rectangle(263, 173, 110, 30));
    jbRemove.setText("Удалить");
    jbClose.setBounds(new Rectangle(263, 255, 110, 30));
    jbClose.setText("Закрыть");
    this.setModal(true);
    this.setResizable(false);
    this.setTitle("Организации");
    this.getContentPane().add(jLabel1, null);
    this.getContentPane().add(jList1, null);
    this.getContentPane().add(jbRemove, null);
    this.getContentPane().add(jbAdd, null);
    this.getContentPane().add(jbChange, null);
    this.getContentPane().add(jbClose, null);
  }
  private void readData() throws Exception{
    if(db == null) throw new Exception("Database is null");
    // make query
  }
  public void setDB(MetrologDB mdb) {
    db = mdb;
  }
}