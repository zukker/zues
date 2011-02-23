package lsdsoft.zeus;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class WorkMode implements Cloneable {
    public static final int MODE_CALIB = 0;
    public static final int MODE_GRAD = 1;
    public static final int MODE_TUNE = 2;
    public static final int MODE_VIEW = 3;
    public static final int MODE_SETUP = 4;
    /**
     * ��������� �������� ��������� ������� ������.
     * "calib" - ����� ���������� ��. <br>
     * "grad" - ����� ����������� ��.
     * "tune" - ����� ��������� ��.
     * "view" - ����� ��������� ������
     * "setup" - ��������� �������.
     */

    public static final String[] MODE_NAMES = {
        "calib", "grad", "tune", "view", "setup"
    };
    /** @todo internationalize this strings */
    public static final String[] MODE_SYNONYMS = {
        "���������� ����������",
        "����������� ����������",
        "��������� ����������",
        "�������� ������",
        "��������� ���������"
    };

    public static final String[] MODE_SHORT_SYNONYMS = {
        "����������",
        "�����������",
        "���������",
        "��������",
        "���������"
    };

    private int workMode = MODE_CALIB;

    public WorkMode( int workMode ) {
        if(isValidIndex(workMode)) {
           this.workMode = workMode;
        }
    }

    public WorkMode( String modeName ) {
        if(modeName == null)
            return;
        for ( int i = 0; i < MODE_NAMES.length; i++ ) {
            if ( modeName.equals( MODE_NAMES[i] ) ) {
                workMode = i;
                break;
            }
        }
    }
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    protected boolean isValidIndex(int index) {
        return index >= 0 && index < MODE_NAMES.length;
    }

    public int getWorkMode() {
        return workMode;
    }

    public String getName() {
        return MODE_NAMES[workMode];
    }

    public String getDescription() {
        return MODE_SYNONYMS[workMode];
    }

    public String getShortDescription() {
        return MODE_SHORT_SYNONYMS[workMode];
    }

    public static int getWorkModesCount() {
        return MODE_NAMES.length;
    }
    public static String[] getWorkModeDescriptions() {
        return MODE_SYNONYMS;
    }
    public static String[] getWorkModeNames() {
        return MODE_NAMES;
    }

}