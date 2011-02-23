package lsdsoft.metrolog;

import java.util.*;
import com.lsdsoft.comm.*;
import lsdsoft.zeus.*;

public class ChannelDataSource extends SignalSource
    implements DataSource, Connection {
  protected String UID = "datasource.channel";
  protected Properties properties = new Properties();
  //protected ArrayList eventListeners = new ArrayList(4);
  protected ArrayList channels = new ArrayList(16);
  protected ChannelAlias aliases = null;
  //protected int channelCount;
  protected boolean conn = false;
  public boolean hasNewData = false;
  public String id = "";


  public ChannelDataSource() {
      init(2);
  }

  ChannelDataSource(int channelCount) {
      init(channelCount);
  }

  public void init(int channelCount) {
    channels = new ArrayList(channelCount);
  }
  public Channel addChannel(String name, int id, int subCount) {
    Channel channel = new Channel(name, id, subCount);
    channels.add(channel);
    return channel;
  }
  public void addChannel(Channel channel) {
    channels.add(channel);
  }
  /**
   * Поиск канала по указанному имени, поиск по точному совпадению имен
   * @param name имя канала, который надо найти
   * @return null если канал не найден, если найден - объект Channel
   */
  public Channel getChannel(String name) {
    Channel channel = null;
    for(int i = 0; i < channels.size(); i++) {
        if ( name.equals( ( ( Channel )channels.get( i ) ).getName() ) ) {
            channel = ( Channel )channels.get( i );
            break;
        }
    }
    return channel;
  }
  /**
   * Поиск канала по указанному идентификатору канала
   * @param id
   * @return объект Channel если найден канал, null если не найден канал
   */
  public Channel getChannel(int id) {
    Channel channel = null;
    for(int i = 0; i < channels.size(); i++)
      if(id == ((Channel)channels.get(i)).getID()) {
        channel = (Channel) channels.get(i);
        break;
      }
    return channel;
  }

  public ChannelValue getValue( String channel, int index ) {
      Channel chan = getChannel( channel );
      if( chan == null ) {
          return null;
      }
      return chan.getValue( index );
  }

  public ChannelValue getValue( String alias ) {
      if( aliases == null )
          return null;
      ChannelAlias.AliasEntry entry = aliases.getAlias( alias );
      Channel chan = getChannel( entry.getName() );
      if( chan == null ) {
          return null;
      }
      return chan.getValue( entry.getIndex() );
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperty(String key, String value) {
    properties.setProperty(key, value);
  }
  public String getProperty(String key) {
    return properties.getProperty(key);
  }
  public String getUID() {
    return UID;
  }
  public void connect() throws Exception{
    conn = true;
  }
  public void disconnect() {
    conn = false;
  }
  public boolean isConnected() {
    return conn;
  }
  //public void addEventListener(ChannelDataEventListener evListener) {
  //  if(!eventListeners.contains(evListener))
  //    eventListeners.add(evListener);
 // }
  //public void removeEventListener(ChannelDataEventListener evListener) {
  //  eventListeners.remove(evListener);
  //}
  //public void removeAllListeners() {
  //  eventListeners.clear();
  //}
  //public void sendEvent(ChannelDataEvent event) {
  //  for(int i = 0; i < eventListeners.size(); i++) {
  //    ((ChannelDataEventListener)eventListeners.get(i)).channelEvent(event);
  //  }
  //}
  public int getChannelCount() {
    return channels.size();
  }

  public void setAliases( ChannelAlias alias ) {
      aliases = alias;
  }

  public ChannelAlias getAliases() {
      return aliases;
  }
  /** @todo rethink this */
  public synchronized void waitNewData() {
      String str;
      str = Zeus.getInstance().getProperty("debug", "off");
      if (str.equals("on")) return;
      if(hasNewData) {
          hasNewData = false;
          return;
      }
      try {
          while(!hasNewData) {
              Thread.sleep(100);
          }
          //wait( 5000 );
      } catch ( InterruptedException ex ) {
      }
  }


};


