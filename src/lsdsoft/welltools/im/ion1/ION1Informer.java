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
     * Значения пределов погрешностей: азимут, зенит, поворот
     */
    private static double[] errors = { 1.5, 0.25, 3 };
    public ION1Informer() {
    }
    static public double[] getErrorLimits(Channel channel) {
        ChannelValue value = channel.getValue(1);
        double zenith = value.getAsDouble();
        // при зените меньшем 7 град предел для азимута 3 град
        errors[0] = (zenith < 7.5)?3:1.5;
        return errors;

    }
    /**
     * Заполнение норм погрешностей для углов прибора
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
