package lsdsoft.units;
import java.lang.*;
import java.text.*;
import java.util.Locale;


// Angle of rotate. value in degrees.
// Angle::angle contain value in seconds of angle

public class Angle {
  public static final int SECONDS_PER_MINUTE = 600;
  public static final int SECONDS_PER_DEGREE = 36000;
  public static final int MINUTES_PER_DEGREE = 60;
  public static final int SECONDS_360DEGREE = 360 * SECONDS_PER_DEGREE;
  public static final double ONE_RADIAN = 57.2957795130823209;

  private int angle;

  public Angle() {
    angle = 0;
  }

  public Angle(int aAngle) {
    angle = aAngle * SECONDS_PER_DEGREE;
  }

  public Angle(int degree, int minute, int second) {
    angle = degree * SECONDS_PER_DEGREE + minute * SECONDS_PER_MINUTE + second;
  }

  public Angle(double aAngle) {
    setAngle(aAngle);
  }
  // operator +=(angle)
  public Angle add(Angle ang) {
    angle += ang.angle;
    return this;
  }
  public Angle sub(Angle ang) {
    angle -= ang.angle;
    return this;
  }
  // operator /=
  public Angle div(double val) {
    angle = (int)((double)angle / val);
    return this;
  }

  // operator -
  public Angle minus(Angle ang) {
    return new Angle(0, 0, angle - ang.angle);
  }
  // operator +
  public Angle plus(Angle ang) {
    return new Angle(0, 0, angle + ang.angle);
  }

  public int cmp(Angle ang) {
    return angle - ang.angle;
  }

  // operator <
  public boolean ls(Angle ang) {
    return angle < ang.angle;
  }
  // operator >
  public boolean gt(Angle ang) {
    return angle > ang.angle;
  }
  // operator ==
  public boolean eq(Angle ang) {
    return angle == ang.angle;
  }
  // operator !=
  public boolean neq(Angle ang) {
    return angle != ang.angle;
  }

  public Angle abs() {
    if(angle < 0) angle = -angle;
    return this;
  }

  public final Angle abs(Angle ang) {
    ang.angle = 0;
    return ang;
  }

  public void normalize() {
    while(angle < 0) angle += SECONDS_360DEGREE;
     angle %= SECONDS_360DEGREE;
  }
  public int getAngle() {
    return angle;
  }
  public int getDegrees() {
    return angle / SECONDS_PER_DEGREE;
  }
  public int getMinutes() {
    return (Math.abs(angle) % SECONDS_PER_DEGREE) / MINUTES_PER_DEGREE;
  }
  public int getSeconds() {
    return Math.abs(angle) % SECONDS_PER_MINUTE;
  }
  public double getValue() {
//    return ((double)getDegrees())
//         + (double)(angle % SECONDS_PER_DEGREE)
//         / (double)SECONDS_PER_DEGREE;
    return ((double)angle / (double)SECONDS_PER_DEGREE);
  }

  public void setAngle(int ang) {
    angle = ang;
  }

  public void setAngle(double aAngle) {
  boolean neg = aAngle < 0;
    if(neg) aAngle = -aAngle;
    angle = (int)aAngle;
    int sec = (int)((aAngle - (double)angle) * ((double)SECONDS_PER_DEGREE));
    angle *= SECONDS_PER_DEGREE;
    angle += sec;
    if(neg) angle = -angle;
  }

  public void toString(String str) {
    //str.sprintf("%d\xb0%d'%d\x22", getDegrees(), getMinutes(), getSeconds());
  }
  public String toString() {
    String str = "";
    double number = getValue();
    NumberFormat form = NumberFormat.getInstance();
    form.setMaximumFractionDigits(3);
    form.setMaximumFractionDigits(3);
    str = form.format(number);

    //str.sprintf("%d\xb0%d'%d\x22", getDegrees(), getMinutes(), getSeconds());
    return str;
  }

  public String toShortString() {
    String str = "";
    double number = getValue();
    DecimalFormat form = (DecimalFormat)NumberFormat.getInstance(Locale.US);
    form.applyPattern("#.#");
    //form.setMaximumFractionDigits(3);
    //form.getDecimalFormatSymbols().setDecimalSeparator('.');
    str = form.format(number);
    //str.sprintf("%d\xb0%d'", getDegrees(), getMinutes());
    return str;
  }

  public String toFloatString() {
    String str = "";
    double number = getValue();
    DecimalFormat form = (DecimalFormat)NumberFormat.getInstance(Locale.US);
    //form.applyPattern("#.###");
    form.setMaximumFractionDigits(3);
    str = form.format(number);
    return str;
  }

  public String toShortFloatString() {
    String str = "";
    //str.sprintf("%.0f\xb0", getValue());
    return str;
  }
  public static double norm( double value ) {
      while( value < 0 ) value += 360.0;
      while( value >= 360.0 ) value -= 360.0;
      return value;

  }

};

