/**
 *  Connection to communication port (serial or parallel)
 */

package com.lsdsoft.comm;


import java.util.*;
import javax.comm.*;


public class CommConnection
    implements Connection {
    protected boolean hasConnect = false;
    protected CommPortIdentifier commIdent = null;
    protected CommPort port = null;
    protected String portName = "COM1";
    public CommConnection() {
    }

    public CommConnection( String portName ) {
        setPortName( portName );
    }

    public static ArrayList getPortNames() {
        ArrayList list = new ArrayList( 4 );
        Enumeration portIdents = CommPortIdentifier.getPortIdentifiers();
        while ( portIdents.hasMoreElements() ) {
            CommPortIdentifier portID = ( CommPortIdentifier ) portIdents.nextElement();
            list.add( portID.getName() );
        }
        return list;
    }
    public static String[] getStringPortNames() {
        String names[];
        ArrayList list = getPortNames();
        int size = list.size();
        names = new String[size];
        for(int i =0; i < size; i++) {
            names[i] = (String)list.get(i);
        }

        return names;
    }


    public void setPortName( String portName ) {
        this.portName = portName;
    }

    public String getPortName() {
        return portName;
    }

    /**
     * Return instance of comm port.<br>
     * if port opened it return not null object
     */
    public CommPort getPort() {
        return port;
    }

    /**
     * Check for connection
     */
    public boolean isConnected() {
        //commIdent.
        return hasConnect;
    }

    /**
     * Make connection to assosiated port name
     */
    public void connect() throws Exception {
        if ( commIdent == null ) {
            //ArrayList list = CommConnection.getPortNames();
            try {
                commIdent = CommPortIdentifier.getPortIdentifier( portName );
                if(commIdent.isCurrentlyOwned()) {
                    System.out.println("port owned by " + commIdent.getCurrentOwner());
                }
                port = commIdent.open( "UAKSI", 500 );
                hasConnect = true;
            } catch ( PortInUseException ex ) {
                commIdent = null;
                throw new CommConnectionException("Порт '" + portName + "' занят другим приложением: " + ex.currentOwner);
            } catch (NoSuchPortException ex) {
                commIdent = null;
                throw new CommConnectionException("Не найден порт: " + portName);
            }
        }
    }

    public void disconnect() {
        if ( hasConnect ) {
            port.close();
            port = null;
            commIdent = null;
            hasConnect = false;
        }
    }
};
