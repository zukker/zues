package lsdsoft.util;

/**
 * <p>Title: ����������� ����������</p>
 * <p>Description: ��������� � ������������</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ��� "����-���"</p>
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

  /** ������� ������������
   */
  public String Surname = null;
  /** ��� ������������
   */
  public String Name = null;
  /** �������� ������������
   */
  public String Patron = null;
  /** ���������� ���������
   */
  public String Post = null;
  /** �������� �������� �� ���������: surname, name, patron, post
   */
  public Calibrator() {
    Surname = "Surname";
    Name = "Name";
    Patron = "Patron";
    Post = "Post";
  }
  /** ����������� � ������ �������������� ������
   */
  public Calibrator(String surname, String name, String patron, String post) {
    Surname = surname;
    Name = name;
    Patron = patron;
    Post = post;
    Format();
  }
  /** ����������� ������ ���� ������� �.�.
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
  /** ����������� ��� ������ � ���: ������ ������ ���������, ��������� ��������
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