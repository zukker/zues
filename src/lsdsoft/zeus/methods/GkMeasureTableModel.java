package lsdsoft.zeus.methods;


import javax.swing.table.*;
import lsdsoft.metrolog.*;
import java.text.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class GkMeasureTableModel
    extends AbstractTableModel {
    private static final int COLUMN_COUNT = 6;
    private String[] columnNames = {
        "<html><center><font size=-2><b>№<br>п/п</b></font>",
        "<html><center><font size=-2><b>Вых. сигнал<br>прибора,<br>у.е.",
        "<html><center><font size=-2><b>Измеренное<br>значение,<br>мкР/ч",
        "<html><center><font size=-2><b>Эталонное<br>значение,<br>мкР/ч",
        "<html><center><font size=-2><b>Оценка абс.<br>погрешности,<br>мкР/ч</b></font></center>",
        //"<html><center><font size=-2><b>Качество<br>калибровки<br>%</b></font></center>",
        "<html><center><font size=-2><b>Признак<br>годности</b></font></center>",
    };
    private String VALID = "<html><center><font size=-1 color=#00cc00><b>ГОДЕН";
    private String INVALID = "<html><font size=-1 color=#ff0000><b>НЕ ГОДЕН";

    protected MeasureTable table;
    public GkMeasureTableModel( MeasureTable table ) {
        super();
        this.table = table;
    }

    public int getRowCount() {
        return table.size();
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public Object getValueAt( int rowIndex, int columnIndex ) {
        MeasureChain chain = table.getChain( rowIndex );
        Object obj;
        double value = 0;
        NumberFormat form = NumberFormat.getInstance();
        form.setMaximumFractionDigits( 1 );
        String str = "";
        switch ( columnIndex ) {
            case 0:
                str = form.format( rowIndex + 1 );
                break;
            case 1:
                str = form.format( chain.getCodeAverage() );
                break;
            case 2:
                str = "<html>" + form.format( chain.getToolValue().value );
                if(chain.getToolValue().delta > 0) {
                    //str += "&plusmn;" + form.format( chain.getToolValue().delta );
                }
                break;
            case 3:
                str = "<html>" + form.format( chain.getAccurateValue().value );
                str += "&plusmn;" + form.format( chain.getAccurateValue().delta );
                break;
            case 4:
                str = form.format( chain.getDelta() );
                break;
            case 5:
                str = chain.isValid() ? VALID : INVALID;
                //    str = form.format( chain.getQuality());
                break;
            default:
                str = "";

        }
        return str;
    }

    public String getColumnName( int col ) {
        return columnNames[col];
    }

    public Class getColumnClass( int c ) {
        return columnNames[c].getClass();
    }

}
