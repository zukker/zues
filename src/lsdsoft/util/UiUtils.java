package lsdsoft.util;


import javax.swing.*;
import java.awt.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class UiUtils {
    public static void showSystemError( Component parent, String message ) {
        JOptionPane.showMessageDialog( parent, message,
                                       "Системная ошибка ",
                                       JOptionPane.ERROR_MESSAGE );
    }

    public static void showError( Component parent, String message ) {
        JOptionPane.showMessageDialog( parent, message,
                                       "Ошибка ",
                                       JOptionPane.ERROR_MESSAGE );
    }

    public static int showConfirmError( Component parent, String message ) {
        return JOptionPane.showConfirmDialog( parent, message,
                                              "Ошибка ",
                                              JOptionPane.YES_NO_OPTION );
    }

    public static void toScreenCenter( Component component ) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = component.getSize();
        component.setLocation( ( screenSize.width - frameSize.width ) / 2,
                               ( screenSize.height - frameSize.height ) / 2 );

    }
}