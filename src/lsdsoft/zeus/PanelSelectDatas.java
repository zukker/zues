package lsdsoft.zeus;


import javax.swing.*;
import java.awt.*;
//import com.borland.jbcl.layout.*;
import java.util.*;
import java.awt.event.*;
import lsdsoft.util.*;
import javax.swing.event.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class PanelSelectDatas
    extends JPanel {
    private Zeus zeus = Zeus.getInstance();
    WorkIndex workIndex;
    ButtonGroup bgStart = new ButtonGroup();
    boolean isNewData = true;
    JScrollPane jScrollPane1 = new JScrollPane();
    JTable tWorks = new JTable();
    JPanel jPanel1 = new JPanel();
    JRadioButton rbContinue = new JRadioButton();
    JRadioButton rbNewWork = new JRadioButton();

    public PanelSelectDatas() {
        try {
            initTable();
            jbInit();


            selectNew();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void initTable() {
        workIndex = DataFactory.getWorkIndex();
        Vector rows = new Vector();
        Vector cols = new Vector();
        cols.add( "Вид работы" );
        cols.add( "Дата" );
        cols.add( "Состояние" );
        int size = 0;
        if ( workIndex != null ) {
            size = workIndex.items.size();
        }
        rbContinue.setEnabled( size > 0 );
        for ( int i = 0; i < size; i++ ) {
            Vector row = new Vector();
            WorkIndexItem item = workIndex.get( i );
            row.add( item.getWorkMode().getShortDescription() );
            row.add( TextUtil.dateToString( workIndex.get( i ).getDate(),
                                            "yyyy.MM.dd HH:mm:ss" ) );
            row.add( ( item.isFinished ) ? "Завершена" : "Продолжается" );
            rows.add( row );

        }
        tWorks = new JTable( rows, cols );
        tWorks.setCellEditor( null );
        tWorks.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        ListSelectionModel rowSM = tWorks.getSelectionModel();
        rowSM.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                //Ignore extra messages.
                if ( e.getValueIsAdjusting() ) {
                    return;
                }

                ListSelectionModel lsm =
                    ( ListSelectionModel ) e.getSource();
                if ( lsm.isSelectionEmpty() ) {
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    selectRow();
                }
            }
        } );

    }

    private void selectNew() {
        tWorks.setBackground( Color.LIGHT_GRAY );
        tWorks.setEnabled( false );
        zeus.setNewWork( true );
    }

    private void selectContinue() {
        tWorks.setBackground( Color.WHITE );
        tWorks.setEnabled( true );

        zeus.setNewWork( false );
        selectRow();
    }

    private void selectRow() {
        int row = tWorks.getSelectedRow();
        if ( row < 0 || row > workIndex.items.size() ) {
            return;
        }
        WorkIndexItem item = workIndex.get( row );
        zeus.setWorkID( item.getID() );
    }

    private void jbInit() throws Exception {
        //this.setSize(new Dimension(397, 204));
        //this.setVisible(true);
        this.setLayout(null );
        //tWorks.setBackground( Color.lightGray );
        tWorks.setBorder( BorderFactory.createLineBorder( Color.black ) );
        //tWorks.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
        tWorks.setColumnSelectionAllowed( false );
        //tWorks.setRowSelectionAllowed( false );

        jScrollPane1.setOpaque( true );
    jScrollPane1.setBounds(new Rectangle(0, 54, 422, 155));
        this.setMinimumSize( new Dimension( 165, 200 ) );
        rbContinue.setText( "Продолжить работу" );
    rbContinue.setBounds(new Rectangle(1, 24, 400, 26));
        rbContinue.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rbContinue_actionPerformed( e );
            }
        } );
        //rbContinue.setEnabled( true );
        rbContinue.setOpaque( false );
        rbContinue.setPreferredSize( new Dimension( 200, 26 ) );
        rbContinue.setMargin( new Insets( 6, 12, 6, 6 ) );
        rbNewWork.setText( "Новая работа" );
    rbNewWork.setBounds(new Rectangle(0, 0, 400, 27));
        rbNewWork.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rbNewWork_actionPerformed( e );
            }
        } );
        rbNewWork.setMargin( new Insets( 4, 12, 4, 4 ) );
        rbNewWork.setSelected( true );
        rbNewWork.setOpaque( false );
        //rbNewWork.setPreferredSize( new Dimension( 200, 23 ) );
        //jPanel1.setMinimumSize( new Dimension( 200, 40 ) );
        //jPanel1.setPreferredSize( new Dimension( 200, 48 ) );
        jPanel1.setLayout(null );
        jPanel1.setBounds(new Rectangle(0, 0, 400, 54));
        this.add( jPanel1, null );
        jPanel1.add( rbNewWork, null );
    jPanel1.add(rbContinue, null);
    this.add(jScrollPane1, null);
        jScrollPane1.getViewport().add( tWorks, null );
        //this.setVisible( true );
    bgStart.add(rbNewWork);
    bgStart.add(rbContinue);
    }

    void rbNewWork_actionPerformed( ActionEvent e ) {
        selectNew();
    }

    void rbContinue_actionPerformed( ActionEvent e ) {
        selectContinue();
    }

    void tWorks_caretPositionChanged( InputMethodEvent e ) {
        selectRow();
    }

}