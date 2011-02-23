package lsdsoft.util;

import lsdsoft.util.Polynomial;
import lsdsoft.util.GradChar;
import lsdsoft.util.Function;

/**
 * <p>Title: polynomial grade characteristic</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CMI Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class PolyGradChar extends Polynomial implements GradChar, Function{
  public PolyGradChar() {
    super();
  }
  public PolyGradChar(int degree) {
    super(degree);
  }
  public double Calc(double Value) {
    return super.Calc(Value);
  }
}