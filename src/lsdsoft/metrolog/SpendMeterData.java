package lsdsoft.metrolog;

//import java.awt.*;
//import javax.swing.*;
//import javax.swing.table.TableColumn;
//import java.net.URL;
//import javax.swing.border.*;
//import java.awt.event.*;
//import javax.swing.table.AbstractTableModel;

/**
 * <p>Title: данные калибровки расходомера в одной точке</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ÷ћ» "”рал-√ео"</p>
 * @author lsdsoft
 * @version 1.0
 */
import java.lang.IllegalArgumentException;
import lsdsoft.util.*;
import org.w3c.dom.*;


public class SpendMeterData implements XMLStorable {
  public class SpendMeterMeasure {
    public double deviceValue;
    public double calcValue;
    public double standartValue;
  }
  private static final String tagName = "smdata";
  private static final String tagTable = "table";
  private static final String tagMeasure = "measure";
  private static final String attrPoints = "points";
  private static final String attrMeasures = "measures";
  private static final String attrDevice = "device";
  private static final String attrValue = "value";
  private static final String attrOrder = "order";
  private static final String attrStd = "etalon";

  /** количество измерений в точке
   */
  private int NumMeasures = 6;
  /** ћаксимальное число измерений в одной точке
   */
  private static final int MaxMeasures = 32;
  /**  оличество воспроизводимых точек
   */
  private int repPoints = 10;
  //private int NumData = 3;
  private static int MAX_REPPOINTS = 32;
  /** точки воспроизведени€ расхода по умолчанию
   */
  private static double defRepValues[] = {0.5, 10, 20, 30, 40, 50, 60, 70, 80, 90 };
  private ReproductionPoints repValues = new ReproductionPoints(defRepValues);
  /** ƒанные показаний со скважинного прибора и с эталонного
   */
  private SpendMeterMeasure Data[][];
  private double Qavearge;
  /** —кважинный расходомер
   */
  public SpendMeterDevice Device = null;
  /** Ёталонный расходомер
   */
  public SpendMeterStandartDevice SDevice = null;
  public SpendMeterData() {
    alloc(10, 6);
  }
  public SpendMeterData(int repNum, int numM) {
    alloc(repNum, numM);
  }

  /** –азмещение массива под данные измерений
   * @param repNum число воспроизводимых точек
   * @param numM число измерений в точке
   */
  public void alloc(int repNum, int numM) {
    if(numM < 1) throw new IllegalArgumentException("numM<1");
    if(numM > MaxMeasures) numM = MaxMeasures;
    if(repNum < 1) throw new IllegalArgumentException("repNum<1");
    if(repNum > MAX_REPPOINTS) repNum = MAX_REPPOINTS;
    Data = new SpendMeterMeasure[repNum][numM];
    for(int i =0; i< repNum; i++) {
      for(int j = 0; j < numM; j++)
        Data[i][j] = new SpendMeterMeasure();
    }
    NumMeasures = numM;
  }
  /** ѕолучение числа измерений в точке
   * @return количество измерений в точке
   */
  public int getMeasures() { return NumMeasures; }
  public SpendMeterMeasure[][] getData() { return Data; }

  /** ”становка скважинного расходомера
   * @param dev скважинный прибор
  */
  public void setDevice(SpendMeterDevice dev) {
    Device = dev;
  }
  /** –асчет одной строки таблицы. ƒо расчета нужно установить скважинный прибор
   * @param row номер строки в таблице. ≈сли он выходит за границы, то расчета не будет
   */
  public void calcRow(int row) {
    if(row < 0 || row >= NumMeasures) return;
   // Data[row][1] = Device.Grad.Calc(Data[row][0]);
  }
  /** load spend meter data from xml dom node
 * Node must be an element, tag name must be 'polynom'
 * @param parentNode parent node from which take values
 */
public void load(Node parentNode) {
  if(! (parentNode instanceof Element)) return;
  Element parentElem = (Element)parentNode;
  NodeList list = parentElem.getElementsByTagName(tagName);
  if(list.getLength() < 1) return;
  // get first <smdata> tag
  Element elem = (Element)list.item(0);
  // get reproduction points
  repValues.load(elem);
  // get num of reproduction points
  int points = XMLUtil.getIntegerAttribute(elem, attrPoints);
  if(points < 1) points = 1;
  // normally it does not happen (for equal num of repPoints and repValues)
  repValues.setSize(points);
  // get num of measures in point
  int measures = XMLUtil.getIntegerAttribute(elem, attrMeasures);
  if(measures < 1) measures = 1;
  // allocation data
  alloc(points, measures);
  if(XMLUtil.getState() == XMLUtil.DONE) {
    // select <table ..> tags
    list = elem.getElementsByTagName(tagTable);
    for(int i = 0; i < list.getLength(); i++) {
      // i-th element <table>
      Element cElem = (Element)list.item(i);
      int order = XMLUtil.getIntegerAttribute(elem, attrOrder);
      NodeList meas = cElem.getElementsByTagName(tagMeasure);
      for(int mn = 0; mn < NumMeasures && mn < meas.getLength(); mn++) {
     //   System.out.println("mn="+mn);
        if(Data[i][mn] == null) System.out.println("data is null");
        Data[i][mn].deviceValue = XMLUtil.getDoubleAttribute((Element)meas.item(mn), attrDevice);
        Data[i][mn].standartValue = XMLUtil.getDoubleAttribute((Element)meas.item(mn), attrStd);
      }
    }
  }
}
/** store in parent xml dom node
 * @param parentNode node to which store values
 */
public void save(Node parentNode) {
  Document doc = parentNode.getOwnerDocument();
  Element elem = doc.createElement(tagName);
  parentNode.appendChild(elem);
  XMLUtil.setIntegerAttribute(elem, attrPoints, repPoints);
  XMLUtil.setIntegerAttribute(elem, attrMeasures, NumMeasures);
  repValues.save(elem);
  for(int i = 0; i < repPoints; i++) {
    Element tElem = doc.createElement(tagTable);
    tElem.setAttribute(attrOrder, String.valueOf(i));
    for(int j = 0; j < NumMeasures; j++) {
      Element mElem = doc.createElement(tagMeasure);
      XMLUtil.setDoubleAttribute(mElem, attrDevice, Data[i][j].deviceValue);
      XMLUtil.setDoubleAttribute(mElem, attrStd, Data[i][j].standartValue);
      tElem.appendChild(mElem);
    }
    elem.appendChild(tElem);
  }

}

}