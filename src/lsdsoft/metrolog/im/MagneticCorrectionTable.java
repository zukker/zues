/**
 * Created: 26-okt-2004
 */
package lsdsoft.metrolog.im;

import com.lsdsoft.math.*;
import lsdsoft.units.*;
import org.w3c.dom.*;
import lsdsoft.util.*;
import java.util.*;

public class MagneticCorrectionTable {
    private static final String TAG_MAGNETIC = "magnetic";
    private static final String TAG_AZIMUTHS = "azimuths";
    private static final String TAG_ZENITHS = "zeniths";
    private static final String TAG_VAL = "val";
    private static final String TAG_CORRECTION = "correction";
    private SurfaceInterpolation surface = null;
//  IMParser parser;

    boolean loaded;
//    private void load( void * buf, int size );

    //char* parseFloat(char* buffer,
    int pos;
    int size;
    boolean isEOS() {
        return false;
    }

    public MagneticCorrectionTable() {

    }

    public MagneticCorrectionTable( String num ) {

    }

/*    public void load( String fileName ) {
        Document doc = DataStorage.

    }
*/
    public void load( Node parentNode ) {
        int countX = 0, countY = 0, count;
    Element parentElem = ( Element ) parentNode;
    Element elem;
    NodeList list = parentElem.getElementsByTagName( TAG_AZIMUTHS );
    NodeList list2 = null, list3 = null;
    if(list.getLength() > 0) {
        list2 = ((Element)list.item(0)).getElementsByTagName(TAG_VAL);
        countX = list2.getLength();
    }
    list = parentElem.getElementsByTagName( TAG_ZENITHS );
    if(list.getLength() > 0) {
        list3 = ((Element)list.item(0)).getElementsByTagName(TAG_VAL);
        countY = list3.getLength();
    }
    if( countX * countY > 0 ) {
        surface = new SurfaceInterpolation( countX, countY );
        for( int i  = 0; i < list2.getLength(); i++ ) {
            String s = XMLUtil.getTextTag(list2.item(i));
            try {
                surface.setGridX( i, Double.parseDouble( s ));
            } catch ( NumberFormatException ex ) {
                System.err.println(ex);
            }

        }
        for( int i  = 0; i < list3.getLength(); i++ ) {
            String s = XMLUtil.getTextTag(list3.item(i));
            try {
                surface.setGridY( i, Double.parseDouble( s ));
            } catch ( NumberFormatException ex ) {
                System.err.println(ex);
            }

        }
        String s = XMLUtil.getTextTag( parentNode, TAG_CORRECTION );
        StringTokenizer st = new StringTokenizer( s );
        for( int y = 0; y < countY; y++) {
            for( int x = 0; x < countX; x++) {
                if( st.hasMoreTokens() ) {
                    String t = st.nextToken();
                    surface.setValue(x, y, Double.parseDouble( t ));
                }
            }
        }

    }
    }
/*
        TXmlDDocument * xmlDoc = new TXmlDDocument;
        TXmlDNode * node;
        TXmlDElement * elem;
        xmlDoc - > Load( fileName );

        node = xmlDoc - > SelectSingleNode( tagMagnetic );
        if ( node ) {
            TXmlDNodeList * list1, * list2;
            TXmlDNode * node2;
            node2 = node - > SelectSingleNode( tagAzimuts );
            list1 = node2 - > SelectNodes( tagVal );
            if ( list1 )
                countX = list1 - > Count;
            node2 = node - > SelectSingleNode( tagZenits );
            list2 = node2 - > SelectNodes( tagVal );
            if ( list2 )
                countY = list2 - > Count;
            if ( countX * countY != 0 ) {
                surface = new SurfaceInterpolation( countX, countY );
                // setting x values (azimuts)
                for ( int i = 0; i < countX; i++ )
                    surface - >
                        setGridX( i, atof( list1 - > Items[i] - > Text.c_str() ) );
                    //}
                    // setting zenits
                    //node2 = node->SelectSingleNode(tagZenits);
                    //list = node2->SelectNodes(tagVal);
                    //if(list) {
                    //countY = list->Count;
                for ( int i = 0; i < countY; i++ )
                    surface - >
                        setGridY( i, atof( list2 - > Items[i] - > Text.c_str() ) );
                node2 = node - > SelectSingleNode( tagCorrection );
                if ( node2 ) {
                    char * buf = node2 - > Text.c_str();
                    int count = 0;
                    for ( ; ; ) {
                        char * p;
                        double value = strtod( buf, & p );
                        if ( ( buf == p ) || ( p == 0 ) )
                            break;
                        surface - >
                            setValue( count % countX, count / countX, value );
                        buf = p;
                        count++;
                    }
                }
            }
        }
        if ( surface )
            surface - > preCalc();
        delete xmlDoc;
    }
*/
    /*
     void MagneticCorrectionTable::load(void * buffer, int size) {
      char* buf = (char*)buffer;
     }
     */

    // loads table from text file
//  void load();
//  void load(AnsiString fileName);
//  bool isLoaded() { return loaded; }
    public void correct( InclinometerAngles angles ) {
        if ( surface == null )
            return;
        Angle z = angles.zenith.abs();
        z.normalize();
        Angle a = angles.azimuth;
        //a.normalize();
        angles.azimuth.add( new Angle(surface.calc( a.getValue(), z.getValue() )));
    }
}




