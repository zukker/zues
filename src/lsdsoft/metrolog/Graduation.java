package lsdsoft.metrolog;

/**
 * <p>�������������� �������������� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public interface Graduation {
    /**
     * ������ �������� �������������� ��������������
     * @param param ��� ���������
     * @param value �������� ���������
     */
    public void setParameter(String param, String value);
    /**
     * �������� �������� ���������
     * @param param ��� ���������
     * @return
     */
    public String getParameter(String param);
    /**
     * ������ �������� �������������� ��������������
     * @return
     */
    public double calc();

}