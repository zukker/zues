package lsdsoft.metrolog;

/**
 * <p>Title: источник данных скважинных приборов</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */

public class WellToolDataSource extends ChannelDataSource {
  public static final int MODE_CALIB = 0;
  public static final int MODE_GRAD = 1;
  public static final int MODE_TUNE = 2;

  protected ToolItemInfo tool;
  protected int mode;

  public WellToolDataSource() {
    UID = "wtds"; // default well tool data source
  }

  public void setWellTool(ToolItemInfo tool) {
    this.tool = tool;
    setWellTool();
  }

  public void setMode(int mode) {
    this.mode = mode;
    setMode();
  }

  public void setMode() {
  }
  public void setWellTool() {
  }

  public void selectChannel(String channel) {
  }
};
