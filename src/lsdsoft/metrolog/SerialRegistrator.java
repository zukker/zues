package lsdsoft.metrolog;

/**
 * <p>Title: регистратор подключаемы по последовательному порту</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */
import javax.comm.*;
import java.io.*;
//import lsdsoft.metrolog.*;
//import lsdsoft.metrolog.Registrator;


public class SerialRegistrator implements Registrator{
  protected SerialPort port = null;
  protected CommPortIdentifier portId;
  protected String portName = "COM1";
  protected boolean opened = false;
  protected OutputStream os;
  protected InputStream is;

  public SerialRegistrator() {
     System.out.println("serial cntr");
    init();
  }
  public SerialRegistrator(String pName) {
    System.out.println("serial cntr");
    setPortName(pName);
    init();
  }
  public void init() {
    try {
      portId = CommPortIdentifier.getPortIdentifier(portName);
      port = (SerialPort)portId.open("", 10000);
    os = port.getOutputStream();
    is = port.getInputStream();
    opened = true;
    System.out.println("port opened");
    }
    catch (Exception e) {
       System.out.println("error");
      System.out.println(e.getMessage());

    }
//    catch (NoSuchPortException e) {
//      throw new Exception(e.getMessage());
//    }
//    catch (PortInUseException e) {
      //throw new SerialConnectionException(e.getMessage());
//    }



  }
  public void setPortName(String pName) {
    portName = pName;
  }
  public void selectChannel(int channel) {

  }
  public byte getByte() throws IOException {
    byte val = 0;
    if(!opened)
      System.out.println("port not opened");
    if(opened)
      val = (byte)is.read();
    return val;
  }
  public int getData() throws IOException {
    return (int)getByte();
  }

  public void sendByte(final byte val) throws IOException {
    if(opened)
     os.write(val);
  }

  public String getName() {
    return "Std.SM.Serial.Reg";
  }
}