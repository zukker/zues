package lsdsoft.metrolog;

import java.util.*;
import org.w3c.dom.Node;
import lsdsoft.util.XMLUtil;
import lsdsoft.util.*;

/**
 * <p>Graduation based on standard properties.</p>
 * Main param is 'func' where sits function of conversion
 * from one value to other. This function is formula, which contain variables, stored
 * in same properties.
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class BaseGraduation implements Graduation, XMLStorable {
    protected Properties props = new Properties();
    public BaseGraduation() {
    }
    public void setParameter(String param, String value) {
        props.setProperty(param, value);
    }
    public String getParameter(String param) {
        return props.getProperty( param, "" );
    }
    public Properties getParameters() {
        return props;
    }
    public void setParameters(Properties params) {
        props = params;
    }
    public double calc() {
        return 0;
    }
    public void load( Node parentNode ) {
        XMLUtil.loadAttributesFromNode( parentNode, props );
    }
    public void save( Node parentNode ) {
        XMLUtil.saveAttributesToNode(parentNode, props);
    }

}
