package com.lsdsoft.math;
public class rslu {
// Решение системы линейных уравнений методом Гаусса (метод
// последовательного исключения неизвестных)
// Система из n линейных уравнений вида
//  A11X1 + A12X2 + ... + A1nXn = B1
//  A21X1 + A22X2 + ... + A2nXn = B2
//     ...
//  An1X1 + An2X2 + ... + AnnXn = Bn
// Приведение матрицы А к треугольному виду
// Вход: n - расмерность матрицы
//       A - матрица коэффициентов
//       B - вектор свободных членов
// Выход: X - вектор неизвестных
  public static void RSLU_Gauss(int n, double[][] A, double[] B, double[] X) {
    // прямой ход
    // цикл по строкам
    for(int i = 0; i < n - 1; i++)
      for(int j = i + 1; j < n; j++) {
      // исключение деления на ноль
      if(A[j][i] == 0) continue;
      double l = A[i][i] / A[j][i];
      // цикл по столбцам
      for(int k = i; k < n; k++) {
        A[j][k] *= l;
        A[j][k] -= A[i][k];
        }
      B[j] = B[j] * l - B[i];
      }
    // обратный ход
    for(int i = n - 1; i >= 0; i--) {
      if(A[i][i] == 0)continue;
      X[i] = B[i] / A[i][i];
      for(int j = 0; j < i; j++)
        B[j] -= A[j][i] * X[i];
      }
  }
};
