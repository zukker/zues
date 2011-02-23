package lsdsoft.metrolog;

import java.util.*;
import lsdsoft.zeus.*;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class ToolDataSource extends ChannelDataSource {
    protected ArrayList grads = new ArrayList(2);
    protected String tooltype = "";
    protected String number = "";

    public ToolDataSource() {
        number = Zeus.getInstance().getToolNumber();
        tooltype = Zeus.getInstance().getToolType();
        properties.setProperty("number", number );
        properties.setProperty("tooltype", tooltype );
        try {
            loadAliases();
            loadGraduation();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void loadAliases() throws Exception {
        setAliases( DataFactory.createChannelAlias( tooltype ) );
    }

    public void loadGraduation() throws Exception {
        ///DataFactory.getDataStorage().loadMeasureDatas( tooltype, number, "");
        String fn = DataFactory.getDataStorage().getFileName( tooltype, number,
            "" );
        String fngrad = fn + tooltype + ".grad";
        String fngrad2 = fn + tooltype + "." + number + ".grad";
        File file1 = new File( fngrad );
        File file2 = new File( fngrad2 );
        Properties grad = new Properties();
        InputStream ins = null;
        if(file1.exists()) {
            ins = new FileInputStream( file1 );
        } else
        if(file2.exists()) {
            ins = new FileInputStream( file2 );
        }
        // load list of graduations
        if ( ins == null ) {
            System.err.println(
                "Unable to load graduation: graduation index not found." );
            return;
        }
        grad.load( ins );
        ins.close();
        Enumeration gradList = grad.keys();
        for ( ; gradList.hasMoreElements(); ) {
            String key = ( String )gradList.nextElement();
            String fileName = fn + grad.getProperty( key );
            try {
                InputStream in = new FileInputStream( fileName );
                Properties gradProps = new Properties();
                // load list of graduations
                gradProps.load( in );
                BaseGraduation graduation = new BaseGraduation();
                graduation.setParameters( gradProps );
                grads.add( graduation );
            } catch ( Exception ex ) {
                System.err.println( "Unable to load graduation: " + fileName );
            }
        }
    }
    public Graduation getGraduation(String channel) {
        for(int i = 0; i < grads.size(); i++ ) {
            Graduation graduation = (Graduation) grads.get(i);
            if(channel.equals(graduation.getParameter("channel")) ) {
                return graduation;
            }
        }
        return null;
    }

    public ArrayList getGraduations() {
        return grads;
    }


}
