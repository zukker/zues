package lsdsoft.test;

import lsdsoft.welltools.im.ins60.*;
import java.io.*;
import lsdsoft.units.InclinometerAngles;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class TestINS60 {
    private INS60CorrectionTable table;
    private INS60Computer comp;
    private String name;

    public TestINS60( String tableFileName ) {
        name = tableFileName;
    }
    public void run() {
        try {
            table = new INS60CorrectionTable();
            table.setNumber("303");
            String filename = table.buildFileName();
            System.out.println("Table name: " + filename);
            table.load( new FileInputStream( name ) );
            comp = new INS60Computer(table);
            InclinometerAngles ang;
            //  ��� ������� ���60 � 303
            //  ���� ��������� �� ������� ������ ��� ���������� ��������
            // Ax=45968 Ay=32400 Az=33970 Fx=20388 Fy=28193 Fz=30511 T=21
            //  �����: 91.2
            //  ������: 306.05
            //  �������: 183.92
            ang = comp.CalcAngles( 45870, 32231, 34640, 20134, 28313, 31549, 18);
            System.out.println("Zenith: " + ang.zenith.toFloatString());   // 96.721
            System.out.println("Azimuth: " + ang.azimuth.toFloatString()); // 309.964
            System.out.println("Vizir: " + ang.rotate.toFloatString());    // 179.592
        } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());
        }

    }

}
