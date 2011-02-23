package com.lsdsoft.math;
public class rslu {
// ������� ������� �������� ��������� ������� ������ (�����
// ����������������� ���������� �����������)
// ������� �� n �������� ��������� ����
//  A11X1 + A12X2 + ... + A1nXn = B1
//  A21X1 + A22X2 + ... + A2nXn = B2
//     ...
//  An1X1 + An2X2 + ... + AnnXn = Bn
// ���������� ������� � � ������������ ����
// ����: n - ����������� �������
//       A - ������� �������������
//       B - ������ ��������� ������
// �����: X - ������ �����������
  public static void RSLU_Gauss(int n, double[][] A, double[] B, double[] X) {
    // ������ ���
    // ���� �� �������
    for(int i = 0; i < n - 1; i++)
      for(int j = i + 1; j < n; j++) {
      // ���������� ������� �� ����
      if(A[j][i] == 0) continue;
      double l = A[i][i] / A[j][i];
      // ���� �� ��������
      for(int k = i; k < n; k++) {
        A[j][k] *= l;
        A[j][k] -= A[i][k];
        }
      B[j] = B[j] * l - B[i];
      }
    // �������� ���
    for(int i = n - 1; i >= 0; i--) {
      if(A[i][i] == 0)continue;
      X[i] = B[i] / A[i][i];
      for(int j = 0; j < i; j++)
        B[j] -= A[j][i] * X[i];
      }
  }
};
