package lsdsoft.metrolog;

/**
 * <p>Title: Собитие при вводе данных</p>
 * <p>Description: основное собития для источников данных</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */

public class DataInputEvent {
  protected double value;
  protected int channel;
  public DataInputEvent(double v, int c) {
    value = v;
    channel = c;
  }

  public double getValue() {
    return value;
  }
  public double getChannel() {
    return channel;
  }
}