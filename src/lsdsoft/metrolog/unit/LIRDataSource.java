// Ёбв®з­ЁЄ ¤ ­­ле Ї ­Ґ«Ё ‹€ђ-521
// ¤ взЁЄЁ ‹€ђ-158Ђ

package lsdsoft.metrolog.unit;

import javax.comm.*;
import lsdsoft.metrolog.*;
import com.lsdsoft.comm.*;
import java.io.*;
import java.awt.*;


//#define LIR_PACKET_SIZE 10
//#define LIRCHANNELS 12
//#define IN_BUFFER_SIZE 32

public class LIRDataSource extends ChannelDataSource implements SerialPortEventListener {
    private final int IN_BUFFER_SIZE = 32;
  private final int LIR_PACKET_SIZE = 10;
  private final int LIR_CHANNELS = 2;
  private ChannelValue cv = new ChannelValue();

  private InputStream ins;
  private byte[] inputBuffer;
  private CommConnection conn;
  private int counter = 0;
  // send first 3 bytes of packet data as channel data (0..2 channel)
//  bool flagBufferAsData;
  private int bufferIndex;
  private boolean isSync;
  byte[] packetBuffer = new byte[LIR_PACKET_SIZE];
  private int[] channelData = new int[LIR_CHANNELS];
  int packetCount;
  /**
   * Проверка пакета на достоверность данных
   * Если пакет не верный isSync выставляется в ложь
   */
  private void checkPacket() {
    // проверка всех данных (bcd valid)
    for(int i = 0; i < 8; i++) {
      byte b = inputBuffer[bufferIndex + i];
      if(((b&0x0f) > 9) || (((b>>4)&0x0f) > 9)) {
        isSync = false;
        break;
      }
    }
    // проверка на истинность положения в пакете
    if(!((inputBuffer[bufferIndex + 8] >9 ) &&
         (inputBuffer[bufferIndex + 9] == 10))) {
      isSync = false;
    }
  }
/*
  private StringBuffer bufToString(byte[] buf) {
    StringBuffer str = new StringBuffer(buf.length * 2);
    for(int i = 0; i < 8; i++) {
      str.append(hex[buf[i + bufferIndex]&0xf]);
      str.append(hex[(buf[i + bufferIndex]>>4)&0xf]);
    }
    return str;
  }*/
  /**
   * Преобразование входного пакета в данные по каналам
   */
  private void decodePacket() {
    channelData[0] = bcdDecodeBuffer(inputBuffer, bufferIndex, 4);
    channelData[1] = bcdDecodeBuffer(inputBuffer, bufferIndex + 4, 4);
  }
  /**
   * Проверка и посылка данных слушателям
   */
  private void processPacket() {
    checkPacket();
    if(!isSync) {
      return;
      }
    decodePacket();
    for(int i = 0; i < 2; i++) {
        cv.setAsInteger(channelData[i]);
      //cv.type = ChannelValue.CV_CODE;
      //cv.code = channelData[i];
      //if(i == 1) cv.code = counter;
      ChannelDataEvent ev = new ChannelDataEvent(i, cv);
      //sendEvent(ev);
    }
    //counter ++;
    //Toolkit tk = Toolkit.getDefaultToolkit();
    //tk.beep();
    bufferIndex += LIR_PACKET_SIZE;
  }
  /**
   * преобразует один двоичнодесятичный байт в число
   * Шеснадцатиричное представление входного числа: 0xdd
   * где d - цифра от 0 до 9
   * отсутствует проеверка на правильность числа (предполагается полная достоверность информации)
   * @param bcd входное двоично-десятичное число
   * @return преобразованное число (bcd->hex)
   */
  private int bcdDecode(byte bcd) {
    int b = bcd;
     if(b < 0)b += 256;
    return (b & 0xf) + ((b >> 4) * 10);
  }
  // преобразует 4 двоичнодесятичных байта в число
  /**
   * Преобразует буфер двоично-десятичных чисел в общее число
   * @param buf входной буфер из
   * @param index позиция начиная с которой нужно преобразовать числа
   * @param count количество байт которое нужно преобразовать
   * @return результирующее число
   */
  private int bcdDecodeBuffer(byte[] buf, int index, int count) {
    int val = 0;
    int deg = 1;
    for(int i = 0; i < count; i++) {
      val += bcdDecode(buf[i + index]) * deg;
      deg *= 100;
    }
    // if value negative
    if(val >   9999999) {
      val -= 100000000;
    }
    return val;
  }
  // wait and read packet (3 bytes)
//  void getPacket();
//
  /**
   * Поиск пакета во входном буфере
   * @return true если пакет найден, иначе false
   */
  private boolean findPacket() {
    // буфер должен заканчиваться значением 0x0b 0x0a
    boolean ret = false;
    int prevIndex = bufferIndex;
    for(;bufferIndex < IN_BUFFER_SIZE - LIR_PACKET_SIZE; bufferIndex++) {
      if(inputBuffer[bufferIndex] == 11 && inputBuffer[bufferIndex + 1] == 10) {
        bufferIndex += 2;
        if(bufferIndex == prevIndex + LIR_PACKET_SIZE)
          bufferIndex = prevIndex;
        ret = true;
        isSync = true;
        break;
      }
    }
    return ret;
  }
//  bool isSync;
//  void sync();
  /**
   * Заполняет входной буфер (inputBuffer) из входного потока
   * и сбрасывает индекс буфера в ноль
   */
  private synchronized void getInput() {
    int size, size2;
    try {
      ins.read(inputBuffer);
    } catch(Exception ex) {
      System.err.print(ex.toString());
    }
//    for(int i = 0; i < LIR_PACKET_SIZE; i++)
//      packetBuffer[i] = inputBuffer[i];
    bufferIndex = 0;

  }
  /**
   * Конструктор создает источник данных от панели ЛИР-521 по подключенному соединению conn
   * @param conn Комуникационное соединение, через корое получают данные.
   * Соединение должно быть открытым. Для панели ЛИР соединение через последовательный порт.
   */
  public LIRDataSource(CommConnection conn) {
    this.conn = conn;
    SerialPort port = (SerialPort)conn.getPort();
    try {
      ins = port.getInputStream();

      port.notifyOnDataAvailable(true);
      //port.setInputBufferSize();
      port.enableReceiveThreshold(IN_BUFFER_SIZE);
      inputBuffer = new byte[IN_BUFFER_SIZE];
      port.addEventListener(this);
    } catch (Exception ex) {System.err.print(ex.toString());}
  }

  public void disconnect() {
    SerialPort port = (SerialPort)conn.getPort();
    port.removeEventListener();
  }
  /**
   * Обработчик событий последовательного порта
   * @param ev Событие последовательного порта. Обрабатывается только прием данных
   */
  public void serialEvent(SerialPortEvent ev) {
    switch(ev.getEventType()) {
      case SerialPortEvent.DATA_AVAILABLE:
        getInput();
        while(findPacket()) {
          processPacket();
        }
        break;
    }
  }


};


