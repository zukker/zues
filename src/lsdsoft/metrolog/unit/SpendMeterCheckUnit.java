package lsdsoft.metrolog.unit;

/**
 * <p>Title: Расходомерная поверочная установока</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ЦМИ "Урал-Гео"</p>
 * @author lsdsoft
 * @version 1.0
 */


public class SpendMeterCheckUnit extends AbstractCheckUnit{
  public SpendMeterCheckUnit() {
    controller = new SpendMeterCheckUnitController();
  }
}