package lsdsoft.welltools;

import lsdsoft.metrolog.ChannelDataSource;
import lsdsoft.zeus.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.metrolog.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class DataSourceViaUAKGK
    extends ToolDataSource
    implements ChannelDataSourceFilter,
    SignalEventListener {

    private ChannelDataSource source;
    private Properties handlers = new Properties();
    //public String id = "";
    private UAKGKCheckUnit unit = null;
    public DataSourceViaUAKGK() throws Exception {
        unit = ( UAKGKCheckUnit ) DataFactory.createCheckUnit( UAKGKCheckUnit.class.
            getName() );
        if ( unit == null )
            throw new Exception( "Cannot create UAKGK instance" );
        ChannelDataSource src = unit.getToolDataSource();
        loadHandlers();
        //selectHandler();
        init(src);
    }
    public void connect() throws Exception {
        super.connect();
        unit.connect();
        selectHandler();
    }

    public void selectHandler() throws Exception {
        String type = Zeus.getInstance().getToolType();
        // handler type for specified tool type and channel
        String type2 = type + "." + Zeus.getInstance().getToolChannel();
        if( type == null )
            throw new Exception("No tool type selected!");
        String handlerName = handlers.getProperty( type, "" );

        String handlerName2 = handlers.getProperty( type2, "" );
        if( !handlerName2.equals("") ) {
            handlerName = handlerName2;
        }
        if( !handlerName.equals("") ) {
            System.out.println("Selecting handler '" + handlerName + "'.");
            unit.selectHandler( handlerName );
        }
    }

    public boolean isConnected() {
        return unit.isConnected();
    }

    protected void loadHandlers() {
        try {
            Zeus.getInstance().loadProperties( handlers, "uak.properties" );
        } catch ( Exception ex ) {
            System.err.print("Unable to load hanlers list 'uak.properties'.");
        }
    }
    protected void init( ChannelDataSource source ) throws Exception {
        this.source = source;
        source.addSignalListener(this);
        //output = new ChannelDataSource();
        channels.add( source.getChannel( "sensors" ));
        channels.add( source.getChannel( "values" ));
        //chan1 = addChannel( "angles", 0, 3 );
        //chan2 = addChannel( "angles2", 1, 24 );
    }

    public ChannelDataSource getOutputSource() {
        return this;
    }

    public void signalEvent(SignalEvent ev){
        if(ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
            process();
        }

    }

    public void process() {
        Channel chan = source.getChannel( "sensors" );
        if ( chan == null ) {
            return;
        }
        String s = source.getProperty("id");

        if(s == null) {
            return;
        }
        // берем данные только с новым id
        if(s.equals(id)) {
            return;
        }
        id = s;
        System.out.println("*"+ s );
        // значения по датчикам текущие и предыдущие
        //for ( int i = 0; i < 12; i++ ) {
        //    input[i] = input[i + 12]; // сохраняем предыдущие
        //    input[i + 12] = chan.getValue( i ).getAsInteger();
        //}
        //computer.calc( input, angles2, out );
        //fillValues();
        hasNewData = true;
        synchronized (this) {
            notify();
        }
        sendSignal( new SignalEvent( SignalEvent.SIG_DATA_ARRIVED, this) );
   }

}
