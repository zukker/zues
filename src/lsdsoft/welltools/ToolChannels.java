package lsdsoft.welltools;

import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class ToolChannels extends ArrayList {
  public ToolChannels() {
      super(2);
  }
  public ToolChannel addChannel(String id) {
      ToolChannel ch;
      if(hasChannel(id)) {
          ch = getChannel(id);
      } else {
          ch = new ToolChannel( id );
          add(ch);
      }
      return ch;
  }

  public ToolChannel addChannel(String id, String name) {
      ToolChannel ch = addChannel(id);
      ch.setName(name);
      return ch;
  }
  public boolean hasChannel(String id) {
      boolean ret = false;
      for(int i = 0; i < this.size(); i++) {
          ToolChannel ch = (ToolChannel)get(i);
          if(ch.id.equals(id)) {
              ret = true;
              break;
          }
      }
      return ret;
  }
  public ToolChannel getChannel(String id) {
      ToolChannel ch = null;
      for(int i = 0; i < this.size(); i++) {
          ToolChannel ch2 = (ToolChannel)get(i);
          if(ch2.id.equals(id)) {
              ch = ch2;
              break;
          }
      }
      return ch;
  }
}