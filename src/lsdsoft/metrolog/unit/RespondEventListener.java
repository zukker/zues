package lsdsoft.metrolog.unit;

/**
 * <p>Title: Интерфейс слушателя события отклика</p>
 * <p>Description: При срабатывании события RespondEvent вызывается метод respond
 * объекта слушателя</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public interface RespondEventListener {
  public void respond(RespondEvent ev);
}