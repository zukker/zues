package lsdsoft.metrolog;


/**
 * <p>Title: ������� ����� ��������������� ������ ����������� �������</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ��� "����-���"</p>
 * @author lsdsoft
 * @version 1.0
 */
import lsdsoft.util.*;

public class WellToolChannel {

  private String physicalValue;
  public WellToolChannel(String value) {
    physicalValue = value;
  }
  public String getPhysicalValue() {
    return physicalValue;
  }
}