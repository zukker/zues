package com.amper.gui;

import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Amper 2010</p>
 * @author de-nos
 * @version 1.0
 */
public class ExMetalLookAndFeel extends MetalLookAndFeel{
    public String getName() { return "ExMetal"; }

    public ExMetalLookAndFeel() {
        setCurrentTheme(new OceanTheme(){
            public FontUIResource getControlTextFont() {
                return new FontUIResource("Tahoma", Font.PLAIN, 12);
            }
        });
    }
}

