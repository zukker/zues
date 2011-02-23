package com.lsdsoft.comm;

public class CommConnectionException extends Exception {

    /**
     * Constructs a <code>SerialConnectionException</code>
     * with the specified detail message.
     *
     * @param   s   the detail message.
     */
    public CommConnectionException(String str) {
        super(str);
    }

    /**
     * Constructs a <code>CommConnectionException</code>
     * with no detail message.
     */
    public CommConnectionException() {
        super();
    }
}
