package lsdsoft.mc;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class ObjectRecord {
  public static final int TYPE_STANDART = 0;
  public static final int TYPE_END = 1;
  /**
   * Type of record of object file. May be any value of TYPE_*.
   */
  private int type;
  /**
   * Length of record in bytes, equal data.length.
   */
  private int length;
  /**
   * Address for record location.
   */
  private int address;
  /**
   * Data of record
   */
  private byte[] data;
  public ObjectRecord(int type, int len) {
    data = new byte[len];
    length = len;
    this.type = type;
  }
  public void setAddress(int address) {
    this.address = address;
  }
  public int getAddress() {
    return address;
  }
  public int getLength() {
    return length;
  }
  public void setData(byte[] newData) {

  }
  public byte[] getData() {
    return data;
  }

}