package lsdsoft.metrolog;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class SignalSource {
  protected ArrayList signalListeners = new ArrayList(4);
  /**
   * ����� � ������ ���������� ���������
   * @param evListener ���������, ������� ���� �����
   * @return ������ ���������� �������, ���� �� ������ ������
   * ������������ -1
   */
  protected int findSignalListener(SignalEventListener evListener) {
    return signalListeners.indexOf(evListener);
  }
  SignalSource() {

  }
  protected void finalize() {
    removeSignalListeners();
    signalListeners = null;
  }
  /**
   * ��������� ��������� � ������, ��������� ���������� �����������
   * @param evListener ���������, ������� ����� ��������
   */
  public void addSignalListener(SignalEventListener evListener) {
    if(findSignalListener(evListener) == -1)
      signalListeners.add(evListener);
  }
  /**
   * ������� ��������� ���������
   * @param evListener ���������, ������� ����� ������� �� ������
   */
  public void removeSignalListener(SignalEventListener evListener) {
    signalListeners.remove(evListener);
  }
  /**
   * ������� ���� ����������
   */
  public void removeSignalListeners() {
    signalListeners.clear();
  }
  /**
   * "�������" ����������� ������� ���� ����������
   * @param event ������� ���������� ����������
   */
  public void sendSignal(SignalEvent event) {
    for(int i = 0; i < signalListeners.size(); i++)
      ((SignalEventListener)(signalListeners.get(i))).signalEvent(event);
  }
}

