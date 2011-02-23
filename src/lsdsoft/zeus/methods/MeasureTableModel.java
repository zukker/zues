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

public class MeasureTableModel
    extends AbstractTableModel {
    private static final int COLUMN_COUNT = 6;
    private String[] columnNames = {
        "<html><center><font size=-2><b>Воспр.<br>значение</b></font>",
        "<html><center><font size=-2><b>Эталонное<br>значение",
        "<html><center><font size=-2><b>Значение<br>по прибору",
        "<html><center><font size=-2><b>Оценка абс.<br>погрешности</b></font></center>",
        "<html><center><font size=-2><b>СКО</b></font></center>",
        "<html><center><font size=-2><b>Вероятность<br>годности, %</b></font></center>",
    };
    private String VALID = "<html><center><font size=-1 color=#cccc00><b>";
    private String VALID100 = "<html><center><font size=-1 color=#00bb00><b>100";
    private String INVALID = "<html><font size=-1 color=#ff0000><b>НЕ ГОДЕН";

    protected MeasureTable table;
    public MeasureTableModel( MeasureTable table ) {
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
        NumberFormat form = NumberFormat.getInstance();
        form.setMaximumFractionDigits( 3 );
        String str;
        switch ( columnIndex ) {
            case 0:
                str = form.format( chain.getReproductionValue() );
                break;
            case 1:
                str = form.format( chain.getAccurateValue().value );
                break;
            case 2:
                str = form.format( chain.getToolValue().value );
                break;
            case 3:
                str = form.format( chain.getDelta() );
                break;
            case 4:
                str = form.format( chain.getSKO() );
                break;
            case 5:
                double valid = chain.getValid();
                if (valid <= 0) {
                    str = INVALID;
                } else if (valid >= 100) {
                    str = VALID100;
                } else {
                    str = VALID + String.valueOf((int)Math.round(valid));
                }
                break;
            default:
                str = "";
        }
        return str;
    }

    public String getColumnName( int col ) {
        return columnNames[col];
    }

    public Class getColumnClass( int clss ) {
        return columnNames[clss].getClass();
    }

}
