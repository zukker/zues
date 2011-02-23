package lsdsoft.zeus.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class HtmlViewer extends JFrame {
    JScrollPane jScrollPane1 = new JScrollPane();
    JEditorPane editor = new JEditorPane();
    public HtmlViewer() throws HeadlessException {
        try {
          jbInit();
        }
        catch(Exception e) {
          e.printStackTrace();
        }
    }
    private void jbInit() throws Exception {
      editor.setEditable(false);
      this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(editor, null);
      this.setTitle("Просмотр отчета");
    }
    public void setText(String text) {
        editor.setContentType("text/html");
        editor.setText(text);
    }
    public void loadText( String fileName ) throws Exception {
        File fl = new File( fileName );
        FileInputStream file = new FileInputStream( fl );
        InputStreamReader ins = new InputStreamReader( file, "Windows-1251" );
        char[] buf = new char[ (int)fl.length()];
        ins.read( buf, 0, (int)fl.length() );
        ins.close();
        file.close();
        setText( new String( buf ) );


    }
    public void view() {
        this.setVisible(true);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        jScrollPane1.getVerticalScrollBar().setValue(0);
        jScrollPane1.getVerticalScrollBar().validate();
    }
}