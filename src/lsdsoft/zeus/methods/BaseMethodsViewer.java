package lsdsoft.zeus.methods;


import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

//import com.borland.jbcl.layout.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.ui.*;
import javax.swing.event.*;
import java.lang.reflect.*;
import javax.swing.Timer;
import lsdsoft.util.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.report.*;
import java.io.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class BaseMethodsViewer
    extends AbstractMethodsViewer {


    protected Date startTime;
    protected int delayBeforeMeasure = 4000; // delay in ms
    protected MeasureDatas datas = null;
    protected AbstractCheckUnit unit = null;
    protected ChannelDataSource toolSource = null;
    protected CommandExecuter commandExecuter;
    //TableContainer selectedContainer;

    JPopupMenu tablePopup = new JPopupMenu();
    JMenuItem miChange = new JMenuItem();
    JMenuItem miMeasure = new JMenuItem();

    class TableContainer {
        public JTable table;
        public MeasureTable measureTable;
        public JScrollPane scrollPane = new JScrollPane();
        public void setTableModel(AbstractTableModel model) {
            table.setModel(model);
        }
        public void init() {
            table = new JTable( new MeasureTableModel( measureTable ) );
            Font font = new Font( "Dialog", 0, 14 );
            // init tableX
            table.setFont( font );
            table.setBorder( BorderFactory.createLineBorder( Color.black ) );
            table.setMinimumSize( new Dimension( 300, 200 ) );
            table.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
            table.setRowHeight( 16 );
            table.addMouseListener( mouseAdapter );
            //tables[i].table.setCellSelectionEnabled(false);
            table.setSelectionMode( ListSelectionModel.
                                    SINGLE_SELECTION );
            table.remove(tablePopup);
            table.add( tablePopup );
            tablePopup.setVisible(false);
            scrollPane.setAutoscrolls( true );
            scrollPane.getViewport().add( table, null );

        }
    }


    public BaseMethodsViewer() {
        tablePopup.add( miChange );
        tablePopup.add( miMeasure );
    }

    public void setMeasureDatas( MeasureDatas datas ) {
        this.datas = datas;
        datas.calc();
    }



    protected void execCommand(Command command) {
        if ( commandExecuter != null ) {
            if ( commandExecuter.isAlive() ) {
                return;
            }
        }
        commandExecuter = new CommandExecuter( command );
        //executer.setDaemon(true);
        //System.out.println( "Process started" );
        commandExecuter.start();

    }
    protected void onStart() {
    }

    protected void onStop() {
    }

    public void stopProcess( ActionEvent e ) {
        if ( commandExecuter != null ) {
            if ( commandExecuter.isAlive() ) {
                try {
                    unit.stop();
                } catch ( Exception ex ) {
                    ex.printStackTrace();
                }
                timeUpdater.stop();
                //clearMessages();
                //executer.interrupt();
                commandExecuter.stop();
                onStop();
                //executer.destroy();

            }
        }

    }

    protected void initTable( JTable table ) {
        //TableColumn column = null;
        JTableHeader header = table.getTableHeader();
        header.setVisible( true );
        for ( int i = 0; i < 3; i++ ) {
            //table.getColumn
        }

    }


    /**
     * Переопределить в потомках...
     * Вызывается для обновления показаний времени
     */
    protected void updateTime() {

    }

    Timer timeUpdater = new Timer( 1000, new ActionListener() {
        public void actionPerformed( ActionEvent evt ) {
            updateTime();
        }
    } );

    //CommandExecuter commandExecuter;
    /**
     * Исполнитель команд
     */
    protected class CommandExecuter
        extends Thread {
        private AbstractCheckUnit unit;
        private Vector commandList;
        private Command command;
        public CommandExecuter( Command command ) {
            this.command = command;
        }

        public void run() {

            try {
                startTime = new Date();
                updateTime();
                timeUpdater.start();
                timeUpdater.setRepeats( true );
                command.execute();
                timeUpdater.stop();
            } catch ( Exception ex ) {
                System.err.println( ex.getCause().getMessage() );
            }
            finally {
                timeUpdater.stop();
            }
        }
    }


    MouseAdapter mouseAdapter = new MouseAdapter() {
        public void mouseClicked( MouseEvent e ) {
            if( e.getSource() instanceof JTable ) {
                JTable table = ( JTable ) e.getSource();
                if ( e.getButton() == MouseEvent.BUTTON3 ) {
                    int row = table.rowAtPoint( e.getPoint() );
                    table.setRowSelectionInterval( row, row );
                    tablePopup.show( e.getComponent(), e.getX(), e.getY() );
                    //tablePopup.setVisible(false);
                }
            }
        }
    };

}
