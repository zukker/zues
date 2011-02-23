package lsdsoft.metrolog.unit;

/**
 * <p>Title: —обитие отклика от установки</p>
 * <p>Description: —обытие генерируемое при получении ноыых данных от установки,
 * при отсутствии отклика от установки</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class RespondEvent {
  // маски примен€ютс€ непосредственно в обработчике установки
  // обработчик по своему интерпретирует отклик и его отсутствие
  // маска событи€ отклика
  public static final int EVENT_RESPOND = 0x01;
  // маска событи€ отсутстви€ отклика
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