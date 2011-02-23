package lsdsoft.units;

public class InclinometerAngles {
  /** Zenith angle
  */
  public Angle zenith = new Angle();
  public Angle azimuth = new Angle();
  /* Location of tool ralative pole of tool
     space rotate location
  */
  public Angle rotate = new Angle();
  public Angle vizir = rotate;
};
