package lsdsoft.zeus.methods;


import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import lsdsoft.util.*;
//import com.borland.jbcl.layout.XYLayout;
//import com.borland.jbcl.layout.*;
import lsdsoft.zeus.ui.*;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.*;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: Ural-Geo</p>
 *
 * @author lsdsoft
 * @version 1.0
 */
public class InklImportLas
    extends JDialog {

    private String lasName = "";
    private LasFile las = new LasFile();
    private Tracker tracker = null;
    private Tracker accTracker = null;
    private int offset = 0;
    private int offsetTool = 0;
    private int startDragX = 0;
    public boolean confirm = false;
    // номера кривых в лас-файле
    public int timeTrack = 0;
    public int[] inklTracks = { 1,2,3};
    private int currentTrack; // currently selected inklinometer track (az, zen or rot)
    private GraphCanvas grCanvas = new GraphCanvas();
    Graph graphAcc = new Graph();
    Graph graphTool = new Graph();


    public InklImportLas() {
        try {
            jbInit();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
    public void setAccTracker(Tracker trck) {
        accTracker = trck;
    }
    public Tracker getTracker() {
        return tracker;
    }
    public double getOffset() {
        return offsetTool;
    }
    private void loadLas() {
        JFileChooser fc = new JFileChooser( "/" );
        if(fc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION) {
            lasName = fc.getSelectedFile().getAbsolutePath();
            try {
                las.load( lasName );
                tracker = new Tracker(las);

            } catch ( Exception ex ) {
                UiUtils.showError( this,
                    "<html><center>Ошибка загрузки LAS-файла:" +
                    "<br>" + ex.getLocalizedMessage());
            }
        }
    }
    private void updateUI() {
        if(tracker != null) {
            lLasName.setText(lasName);
            cbTime.removeAllItems();
            cbAzimuth.removeAllItems();
            cbZenith.removeAllItems();
            cbRotate.removeAllItems();
            for(int i = 0; i < tracker.getTrackCount(); i++) {
                cbTime.addItem("" + (i+1));
                cbAzimuth.addItem("" + (i+1));
                cbZenith.addItem("" + (i+1));
                cbRotate.addItem("" + (i+1));
            }
            cbTime.setEnabled( true );
            int trackCnt = tracker.getTrackCount();
            cbRotate.setSelectedIndex( trackCnt - 1 );
            cbAzimuth.setSelectedIndex( trackCnt - 3 );
            cbZenith.setSelectedIndex( trackCnt - 2 );

            cbAzimuth.setEnabled( true );
            cbZenith.setEnabled( true );
            cbRotate.setEnabled( true );
            cbGraph.setEnabled( true );

        } else {
            cbTime.setEnabled(false);
            cbAzimuth.setEnabled(false);
            cbZenith.setEnabled(false);
            cbRotate.setEnabled(false);
            cbGraph.setEnabled( false );

        }
    }
    private void updateToolGraph() {
        graphTool.function.clear();
        graphTool.setInterpolation(Graph.INTERPOLATION_LINE);
        int count = tracker.points.size()>650?650:tracker.points.size();
        int off = offsetTool + offset;
        if(off < 0) {
            off = 0;
        }
        for(int i = 0; i < count; i++) {
            int o = i - offsetTool - offset;
            if( o >= 0) {
                double x = tracker.getRow( o )[timeTrack];
                double y = tracker.getRow( o )[inklTracks[currentTrack]];
                if(currentTrack == 2) {
                    y = 360.0 - y;
                }

                graphTool.function.add( x + offset + offsetTool, y );
                y = tracker.getRow( i )[14];

            }
        }
        grCanvas.repaint();
    }

    private void updateAccGraph() {
        if(accTracker.points.size() > 0) {
            double xoff = accTracker.getRow( 0 )[0];
            graphAcc.function.clear();
            graphAcc.setInterpolation( Graph.INTERPOLATION_LINE );
            int count = ( accTracker.points.size() > 650 ) ? 650 :
                accTracker.points.size();
            for ( int i = 0; i < count; i++ ) {
                int o = i;
                double x = ( accTracker.getRow( i )[0] - xoff ) / 1000.0;
                int tr = currentTrack;
                double y = accTracker.getRow( i )[tr + 1];

                graphAcc.function.add( x , y );
            }
            grCanvas.repaint();
        }
    }

    private void cancelChanges() {
        confirm = false;
        this.setVisible(false);
    }
    private void confirmChanges() {
        confirm = true;
        this.setVisible(false);
    }

    private void jbInit() throws Exception {
        border1 = new TitledBorder( BorderFactory.createLineBorder( Color.gray,
            1 ), "Выбор кривых" );
        this.getContentPane().setLayout( null );
        jLabel2.setText( "Время" );
        jLabel2.setBounds( new Rectangle( 11, 19, 52, 15 ) );
        jLabel3.setText( "Азимут" );
        jLabel3.setBounds( new Rectangle( 89, 19, 43, 15 ) );
        jLabel4.setText( "Зенит" );
        jLabel4.setBounds( new Rectangle( 169, 19, 41, 15 ) );
        jLabel5.setText( "Поворот" );
        jLabel5.setBounds( new Rectangle( 250, 19, 55, 15 ) );
        jPanel1.setBorder( border2 );
        jPanel1.setBounds( new Rectangle( 12, 124, 679, 225 ) );
        jPanel1.setLayout( null );
        jLabel6.setText( "График:" );
        jLabel6.setBounds( new Rectangle( 9, 20, 54, 15 ) );
        cbGraph.setBounds( new Rectangle( 70, 18, 84, 19 ) );
        bImport.setBounds( new Rectangle( 541, 355, 120, 27 ) );
        bImport.setText( "Импорт" );
        bCancel.setBounds( new Rectangle( 401, 355, 117, 27 ) );
        bCancel.setText( "Отмена" );
        panGraph.setPreferredSize( new Dimension( 650, 200 ) );
        panGraph.setBounds( new Rectangle( 10, 43, 650, 168 ) );
        panGraph.setLayout( null );
        this.setResizable( false );
        this.setTitle( "Импорт LAS файла" );
        lLasName.setText( "..." );
        lLasName.setBounds( new Rectangle( 156, 17, 499, 15 ) );
        cbGraph.addItem("Азимут");
        cbGraph.addItem("Зенит");
        cbGraph.addItem("Поворот");
        jPanel3.setBorder( border1 );
        jPanel3.setBounds( new Rectangle( 12, 46, 358, 70 ) );
        jPanel3.setLayout( null );
        cbRotate.setBounds( new Rectangle( 250, 40, 47, 17 ) );
        cbZenith.setBounds( new Rectangle( 169, 40, 47, 17 ) );
        cbAzimuth.setBounds( new Rectangle( 89, 40, 45, 17 ) );
        cbTime.setBounds( new Rectangle( 11, 39, 52, 18 ) );
        grCanvas.setBounds( new Rectangle( 0, 0, 651, 169 ) );
        jLabel1.setForeground( Color.red );
        jLabel1.setText( "Эталон" );
        jLabel1.setBounds( new Rectangle( 481, 25, 66, 15 ) );
        jLabel7.setForeground( Color.blue );
        jLabel7.setText( "Прибор" );
        jLabel7.setBounds( new Rectangle( 565, 25, 80, 15 ) );
        this.getContentPane().add( jPanel1 );
        this.getContentPane().add( jPanel3 );
        jPanel1.add( cbGraph );
        jPanel1.add( jLabel6 );
        jPanel1.add( panGraph );
        jPanel1.add( jLabel7 );
        jPanel1.add( jLabel1 );
        panGraph.add( grCanvas, null );
        jPanel3.add( jLabel4, null );
        jPanel3.add( jLabel5, null );
        jPanel3.add( cbZenith, null );
        jPanel3.add( cbRotate, null );
        jPanel3.add( cbTime, null );
        jPanel3.add( jLabel2, null );
        jPanel3.add( cbAzimuth, null );
        jPanel3.add( jLabel3, null );
        this.getContentPane().add( bImport );
        this.getContentPane().add( bCancel );
        this.getContentPane().add( lLasName );
        this.getContentPane().add( bLoad );
        bLoad.setBounds( new Rectangle( 12, 12, 141, 27 ) );
        bLoad.setText( "Загрузить LAS..." );
        bLoad.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                loadLas();
                updateUI();
                updateToolGraph();
                updateAccGraph();
            }
        }
        );
        bImport.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                confirmChanges();
            }
        }
        );
        bCancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                cancelChanges();
            }
        }
        );

        cbGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                currentTrack = cbGraph.getSelectedIndex();
                updateToolGraph();
                updateAccGraph();
            }
        }
        );
        cbTime.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                timeTrack = cbTime.getSelectedIndex();

                updateToolGraph();
            }
        }
        );
        cbAzimuth.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                inklTracks[0] = cbAzimuth.getSelectedIndex();
                updateToolGraph();
            }
        }
        );
        cbZenith.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                inklTracks[1] = cbZenith.getSelectedIndex();
                updateToolGraph();
            }
        }
        );
        cbRotate.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                inklTracks[2] = cbRotate.getSelectedIndex();
                updateToolGraph();
            }
        }
        );
        grCanvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e){
                //int m = e.getModifiers();
                offset = e.getX() - startDragX;
                //System.out.println(offsetTool + "; "+ offset);
                //if((e.getModifiers() & MouseEvent.BUTTON1_DOWN_MASK)>0) {
                //    offset++;
               // }
               updateToolGraph();

            }
            public void mouseMoved(MouseEvent e){  }
        }
            );
        grCanvas.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e){ }
            public void mouseEntered(MouseEvent e){ }
            public void mouseExited(MouseEvent e){ }
            public void mousePressed(MouseEvent e){
                int b = e.getButton();
                if(b == MouseEvent.BUTTON1) {
                    startDragX = e.getX();

                }
            }
            public void mouseReleased(MouseEvent e){
                if(e.getButton() == MouseEvent.BUTTON1) {
                    offsetTool += e.getX() - startDragX;
                    offset = 0;

                }
            }

        }
            );

        graphAcc.lineWidth = 1;
        graphAcc.lineColor = new Color(0xff0000);
        graphTool.lineWidth = 1;
        graphTool.lineColor = new Color(0x0000ff);

        grCanvas.addGraph(graphAcc);
        grCanvas.addGraph(graphTool);
        grCanvas.areaLB.x = 2;
        grCanvas.areaLB.y = 2;

        grCanvas.clipSize.x = 650.0f;
        grCanvas.clipSize.y = 360.0f;
        grCanvas.clipZero.y = 0f;
        grCanvas.setPreferredSize( new Dimension(650,170));
        setSize(700,416);
        setModal(true);
        UiUtils.toScreenCenter(this);
        updateUI();

    }

    JButton bLoad = new JButton();
    JLabel lLasName = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JComboBox cbTime = new JComboBox();
    JComboBox cbAzimuth = new JComboBox();
    JLabel jLabel4 = new JLabel();
    JComboBox cbZenith = new JComboBox();
    JLabel jLabel5 = new JLabel();
    JComboBox cbRotate = new JComboBox();
    JPanel jPanel1 = new JPanel();
    Border border1 = BorderFactory.createLineBorder( Color.gray, 1 );
    Border border2 = new TitledBorder( border1, "Совмещение по графику" );
    JLabel jLabel6 = new JLabel();
    JComboBox cbGraph = new JComboBox();
    JButton bImport = new JButton();
    JButton bCancel = new JButton();
    JPanel panGraph = new JPanel();
    JPanel jPanel3 = new JPanel();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel7 = new JLabel();
}
