package lsdsoft.util;

import lsdsoft.util.Polynomial;
import lsdsoft.util.GradChar;
import lsdsoft.util.Function;
import java.awt.Color;
import java.awt.Graphics;

/**
 * <p>Title: график функции</p>
 * <p>Description: класс прорисовки функции</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CMI Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class Chart {

  private double UpX;
  private double LowX;

  private double UpY = 0;
  private double LowY = 0;
  public int Thickness = 1;
  public Color color;
  public double Divider = 30;
  public Function func;
  public Chart(Function f) {
    func = f;
  }

  public void paint(ChartCanvas c) {
    //Graphics g = c. graph;
  }
}