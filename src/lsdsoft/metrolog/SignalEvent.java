package lsdsoft.metrolog;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class SignalEvent {
    private int signal;
    private SignalSource source = null;
    public static final int SIG_DATA_ARRIVED = 1;
    public static final int SIG_TIMEOUT = 2;
    public static final int SIG_ERROR = 3;

    public SignalEvent( int sig, SignalSource source ) {
        signal = sig;
        this.source = source;
    }

    public int getSignal() {
        return signal;
    }

    public SignalSource getSource() {
        return source;
    }
}
