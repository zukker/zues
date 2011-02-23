package lsdsoft.metrolog;

/**
 * <p>Title: источник данных регистрирующего устройства</p>
 * <p>Description: получение данных с регистраторов</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */
import java.io.IOException;

public class RegistratorDataSource implements DataSource{
  /** Регистрирующее устройство
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
  /** Получить данные с канала
   * @param channel номер канала с которого нужно получить данные
   * если текущий канал не совпадает то вызывается selectChannel
   * для выпора канала
   */
  public double getData(int channel) throws IOException{
    return registrator.getData();
  }
  /** Получить данные с текущего информационного канала
   */
  public double getData() throws IOException {
    return registrator.getData();
  }
  public String getName() {
    return "Reg.Data.source";
  }
}