package lsdsoft.zeus.methods;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class CommandEntry {
    //internal comannd name
    String name = null;
    // Bean shell command
    String shellCommand = null;
    // description of command (full notation)
    String description = null;
    // short description of command (title)
    String title = null;

    public CommandEntry(String name, String comm, String desc, String title) {
        this.name = name;
        this.shellCommand = comm;
        this.description = desc;
        this.title = title;
    }
    public String getName() {
        return name;
    }
    public String getShellCommand() {
        return shellCommand;
    }
    public String getDescription() {
        return description;
    }
    public String getTitle() {
        return title;
    }

}