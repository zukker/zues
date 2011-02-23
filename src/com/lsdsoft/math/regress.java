package com.lsdsoft.math;
public class regress {
  /**
   * Линейная регрессия
   * @param N количество входных точек
   * @param X входные значения параметра
   * @param Y значения y(х)
   * @param B выходные коэффициенты
   */
  public static void RegressLine(int N, double[] X, double[] Y, double[] B) {
    double sx = 0, sy = 0, sxy = 0, sx2 = 0;
    double n = N;
    for(int i = 0; i < N; i++) {
      sx += X[i];
      sy += Y[i];
      sxy += X[i] * Y[i];
      sx2 += X[i] * X[i];
      }
    if(sx2 == 0) return;
    B[1] = (sx * sy - n * sxy) / (sx * sx - n * sx2);
    B[0] = (sy - B[1] * sx) / n;
  }

/**
 * Полиномиальная регрессия (аппроксимация)
 * расчет аппроксимации полиномом по методу наименьших квадратов
 * @param Degree степень полинома
 * @param N количество входных точек
 * @param X входные значения параметра
 * @param Y значения функции при х
 * @param A выходные коэффициенты полинома
 */
  public static double RegressPoly(int Degree, int N, double[] X, double[] Y, double[] A) {
    int n = Degree + 1;
    double[][] C = new double[n][n];
    double[] D = new double[n];
    double x;

    for(int j = 0; j <= 2 * Degree; j++) {
      // вычисление суммы Xi^j
      double S = 0;
      for(int i = 0; i < N; i++) {
        // вычисление x[i]^j
        x = 1;
        for(int k = 0; k < j; k++) x *= X[i];
        S += x;
        }
      int m1, m2;
      if(j <= Degree) { m1 = 0; m2 = j; }
      else { m1 = j - Degree; m2 = Degree; }
      for(int i = m1; i <= m2; i++)
        C[i][j - i] = S;
    }
    // вычисляем Dk=sum[i=1..N](Xi^k*Yi)
    for(int k = 0; k <= Degree; k++) {
    double s = 0; // сумма
    for(int i = 0; i < N; i++) {
      x = 1;
      for(int d = 0; d < k; d++, x *= X[i]);
      s += x * Y[i];
      }
    D[k] = s;
    }
    //A = new double[n];
    // решаем методом Гаусса
    rslu.RSLU_Gauss(n, C, D, A);
    return calcR2(Degree, X, Y, A);
  }
  public static double calcPolynom(int degree, double x, double[] coefs) {
      double xs = 1;
      double res = 0;
      for(int i = 0; i < degree; i++) {
          res += xs * coefs[i];
          xs *= x;
      }
      return res;
  }
  public static double calcR2(int degree, double[] x, double[] y, double[] coefs) {
      double res = 0;
      for(int i = 0; i < x.length; i++ ) {
          double r = calcPolynom(degree, x[i], coefs) - y[i];
          res += r*r;
      }
      return Math.sqrt(res);
  }
};
