package lsdsoft.metrolog;

import java.io.*;
import lsdsoft.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class PostStreamDataSource extends PostChannelDataSource {
    private int delayms = 250; // rescan input stream each 250 ms (default)
    private InputStream inputStream = null;
    private boolean mayRun = false;
    private static final int MAX_BUFFER = 1024;
    private byte[] buffer = new byte[MAX_BUFFER];
    private PostTask task = new PostTask(this);
    private String fileName = "";

    public PostStreamDataSource() {

    }
    public PostStreamDataSource( String fileName ) {
        init( fileName );
    }

    public PostStreamDataSource( InputStream ins ) {
        init(ins);
    }
    protected void init( String fileName ) {
        try {
            InputStream ins = new FileInputStream( fileName );
            this.fileName = fileName;
            init( ins );
        } catch ( FileNotFoundException ex ) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
    private void init( InputStream ins ) {
        inputStream = ins;
        //buffer = new String( new StringBuffer(MAX_BUFFER));
    }

    public void setPeriod( int millis ) {
        delayms = millis;
    }

    protected void start() {
        if(!task.isAlive() ) {
            if( inputStream != null ) {
                mayRun = true;
                task.start();
            }
        }
    }

    protected void stop() {
        mayRun = false;
    }

    public void finalize() {
        if( inputStream != null ) {
            try {
                inputStream.close();
            } catch ( IOException ex ) {
            }
        }
    }
    private class PostTask extends Thread {
        private ChannelDataSource source = null;
        public PostTask( ChannelDataSource src ) {
            super();
            source = src;
        }
        public void run() {
            while(mayRun) {
                Util.delay(delayms);
                try {
                    init(fileName);
                    //inputStream.reset();
                    int size = inputStream.available();
                    if ( size > MAX_BUFFER ) {
                        size = MAX_BUFFER;
                    }
                    inputStream.read(buffer, 0, size );
                    inputStream.close();
                    parse(new String( buffer, 0, size));
                    sendSignal( new SignalEvent( SignalEvent.SIG_DATA_ARRIVED,
                                                source ) );
                } catch ( IOException ex ) {
                    System.err.println(ex.getLocalizedMessage());
                }
            }
        }
    }
}
