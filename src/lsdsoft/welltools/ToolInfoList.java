package lsdsoft.welltools;

import lsdsoft.util.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class ToolInfoList implements XMLStorable {
  private static final String tagToolTypeIndex = "tooltypeindex";
  private static final String tagWellToolType = "tooltype";
  private static final String tagChannel = "channel";
  private static final String attrID = "id";
  private static final String attrName = "name";
  private static final String attrDir = "dir";
  private static final String attrSource = "datasource";


  private Vector data = new Vector(20, 20);
  public ToolInfoList() {
  }
  public void load(Node parentNode) {
    if(! (parentNode instanceof Element)) return;
    Element parentElem = (Element)parentNode;
    Element elem;
    NodeList list = parentElem.getElementsByTagName(tagToolTypeIndex);
    //if(list.getLength() < 1) return;
    //elem = (Element)list.item(0);
    //System.out.println(elem.getNodeName());
    list = parentElem.getElementsByTagName(tagWellToolType);
    //list = elem.getElementsByTagName(tagCoef);
    for(int i = 0; i < list.getLength(); i++) {
      Element cElem = (Element)list.item(i);
      ToolTypeInfo info = new ToolTypeInfo(cElem.getAttribute(attrID));
      info.setName(cElem.getAttribute(attrName));
      info.setDir(cElem.getAttribute(attrDir));
      info.setSourceName(cElem.getAttribute(attrSource));
      data.add(info);
      NodeList list2 = cElem.getElementsByTagName(tagChannel);
      for(int j = 0; j < list2.getLength(); j++) {
          Element dElem = ( Element )list2.item( j );
          info.getChannels().addChannel(dElem.getAttribute(attrID),
                                        dElem.getAttribute(attrName));
      }
    }

  }
  public void save(Node parentNode) {

  }
  public ToolTypeInfo[] getData() {
      int elementCount = data.size();
      ToolTypeInfo[] result = new ToolTypeInfo[elementCount];
      return (ToolTypeInfo[])data.toArray(result);
  }
  public Vector getVector() {
    return data;
  }

}
