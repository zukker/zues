package com.amper.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.awt.List;

/**
 * <p>Title: ��������� �����.</p>
 * <p>Description: ����� AStringLoader ��� ����������� ��������� ���� � ������ �����.
 * ��������� ��� �� �����������.</p>
 * <p>Copyright: Copyright (c) Amper 2009</p>
 * @author de-nos
 * @version 1.0
 */
public class AStringLoader {
    /**
     * �������� ����� � ������ ����� � ������������ �������� ������ �����.
     * @param filename ��� ������������ �����
     * @param empty_line_skip_enable true - ������ ������ ����� ������������
     *                               false - ����� ����������� ��� ������ �����
     * @return ������ ����� �� �����
     */
    public static String[] loadFile(String filename, boolean empty_line_skip_enable) {
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(filename));
            List array = new List();
            String s;
            while ((s = reader.readLine()) != null) {
                if (empty_line_skip_enable) {
                    if (s.trim().length() > 0) {
                        array.add(s);
                    }
                } else {
                    array.add(s);
                }
            }
            reader.close();
            //String[] ss = new String[array.size()];
            return array.getItems();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return new String[]{""};
    }

    /**
     * �������� ����� � ������ �����.
     * @param filename ��� ������������ �����
     * @return ������ ����� �� �����
     */
    public static String[] loadFile(String filename) {
        return loadFile(filename, false);
    }

    /**
     * �������� ����� � ������ ����� � ��������� ������ �����.
     * @param filename ��� ������������ �����
     * @return ������ ����� �� �����
     */
    public static String[] loadFileWithoutEmptyLine(String filename) {
        return loadFile(filename, true);
    }
}
