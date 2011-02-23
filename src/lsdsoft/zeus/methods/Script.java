package lsdsoft.zeus.methods;

import java.util.*;
import lsdsoft.util.*;
import org.w3c.dom.*;
import lsdsoft.zeus.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class Script implements XMLStorable{
    private static final String TAG_SOURCE = "source";
    private static final String TAG_COMMANDS = "commands";
    private static final String TAG_COMMAND = "command";
    private static final String TAG_NAME = "name";
    private static final String TAG_TITLE = "title";
    private static final String TAG_SHELL_COMMAND = "shellcommand";
    private static final String TAG_DESC = "desc";

    /**
     * List of available command in script
     * Theese commands may executed separately
     */
    protected List commandList = new ArrayList();
    /** source file of Bean shell script
     before executing commands need prepare this file by calling shell.source().
     */
    protected String source = null;
    public Script() {
    }
    public void addCommand( CommandEntry com ) {
        commandList.add(com);
    }
    /**
     *
     * @return
     */
    public List getCommandList() {
        return commandList;
    }
    /**
     *
     * @return
     */
    public String getSource() {
        return source;
    }
    /**
     *
     * @param source
     */
    public void setSource( String source ) {
        this.source = source;
    }
    /**
     * Loading script file. File placed into commands folder.
     * This folder defined (short form) in Zeus properties by key "commands.dir"
     * Full path defined by key "commands.fulldir".
     * File is XML of doctype ...
     * @param filename short name of script file
     * @throws Exception IO ex, parsing ex
     */
    public void load( String filename ) throws Exception {

    }
    /**
     * Loading script from DOM node
     * @param parentNode root node of documnent, from which script loaded
     */
    public void load(Node parentNode) {
    }
    /**
     *
     * @param filename
     * @throws Exception
     */
    public void save( String filename ) throws Exception {
        DataStorage ds = DataFactory.getDataStorage();
        Document doc = ds.createXMLDocument();
        save(doc.getDocumentElement());
        ds.formatXML(doc, filename );
    }
    /**
     *
     * @param parentNode
     */
    public void save(Node parentNode) {
        Document doc = parentNode.getOwnerDocument();
        Element elem;
        int size = commandList.size();
        elem = doc.createElement( TAG_SOURCE );
        parentNode.appendChild( elem );
        //state.save(elem);
        elem = doc.createElement( TAG_COMMANDS );
        parentNode.appendChild( elem );
        for ( int i = 0; i < size; i++ ) {
            Element e = doc.createElement( TAG_COMMAND );
            CommandEntry c = (CommandEntry)commandList.get(i);
            XMLUtil.addTextTag(e, TAG_NAME, c.name);
            XMLUtil.addTextTag(e, TAG_SHELL_COMMAND, c.shellCommand);
            XMLUtil.addTextTag(e, TAG_TITLE, c.title);
            XMLUtil.addTextTag(e, TAG_DESC, c.description);
            //MeasureTable table = getTable( i );
            //table.save( e );
            elem.appendChild( e );
        }

    }

}