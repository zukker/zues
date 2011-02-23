// Управление поверочной установкой УАК-СИ
package lsdsoft.metrolog.unit;

import lsdsoft.units.*;
import com.lsdsoft.comm.*;
import lsdsoft.metrolog.*;
import com.lsdsoft.math.*;
import java.io.*;
import lsdsoft.metrolog.im.*;
import javax.comm.*;
import java.util.*;



public class UAKSICheckUnit extends ChannelDataSource implements Connection,
   ChannelDataEventListener{
  // номер версии УАК-СИ: влияет на формат команд
  // пока только первая версия
  private int version = 2;
  private CommConnection connection;
  private OutputStream outs;
  private byte[] commandBuffer = new byte[5];
  private Angle angleLimit = new Angle(1000);
  private InclinometerAngles lirAngles = new InclinometerAngles();
  private RespondEventListener respondListener = null;
  private int respondMask = 0;
  private Timer timer;
  private RemainderTask task = new RemainderTask(this);
  InclinometerAngles lirAnglesPrev;
  InclinometerAngles velocity;
  InclinometerAngles velocityPrev;
  InclinometerAngles accel;
  InclinometerAngles accelPrev;
  //clock_t clockCurr;
  //clock_t clockPrev;
  //clock_t clockStoped;
  //bool stoped;
  boolean arrived;
  private boolean isResponded = false;
  int ff;
  // average value of angles
  InclinometerAngles avAngles;
  // количество замеров углов
  int measureCount;
  // углы скважинного прибора
  InclinometerAngles devAngles;
  // поправки для лир датчиков относительно абсолютного нуля (беоется из сертификата)
  InclinometerAngles lirDelta;
  // количество шагов для целого оборота рамы
  int stepsPerRound;
  // предел точности выхода к заданному углу
  Angle normDelta;
  //unsigned char packetBuffer[4];
  //unsigned char respondBuffer[4];
  //int packetCount;
  //void decodePacket();
  // wait and read packet (3 bytes)
  //void getPacket();
  private void encodeSteps(byte[] buffer, int steps) {
    if (steps < 0)
      steps = -steps;
    buffer[0] = (byte)(steps / 4096);
    steps %= 4096;
    buffer[1] = (byte)(steps / 256);
    steps %= 256;
    buffer[2] = (byte)(steps / 16);
    buffer[3] = (byte)(steps % 16);
  }
  // indicates new arrived datas
  boolean newData;
  //HANDLE sem;
//public:
//  bool stoped;
  //GKDataConnection dataConnection;
//  IMControlConnection controlConnection;
  private IMDataEventListener eventListener = null;
  private LIRDataSource dataSource = null;
//  private LIRDataConnection dataConnection;

  public UAKSICheckUnit(String portName) {
    connection = new CommConnection(portName);
    stepsPerRound = 250 * 200;
    lirDelta = new InclinometerAngles();
  }
//  public void execCommand(AnsiString func, Parameters& params, VariableValue& retValue);
//  public void registerObject(ShellEnvironment& env);
/**
 * Включение/выключение уведомления слушателя при отклике от установки
 * @param flag флажок уведомления: если true, то уведомлять при отклике,
 * если false, то не уведомлять
 */
  public void notifyOnRespond(boolean flag) {
    respondMask = flag ? respondMask|RespondEvent.EVENT_RESPOND :
                  respondMask&~RespondEvent.EVENT_RESPOND;
  }
  /**
   * Включение/выключение уведомления слушателя при отклике от установки
   * @param flag флажок уведомления: если true, то уведомлять при отклике,
   * если false, то не уведомлять
   */
  public void notifyOnNoRespond(boolean flag) {
    respondMask = flag ? respondMask|RespondEvent.EVENT_NO_RESPOND :
                  respondMask&~RespondEvent.EVENT_NO_RESPOND;
  }
  /**
   * Задание маски уведомления об откликах
   * @param mask маска в которой могут быть флажки EVENT_RESPOND, EVENT_NO_RESPOND
   */
  public void notifyRespond(int mask) {
    respondMask = mask;
  }
  private void processRespondEvent(int flag) {
    if(respondListener != null) {
      if((respondMask & flag) != 0)
        respondListener.respond(new RespondEvent(flag));
    }
  }

  // событие для эталонных датчиков
  /**
   * Посылка события о получении новых инклинометрических углов
   */
  private void sendEvent() {
    if(eventListener != null) {
      if(lirAngles.azimuth.abs().gt(angleLimit)) return;
      if(lirAngles.zenith.abs().gt(angleLimit)) return;
      eventListener.dataEvent(new IMDataEvent(lirAngles));
      // есть отклик от установки
      processRespondEvent(RespondEvent.EVENT_RESPOND);
      arrived = true;
      newData = true;
    }
  }
  /**
   * Проверка отклика от установки
   */
  public boolean hasRespond() {
    return isResponded;
  }
  /**
   * Обработчик канального события
   * @param ev Данные по каналу. Канал 0 - азимут, канал 1 - зенит
   */
  public void channelEvent(ChannelDataEvent ev) {
    int channel = ev.getChannel();
    isResponded = true;
    if(channel == 0) {
      double val = ev.getValue().getAsDouble();
      lirAngles.azimuth.setAngle(val /1000.0);
      lirAngles.azimuth.sub(lirDelta.azimuth);
    }
    if(channel == 1) {
      double val = ev.getValue().getAsDouble();
      lirAngles.zenith.setAngle(val / 1000.0);
      lirAngles.zenith.sub(lirDelta.zenith);
      sendEvent();
    }
  }
  public void addEventListener(IMDataEventListener evListener) {
    eventListener = evListener;
  }
  public void addRespondEventListener(RespondEventListener evListener) {
    respondListener = evListener;
  }

//  public void removeEventListener();
  public void setDelta(InclinometerAngles angles) {
  }
  /**
   * Проверка соединения с установкой
   */
  public boolean isConnected() {
    return connection.isConnected();
  }
  /**
   * Подключение к установке
   * @throws Exception general exception class
   */
  public void connect() throws Exception {
    connection.connect();
    SerialPort port = (SerialPort)connection.getPort();
    outs = port.getOutputStream();
    port.disableReceiveThreshold();
    port.disableReceiveFraming();
    port.setSerialPortParams(9600,
                             SerialPort.DATABITS_8,
                             SerialPort.STOPBITS_1,
                             SerialPort.PARITY_NONE);
    dataSource = new LIRDataSource(connection);
    //dataSource.addEventListener(this);
    // check respond for five seconds
    timer = new Timer(true);
    task = new RemainderTask(this);
    timer.schedule(task, 3000, 3000);
  }
  /**
   * Отключение от установки
   */
  public void disconnect() {
    timer.cancel();
    task.cancel();
    dataSource.disconnect();
    dataSource = null;
    connection.disconnect();
  }

  // connect to lir and get angle values
//  void measureAngles();
//  void measureAverage(int count, InklinometerAngles& angle);
  public void setRoundSteps(int steps) {
  }
//  unsigned int getRoundSteps();
//  void waitForRespond();
  private void rotateMain(byte code, int steps, int speed) {
    if(speed > 255) speed = 255;
    if(speed < 0) speed = 0;
    int dummy = (16 - IntegerCalc.sqrt_i(speed)) /2 ;
    // if has wait commands do one step
    if(dummy > 0)
      steps = (steps < 0)?-1:1;
    commandBuffer[4] = code;
    if(steps < 0)
      steps = -steps;

    try {
      for (int i = 0; i < dummy; i++) {
        encodeSteps(commandBuffer, 0);
        outs.write(commandBuffer);
      }
      encodeSteps(commandBuffer, steps);
      outs.write(commandBuffer);
    }
    catch (IOException ex) {
      System.err.print(ex.toString());
    }
  }
  /**
   * Вращение двигателя Х (вращение рамы - азимутальные углы)
   * @param steps Количество шагов, если отрицательное значение,
   * то вращение в противоположную сторону
   * @param speed относительная скорость вращения
   */
  public void rotateX(int steps, int speed) {
    byte code;
    if(steps < 0)
      code = 0x33; // '3'
    else
      code = 0x32; // '2'
    rotateMain(code, steps, speed);
  }
  /**
   * Вращение двигателя Y (зенитные углы)
   * @param steps Количество шагов
   * @param speed Скорость (0-255)
   */
  public void rotateY(int steps, int speed) {
    byte code;
    if(steps < 0)
      code = 0x35; // '5'
    else
      code = 0x34; // '4'
    rotateMain(code, steps, speed);
  }
  /**
   * Вращение вокруг оси прибора Z
   * @param steps Количество шагов
   * @param speed Скорость
   */
  public void rotateZ(int steps, int speed) {
    byte code;
    if(steps < 0)
      code = 0x38; // '8'
    else
      code = 0x37; // '7'
    rotateMain(code, steps, speed);
  }
  /**
   * Вращение двигателя в соответствии с axis
   * @param axis Символьное обозначение двигателя: 'x', 'y', 'z'
   * @param steps Количество шагов
   * @param speed Скорость
   */
  public void rotate(char axis, int steps, int speed) {
    switch(axis) {
      case 'x': rotateX(steps, speed); break;
      case 'y': rotateY(steps, speed); break;
      case 'z': rotateZ(steps, speed); break;
    }
  }
  // Поворот рамы до угла angle (выход на азимутальную точку)
  public void goToAzimut(Angle angle) {
  }
  // Поворот ложемента до угла angle (выход на зенитную точку)
  public void goToZenit(Angle angle) {
  }
  // Поворот вокруг собственной оси приборадо угла angle
  // (выход на апсидальную точку)
  public void goToApse(Angle angle) {
  }
  // Поворот вокруг указанной оси до угла angle
 // void goTo(char axis, Angle angle);
  // установка верхней границы относительной скорости вращения двигателей
 // void setRotateSpeed(int speed);
  public Angle getAzimut() {
    return lirAngles.azimuth;
  }
  public Angle getZenit() {
    return lirAngles.zenith;
  }
  //unsigned char * getRespondBuffer() { return respondBuffer; }
  //void addEventListener(ChannelDataEventListener* evListener);
  //void removeEventListener();
  private class RemainderTask extends TimerTask {
    private UAKSICheckUnit cu;
    public RemainderTask(UAKSICheckUnit unit) {
      cu = unit;
    }
    public void run() {
      if(!cu.isResponded)
        cu.processRespondEvent(RespondEvent.EVENT_NO_RESPOND);
      cu.isResponded = false;
    }
  }

}
