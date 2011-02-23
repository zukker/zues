package lsdsoft.metrolog.unit;

/**
 * <p>Title: ������� ������� �� ���������</p>
 * <p>Description: ������� ������������ ��� ��������� ����� ������ �� ���������,
 * ��� ���������� ������� �� ���������</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class RespondEvent {
  // ����� ����������� ��������������� � ����������� ���������
  // ���������� �� ������ �������������� ������ � ��� ����������
  // ����� ������� �������
  public static final int EVENT_RESPOND = 0x01;
  // ����� ������� ���������� �������
  public static final int EVENT_NO_RESPOND = 0x02;
  public static final int EVENT_BAD_RESPOND = 0x04;

  private int respondValue;
  public int getRespond() {
    return respondValue;
  }
  public RespondEvent(int resp) {
    respondValue = resp;
  }
}