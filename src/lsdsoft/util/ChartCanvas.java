package lsdsoft.util;

import java.awt.Graphics;
import lsdsoft.util.Polynomial;
import lsdsoft.util.GradChar;
import lsdsoft.util.Function;
import java.awt.Canvas;

/**
 * <p>Title: холст для прорисовки графиков функций</p>
 * <p>Description: класс, куда прорисовывается функция. Содержит размеры, начало координат</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CMI Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 */

public class ChartCanvas {
  private Canvas canvas = null;
  private Chart[] charts = null;
  private int Width =  0;
  private int Height = 0;
  private double ScaleX = 1;
  private double ScaleY = 1;

  public ChartCanvas(Canvas c) {
    canvas = c;
  }
  public void resize() {
  }
  public void paintAxis() {
  }
  public void paint() {
    for(int i = 0; i < charts.length; i++)
      ;
//      charts[i].paint(canvas);
  }
}