package lsdsoft.metrolog;

/**
 * <p>Title: список воспроизводимых точек</p>
 * <p>Description: воспроизводимые точки используются для задания на поверочной
 * установке занчения физической величины для эталонного прибора. Точки
 * выбираются в соответствии с методикой калибровки. Это значение не обязательно
 * должно совпадать с показаниями эталонного прибора.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */
import lsdsoft.util.*;
import org.w3c.dom.*;

public class ReproductionPoints implements XMLStorable {
  private static final String tagName = "reppoints";
  private static final String tagPoint = "point";
  private static final String attrValue = "value";
  /** значения воспроизводимых точек
   */
  private double[] data = { 0 };
  /** Создание из готовых значений
   */
  public ReproductionPoints(double[] fromCopy) {
    data = fromCopy;
  }
  public double[] getData() {
    return data;
  }
  public void setSize(int newSize) {
    if(newSize == data.length) return;
    double[] newData = new double[newSize];
    int size = newSize;
    if(data.length < newSize) size = data.length;
    System.arraycopy(data,0, newData, 0, size);
    data = newData;
  }
  /** load reproduction values from xml dom node
   * Node must be an element, tag name must be 'polynom'
   * @param parentNode parent node from which take values
   */
  public void load(Node parentNode) {
    if(! (parentNode instanceof Element)) return;
    Element parentElem = (Element)parentNode;
    NodeList list = parentElem.getElementsByTagName(tagName);
    if(list.getLength() < 1) return;
    Element elem = (Element)list.item(0);
    // get list of points
    list = elem.getElementsByTagName(tagPoint);
    int size = list.getLength();
    // do something if not empty
    if( size > 0) {
      data = new double[list.getLength()];
      for( int i = 0; i < size; i++) {
        Element cElem = (Element)list.item(i);
        data[i] = XMLUtil.getDoubleAttribute(cElem, attrValue);
      }
    }
  }
  /** store reproduction values in parent xml dom node
   * @param parentNode node to which store values
   */
  public void save(Node parentNode) {
    Document doc = parentNode.getOwnerDocument();
    Element elem = doc.createElement(tagName);
    for(int i = 0; i < data.length; i++) {
        Element cElem = doc.createElement(tagPoint);
        XMLUtil.setDoubleAttribute(cElem, attrValue, data[i]);
        elem.appendChild(cElem);
      }
    parentNode.appendChild(elem);
  }
}