package lsdsoft.util;

/**
 * <p>Title: interface for XML DOM read/write</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CMI Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */
import org.w3c.dom.Node;

public interface XMLStorable {
  /** Загрузка значений из родительского узла
   * @param parentNode родительский узел
   */
  public void load(Node parentNode);
  /** Сохранение значений в родительский узел
   * @param parentNode родительский узел
   */
  public void save(Node parentNode);

}