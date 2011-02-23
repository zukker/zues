package lsdsoft.util;

/**
 * <p>Title: Калибровщик аппаратуры</p>
 * <p>Description: инфрмация о калибровщике</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft (lsdsoft@mail.ru)
 * @version 1.0
 */

import org.w3c.dom.*;

public class Calibrator implements XMLStorable {
  private static final String tagName = "calibrator";
  private static final String attrName = "name";
  private static final String attrSurname = "surname";
  private static final String attrPatron = "patron";
  private static final String attrTitle = "title";

  /** Фамилия калибровщика
   */
  public String Surname = null;
  /** Имя калибровщика
   */
  public String Name = null;
  /** Отчество калибровщика
   */
  public String Patron = null;
  /** Занимаемая должность
   */
  public String Post = null;
  /** Задаются значения по умолчанию: surname, name, patron, post
   */
  public Calibrator() {
    Surname = "Surname";
    Name = "Name";
    Patron = "Patron";
    Post = "Post";
  }
  /** Конструктор с полной инициализацией данных
   */
  public Calibrator(String surname, String name, String patron, String post) {
    Surname = surname;
    Name = name;
    Patron = patron;
    Post = post;
    Format();
  }
  /** Возвращаетс строку вида Фамилия И.О.
   */
  public String ShortString() {
    String s = Surname;
    s += ' ';
    if(Name.length() > 0) {
      s += Name.charAt(0); s += '.';
    }
    if(Patron.length() > 0) {
      s += Patron.charAt(0); s += '.';
    }
    return s;
  }
  /** Преобразует все строки в вид: первый символ заглавный, остальные строчные
   */
  public void Format() {
    lsdsoft.util.TextUtil.toCapitalize(Surname);
    lsdsoft.util.TextUtil.toCapitalize(Name);
    lsdsoft.util.TextUtil.toCapitalize(Patron);
  }
  public void load(Node parentNode) {
    if(! (parentNode instanceof Element)) return;
    Element parentElem = (Element)parentNode;
    NodeList list = parentElem.getElementsByTagName(tagName);
    if(list.getLength() < 1) return;
    // get "calibrator" tag element
    Element elem = (Element)list.item(0);
    Name = elem.getAttribute(attrName);
    Surname = elem.getAttribute(attrSurname);
    Patron = elem.getAttribute(attrPatron);
    Post = elem.getAttribute(attrTitle);
  }
  public void save(Node parentNode) {
    Document doc = parentNode.getOwnerDocument();
    Element elem = doc.createElement(tagName);
    elem.setAttribute(attrName, Name);
    elem.setAttribute(attrSurname, Surname);
    elem.setAttribute(attrPatron, Patron);
    elem.setAttribute(attrTitle, Post);
    parentNode.appendChild(elem);

  }
}