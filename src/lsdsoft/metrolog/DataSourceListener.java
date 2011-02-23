package lsdsoft.metrolog;

/**
 * <p>Title: Слушатель ввода данных</p>
 * <p>Description: интерфецс слушателя</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */

public interface DataSourceListener {
  public void inputPerformed(DataInputEvent e);
}