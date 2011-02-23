package lsdsoft.metrolog;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class Channel {
    private String name;
    private int id;
    private int subCount;
    private ArrayList values;

  public Channel(String name, int id, int subCount) {
    this.name = name;
    this.id = id;
    this.subCount = subCount;
    values = new ArrayList(subCount);
    for(int i = 0; i < subCount; i++)
      values.add(i, new ChannelValue());
  }
  public String getName() {
    return name;
  }
  public int getID() {
    return id;
  }
  public int getSubCount() {
    return subCount;
  }
  public ChannelValue getValue(int index) throws IndexOutOfBoundsException {
    return (ChannelValue)values.get(index);
  }
  public void setValue(int index, ChannelValue value) throws IndexOutOfBoundsException{
    values.set(index, value);
  }
  // set value as int value
  public void setValue(int index, int code) throws IndexOutOfBoundsException{
      ChannelValue val = getValue( index );
      val.setAsInteger(code);
      //val.type = ChannelValue.CV_CODE;
      //val.code = code;
  }

  public void setValue(int index, double dvalue) throws IndexOutOfBoundsException{
      ChannelValue val = getValue( index );
      val.setAsDouble( dvalue );
  }


}