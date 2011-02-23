package lsdsoft.test;

import lsdsoft.welltools.im.immn73.*;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class TestImmn73 {
    private IMMN73CorrectionTable table;
    private IMMN73Computer comp;
    private String name;
    private int[] sensors = new int[16];
    private double[] angles = new double[3];

    public TestImmn73( String tableFileName ) {
        name = tableFileName;
    }
    public void run() {
        double out[] = new double[2];
        try {
            table = new IMMN73CorrectionTable();
            table.load( new FileInputStream( name ) );
            comp = new IMMN73Computer( table );
            sensors[10] = 3751; //Tx
            sensors[8] = 1953; //Ty
            comp.compute(sensors, angles );
            System.out.println(angles[1]);
            comp.correct(angles, out);
        } catch ( Exception ex ) {
        }

    }

}