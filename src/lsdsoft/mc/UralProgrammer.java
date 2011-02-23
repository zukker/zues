package lsdsoft.mc;

import com.lsdsoft.comm.*;
import java.io.*;
import javax.comm.*;

/**
 * <p>Title: ѕрограмматор "”рал-1"</p>
 * <p>Description: ѕрограмматор предназначен дл€ прошивки микроконтроллеров
 * семейства AVR mega фирмы Atmel. ƒл€ этого используетс€ возможность
 * чипов самопрограммировани€. —истема представл€ет собой одим мастер контроллер
 * и несколько slave контроллеров. ѕодчиненные котроллеры подключаютс€ к мастеру
 * по TWI (I2C). ¬ мастер-котроллере уже должен быть прошит bootloader (bootmst.hex).
 * ƒл€ прошивки мастер-контроллера нужно его перевести в режим программировани€,
 * задав этот режим из приложени€ либо во врем€ включени€ питани€ посылать по
 * последовательному порту число 0xAE. ¬течение пол секунды контроллер должен
 * ответить ответным числом 0xEA. ƒл€ прошивки используетс€ протокол AVRprog
 * описанный в AVR appnotes AVR109:self-programming. </p>
 *
 * <p>“аблица 1.  оманды AVRProg</p>
 * <table>
 * <tr>
 * <th> оманда
 * <th>’ост пишет
 * <th>’ост читает
 * <tr>
 * <td>Enter Programming Mode<td>УPФ<td>13d
 * <tr>
 * <td>Auto Increment Address<td>УaФ<td>dd
 * <tr>
 * <td>Set Address УAФ ah al 13d
 * <tr>
 * <td>Write Program Memory, Low Byte УcФ dd 13d
 * Write Program Memory, High Byte УCФ dd 13d
 * Issue Page Write УmФ 13d
 * Read Lock Bits УСrФ dd
 * Read Program Memory УRФ dd
 * (dd)
 * Read Data Memory УdФ dd
 * Write Data Memory УDФ dd 13d
 * Chip Erase УeФ 13d
 * Write Lock Bits УlФ dd 13d
 * Write Fuse Bits УfФ dd 13d
 * Read Fuse Bits УFФ dd
 * Read High Fuse Bits УNФ dd
 * Leave Programming Mode УLФ 13d
 * Select Device Type УTФ dd 13d
 * Read Signature Bytes УsФ 3*dd
 * Return Supported Device Codes УtФ n*dd 00d
 * Return Software Identifier УSФ s[7]
 * Return Software Version УVФ dd dd
 * Return Hardware Version УvФ dd dd
 * Return Programmer Type УpФ dd
 * Set LED УxФ dd 13d
 * Clear LED УyФ dd 13d
 * </table>
 *
 * <p><b>ƒополнительные команды:</b></p>
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
   * ќжидание ответа от программатора
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
      throw new Exception("Ќет ответа");
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
   * ¬ыбор подчиненного устройства дл€ програмиировани€
   * @param number адрес i2c подчиненного устройства
   */
  public void selectDevice(int number) throws Exception {
    outs.write( 'W' );
    outs.write( number );

    if(!waitRespond( '\r' ))
      throw new Exception("\rѕрограмматор не ответил (seldev)");

  }
  public void setNewAddress(int oldAddress, int newAddress) throws Exception {
    selectDevice(oldAddress);
    outs.write( 'G' );
    outs.write( newAddress );

    if(!waitRespond( '\r' ))
      throw new Exception("\rѕрограмматор не ответил (setna)");

  }
  /**
   * ѕроверка присутстви€ подчиненного устройства на шине
   * ƒо использовани€ этого метода надо выбрать устройство использу€ selectDevice
   * @return "истина" если устройство отвечает в противном случае "ложь"
   */
  public boolean checkSlave() throws Exception{
    outs.write( 'w' );
    return waitRespond( 'Y' );
  }
  public void enterProgrammingMode() throws Exception {
    flushInput();
    outs.write( 'P' );
    if(!waitRespond( '\r' ))
      throw new Exception("\rѕрограмматор не ответил (epm)");
  }
  public void leaveProgrammingMode() {

  }
  public void eraseChip() throws Exception{
    outs.write( COMMAND_ERASE_CHIP );
    if(!waitRespond( '\r' ))
      throw new Exception("\rѕрограмматор не ответил (eras)");
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
      throw new Exception("\rѕрограмматор не ответил (wrt)");
  }
  /**
   * «адать адрес, в микроконтроллере используетс€ адресаци€ слов
   * @param address ÷елевой адрес байта
   */

  public void setAddress(int address) throws Exception{
    flushInput();
    outs.write( 'A' );
    // переводим из адресации байтов в адресацию слов
    address >>= 1;
    outs.write( (address >> 8) & 0xff );
    outs.write( address & 0xff );
    if(!waitRespond( '\r' ))
      throw new Exception("\rѕрограмматор не ответил (setA)");
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
   * ѕолучить размер страницы микроконтроллера дл€ программировани€
   * ƒл€ mega8 - 32 words, дл€ mega16 - 64 words
   * @return размер страницы в словах
   */
  public int getPageSize() throws Exception{
    int size = 0;
      outs.write('g');
      size = waitRespond();
    return size;
  }
  /**
   * ѕрошить запись в микроконтоллер
   * @param rec «апись дл€ прошивки
   */
  void programRecord(ObjectRecord rec) throws Exception {
    // @todo ¬ырвнивание если адрес не на границе страницы
    int address = rec.getAddress();
    int len = rec.getLength();
    // размер страницы выбранного котроллера в байтах
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