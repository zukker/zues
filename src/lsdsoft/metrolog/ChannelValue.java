package lsdsoft.metrolog;

import lsdsoft.units.*;

  public class ChannelValue {
    public static final int CV_INTEGER = 0;
    public static final int CV_FLOAT = 1;
    public static final int CV_ANGLE = 2;
    public static final int CV_BOOLEAN = 3;
    public static final int CV_VALUE = 4;
    private int type;
    //private Angle angle = new Angle();
    private int intValue;
    private double doubleValue;
    private boolean booleanValue;
    private Value mvalue = new Value();
    private boolean updated = false;

    public void setAsInteger( int value ) {
        type = CV_INTEGER;
        intValue = value;
        doubleValue = value;
        mvalue.value = value;
        mvalue.delta = 0;
        booleanValue = value != 0;
        updated = true;
    }
    public void setAsDouble( double value ) {
        type = CV_FLOAT;
        intValue = (int)value;
        doubleValue = value;
        booleanValue = intValue != 0;
        mvalue.value = value;
        mvalue.delta = 0;
        updated = true;
    }

    public void setAsValue( double value, double delta ) {
        type = CV_VALUE;
        intValue = (int)value;
        doubleValue = value;
        booleanValue = intValue != 0;
        mvalue.value = value;
        mvalue.delta = delta;
        updated = true;
    }
    public int getAsInteger() {
        return intValue;
    }
    public double getAsDouble() {
        return doubleValue;
    }
    public boolean getAsBoolean() {
        return booleanValue;
    }
    public Value getAsValue() {
        return mvalue;
    }
    public void pick() {
        updated = false;
    }
    public boolean isUpdated() {
        return updated;
    }
    //public ChannelValue() {}
  };
