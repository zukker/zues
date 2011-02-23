package lsdsoft.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class Util {
    public Util() {
    }

    public static void delay( int milli ) {
        try {
            Thread.sleep( milli );
        } catch ( Exception ex ) {}
    }

}