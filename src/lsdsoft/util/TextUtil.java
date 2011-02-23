package lsdsoft.util;


import java.text.*;
import java.util.*;


/**
 * <p>Title: util set for mtrolog</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CMI Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class TextUtil {
    private static String defaultDatePattern = "yyyy.MM.dd HH:mm:ss z";
    public final char PLUS_MINUS = '\u0177';
    /**
     */
    public static void toCapitalize( String str ) {
        char First = Character.toLowerCase( str.charAt( 0 ) );
        str.toLowerCase();
        str.toCharArray()[0] = First;
    }
    public static String millisToString( long millis ) {
        Date date = new Date(millis);
        SimpleDateFormat df = new SimpleDateFormat( "HH:mm:ss" );
        df.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        String s = df.format( date );
        return s;
    }

    public static String dateToString( Date date, String pattern ) {
        SimpleDateFormat df = new SimpleDateFormat( pattern );
        String s = df.format( date );
        return s;
    }

    public static String dateToString( GregorianCalendar date, String pattern ) {
        SimpleDateFormat df = new SimpleDateFormat( pattern );
        String s = df.format( date );
        return s;
    }

    public static String dateToString( Date date ) {
        return dateToString( date, defaultDatePattern );
    }

    public static Date stringToDate( String dateStr, String pattern ) {
        SimpleDateFormat df = new SimpleDateFormat( pattern );
        df.setLenient( true );
        Date date = null;
        try {
            date = df.parse( dateStr );
        } catch ( ParseException ex ) {
            date = new Date();
        }
        return date;
    }

    public static Date stringToDate( String dateStr ) {
        return stringToDate( dateStr, defaultDatePattern );
    }
}
