package lsdsoft.welltools.im.ion1;

import com.lsdsoft.math.*;
import lsdsoft.metrolog.*;

/**
 * <p>Title: Данные градуировки прибора ИОН-1</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class ION1GraduationData {
    // алгоритмические углы
    public double algAx;
    public double algAy;
    public double algAz;
    public double algHxy;
    public double algHyz;
    public double algHzx;
    // нули датчиков
    public double zeroAx;
    public double zeroAy;
    public double zeroAz;
    public double zeroHxy;
    public double zeroHyz;
    public double zeroHzx;
    // таблицы отклонений датчиков
    // прямой проход
    public TableFunction tableAz = new TableFunction();
    public TableFunction tableAx = new TableFunction();
    public TableFunction tableAy = new TableFunction();
    public TableFunction tableHxy = new TableFunction();
    public TableFunction tableHyz = new TableFunction();
    public TableFunction tableHzx = new TableFunction();
    // обратный проход
    public TableFunction tableAzrev = new TableFunction();
    public TableFunction tableAxrev = new TableFunction();
    public TableFunction tableAyrev = new TableFunction();
    public TableFunction tableHxyrev = new TableFunction();
    public TableFunction tableHyzrev = new TableFunction();
    public TableFunction tableHzxrev = new TableFunction();
    private double offsetAx;
    private double offsetAy;
    private double offsetAz;
    private double offsetHxy;
    private double offsetHyz;
    private double offsetHzx;
    class TableInfo {
        TableFunction function;
        String name;
        // is table calculated after import data
        boolean isCalculated;
        TableInfo( TableFunction function, String name, boolean isCalculated) {
            this.function = function;
            this.name = name;
            this.isCalculated = isCalculated;
        }
    }
    TableInfo[] tableInfos = {
      new TableInfo( tableAx, "Ax", false),
      new TableInfo( tableAy, "Ay", false),
      new TableInfo( tableAz, "Az", false),
      new TableInfo( tableHxy, "Hxy", false),
      new TableInfo( tableHyz, "Hyz", false),
      new TableInfo( tableHzx, "Hzx", false),

      new TableInfo( tableAxrev, "Axrev", false),
      new TableInfo( tableAyrev, "Ayrev", false),
      new TableInfo( tableAzrev, "Azrev", false),
      new TableInfo( tableHxyrev, "Hxyrev", false),
      new TableInfo( tableHyzrev, "Hyzrev", false),
      new TableInfo( tableHzxrev, "Hzxrev", false),

    };


    public ION1GraduationData() {
    }

    public ION1CorrectionTable buildCorrectionTable() {
        ION1CorrectionTable table = new ION1CorrectionTable();
        buildCorrectionTable( table );
        return table;
    }
    public void buildCorrectionTable( ION1CorrectionTable table ) {
    }
    public void calc() {
    }
    /**
     * После импорта данные содержат показания эталонного датчика и измеренные
     * значика прибора. После вычислений даннве содержат отклонения от эталонного
     * датчика, причем при нуле отклонение равно нулю.
     */
    public void calcAfterImport() {
        // обработка таблицы Ax
        try {
            offsetAx = calcOffset( tableAx );
            offsetTable( tableAx, offsetAx );
            differenceTable( tableAx );
            offsetTable( tableAxrev, offsetAx );
            differenceTable( tableAxrev );
        } catch ( Exception ex ) {
        }
        // Ay
        offsetAy = calcOffset(tableAy);
        offsetTable( tableAy, offsetAy );
        differenceTable( tableAy );
        offsetTable( tableAyrev, offsetAy );
        differenceTable( tableAyrev );
        // Az
        offsetAz = calcOffset(tableAz);
        offsetTable( tableAz, offsetAz );
        differenceTable( tableAz );
        offsetTable( tableAzrev, offsetAz );
        differenceTable( tableAzrev );
        // Hxy
        offsetHxy = calcOffset(tableHxy);
        offsetTable( tableHxy, offsetHxy );
        differenceTable( tableHxy );
        offsetTable( tableHxyrev, offsetAz );
        differenceTable( tableHxyrev );
    }
    /**
     * Замена значений функции на разницу между 'x' и 'y'
     * @param table
     */
    private void differenceTable( TableFunction table ) {
        int size = table.size();
        for(int i = 0; i < size; i++) {
            table.get(i).y -= table.get(i).x;
        }

    }
    private void reductionTable( TableFunction table, double offset ) {
    }
    /**
     * Вычисление смещения значений относительно нуля
     * @param table
     * @return
     */
    private double calcOffset( TableFunction table ) {
        SplineInterpolation spline = new SplineInterpolation();
        spline.buildByTable( table );
        try {
            spline.preCalc();
        } catch ( Exception ex ) {
        }
        return spline.calc( 0.0 );
    }

    /**
     * Смещение показаний 'y' на число offset
     * @param table
     * @param offset
     */
    private void offsetTable( TableFunction table, double offset ) {
        int size = table.size();
        for(int i = 0; i < size; i++) {
            double y = table.get(i).y;
            double x = table.get(i).x;
            y = norm( y - offset);
            if( x < 180.0 && y > 200.0) {
                y -= 360.0;
            }
            if( x > 180.0 && y < 100.0) {
                y+= 360.0;
            }
            table.get(i).y = y;
        }
    }
    private void addTable( MeasureDatas datas, String name ) {
        MeasureTable table;
        table = new MeasureTable(0, 9);
        table.setProperty("name", name);
        datas.addTable( table );
    }
    /**
     * export calibration data to measure data
     * @param datas measures to which export data
     */
    public void exportData( MeasureDatas datas ) {
        if( datas == null )
            return;
        datas.clear();
        addTable( datas, "Ax" );
        addTable( datas, "Ay" );
        addTable( datas, "Az" );
        addTable( datas, "Hxy" );
        addTable( datas, "Hyz" );
        addTable( datas, "Hzx" );
        addTable( datas, "Axrev" );
        addTable( datas, "Ayrev" );
        addTable( datas, "Azrev" );
        addTable( datas, "Hxyrev" );
        addTable( datas, "Hyzrev" );
        addTable( datas, "Hzxrev" );
        //datas.ensurePointsCount(9);
    }
    private void importTable( MeasureTable mtable, TableFunction func ) {
        int size = mtable.size();
        if( size < 1 )
            return;
        int filterSize = mtable.getChain(0).size();
        MedianFilter filter1 = new MedianFilter( filterSize );
        MedianFilter filter2 = new MedianFilter( filterSize );
        func.clear();
        for(int i = 0; i < size; i++) {
            MeasureChain chain = mtable.getChain(i);
            for(int j = 0; j < chain.size(); j++) {
                filter1.add(chain.getPoint(j).getToolValue());
                filter2.add(chain.getPoint(j).getAccurateValue());
            }
            func.add(filter2.cuttedAverage(), filter1.cuttedAverage());
        }
    }
    /**
     * Import calibration data from measure data
     * @param datas measures from which imported data
     */
    public void importData( MeasureDatas datas ) {
        importTable( datas, "Ax" );
        importTable( datas, "Ay" );
        importTable( datas, "Az" );
        importTable( datas, "Hxy" );
        importTable( datas, "Hyz" );
        importTable( datas, "Hzx" );
        importTable( datas, "Axrev" );
        importTable( datas, "Ayrev" );
        importTable( datas, "Azrev" );
        importTable( datas, "Hxyrev" );
        importTable( datas, "Hyzrev" );
        importTable( datas, "Hzxrev" );

    }

    public void importTable( MeasureDatas datas, String name ) {
        TableInfo info = findTableInfo(name);
        if(info == null)
            return;
        importTable( datas.selectTable("name", name), info.function );
        info.isCalculated = false;
    }
    public double norm( double value ) {
        while( value < 0 ) value += 360.0;
        while( value >= 360.0 ) value -= 360.0;
        return value;
    }

    public TableInfo findTableInfo(String name) {
        for(int i = 0; i < tableInfos.length; i++) {
            if( tableInfos[i].name.equals(name) )
                return tableInfos[i];
        }
        return null;
    }


}