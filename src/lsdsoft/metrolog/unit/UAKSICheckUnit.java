// ���������� ���������� ���������� ���-��
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
  // ����� ������ ���-��: ������ �� ������ ������
  // ���� ������ ������ ������
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
  // ���������� ������� �����
  int measureCount;
  // ���� ����������� �������
  InclinometerAngles devAngles;
  // �������� ��� ��� �������� ������������ ����������� ���� (������� �� �����������)
  InclinometerAngles lirDelta;
  // ���������� ����� ��� ������ ������� ����
  int stepsPerRound;
  // ������ �������� ������ � ��������� ����
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
 * ���������/���������� ����������� ��������� ��� ������� �� ���������
 * @param flag ������ �����������: ���� true, �� ���������� ��� �������,
 * ���� false, �� �� ����������
 */
  public void notifyOnRespond(boolean flag) {
    respondMask = flag ? respondMask|RespondEvent.EVENT_RESPOND :
                  respondMask&~RespondEvent.EVENT_RESPOND;
  }
  /**
   * ���������/���������� ����������� ��������� ��� ������� �� ���������
   * @param flag ������ �����������: ���� true, �� ���������� ��� �������,
   * ���� false, �� �� ����������
   */
  public void notifyOnNoRespond(boolean flag) {
    respondMask = flag ? respondMask|RespondEvent.EVENT_NO_RESPOND :
                  respondMask&~RespondEvent.EVENT_NO_RESPOND;
  }
  /**
   * ������� ����� ����������� �� ��������
   * @param mask ����� � ������� ����� ���� ������ EVENT_RESPOND, EVENT_NO_RESPOND
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

  // ������� ��� ��������� ��������
  /**
   * ������� ������� � ��������� ����� ������������������ �����
   */
  private void sendEvent() {
    if(eventListener != null) {
      if(lirAngles.azimuth.abs().gt(angleLimit)) return;
      if(lirAngles.zenith.abs().gt(angleLimit)) return;
      eventListener.dataEvent(new IMDataEvent(lirAngles));
      // ���� ������ �� ���������
      processRespondEvent(RespondEvent.EVENT_RESPOND);
      arrived = true;
      newData = true;
    }
  }
  /**
   * �������� ������� �� ���������
   */
  public boolean hasRespond() {
    return isResponded;
  }
  /**
   * ���������� ���������� �������
   * @param ev ������ �� ������. ����� 0 - ������, ����� 1 - �����
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
   * �������� ���������� � ����������
   */
  public boolean isConnected() {
    return connection.isConnected();
  }
  /**
   * ����������� � ���������
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
   * ���������� �� ���������
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
   * �������� ��������� � (�������� ���� - ������������ ����)
   * @param steps ���������� �����, ���� ������������� ��������,
   * �� �������� � ��������������� �������
   * @param speed ������������� �������� ��������
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
   * �������� ��������� Y (�������� ����)
   * @param steps ���������� �����
   * @param speed �������� (0-255)
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
   * �������� ������ ��� ������� Z
   * @param steps ���������� �����
   * @param speed ��������
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
   * �������� ��������� � ������������ � axis
   * @param axis ���������� ����������� ���������: 'x', 'y', 'z'
   * @param steps ���������� �����
   * @param speed ��������
   */
  public void rotate(char axis, int steps, int speed) {
    switch(axis) {
      case 'x': rotateX(steps, speed); break;
      case 'y': rotateY(steps, speed); break;
      case 'z': rotateZ(steps, speed); break;
    }
  }
  // ������� ���� �� ���� angle (����� �� ������������ �����)
  public void goToAzimut(Angle angle) {
  }
  // ������� ��������� �� ���� angle (����� �� �������� �����)
  public void goToZenit(Angle angle) {
  }
  // ������� ������ ����������� ��� ��������� ���� angle
  // (����� �� ����������� �����)
  public void goToApse(Angle angle) {
  }
  // ������� ������ ��������� ��� �� ���� angle
 // void goTo(char axis, Angle angle);
  // ��������� ������� ������� ������������� �������� �������� ����������
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
