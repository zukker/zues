package lsdsoft.mc;

import com.lsdsoft.comm.*;
import java.io.*;
import javax.comm.*;

/**
 * <p>Title: ������������ "����-1"</p>
 * <p>Description: ������������ ������������ ��� �������� �����������������
 * ��������� AVR mega ����� Atmel. ��� ����� ������������ �����������
 * ����� ��������������������. ������� ������������ ����� ���� ������ ����������
 * � ��������� slave ������������. ����������� ���������� ������������ � �������
 * �� TWI (I2C). � ������-���������� ��� ������ ���� ������ bootloader (bootmst.hex).
 * ��� �������� ������-����������� ����� ��� ��������� � ����� ����������������,
 * ����� ���� ����� �� ���������� ���� �� ����� ��������� ������� �������� ��
 * ����������������� ����� ����� 0xAE. �������� ��� ������� ���������� ������
 * �������� �������� ������ 0xEA. ��� �������� ������������ �������� AVRprog
 * ��������� � AVR appnotes AVR109:self-programming. </p>
 *
 * <p>������� 1. ������� AVRProg</p>
 * <table>
 * <tr>
 * <th>�������
 * <th>���� �����
 * <th>���� ������
 * <tr>
 * <td>Enter Programming Mode<td>�P�<td>13d
 * <tr>
 * <td>Auto Increment Address<td>�a�<td>dd
 * <tr>
 * <td>Set Address �A� ah al 13d
 * <tr>
 * <td>Write Program Memory, Low Byte �c� dd 13d
 * Write Program Memory, High Byte �C� dd 13d
 * Issue Page Write �m� 13d
 * Read Lock Bits ��r� dd
 * Read Program Memory �R� dd
 * (dd)
 * Read Data Memory �d� dd
 * Write Data Memory �D� dd 13d
 * Chip Erase �e� 13d
 * Write Lock Bits �l� dd 13d
 * Write Fuse Bits �f� dd 13d
 * Read Fuse Bits �F� dd
 * Read High Fuse Bits �N� dd
 * Leave Programming Mode �L� 13d
 * Select Device Type �T� dd 13d
 * Read Signature Bytes �s� 3*dd
 * Return Supported Device Codes �t� n*dd 00d
 * Return Software Identifier �S� s[7]
 * Return Software Version �V� dd dd
 * Return Hardware Version �v� dd dd
 * Return Programmer Type �p� dd
 * Set LED �x� dd 13d
 * Clear LED �y� dd 13d
 * </table>
 *
 * <p><b>�������������� �������:</b></p>
 * Checking slave      "w"  dd        "Y" or other of no device
 * Select slave        "W"  dd        0x0d
 * Reset master        "o"            0x0d
 * Reset slave         "O"            0x0d
 * Get current mode    "M"            dd  (return 'P' -program mode in boot section or 'A' - application mode
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class UralProgrammer extends CommConnection {
  private static final int COMMAND_ERASE_CHIP = '.';
  private static final int COMMAND_WRITE_PAGE = 'm';
  private int timeout = 2500;
  InputStream ins;
  OutputStream outs;
  SerialPort port;
  public UralProgrammer() {
  }
  public void connect() throws Exception {
    super.connect();
    port = (SerialPort)super.port;
    port.setSerialPortParams(9600,
                             SerialPort.DATABITS_8,
                             SerialPort.STOPBITS_1,
                             SerialPort.PARITY_NONE);
    outs = port.getOutputStream();
    ins = port.getInputStream();
    port.enableReceiveTimeout(5000);
    outs.write(0xae);  //
    delay(100);
    flushInput();
  }
  private void delay(int milli) {
    long t = System.currentTimeMillis();
    while(System.currentTimeMillis() - t < milli) ;
  }
  private void flushInput() throws Exception {
    ins.skip(ins.available());
  }
  /**
   * �������� ������ �� �������������
   */
  private boolean waitRespond(int b) throws Exception {
    boolean ret = false;
    return waitRespond() == b;
  }
  private int waitRespond() throws Exception{
    int ret = -1;
    long t = System.currentTimeMillis();
    while(System.currentTimeMillis() - t < timeout) {
      if (ins.available() > 0) {
        ret = ins.read();
        break;
      }
    }
    if(ret == -1 )
      throw new Exception("��� ������");
    return ret;
  }
  public String getSoftwareID() {
    byte[] b = new byte[10];
    try {
      outs.write('S');
      ins.read(b);
      ins.reset();
    } catch (Exception ex) { }
    return new String(b);
  }
  /**
   * ����� ������������ ���������� ��� ����������������
   * @param number ����� i2c ������������ ����������
   */
  public void selectDevice(int number) throws Exception {
    outs.write( 'W' );
    outs.write( number );

    if(!waitRespond( '\r' ))
      throw new Exception("\r������������ �� ������� (seldev)");

  }
  public void setNewAddress(int oldAddress, int newAddress) throws Exception {
    selectDevice(oldAddress);
    outs.write( 'G' );
    outs.write( newAddress );

    if(!waitRespond( '\r' ))
      throw new Exception("\r������������ �� ������� (setna)");

  }
  /**
   * �������� ����������� ������������ ���������� �� ����
   * �� ������������� ����� ������ ���� ������� ���������� ��������� selectDevice
   * @return "������" ���� ���������� �������� � ��������� ������ "����"
   */
  public boolean checkSlave() throws Exception{
    outs.write( 'w' );
    return waitRespond( 'Y' );
  }
  public void enterProgrammingMode() throws Exception {
    flushInput();
    outs.write( 'P' );
    if(!waitRespond( '\r' ))
      throw new Exception("\r������������ �� ������� (epm)");
  }
  public void leaveProgrammingMode() {

  }
  public void eraseChip() throws Exception{
    outs.write( COMMAND_ERASE_CHIP );
    if(!waitRespond( '\r' ))
      throw new Exception("\r������������ �� ������� (eras)");
  }
  /**
   *
   */
  private void writePage(int address) throws Exception {
    setAddress(address);
    flushInput();
    outs.write( COMMAND_WRITE_PAGE );
    delay(200);
    if(!waitRespond( '\r' ))
      throw new Exception("\r������������ �� ������� (wrt)");
  }
  /**
   * ������ �����, � ���������������� ������������ ��������� ����
   * @param address ������� ����� �����
   */

  public void setAddress(int address) throws Exception{
    flushInput();
    outs.write( 'A' );
    // ��������� �� ��������� ������ � ��������� ����
    address >>= 1;
    outs.write( (address >> 8) & 0xff );
    outs.write( address & 0xff );
    if(!waitRespond( '\r' ))
      throw new Exception("\r������������ �� ������� (setA)");
  }
  public void fillPage(byte[] data, int address, int bytes) throws Exception {
    for(int i = address; i < address + bytes; i+=2) {
      //writeMemory(data[i * 2], data[i * 2 + 1]);
      if(i>= data.length)
        writeMemory( 0xff, 0xff );
      else
        writeMemory( data[i], data[i + 1]);
      //writeMemory( i, i+1);
      //System.out.print(data[i ]);
      //writeMemory(i*2, i * 2+1);
    }
    ins.skip(ins.available());
  }
  private void writeMemory(int byteLo, int byteHi) throws Exception{
    //outs.write('c');
    outs.write('C');
    outs.write(byteLo);
    //outs.write(i);
    //waitRespond('\r');
    // write program memory high nyte
    //outs.write('C');
    outs.write(byteHi);
    outs.flush();
  }
  /**
   * �������� ������ �������� ���������������� ��� ����������������
   * ��� mega8 - 32 words, ��� mega16 - 64 words
   * @return ������ �������� � ������
   */
  public int getPageSize() throws Exception{
    int size = 0;
      outs.write('g');
      size = waitRespond();
    return size;
  }
  /**
   * ������� ������ � ��������������
   * @param rec ������ ��� ��������
   */
  void programRecord(ObjectRecord rec) throws Exception {
    // @todo ����������� ���� ����� �� �� ������� ��������
    int address = rec.getAddress();
    int len = rec.getLength();
    // ������ �������� ���������� ���������� � ������
    int pageSize = getPageSize() * 2;
    byte[] b = rec.getData();
    int pagesToWrite = len / pageSize;
    if( pagesToWrite * pageSize < len )
      pagesToWrite++;
    //pagesToWrite = 1;
    // erase chip
    System.out.println("Erase chip");
    eraseChip();
    for(int page = 0; page < pagesToWrite; page++) {
      System.out.println("Set address: " + address);
      setAddress( address );
      System.out.println("Fill page");

      fillPage( b, address, pageSize );
      System.out.println("Write page");
      writePage( address );
      address += pageSize;
    }
  }
}