package lsdsoft.welltools.im.ins60;

import lsdsoft.metrolog.ChannelDataSource;
import lsdsoft.zeus.*;
import lsdsoft.metrolog.unit.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class INS60DataSourceViaUAKSI
    extends INS60Filter {
    //private ION1Filter filter = null;
    private UAKSI2CheckUnit unit = null;
    public INS60DataSourceViaUAKSI() throws Exception {
        unit = ( UAKSI2CheckUnit ) DataFactory.createCalibrationRigForChannel("angles");
        if ( unit == null ) {
            throw new Exception( "Cannot create UAKSI instance" );
        }
        ChannelDataSource src = unit.getToolDataSource();
        src.setProperty("number", Zeus.getInstance().getToolNumber());
        String tool = Zeus.getInstance().getToolType();
        unit.selectHandler("ksa");
        if(tool.equals("ins60m")) {
            modern = true;
        }
        init(src);

    }
    public boolean isConnected() {
        return unit.isConnected();
    }

}
