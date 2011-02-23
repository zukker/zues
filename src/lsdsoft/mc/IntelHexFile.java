package lsdsoft.mc;

import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class IntelHexFile extends ObjectFile {
  private int pos;
  private byte[] buffer;

  public IntelHexFile() {
  }
  private int hexToByte( char hex ) {
    int b = 0;
    if(hex >= '0' && hex <= '9')
      b = hex - '0';
    else
      if(hex >= 'A' && hex <= 'F')
        b = hex - 'A' + 10;

    return b;
  }
  private int getByte() {
    return toByte((char)buffer[pos++], (char)buffer[pos++]);
  }
  private int toByte(char charHi, char charLo) {
    return hexToByte(charHi) * 16 + hexToByte(charLo);
  }
  public void load(InputStream ins) throws Exception {
    int line = 0;
    int size = ins.available();
    buffer = new byte[size];
    ins.read(buffer);
    pos = 0;
    byte b;
    int i, i2;
    while(pos < size) {
      line++;
      // check char ':'
      if(buffer[pos++] != 0x3a)
        throw new Exception("Invalid start of record in line " + line);
      int checkSum = 0;
      int len = getByte();
      checkSum += len;
      // get address of data
      i = getByte();
      i2 = getByte();
      checkSum += i;
      checkSum += i2;
      int address = i * 256 + i2;
      // get record type
      int rt = getByte();
      checkSum += rt;
      ObjectRecord rec = new ObjectRecord(ObjectRecord.TYPE_STANDART, len);
      records.add(rec);
      rec.setAddress(address);
      // get data
      byte[] data = rec.getData();
      //load data
      for(i = 0; i < len; i++) {
        data[i] = (byte)getByte();
        rt = ((char)data[i])&(char)0xff;
        checkSum += rt;
      }
      checkSum = (0x10000 - checkSum) & 0xff;
      int cs = (getByte() + 0x100) &0xff;
      if(cs != checkSum)
        throw new Exception("Invalid check sum in line " + line);
      // skip white spaces;
      while((pos < size) && (buffer[pos] < ' ')) pos++;
    }
    buffer = null;
  }
}