package lsdsoft.metrolog;

import java.util.*;

/**
 * <p>����� ��������� ��� ����������.</p>
 * <p>�������� �������� ������� � �����������, � ����� ����������� ������
 * ���������� �����������</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class MeasurePoint {
    /**
     * �������� �� ���������� ������� ��� ������ (���������) ��������.
     * @todo: ���������� �� accurateValue & accurateDelta
     */
    public Value accurate = new Value();
    public Date time = null;
    //protected double accurateValue = 0;
    //protected double accurateDelta = 0;
    /**
     * �������� �������� �� �������.
     */
    public Value tool = new Value();
    //protected double toolValue = 0;
    //protected double toolDelta = 0;
    /**
     * �������� �������� �� ������ �������.
     * @todo: ������ ������ ��������� ������� ������� ��� ���������, �� �������
     * ������� �������� ���������� ��������.
     */
    public int toolCode = 0;
    /**
     * ������ ���������� �����������.
     */
    protected double delta = 0;
    /**
     * �������� ����������
     */
    protected double quality = 0;

    public MeasurePoint() {
        time = new Date();
    }
    public boolean isEmpty() {
        return accurate.value == 0 && tool.value == 0 && toolCode == 0;
    }
    public void setAccurateValue( double value ) {
        //accurateValue = value;
        accurate.value = value;
    }

    public void setAccurateValue( Value value ) {
        accurate.value = value.value;
        accurate.delta = value.delta;
    }

    public double getAccurateValue() {
        return accurate.value;
    }

    public void setToolValue( double value ) {
        //toolValue = value;
        tool.value = value;
    }

    public void setToolValue( Value value ) {
        tool = value;
        //toolValue = value.value;
    }

    public double getToolValue() {
        return tool.value;
    }

    public double getDelta() {
        return delta;
    }

    public long getTime() {
        return time.getTime();
    }

    public Date getDate() {
        return time;
    }

    public void calc() {
        //delta = toolValue - accurateValue;
        delta = tool.value - accurate.value;
    }

}
