package lsdsoft.zeus.ui;


import javax.swing.*;
import java.awt.*;
import lsdsoft.metrolog.*;
import lsdsoft.util.*;
import java.awt.event.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class MeasureChainEditor
    extends JDialog {
    MeasureChain chain = null;
    JScrollPane jScrollPane1 = new JScrollPane();
    JTable table = new JTable();
    JButton bOk = new JButton();
    JButton bCancel = new JButton();
    public MeasureChainEditor() {
    }
    public MeasureChainEditor(Frame owner, boolean modal) {
        super(owner, false);
    }

    private void start() {
        if ( chain == null ) {
            return;
        }
        try {
            jbInit();
            buildTable();
            this.setModal(true);
            this.setVisible( true );

        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    private void buildTable() {
        if ( chain == null ) {
            return;
        }
        table.setModel( new MeasureChainModel( chain ) );
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout( null );
        jScrollPane1.setAutoscrolls( true );
        jScrollPane1.setDebugGraphicsOptions( 0 );
        jScrollPane1.setBounds( new Rectangle( 55, 18, 172, 181 ) );
        bOk.setBounds( new Rectangle( 12, 217, 127, 38 ) );
        bOk.setText("Сохранить" );
    bOk.addActionListener(new MeasureChainEditor_bOk_actionAdapter(this));
        bCancel.setBounds( new Rectangle( 148, 217, 131, 38 ) );
        bCancel.setText("Отмена" );
        //this.setPreferredSize( new Dimension( 300, 200 ) );
        this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        this.setResizable( false );
        //this.setState( Frame.NORMAL );
        this.setSize( 300, 300 );
        this.setTitle( "Изменение значений" );
    this.getContentPane().add( bOk, null );
        this.getContentPane().add( bCancel, null );
        this.getContentPane().add( jScrollPane1, null );
        jScrollPane1.getViewport().add( table, null );
        UiUtils.toScreenCenter(this);
    }


    public static void edit( MeasureChain chain , JFrame parent) {
        MeasureChainEditor editor = new MeasureChainEditor(parent, true);
        editor.chain = chain;
        //parent.getContentPane().add(editor);
        editor.start();
    }

  void bOk_actionPerformed(ActionEvent e) {
      this.hide();
  }

}

class MeasureChainEditor_bOk_actionAdapter implements java.awt.event.ActionListener {
  MeasureChainEditor adaptee;

  MeasureChainEditor_bOk_actionAdapter(MeasureChainEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.bOk_actionPerformed(e);
  }
}