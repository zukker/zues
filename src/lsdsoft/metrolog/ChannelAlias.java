package lsdsoft.metrolog;

import java.util.Hashtable;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class ChannelAlias extends Hashtable {
    public ChannelAlias() {
    }

    public class AliasEntry {
        String name;
        int index;
        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
    };
    public void buildFromProperties( Properties props ) {
        clear();
        int size = props.size();
        Enumeration keys = props.keys();
        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = props.getProperty(key);
            if( value == null )
                continue;
            for(int i = 1; i < value.length(); i++ ) {
                if( value.charAt(i) == '.' ) {
                    AliasEntry entry = new AliasEntry();
                    entry.name = value.substring( 0, i );
                    entry.index = Integer.parseInt( value.substring( i + 1 ), 10 );
                    put(key, entry);

                }
            }
            /*
            String[] strs = value.split(".");
            if(strs.length == 2 ) {
                AliasEntry entry = new AliasEntry();
                entry.name = strs[0];
                entry.index = Integer.parseInt( strs[1], 10 );
                put(key, entry);
            }
                */
        }

    }
    public AliasEntry getAlias( String key ) {
        return (AliasEntry) this.get(key);
    }
}