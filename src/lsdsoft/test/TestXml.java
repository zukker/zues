package lsdsoft.test;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import lsdsoft.util.Calibrator;
import lsdsoft.util.Polynomial;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.io.*;
import lsdsoft.metrolog.SpendMeterData;
import java.util.Locale;

public class TestXml {

  public TestXml() {
  }
  public void run() {
    Calibrator calib = new Calibrator();
    Polynomial pol = new Polynomial();
    SpendMeterData smdata = new SpendMeterData();
    DOMParser parser= new DOMParser();
    FileOutputStream os1 = null;
    Document doc = null;
    try {
      os1 = new FileOutputStream("IP_192.168.10.1");
      //doc.setEncoding("Windows-1251");
      //Locale loc = new Locale("en", "EN");
      //parser.setLocale(loc);
      parser.parse(new InputSource(new FileInputStream("d:/projects/java/data3.xdt")));
      doc = parser.getDocument();
    }
    catch(Exception e) { throw new RuntimeException(e.getMessage()+": error reading doc");}
    byte[] mes;
    try {
      smdata.load(doc.getDocumentElement());
      calib.load(doc.getDocumentElement());
      pol.load(doc.getDocumentElement());
      mes = calib.ShortString().getBytes("Cp866");
      System.out.println(new String(mes));
      mes = pol.toHTML().getBytes("Cp866");
      System.out.println(new String(mes));
      calib.save(doc.getDocumentElement());
      smdata.save(doc.getDocumentElement());
      pol.save(doc.getDocumentElement());

    } catch(Exception e) {e.printStackTrace();}
    OutputFormat of = new OutputFormat(doc);
    of.setIndenting(true);
    of.setIndent(2);
    of.setEncoding("KOI8-R");
    of.setLineWidth(78);
    of.setVersion("1.0");
    XMLSerializer ser = new XMLSerializer(os1, of);
    try {
      ser.serialize(doc);
    }catch(Exception e) {e.printStackTrace();}
  }
}