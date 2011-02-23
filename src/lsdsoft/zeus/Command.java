package lsdsoft.zeus;


import java.lang.reflect.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class Command {
        protected Class[] params = {};

        String description;
        String name;
        String method;
        boolean executed = false;
        boolean done = false;
        AbstractMethodsViewer viewer;
        public Command( AbstractMethodsViewer viewer, String method, String name,
                        String desc ) {
            this.viewer = viewer;
            this.method = method;
            this.name = name;
            this.description = desc;
        }

        public void execute() throws Exception {
            //Method[] meths = viewer.getClass().getMethods();
            Method method = viewer.getClass().getMethod( this.method, params );
            if ( method != null ) {
                executed = true;
                done = false;
                method.invoke( viewer, null );
                executed = false;
                done = true;
            }

        }

        public String toString() {
            return name;
        }
    }



