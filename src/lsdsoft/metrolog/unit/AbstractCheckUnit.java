package lsdsoft.metrolog.unit;


import com.lsdsoft.comm.*;
import lsdsoft.metrolog.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class AbstractCheckUnit
    extends ChannelDataSource
    implements CheckUnit, Connection {
    protected CheckUnitController controller = null;
    protected boolean hasConnect = false;
    protected ToolItemInfo toolInfo = new ToolItemInfo();
    protected boolean inited = false;

    public void setNumber(String number) {
        toolInfo.number = number;
    }
    public void init() {
    }
    public CheckUnitController getController() {
        return controller;
    }

    public void goToPoint( char plane, Object value ) throws CheckUnitException {
    }

    public Object getValue( char plane ) throws CheckUnitException {
        return null;
    }
    /**
     * Возвращает предел погрешности установеи для указанного канала
     * @param plane char имя канала
     * @param conditions Object дополнительные условия измерения
     * @return double предел погрешности в единицах имерения для указанного канала
     */
    public double getErrorLimitFor(char plane, Object conditions) {
       return 0;
    }

    public boolean isComplete( char plane ) {
        return true;
    }

    public char[] getPossiblePlanes() {
        return null;
    }

    public boolean isConnected() {
        return hasConnect;
    }

    public void connect() throws Exception {
        hasConnect = true;
    }

    public void disconnect() {
        hasConnect = false;
    }

    public void stop() throws Exception {

    }
}
