package com.lsdsoft.comm;


public interface Connection {
    boolean isConnected();

    void connect() throws Exception;

    void disconnect();
};
