package lsdsoft.zeus.methods;


import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

//import com.borland.jbcl.layout.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.util.*;
import lsdsoft.welltools.im.ion1.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.report.*;
import lsdsoft.zeus.ui.*;
import bsh.*;
import java.net.*;
import java.text.*;


/**
 * <p>Title: Обозреватель для прибора ИНКЛ-75</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class Inkl75MethodsViewer
    extends UaksiMethodsViewer
    implements SignalEventListener {

    //Interpreter shell = new Interpreter();



    private Command[] inklCommands = {
        new Command( this, "doCalibrationFull", "калибровка полностью", "" ),
        //new Command( this, "doCalibrationRotate", "калибровка угла поворота",
        //             "" ),
        new Command( this, "doCalibrationZenith", "калибровка зенитного угла",
                     "" ),
        new Command( this, "doCalibrationZenithPart",
                     "калибровка зенитного угла частично", "" ),
        new Command( this, "doCalibrationAzimuth",
                     "калибровка азимутального угла", "" ),
        new Command( this, "doCalibrationAzimuthPart",
                     "калибровка азимутального угла частично", "" ),
        //new Command( this, "doJoinRotates", "совмещение углов поворота", "" ),
    };
    ///////////////////////////////////////////////////////////////////////////


    // нормы погрешности на ИНКЛ-75 (азимут, зенит, поворот)
    private final static double[] INKL_ERRORS = {2.0, 0.5, 3.0};
    private final static double[] UAKSI_ERRORS = {0.5, 0.08, 1};



    private JButton bImportLas = new JButton("Импорт LAS");
    private JButton bSaveTracker = new JButton();

    Tracker tracker = new Tracker(4);




    public Inkl75MethodsViewer() {
        super();
        try {
            commands = inklCommands;
            rotateTableIndex = -1;
            azimuthTableIndex = 0;
            zenithTableIndex = 1;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void doCalibrationFull() throws Exception {
        int res = JOptionPane.showConfirmDialog( this,
            "Визирные углы совмещены? Продолжаем?",
                                                 "Предупреждение ",
                                                 JOptionPane.YES_NO_OPTION );
        if ( res == JOptionPane.YES_OPTION ) {
            //doCalibrationRotate();
            doCalibrationZenith();
            doCalibrationAzimuth();
        }
    }

    protected void addToTracker() {
        double[] vals = new double[4];
        long time = System.currentTimeMillis();
        vals[0] = time;
        //long t2 = (long)vals[0];
        vals[1] = accValues.azimuth.value;
        vals[2] = accValues.zenith.value;
        vals[3] = accValues.rotate.value;
        tracker.points.add(vals);
    }



    public void start() {
        super.start();
        boolean quit = false;
        try {
            /*
                         try {
                toolSource = DataFactory.createToolDataSource();
                         } catch ( Exception ex ) {
                UiUtils.showError( this,
                                   ex.getLocalizedMessage() );
                         }
                         if ( toolSource == null ) {
                int res = UiUtils.showConfirmError( this,
                    "<html><center>Не удалось создать источник данных прибора" +
                    "<br>Продолжить?" );
                quit = res != 0;
                         } else {
                toolSource.addSignalListener( this );
                         }
             */
            jbInit();
            //redrawToolValues();
            /*
                         // init shell vars
                         shell.set( "zeus", zeus );
                         shell.set( "unit", unit );
                         //shell.eval( "importCommands(\"/data/commands/\")" );
                         //ClassManager.
                         //shell.getClassManager().addClassPath(new URL("file", "", "d:/zeus/data"));
                         //URL url = shell.getClassManager().getResource( "test.bsh" );
                         shell.getNameSpace().importCommands( "/commands" );
                         //shell.eval("debug()");
                         shell.source("data/commands/Test1.bsh");
                         shell.eval( "Test1.print1()" );
                         Object o = shell.get( "coms" );
                         if ( o instanceof String ) {

                         }
             */

        } catch ( Exception ex ) {
            UiUtils.showError( this, ex.getLocalizedMessage() );
            //ex.printStackTrace();
        }
    }

    private Tracker buildTrackerFromDatas(){
        Tracker tracker = new Tracker( 4 );
        int tableCount = datas.size();
        for ( int i = 0; i < tableCount; i++ ) {
            MeasureTable table = datas.getTable( i );
            String type = table.getProperty( "type" );
            int iTrack = 1; // index in array of track nums
            if ( type.equals( "zenith" ) ) {
                iTrack = 2;
            } else
            if ( type.equals( "rotate" ) ) {
                iTrack = 3;
            }
            System.out.println( type );

            for ( int rr = 0; rr < table.size(); rr++ ) {
                MeasureChain mc = table.getChain( rr );
                int points = mc.size();
                for ( int pnt = 0; pnt < points; pnt++ ) {
                    MeasurePoint mp = mc.getPoint( pnt );
                    double acc = mp.getAccurateValue();
                    if ( acc != 0.0 ) {
                        double[] vals = new double[4];
                        vals[0] = mp.getTime();
                        System.out.println( ( long )vals[0] + " " +
                                            TextUtil.
                                            dateToString( mp.getDate(),
                            "HH:mm:ss" ) );
                        vals[iTrack] = acc;

                        tracker.points.add( vals );
                    }
                }
            }
        }
        // sort by time column
        tracker.sort(0);
        return tracker;
    }

    private void importLas() {
        InklImportLas imp = new InklImportLas();
        Tracker accTracker = buildTrackerFromDatas();
        saveTracker( accTracker );
        imp.setAccTracker( accTracker );
        imp.setVisible( true );
        //System.out.println( "@AccstartTime:" +
        //                    TextUtil.dateToString( new Date( ( long )accTracker.
        //    getRow( 0 )[0] ), "HH:mm:ss" ));
        if ( imp.confirm ) {
            Tracker toolTracker = imp.getTracker();
            double offset = imp.getOffset();
            double[] row = tracker.getRow( 0 );
            double toolStartTime = accTracker.getRow( 0 )[0];
            toolStartTime += offset * 1000.0;
            //String tt2 = TextUtil.dateToString( new Date((long)toolStartTime), "HH:mm:ss");
            //System.out.println("__" + tt2);
            int trkSize = toolTracker.points.size();
            // заносим соответсвуюющее время в колонку (синхронизация)
            for ( int i = 0; i < trkSize; i++ ) {
                row = toolTracker.getRow( i );
                row[imp.timeTrack] *= 1000.0; // преобразовывание секунд в милисекунды
                row[imp.timeTrack] += toolStartTime;
                Date dat = new Date( ( long )row[imp.timeTrack] );
                //String tt = TextUtil.dateToString( dat, "HH:mm:ss");
                //System.out.println(tt + " " + row[14] + " " + row[15] + " " + row[16]);

            }
            toolTracker.selectTrackForSearch( imp.timeTrack );
            //int rr = tracker.findRow(startTime.getTime());
            int tableCount = datas.size();
            for ( int i = 0; i < tableCount; i++ ) {
                MeasureTable table = datas.getTable( i );
                String type = table.getProperty( "type" );
                int iTrack = 0; // index in array of track nums
                if ( type.equals( "zenith" ) ) {
                    iTrack = 1;
                } else
                if ( type.equals( "rotate" ) ) {
                    iTrack = 2;
                }
                int trackNum = imp.inklTracks[iTrack];
                System.out.println( type + "(" + trackNum + ")" );
                for ( int rr = 0; rr < table.size(); rr++ ) {
                    MeasureChain mc = table.getChain( rr );
                    int points = mc.size();
                    for ( int pnt = 0; pnt < points; pnt++ ) {
                        MeasurePoint mp = mc.getPoint( pnt );
                        System.out.println( "#search for: " +
                                            TextUtil.
                                            dateToString( mp.getDate(),
                            "HH:mm:ss" ) );

                        int iRow = toolTracker.findRow( mp.getTime() );
                        double newToolVal = 0;
                        if ( iRow < 0 ) {
                            double[] row1 = toolTracker.getRow( -iRow );
                            double[] row2 = toolTracker.getRow( -iRow - 1 );
                            if ( row1 == null || row2 == null ) {
                                System.out.println( "### error finding value" );

                            } else {
                                newToolVal = ( row1[trackNum] + row2[trackNum] ) /
                                    2.0;
                            }
                        } else {
                            row = toolTracker.getRow( iRow );
                            newToolVal = row[trackNum];
                        }
                        System.out.print( iRow + "# " );
                        // для азимута и поворота из крогового значения взять
                        // ближайшее от эталона расположенное на оси
                        if ( ( iTrack == 0 ) || ( iTrack == 2 ) ) {
                            double accVal = mp.getAccurateValue();
                            double d1 = Math.abs( accVal - newToolVal );
                            double d2 = Math.abs( accVal - ( newToolVal - 360.0 ) );
                            if ( d2 < d1 ) {
                                newToolVal -= 360.0;
                            }
                        }
                        Value newToolValue = new Value( newToolVal,
                            INKL_ERRORS[iTrack] );
                        System.out.println( TextUtil.dateToString( mp.getDate(),
                            "HH:mm:ss" ) + " " + mp.getAccurateValue() + " " +
                                            newToolValue );

                        mp.setToolValue( newToolValue );
                    }
                }

            }
            datas.calc();
            selectedContainer.table.repaint();

        }
        /*
                 LasFile las = new LasFile();
                 String lasName = "";
                 try {
            las.load( new File( lasName ) );
                 } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());
                 }
                 Tracker tracker = new Tracker(las);
         */
    }

    private void saveTracker( Tracker tracker ) {
        Date date = new Date();
        for ( int i = 0; i < tracker.points.size(); i++ ) {
            double[] vals = tracker.getRow( i );
            date.setTime( ( long )vals[0] );
            System.out.print( "#" + i + " " +
                              TextUtil.dateToString( date, "HH:mm:ss" ) );
            for ( int j = 1; j < vals.length; j++ ) {
                System.out.print( " " + vals[j] );
            }
            System.out.println();
        }

    }

    private void jbInit() throws Exception {
        // import las buuton
        bImportLas.setText( "Импорт LAS" );
        bImportLas.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                importLas();
            }
        } );
        // save tracker buuton
        /**
                 bSaveTracker.setText( "Save Tracker" );
         bSaveTracker.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                saveTracker(tracker);
            }
                 } );
         **/



        jToolBar1.add( bImportLas, null );


    }

}
