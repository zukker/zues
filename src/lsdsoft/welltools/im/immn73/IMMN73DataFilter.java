///////////////////////////////////////////////////////////////////////
//
// (c) Copyright 2003 "Ural-Geo"
//     info@uralgeo.com
//
// author: lsdsoft@mail.ru
//
///////////////////////////////////////////////////////////////////////
/*
  Фильтр данных для скважинного инклинометра ИММН 73
  Разделяет сплошной поток из последовательного порта, посылаемого панелью,
  на каналы показаний датчиков. После принятия полного пакета вызывает обработчик каналов

*/
package lsdsoft.welltools.im.immn73;

import lsdsoft.metrolog.*;
import com.lsdsoft.comm.*;
import javax.comm.*;
import java.io.*;

//#include "ChannelDataSource.hpp"
//#include "SerialConnection.hpp"
//#include "SerialPortEventListener.hpp"
//#include "IMDataEventListener.hpp"
//#include "ComPort.h"
//#include "FIFOBuffer.hpp"

//#define IMMN73_PACKET_SIZE 8
//#define IONCHANNELS 15

public class IMMN73DataFilter extends ChannelDataSource
               implements SerialPortEventListener
                     {
  private static final int IMMN73_PACKET_SIZE = 8;
  private static final int COMMAND_SELECT = 1;
  private static final int COMMAND_REQUEST = 2;
  private CommConnection connection = new CommConnection();
  private SerialPort port;
  private InputStream ins = null;
  private OutputStream outs = null;
  private byte[] inputBuffer = new byte[IMMN73_PACKET_SIZE];
  private byte[] requestPacket = {0x52, 0x0d};
  private byte[] selectPacket = {0x53, 0x30, 0x0d};
  private int bufferIndex;
  private int command;
  private boolean commandDone = false;
  private int askedSensor;
  //bool flagBufferAsData;
  byte[] packetBuffer;
  //FIFOBuffer fifo;
  int askedParam;
  private boolean startPacket;
  int packetCount;
  int nextParam;
  //InklinometerAngles angles;
  // wait and read packet (3 bytes)
//  void getPacket();
  // bytes to skip from buffer
  //int toSkip;
  //bool isSync;
  //boolean arrived;
  //HANDLE sem;
  /**
   * Initializing serial port for IMMN73 panel
   */
  private void setupPort() {
    try {
    port.setSerialPortParams(2400,
        SerialPort.DATABITS_8,
        SerialPort.STOPBITS_1,
        SerialPort.PARITY_ODD);
    //port.setInBufferSize(IMMN73_PACKET_SIZE);
    //port.setOutBufferSize(32);
    }
    catch (Exception ex) {}
  }
  /**
   *
   */
  public void setPortName(String portName) {
    connection.setPortName(portName);
  }
  /**
   *
   */
  public void selectSensor(int sensor) {
    if(sensor > 7) {
      return;
    }
    selectPacket[1] = (byte)(0x30 + sensor);
    askedSensor = sensor;
    try {
      outs.flush();
      outs.write(selectPacket, 0, 3);
      command = COMMAND_SELECT;
      commandDone = false;
    } catch(Exception ex) {
      System.err.print(ex.getMessage());
    }
  }
  /**
   *
   */
  public void request() throws Exception{
    command = COMMAND_REQUEST;
    commandDone = false;
    outs.write(requestPacket);
  }
//  void sync();
//  void skip();
  /**
   *
   */
  private void getInput() throws Exception{
    ins.read(packetBuffer, 0, IMMN73_PACKET_SIZE);
  }
//  public boolean findPacket();
//  int decodeChunk(unsigned char * buffer);
  /**
   *
   */
  void processPacket() {
    if(command == COMMAND_SELECT) {
      commandDone = true;
    }
      //port.
      /*
    COMMAND_READ: break;
    case COMMAND_SELECT:
       commandDone = true;
       port.clearInput();
       Sleep(150);
       reset();
//     Sleep(750);
//     reset();
       return;
    }
    //if(!reseted) {

    port.clearInput();
    for(int i = 0; i < 2; i++) {
      int value = decodeChunk(packetBuffer + i * 4);
      ChannelDataEvent ev(askedParam * 2 + i, value);
      sendEvent(ev);
    }
    arrived = true;
    commandDone = true;
*/
  }
  void waitForArrived() {

  }
  int hexCharToInt(char hex) {
    int ret = 0;
    return ret;
  }

  public IMMN73DataFilter(String portName) {
    setPortName(portName);

  }
  public CommConnection getConnection() {
    return connection;
  }
  /**
   * Serial port event handler
   */
  public void serialEvent(SerialPortEvent ev) {
    switch(ev.getEventType()) {
      case SerialPortEvent.DATA_AVAILABLE:
        try {
          getInput();
          processPacket();
        } catch(Exception ex) {
          System.err.print(ex.getMessage());
        }
        break;
    }
  }
  /**
   *
   */
  public void connect() {
    try {
      CommPort cport;
      connection.connect();
      cport = connection.getPort();
      if(cport instanceof SerialPort) {
        port = (SerialPort)cport;
        setupPort();
        port.addEventListener(this);
        ins = port.getInputStream();
        outs = port.getOutputStream();
      } else
        throw new Exception("Invalid communication port type: " + connection.getPortName());
    } catch(Exception ex) {
      System.err.print(ex.getMessage());
    }
  }
  public void disconnect() {
    if(port != null) {
      connection.disconnect();
      port = null;
    }
  }
  //void addEventListener(ChannelDataEventListener* evListener);
  //void removeEventListener();
  void ask() {
  }

//  void setBufferAsData(bool state);
//  void measureAverage(unsigned int count, InklinometerAngles& angles);
  // process one packet
//  void process();
  private void finish() {
  }
  //int getChannel() { return channel; }
  //int getChannelValue(int chanIndex) { return channelData[chanIndex]; }
  //int getValue() {return value; }
  //void setPort(SerialPort * port);
  //virtual void getAngles(InklinometerAngles &ang);
  //void getAngles(double & rotate, double& zenit, double& azimut);


};


