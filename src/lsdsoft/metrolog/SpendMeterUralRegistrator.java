package lsdsoft.metrolog;

/**
 * <p>Title: стандартный регистратор РЦСМ Урал</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */
import javax.comm.*;
import java.io.*;

public class SpendMeterUralRegistrator extends UralRegistrator {
  public SpendMeterUralRegistrator() {
    super();
    System.out.println("smur");
    try {
    port.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
    port.setDTR(false);
    port.setRTS(true);
    } catch(Exception e) {}
  }
}