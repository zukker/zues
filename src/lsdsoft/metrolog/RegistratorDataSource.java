package lsdsoft.metrolog;

/**
 * <p>Title: �������� ������ ��������������� ����������</p>
 * <p>Description: ��������� ������ � �������������</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ��� "����-���"</p>
 * @author lsdsoft
 * @version 1.0
 */
import java.io.IOException;

public class RegistratorDataSource implements DataSource{
  /** �������������� ����������
   */
  protected Registrator registrator = null;
  public RegistratorDataSource(Registrator reg) {
    registrator = reg;
  }
  public String getUID() {
    return "ds.reg";
  }
  public void init() {
    registrator.init();
  }
  public void selectChannel(int channel) {
  }
  /** �������� ������ � ������
   * @param channel ����� ������ � �������� ����� �������� ������
   * ���� ������� ����� �� ��������� �� ���������� selectChannel
   * ��� ������ ������
   */
  public double getData(int channel) throws IOException{
    return registrator.getData();
  }
  /** �������� ������ � �������� ��������������� ������
   */
  public double getData() throws IOException {
    return registrator.getData();
  }
  public String getName() {
    return "Reg.Data.source";
  }
}