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

public class UralRegistrator extends SerialRegistrator{
  public UralRegistrator() {
    super();
    System.out.println("ur");
//    init();
    try {
    port.setSerialPortParams(9600, SerialPort.DATABITS_8,
         SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
//    port.setDTR(true);
 //   port.setRTS(false);

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
  public int getData() throws IOException {
    sendByte((byte)0xff);
    sendByte((byte)0xff);
    return (int)getByte();
  }

  public void selectChannel(int channel) {
  }

}