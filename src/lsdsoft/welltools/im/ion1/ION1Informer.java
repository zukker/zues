package lsdsoft.welltools.im.ion1;

import lsdsoft.metrolog.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class ION1Informer {
    /**
     * �������� �������� ������������: ������, �����, �������
     */
    private static double[] errors = { 1.5, 0.25, 3 };
    public ION1Informer() {
    }
    static public double[] getErrorLimits(Channel channel) {
        ChannelValue value = channel.getValue(1);
        double zenith = value.getAsDouble();
        // ��� ������ ������� 7 ���� ������ ��� ������� 3 ����
        errors[0] = (zenith < 7.5)?3:1.5;
        return errors;

    }
    /**
     * ���������� ���� ������������ ��� ����� �������
     * @param azimuth Value
     * @param zenith Value
     * @param rotate Value
     */
    static public void fillDelta(Value azimuth, Value zenith, Value rotate) {
        zenith.delta = errors[1];
        rotate.delta = errors[2];
        azimuth.delta = (zenith.value < 7.5)?3:1.5;
    }
}
