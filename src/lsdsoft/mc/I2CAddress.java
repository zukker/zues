package lsdsoft.mc;

import lsdsoft.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class I2CAddress {
  private int address = 0;
  public I2CAddress(int i2cAddr) {
    address = i2cAddr;
  }
  public int getAddress() {
    return address;
  }
  public String toString() {
    StringBuffer str = new StringBuffer(2);
    str.append(Hex.hexChar[(address >> 4) & 0xf]);
    str.append(Hex.hexChar[address & 0xf]);
    return str.toString();
  }
}