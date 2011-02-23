package lsdsoft.welltools.im.ion1;

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

public class ION1DataSourceViaUAKSI
    extends ION1Filter {
    //private ION1Filter filter = null;
    private UAKSI2CheckUnit unit = null;
    public ION1DataSourceViaUAKSI() throws Exception {
        unit = ( UAKSI2CheckUnit ) DataFactory.createCalibrationRigForChannel("angles");
        if ( unit == null ) {
            throw new Exception( "Cannot create UAKSI instance" );
        }
        ChannelDataSource src = unit.getToolDataSource();
        src.setProperty("number", Zeus.getInstance().getToolNumber());
        //String tool = Zeus.getInstance().getToolType();
        unit.selectHandler("ion1");
        init(src);
        setProperty("table.date", table.getDate());
        /*
        unit = ( UAKSI2CheckUnit ) DataFactory.createCheckUnit( UAKSI2CheckUnit.class.
            getName() );
        if ( unit == null )
            throw new Exception( "Cannot create UAKSI instance" );
        ChannelDataSource src = unit.getToolDataSource();
        src.setProperty("number", Zeus.getInstance().getToolNumber());
        init(src);
*/
    }
    public boolean isConnected() {
        return unit.isConnected();
    }

}
