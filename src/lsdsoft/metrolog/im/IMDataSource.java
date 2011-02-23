package lsdsoft.metrolog.im;

import lsdsoft.units.*;
import lsdsoft.metrolog.*;
import java.util.*;

public class IMDataSource extends WellToolDataSource {
  private final static String SOURCE_ID = "ds.wt.imempty";
  protected ArrayList dataEventListeners;
  protected InclinometerAngles angles;
  public IMDataSource() {
    tool = null;
    UID = SOURCE_ID;
  }
  //public String getUID() {
  //  return id;
 // }
  void addDataEventListener(IMDataEventListener evListener) {
    if(!dataEventListeners.contains(evListener))
        dataEventListeners.add(evListener);
  }
  void removeDataEventListener(IMDataEventListener evListener) {
    dataEventListeners.remove(evListener);
  }
  void getLastAngles(InclinometerAngles ang) {
    ang = angles;
  }

  public void getAngles(InclinometerAngles ang) {
    ang = angles;
  }
};

