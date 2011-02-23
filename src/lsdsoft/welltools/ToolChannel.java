package lsdsoft.welltools;

import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class ToolChannel {
    protected String id;
    protected String name = "";
    public ToolChannel() {
    }
    public ToolChannel( String id ) {
        this.id = id;
    }
    public String getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}