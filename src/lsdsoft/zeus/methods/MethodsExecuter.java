package lsdsoft.zeus.methods;

import java.util.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
//import bsh.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class MethodsExecuter {
    Date startTime;
    //MeasureDatas datas = null;
    //AbstractCheckUnit unit = null;
    //ChannelDataSource toolSource = null;
    //Interpreter shell = new Interpreter();
    Script script = new Script();
    // list of available commands, item of CommandEntry type
    List commandList = new ArrayList();
    //CommandExecuter commandExecuter;

    public MethodsExecuter() {

    }
    //public Interpreter getShell() {
    //    return shell;
    //}

    public void loadScript(String script) {
        try {
            this.script.load( script );
        } catch ( Exception ex ) {
        }
    }

    public List getCommandList() {
        return commandList;
    }
    public void executeCommand(String command ) {

    }

}
