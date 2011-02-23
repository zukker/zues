package lsdsoft.metrolog.im;

import lsdsoft.units.*;

public class IMDataEvent {
  private InclinometerAngles angles;

  public IMDataEvent(InclinometerAngles angles) {
    this.angles = angles;
  }
  public Angle getRotate() {
    return angles.rotate;
  }
  public Angle getZenit() {
    return angles.zenith;
  }
  public Angle getAzimut() {
    return angles.azimuth;
  }
};

