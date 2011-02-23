package lsdsoft.zeus.ui;


import java.text.*;

import javax.swing.table.*;

import lsdsoft.metrolog.*;
import java.awt.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */
public class MeasureChainModel
    extends AbstractTableModel {
    private static final int COLUMN_COUNT = 2;
    private String[] columnNames = {
        "<html><center><font size=-2><b>Эталонное<br>значение",
        "<html><center><font size=-2><b>Значение<br>по прибору",
    };

    protected MeasureChain chain;
    public MeasureChainModel( MeasureChain chain ) {
        super();
        this.chain = chain;
    }

    public int getRowCount() {
        return chain.size();
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return true;
    }

    public Object getValueAt( int rowIndex, int columnIndex ) {
        Object obj;
        double value = 0;
        NumberFormat form = NumberFormat.getInstance();
        form.setMaximumFractionDigits( 3 );
        String str = "";
        switch ( columnIndex ) {
            case 0:
                str = form.format( chain.getPoint( rowIndex ).getAccurateValue() );
                break;
            case 1:
                str = form.format( chain.getPoint( rowIndex ).getToolValue() );
                break;
        }
        return str;
    }

    public void setValueAt( Object aValue,
                            int rowIndex,
                            int columnIndex ) {
        NumberFormat form = NumberFormat.getInstance();
        double value = 0.0;
        try {
            value = form.parse( aValue.toString() ).doubleValue();
        } catch ( ParseException ex ) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        switch(columnIndex) {
            case 0:
                chain.getPoint( rowIndex ).accurate.value = value;
                break;
            case 1:
                chain.getPoint( rowIndex ).setToolValue( value );
                break;
        }
    }

    public String getColumnName( int col ) {
        return columnNames[col];
    }

    public Class getColumnClass( int c ) {
        return columnNames[c].getClass();
    }

}
