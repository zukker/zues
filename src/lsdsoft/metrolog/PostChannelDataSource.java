package lsdsoft.metrolog;

import java.util.regex.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class PostChannelDataSource extends ToolDataSource {
  private Channel sensors;
  private Channel values;
  private int id = 0;
  public PostChannelDataSource() {
    // only 2 channels
    sensors = addChannel("sensors", 0, 32);
    values = addChannel("values", 1, 32);
  }
  public int parse(String str) {
    String[] args = str.split(" ");
    String[] exp;
    if(args.length == 0)
      return -1;
    if(!args[0].equals("post"))
      return -1;
  for ( int i = 1; i < args.length; i++ ) {
      // split string "var=value"
      exp = args[i].split( "=" );
      if ( exp.length < 2 )
          continue;
      if ( Pattern.matches( "sens\\d+", exp[0] ) ) {
          int index = Integer.parseInt( exp[0].substring( 4 ) );
          int value = Integer.parseInt( exp[1] );
          ChannelValue val = new ChannelValue();
          val.setAsInteger( value );
          //val.type = ChannelValue.CV_CODE;
          //val.code = value;
          if ( index < sensors.getSubCount() )
              sensors.setValue( index, val );
      } else
      if ( Pattern.matches( "val\\d+", exp[0] ) ) {
          try {
              int index = Integer.parseInt( exp[0].substring( 3 ) );
              double value = Double.parseDouble( exp[1] );
              ChannelValue val = new ChannelValue();

              //val.setAsValue( value, nerr );
              val.setAsDouble( value );

              if ( index < values.getSubCount() ) {
                  values.setValue( index, val );
              }
          } catch ( IndexOutOfBoundsException ex1 ) {
          } catch ( NumberFormatException ex1 ) {
          }

      } else {
          properties.setProperty( exp[0], exp[1] );
      }

  }

  // добавление нормы для приборов ИММН-36, -38, -60, -73a
  double nerr = 0;
  String toolType = properties.getProperty( "tooltype" );
  //System.out.println("toolType:" + toolType);
  if ( toolType.equals( "immn36" ) ||
       toolType.equals( "immn38" ) ||
       toolType.equals( "immn60" ) ||
       toolType.equals( "immn73a" ) ) {
      values.getValue(0).getAsValue().delta = 0.25;
      double zenith = values.getValue(0).getAsDouble();
      if( zenith < 1) {
          nerr = 60;
      } else if( zenith < 2) {
          nerr = 30;
      } else if( zenith < 3) {
          nerr = 10;
      } else if( zenith < 7) {
          nerr = 3;
      } else {
          nerr = 1.5;
      }
      values.getValue(1).getAsValue().delta = nerr;
      values.getValue(2).getAsValue().delta = 3;
  }

    String s = properties.getProperty("id");
    // if id changed then we have new data arrived
    if ( s != null ) {
        try {
            int i = Integer.parseInt( s.trim(), 10 );
            if ( i != id ) {
                id = i;
                hasNewData = true;
            }
        } catch ( Exception ex ) {
            System.err.print( "Invalid ID in stream" );
        }
    }
    return 1;
  }
}
