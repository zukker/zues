package lsdsoft.welltools;

import java.util.*;

/**
 * <p>Сводная информация о типе скважинного прибора</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class ToolTypeInfo {
  private String id;
  private String name;
  private String dir;
  private String sourceName;
  private ToolChannels toolChannels = new ToolChannels();
  public ToolTypeInfo(String id) {
    this.id = id;
    setName(id);
  }
  public ToolTypeInfo(String id, String name) {
    this.id = id;
    this.name = name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getID() {
    return id;
  }
  public String getName() {
    return name;
  }
  public void setDir(String dir) {
    this.dir = dir;
  }
  public String getDir() {
    return dir;
  }
  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }
  public String getSourceName() {
    return sourceName;
  }
  public String toString() {
    return name;
  }
  public ToolChannels getChannels() {
      return toolChannels;
  }
}
