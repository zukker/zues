package lsdsoft.util;

/**
 * <p>Title: polinomial implementation</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CMI Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */
import org.w3c.dom.*;
import java.text.*;

public class Polynomial implements XMLStorable{
  private static final String tagName = "polynom";
  private static final String attrDegree = "degree";
  private static final String tagCoef = "coef";
  private static final String attrValue = "value";
  private String argumentChar = "x";
  /** Степень полинома 
   */
  private int Degree = 0;
  /** Коэффициенты полинома
   */
  private double Coef[] = null;
  /** Создает полином первой степени
   */
  public Polynomial() {
    this(1);
  }
  /** Создает полином указанной степени
   * @param degree степень полинома
   */
  public Polynomial(int degree) {
    setDegree(degree);
  }
  /** Получить степент полинома
   * @return значение степени полинома
   */
  public int getDegree() {
    return Degree;
  }
  /** Создает полином и копирует коэффициенты
   * @param degree степень
   */
  public void setDegree(int degree) {
    double tmp[] = new double[degree + 1];
    if(Coef != null) {
      int copySize = (Degree > degree)?degree:Degree;
      for(int i = 0; i <= copySize; i++)
        tmp[i] = Coef[i];
    }
    Degree = degree;
    Coef = tmp;
  }
  /** Возвращает значение коэффициентов
   * @param Index значение степени, при которой нужно получить коэффициент
   * @return Значение коэффициента
   */
  public double getAt(int Index) {
    if(Index < 0 || Index >= Degree) return 0;
    return Coef[Index];
  }
  /** Получить все коэфиициенты
   * @return массив со значениями коэффициентов
   */
  public double[] getData() {
    return Coef;
  }
  /** Установить значение коэффициента при степени
   * Осуществляется проверка выхода за границы
   * Для более быстрого обращения исп getData
   * @param Index степень при которой нучно установить коэффициент
   * @param Value значение коэффициента
   */
  public void setAt(int Index, double Value) {
    if(Index < 0 || Index >= Degree) return;
    Coef[Index] = Value;
  }
  public String getArgumentChar() {
    return argumentChar;
  }
  public void setArgumentChar(String arg) {
    argumentChar = arg;
  }
  /** Вычисление значения полинома в заданной точке
   * @param Value значение точки, в которой нужновычислить полином
   * @return значение полинома
   */
  public double Calc(double Value) {
    double V = 0, x = 1;
    for(int i = 0; i <= Degree; i++) {
      V += Coef[i] * x;
      x *= Value;
    }
    return V;
  }
  /** Convert current polynomial state to html string
   * Main aim of it is using in swing
   * @return generated html string
   */
  public String toHTML() {
    StringBuffer str = new StringBuffer(30);
    str.append("<html>");
    DecimalFormat form = new DecimalFormat("#0.0##");
    for(int i = 0; i <= Degree; i++) {
      form.setMaximumFractionDigits(i * 2 + 2);
      if(i > 0 && Coef[i] >= 0) str.append('+');
      str.append(form.format(Coef[i]));
      if(i > 0) {
        str.append("&middot;");
        str.append(argumentChar);
      }
      if(i > 1) {
        str.append("<sup>");
        str.append(i);
        str.append("</sup>");
      }
    }
    str.append("</html>");
    return str.toString();
  }
  /** Make string presentation of polynomial
   * @return generated string
   */
  public String toString() {
    StringBuffer str = new StringBuffer(20);
    DecimalFormat form = new DecimalFormat("#0.0##");
    for(int i = 0; i <= Degree; i++) {
      form.setMaximumFractionDigits(i * 2 + 2);
      if(i > 0 && Coef[i] >= 0) str.append('+');
      str.append(form.format(Coef[i]));
      if(i > 0) {
        str.append('*');
        str.append(argumentChar);
      }
      if(i > 1) { str.append('^'); str.append(i);}
    }
    return str.toString();
  }
  /** load plynomial from xml dom node
   * Node must be an element, tag name must be 'polynom'
   * @param parentNode parent node from which take values
   */
  public void load(Node parentNode) {
    if(! (parentNode instanceof Element)) return;
    Element parentElem = (Element)parentNode;
    NodeList list = parentElem.getElementsByTagName(tagName);
    if(list.getLength() < 1) return;
    Element elem = (Element)list.item(0);
    System.out.println(elem.getNodeName());
    int deg = XMLUtil.getIntegerAttribute(elem, attrDegree);
    if(XMLUtil.getState() == XMLUtil.DONE) {
      setDegree(deg);
      list = elem.getElementsByTagName(tagCoef);
      for(int i = 0; i < list.getLength(); i++) {
        Element cElem = (Element)list.item(i);
        int d = XMLUtil.getIntegerAttribute(cElem, attrDegree);
        double c = XMLUtil.getDoubleAttribute(cElem, attrValue);
        if(d <= Degree && d >= 0) Coef[d] = c;
      }
    }
  }
  /** store polynomial in parent xml dom node
   * @param parentNode node to which store values
   */
  public void save(Node parentNode) {
    Document doc = parentNode.getOwnerDocument();
    Element elem = doc.createElement(tagName);
    XMLUtil.setIntegerAttribute(elem, attrDegree, Degree);
    for(int i = 0; i <= Degree; i++) {
      if(Coef[i] != 0.0) {
        Element cElem = doc.createElement(tagCoef);
        XMLUtil.setIntegerAttribute(cElem, attrDegree, i);
        XMLUtil.setDoubleAttribute(cElem, attrValue, Coef[i]);
        elem.appendChild(cElem);
      }
    }
    parentNode.appendChild(elem);
  }


}