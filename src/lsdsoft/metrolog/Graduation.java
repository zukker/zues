package lsdsoft.metrolog;

/**
 * <p>Градуировочная характеристика </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public interface Graduation {
    /**
     * Задать параметр градуировочной характеристики
     * @param param имя параметра
     * @param value значение параметра
     */
    public void setParameter(String param, String value);
    /**
     * Получить значение параметра
     * @param param имя параметра
     * @return
     */
    public String getParameter(String param);
    /**
     * Расчет значения градуировочной характеристики
     * @return
     */
    public double calc();

}