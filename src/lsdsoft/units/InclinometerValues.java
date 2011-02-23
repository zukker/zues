package lsdsoft.units;

import lsdsoft.metrolog.*;

public class InclinometerValues {
  /** Zenith angle
  */
  public Value zenith = new Value();
  public Value azimuth = new Value();
  /* Location of tool ralative pole of tool
     space rotate location
  */
  public Value rotate = new Value();
  public Value vizir = rotate;
};
