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
   * ѕоиск в списке указанного слушател€
   * @param evListener слушатель, который надо найти
   * @return индекс найденного объекта, если не найден объект
   * возвращаетс€ -1
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
   * ƒобавл€ет слушател€ к списку, повторное добавление исключаетс€
   * @param evListener слушатель, который нужно добавить
   */
  public void addSignalListener(SignalEventListener evListener) {
    if(findSignalListener(evListener) == -1)
      signalListeners.add(evListener);
  }
  /**
   * ”дал€ет указанный слушатель
   * @param evListener слушатель, который нужно удалить из списка
   */
  public void removeSignalListener(SignalEventListener evListener) {
    signalListeners.remove(evListener);
  }
  /**
   * ”далает всех слушателей
   */
  public void removeSignalListeners() {
    signalListeners.clear();
  }
  /**
   * "–аздача" сигнального событи€ всем слушател€м
   * @param event событие посылаемое слушател€м
   */
  public void sendSignal(SignalEvent event) {
    for(int i = 0; i < signalListeners.size(); i++)
      ((SignalEventListener)(signalListeners.get(i))).signalEvent(event);
  }
}

