package lsdsoft.metrolog;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public interface ChannelDataSourceFilter {
  public ChannelDataSource getOutputSource();
  public void process();
}