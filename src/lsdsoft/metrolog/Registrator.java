package lsdsoft.metrolog;

/**
 * <p>Title: интерфейс регистрирующих устройств</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */
import java.io.IOException;

public interface Registrator {
  public void init();
  public void selectChannel(int channel);
  public byte getByte() throws IOException ;
  public int getData() throws IOException ;
  public void sendByte(final byte val) throws IOException ;
  public String getName();
}