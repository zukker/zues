package lsdsoft.welltools.im.ion1;

/**
 * ��������� ������ *.tprv ������������ ��� "���������" �������� ���-1 � ��������� 
 * ION_tarirovka.exe. 
 */

import java.io.*;

import lsdsoft.util.LittleEndianDataInputStream;

public class TprvFile {
    public class Axis {
        public double[] A;
        public double[] H;
    };
    
    public String number = null;
    public String metrolog = null;
    public Axis axHyz = new Axis();
    public Axis ayHzx = new Axis();
    public Axis azHxy = new Axis();
    public double zeroAz;
    public double[] other = new double[24];
    public static final int AZ_ZERO = 8;
    /* �������������� ������:
     * [0] - Ax ����
     * [1] - Ax ��������������� ����
     * [4] - Ay ����
     * [5] - Ay ��������������� ����
     * [8] - Az ����
     * [9] - Az ��������������� ����
     * [10] - Az ��������� ����������� 0-180
     * [11] - Az ��������� ����������� 90-270
     * [12] - Hxy ����
     * [13] - Hxy ��� ����
     * [16] - Hyz ����
     * [17] - Hyz ��� ����
     * [20] - Hzx ����
     * [21] - Hzx ��� ����
     * 
     */
    
    public void load(String fileName){
        try {
            //FileInputStream fis = new FileInputStream(fileName);
            LittleEndianDataInputStream dis = new LittleEndianDataInputStream(fileName);
            int size = dis.available();
            byte[] buffer = new byte[size];
            //fis.read(buffer);
            dis.skip(4); // skip header
            int len = dis.readInt();
            number = readString(dis, len);
            len = dis.readInt();
            metrolog = readString(dis, len);
            readCurve(dis, axHyz);
            readCurve(dis, ayHzx);
            readCurve(dis, azHxy);
            System.out.println("### Other  ");
            readOthers(dis);
            dis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     
    }
    
    private String readString(DataInput dis, int len) throws IOException {
        String ret = "";
        for(int i = 0; i < len; i++) {
            ret += (char)dis.readByte();
        }
        return ret;
    }
    private void readCurve(DataInput dis, Axis axis) throws IOException {
        int points = dis.readInt();
        System.out.println("points: " + points);
        if ((points > 0) && (points < 300)) {

            axis.A = new double[points];
            axis.H = new double[points];
            for (int i = 0; i < points; i++) {
                axis.A[i] = dis.readDouble();
                axis.H[i] = dis.readDouble();
                System.out.println(axis.A[i] + "; " + axis.H[i]);
            }
        }
    }

    private void readOthers(DataInput dis) throws IOException {
        for (int i = 0; i < other.length; ) {
            byte flag = dis.readByte();
            other[i] = dis.readDouble();
            System.out.println("other[" + i + "] = " + other[i]);
            i++;
            other[i] = dis.readDouble();
            System.out.println("other[" + i + "] = " + other[i]);
            i++;
        }
        
    }


}
