package lsdsoft.metrolog;

import java.text.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class Value {
    static final double degs[] = {1, 10, 100, 1000, 10000, 100000, 1000000 };
    public double value;
    public double delta;
    public Value() {
        value = 0;
        delta = 0;
    }
    public Value(String val) {
        int i = val.indexOf('@');
        if(i > 0) {
            value = Double.parseDouble(val.substring(0, i ));
            delta = Double.parseDouble(val.substring( i + 1));
        } else {
            value = Double.parseDouble( val );
            delta = 0;
        }
    }

    public Value(double val, double del) {
        value = val;
        delta = del;
    }
    public void add(Value val) {
        value += val.value;
        delta += val.delta;
    }
    public String toString() {
        return toString(2);
    }
    public String toString(int frac) {
        if(frac < 0) {
            frac = 0;
        }
        if(frac >= degs.length ) {
            frac = degs.length - 1;
        }
        double div = degs[frac];
        String s = Double.toString(Math.floor(value * div + 0.5) / div );
        if(delta > 0) {
            s += "@" + Double.toString(Math.floor(delta * div + 0.5) / div );
        }
        return s;
    }
    public String toHtml(int frac) {
        NumberFormat nf = DecimalFormat.getNumberInstance();
        nf.setMaximumFractionDigits(frac);
        if(frac < 0) {
            frac = 0;
        }
        if(frac >= degs.length )
            frac = degs.length - 1;
        double div = degs[frac];
        //String s = Double.toString(Math.floor(value * div + 0.5) / div );
        String s = nf.format(Math.floor(value * div + 0.5) / div );
        if(delta > 0) {
            s += " &plusmn; " + nf.format(Math.floor(delta * div + 0.5) / div );
        }
        return s;
    }
}
