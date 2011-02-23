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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.*;


/**
 * <p>Title: ќбозреватель методики калибровки дл€ приборов ћ»Ќ, ћ»Ќ+√ </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class Min2MethodsViewer
    extends UaksiMethodsViewer
    implements SignalEventListener {

    Command[] minCommands = {
        new Command( this, "doCalibrationFull", "калибровка полностью", "" ),
        new Command( this, "doCalibrationRotatePart",
                     "калибровка угла поворота частично", "" ),
        new Command( this, "doCalibrationRotate", "калибровка угла поворота",
                     "" ),
        new Command( this, "doCalibrationZenith", "калибровка зенитного угла",
                     "" ),
        new Command( this, "doCalibrationZenithPart",
                     "калибровка зенитного угла частично", "" ),
        new Command( this, "doCalibrationAzimuth",
                     "калибровка азимутального угла", "" ),
        new Command( this, "doCalibrationAzimuthAtZenith2",
                     "калибровка азимутального угла при «", "" ),
        new Command( this, "doCalibrationAzimuthPart",
                     "калибровка азимутального угла частично", "" ),
        new Command( this, "doJoinRotates", "совмещение углов поворота", "" ),
    };

    ///////////////////////////////////////////////////////////////////////////
    protected JComboBox cbAzRotates = new JComboBox();
    protected JComboBox cbRotateZens = new JComboBox();
    protected JLabel lAzRotates = new JLabel();
    protected JLabel lRotateZens = new JLabel();


    public Min2MethodsViewer() {
        super();
        commands = minCommands;
        rotateTableIndex = 0;
        zenithTableIndex = 1;
        azimuthTableIndex = 2;

    }

    public void start() {
        super.start();
        try {

            jbInit();
            initRotateTab();
            switchOn();

            //initAzimuthTab();
        } catch ( Exception ex ) {
            UiUtils.showError( this, ex.getLocalizedMessage() );
            //ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        cbAzRotates.setBounds( new Rectangle( 350, 7, 115, 24 ) );
        cbAzRotates.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                changeAzimuthTable();
            }
        } );
        lAzRotates.setText( "”гол поворота:" );
        lAzRotates.setBounds( new Rectangle( 252, 11, 134, 15 ) );
        lRotateZens.setText( "«енитный угол:" );
        lRotateZens.setBounds( 7, 11, 124, 15 );
        cbRotateZens.setBounds( new Rectangle( 110, 7, 115, 24 ) );
        cbRotateZens.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                changeRotateTable();
            }
        } );
        panelZ.add( lRotateZens, null );
        panelZ.add( cbRotateZens, null );

        panelX.add( cbAzRotates, null );
        panelX.add( lAzRotates, null );

    }

    protected void initRotateTab() {
        Vector tables = datas.selectTables( "type", "rotate" );
        int size = tables.size();
        cbRotateZens.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "zenith" );
            if ( value != null ) {
                cbRotateZens.addItem( value );
            }
        }

    }
    protected void initAzimuthTab() {
        Vector tables = datas.selectTables( "type", "azimuth" );
        Vector tbz = new Vector();
        int size = tables.size();
        cbAzZeniths.removeAllItems();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "zenith" );
            if ( value != null ) {
                //cbAzZeniths.get
                //String str = (String)tbz.get(0);
                if( !tbz.contains(value)) {
                    cbAzZeniths.addItem( value );
                    tbz.add(value);
                }
            }
        }
        cbAzRotates.removeAllItems();
        tbz.clear();
        for ( int i = 0; i < size; i++ ) {
            MeasureTable table = ( MeasureTable ) tables.get( i );
            String value = table.getProperty( "rotate" );
            if ( value != null ) {
                if( !tbz.contains(value)) {
                    cbAzRotates.addItem( value );
                    tbz.add(value);
                }
            }
        }
        tbz.clear();
    }

    protected void changeRotateTable() {
        String value = ( String ) cbRotateZens.getSelectedItem();
        Vector tables = datas.selectTypedTables( "rotate", "zenith", value );
        if ( tables.size() > 0 ) {
            this.tables[0].measureTable = ( MeasureTable ) tables.get( 0 );
        }
        this.tables[0].init();
    }

    protected void changeAzimuthTable() {
        String value = ( String ) cbAzZeniths.getSelectedItem();
        String valRotate = ( String ) cbAzRotates.getSelectedItem();

        Vector tables = datas.selectTypedTables( "azimuth", "zenith", value );
        if ( tables.size() > 0 ) {
            // выборка из списка таблицы с указанным углом поворота
            for(int i = 0; i < tables.size(); i++) {
                MeasureTable table = ( MeasureTable )tables.get( i );
                if(table.getProperty("rotate").equals(valRotate)) {
                    //System.out.println("sel table " + value + " " + valRotate);
                    this.tables[2].measureTable = table;
                    break;
                }
            }
        }
        // TODO
        this.tables[2].init();
    }

    public void doCalibrationFull() throws Exception {
        doJoinRotates();
        doCalibrationRotate();
        doCalibrationZenith();
        doCalibrationAzimuth();
    }

    public void doCalibrationAzimuth() {
        int zens = cbAzZeniths.getItemCount();
        //int rots = cbAzRotates.getItemCount();
        for ( int i = 0; i < zens; i++ ) {
            cbAzZeniths.setSelectedIndex( i );
            doCalibrationAzimuthAtZenith2();
        }
    }

    public void doCalibrationAzimuthAtZenith() {
        int rots = cbAzRotates.getItemCount();
        for ( int rot = 0; rot < rots; rot++ ) {
            cbAzRotates.setSelectedIndex( rot );
            changeAzimuthTable();
            doCalibrationAzimuthPart();
        }
    }
    public void doCalibrationAzimuthAtZenith2(  ) {
        int rots = cbAzRotates.getItemCount();
        cbAzRotates.setSelectedIndex( 0 );
        changeAzimuthTable();
        selectedContainer = tables[2];
        MeasureTable workTable = selectedContainer.measureTable;
        Double zenith = new Double( workTable.getProperty( "zenith" ) );
        doGoTo( 'y', zenith );  // выход на зенитную точку

        String name = workTable.getProperty( "name" );
        int pointsCount = workTable.size();
        char plane = nameToPlane( name );

        int first = 0;
        int inc = 1;
        Double firstPoint = new Double( workTable.getChain( 0 ).
                                        getReproductionValue() );
        Double lastPoint = new Double( workTable.getChain( pointsCount - 1 ).
                                       getReproductionValue() );
        Value current = doMeasureAccurate( plane );
        if ( Math.abs( current.value - firstPoint.doubleValue() ) <
             Math.abs( current.value - lastPoint.doubleValue() ) ) {
            first = 0;
            inc = 1;
        } else {
            first = pointsCount - 1;
            inc = -1;
        }
        for ( int i = 0; i < pointsCount; i++, first += inc ) {
            try {
                Double value = new Double( workTable.getChain( first ).
                                           getReproductionValue() );
                doGoTo( 'x', value );  // выход на азимутальную точку
                for ( int rot = 0; rot < rots; rot++ ) {

                    cbAzRotates.setSelectedIndex( rot );
                    // смена таблицы
                    changeAzimuthTable();
                    workTable = this.tables[2].measureTable;
                    // выход на точку по повороту
                    Double rotate = new Double( workTable.getProperty( "rotate" ) );
                    doGoTo( 'z', rotate );
                    Thread.sleep( 4000 );
                    // замер
                    doMeasure( workTable, first, 'x' );
                    selectedContainer.table.repaint();
                }
                //tables[2].table.repaint();
            } catch ( Exception ex ) {
                System.err.println( ex.getMessage() );
                ex.printStackTrace();
            }
        }

    }

    public void doCalibrationRotate() {
        int size = cbRotateZens.getItemCount();
        for ( int i = 0; i < size; i++ ) {
            cbRotateZens.setSelectedIndex( i );
            changeRotateTable();
            doCalibrationRotatePart();
        }
    }

    public void doCalibrationRotatePart() {
        clearMessages();
        selectedContainer = tables[0];
        lMessage1.setText( " алибровка угла поворота" );
        MeasureTable workTable = selectedContainer.measureTable;
        Number dd = null;
        try {
            dd = df.parse( workTable.getProperty( "zenith" ) );
        } catch ( ParseException ex ) {
            System.err.println("error parsing input at"+ex.getErrorOffset());
        }
        Double zenith = new Double( dd.doubleValue() );
        doGoTo( 'y', zenith );
        doCalibrationTable( workTable );
        lMessage2.setText( " алибровка выполнена" );

    }


    public void doCalibrationAzimuthPart() {
        clearMessages();
        selectedContainer = tables[2];
        lMessage1.setText( " алибровка азимутального угла" );
        MeasureTable workTable = selectedContainer.measureTable;
        Double zenith = new Double( workTable.getProperty( "zenith" ) );
        Double rotate = new Double( workTable.getProperty( "rotate" ) );
        doGoTo( 'y', zenith );
        doGoTo( 'z', rotate );
        //lMessage2.setText( "ѕоиск максимального отклонени€" );
        // перед выборкой угла поворота выставл€ем прибор на север
        //doGoTo('x', new Double(0) );
        //doFindMaxDeviation();
        doCalibrationTable( workTable );
        lMessage2.setText( " алибровка выполнена" );
    }

    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                //Channel chan = toolSource.getChannel( "angles" );
                //if ( chan == null ) {
                //    chan = toolSource.getChannel( "values" );
               // }
                ChannelValue val;// = toolSource.getValue("azimuth");
                Channel chan = toolSource.getChannel( "angles" );
                if ( chan == null ) {
                    chan = toolSource.getChannel( "values" );
                }
                toolValues.azimuth = toolSource.getValue( "azimuth" ).getAsValue();
                toolValues.zenith = toolSource.getValue( "zenith" ).getAsValue();
                toolValues.rotate = toolSource.getValue( "rotate" ).getAsValue();

                val = toolSource.getValue("zenith");
                //double zenith = toolAngles.zenith.getValue();
                //if( val != null ) {
                //    val.getAsValue().delta = 0.2;
                //    zenith = val.getAsDouble();
                //    toolAngles.zenith.setAngle( zenith );
                //}
                //val = toolSource.getValue("azimuth");
                //if( val != null ) {
                //    toolAngles.azimuth.setAngle( val.getAsDouble() );
               // }
                //val = toolSource.getValue("rotate");
                //if( val != null ) {
                 //   toolAngles.rotate.setAngle( val.getAsDouble() );
                //}
                if ( chan != null ) {
                    //System.out.println( "### tool" );
                    {
                        Value val1;
                        //------ process zenith degree
                        val1 = toolValues.zenith;
                        double zen = val1.value;
                        double err = 100;
                        val1.delta = 0.2;
                        //System.out.print( "Zn=" + formatValue( val1 ) + ";  " );
                        //------ process azimuth degree
                        val1 = toolValues.azimuth;
                        if(zen < 6.5) {
                            err = 0.125/Math.sin(zen/180.0*Math.PI) + 0.4;
                        } else {
                            err = 1.5;
                        }
                        val1.delta = err;
                        //System.out.print( "Az=" + formatValue( val1 ) + ";  " );
                        //------ process vizir (apsida)
                        //val1 = chan.getValue( 2 ).getAsValue();
                        val1 = toolValues.rotate;
                        if(zen < 6.5) {
                            err = 0.125/Math.sin(zen/180.0*Math.PI)+ 0.4;
                        } else {
                            err = 1.5;
                        }
                        val1.delta = err;
                        //System.out.print( "Rt=" + formatValue( val1 ) + ";  " );
                        //------ process zenith (magnet)
                        val1 = chan.getValue( 3 ).getAsValue();
                        if ( (zen >= 0) && (zen <= 3) ) {
                            err = 2;
                        } else {
                            err = 4*Math.sin(zen/180.0*Math.PI)+1.8;
                        }
                        val1.delta = err;
                        //System.out.print( "Vz=" + formatValue( val1 ) + ';' );
                        //System.out.println();
                    }
                }
                    //toolAngles.zenit = chan.getValue( 1 ).angle;
                    //toolAngles.rotate = chan.getValue( 2 ).angle;
                    //toolAngles.zenit.setAngle(chan.getValue( 1 ).doubleValue );
                    //toolAngles.rotate.setAngle( chan.getValue( 2 ).doubleValue );

                    redrawToolValues();
                //}

            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Ќет св€зи с прибором" );
            }

        } else
        if ( src.equals( unit ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = unit.getChannel( "angles" );
                if ( chan != null ) {
                    accValues.azimuth = chan.getValue( 0 ).getAsValue();
                    accValues.zenith = chan.getValue( 1 ).getAsValue();
                    accValues.rotate = chan.getValue( 2 ).getAsValue();
                    redrawAccurateValues();
                }

            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Ќет св€зи с установкой" );
            }

        }

    }

}
