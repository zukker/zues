package lsdsoft.zeus;


import java.util.*;

import javax.swing.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class AbstractMethodsViewer
    extends JFrame
    implements MethodsViewer {
    protected Properties properties = null;
    protected WorkState workState = null;
    public AbstractMethodsViewer() {
        super();
    }
    public void setProperties( Properties props ) {
        this.properties = ( Properties ) props.clone();
    }

    public void setWorkState( WorkState workState ) {
        try {
            this.workState = ( WorkState ) workState.clone();
        } catch ( CloneNotSupportedException ex ) {
            System.err.println( ex );
        }
    }

    public void start() {

    }

}