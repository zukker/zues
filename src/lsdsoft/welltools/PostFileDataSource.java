package lsdsoft.welltools;

import lsdsoft.metrolog.PostStreamDataSource;
import lsdsoft.zeus.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class PostFileDataSource extends PostStreamDataSource {
    private static final String POST_FILE_NAME = "postfile.properties";
    public PostFileDataSource() {
        Properties props = new Properties();
        try {
            Zeus.getInstance().loadProperties( props, POST_FILE_NAME );
            String fileName = props.getProperty( Zeus.getInstance().getConfig().
                                                 getProperty( Zeus.
                PROP_TOOL_TYPE ) );
            init( fileName );
            start();
        } catch ( Exception ex ) {
            System.err.println(ex.getLocalizedMessage());

        }
    }
    public void connect() throws Exception{
        Properties props = new Properties();
        Zeus.getInstance().loadProperties( props, POST_FILE_NAME );
        String fileName = props.getProperty( getProperty( Zeus.PROP_TOOL_TYPE ) );
        init( fileName );
        start();
        conn = true;
    }

    public void disconnect() {
        stop();
        conn = false;
    }

}