package lsdsoft.welltools.im.ion1;

import java.lang.Math;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */
/**
����������� ��������� �������� ������������ � ���� �������������,
��������� ������������ ������� ���������� ������� � ������� ��������,
����������� � ��������� ������� ����������� ������� ��� ����������.

������ ������������ ������� �������� ���� � ����� �����������
1027 ����� �� 7 ��������, �� ���� ����� 1027*7=7189 ��������.

6500  6500  6500  2500  2500  2500  4
9432  5354  12754 16382 16374 16373 10
0     1024 1024 1024 1024 1024 1024
16    1025 1024 1024 1024 1024 1024
32    1025 1024 1024 1024 1024 1024
48    1026 1024 1024 1024 1024 1024
...................................
...................................
...................................
16336 1023 1024 1025 1024 1024 1024
16352 1023 1024 1025 1024 1024 1024
16368 1024 1024 1024 1024 1024 1024
16383 1024 1024 1024 1024 1024 1024

� ������ ������ ������ 6 �������� - ��������������� ���� ���������-
����� �������� (1 �������-Ax, 2 �������-Ay ...... 6 �������-Hzx) �
��.��.�.
�� ������ ������ ������ 6 �������� - ������� ���� ��������������
�������� (1 �������-Ax, 2 �������-Ay ...... 6 �������-Hzx) � ��.��.�.
7 ������� � ������ ���� ������� - �������������������� �������� �������
Az � ������� �������.
����������� 1025 ����� �������� ��������������� ��������� �������:
        1 ������� - ���� � ��.��.�,
        2 ������� - �������� ������� Ax � ������� + �������� 1024,
        3 ������� - �������� ������� Ay � ������� + �������� 1024,
        4 ������� - �������� ������� Az � ������� + �������� 1024,
        5 ������� - �������� ������� Hxy � ������� + �������� 1024,
        6 ������� - �������� ������� Hyz � ������� + �������� 1024,
        7 ������� - �������� ������� Hzx � ������� + �������� 1024.


������� ������ �������.


*inp - ��������� �� ������  �� 24 �������� ������������ INT (2 - �����,
��������), ��� ��������� ��������� �������� ������������ � ��.��.� ���
���� ��������. ������ 12 �������� - ���������� ���������, ������ 12
�������� - ������� ���������.

*tabl - ��������� �� ������ �������� �� 7189 �������� ������������ INT
(2 - �����, ��������). ������ �������� ����������� ��� ������ �����
���������� ������� ������� ����� ������� ������.

�������� ������ �������.


*angls - ��������� �� ������ �� 6 �������� ������������ DOUBLE (8 - ����,
��������), ��� ��������� ��������� �������� ������������ � ����.,
���������� �� ��������� �������� ������������ � ��.��.� � ������ ���������-
������� �����, ����������� ������� � ������� ����� ��������:
          angls[0] - Ax,
          angls[1] - Ay,
          .............
          angls[5] - Hxy.
�������� ��������� �� 0 �� 360 ����.

*out - ��������� �� ������ �� 3 ������������������ ����� ������-
������ DOUBLE (8 - ����, ��������) � ����.:
          out[0] - ���� �������� (�����������),
          out[1] - �����,
          out[2] - ������.
�������� ��������� �� 0 �� 360 ����.
*/

public class ION1Computer {
  private final static int MAX_KOD = 16384;
  private final static double _PI = 3.14159265358979;
  private final static double _HALF_PI = 1.570796326794895;
  private final static double _PI45 = 0.7853981633975;
  private final static double _PI135 = 2.356194490193;
  private final static double _ED = 0.02197265625;
  private final static double _ED16 = 0.3515625;
  private final static int NUMB_TAR = 1024;
  private final static int UNI_TAR = 72;
  private final static double SHG_TAR_DAT = 5.0;

  private ION1CorrectionTable table = null;
  public ION1Computer() {
  }
  /**
   * ������� ����������� ������� ��� ���������, ��� ��� ������ �� ��������.
   * <table width=100%>
   *   <tr>
   *   <th> header1
   * <th>heder2
   * </tr>
   * <tr>
   * <td>dgwqjwegjdqgwd qjw djqgw</td>
   * <td>aui7dyg2 i2gdi2id y2i</td>
   * </tr>
   * </table>
   * @param table ����������� �������
   */
  public void setTable(ION1CorrectionTable table) {
    this.table = table;
  }

  public ION1CorrectionTable getTable() {
    return table;
  }

  /**
   * ������ ��������� �������� � ������������������ ����, ����� �������������� �����
   * ������ ����������� �������
   * @param inp  ������ �� 24 ��������� - �������� ��������� ��������, ������
   * 12 - ���������� ��������, ������ 12 - �������
   * Ax' Ax" Ay' Ay" Az' Az" Hxy' Hxy" Hyz' Hyz" Hzx' Hzx"
   * @param angls �������� �� �������� � ������� �������� � ������ ����� �
   * ��������, ����� � �������� �� �������� ��� �������� (����� 18 ��������)
   * @param out �������� �������� �����: out[0] - �������, out[1] - �����, out[2] - ������
   * @return �����-�� ��� (�� :)
   * 
   */
  // TODO ��������� �������� �������� ������� angls
  public int calc(int[] inp, double[] angls, double[] out) {
    if(table == null)
      return -1;
    int[] tabl = table.getTable();
    double delt[] = new double[4];
    double dan, zn, ch;
    double ugl[] = new double[6];
    double dat[] = new double[6];
    double h[] = new double[6];
    double b[] = new double[5];
    int i, koef, j, i1, kl = 0;
    int[] in = new int[2];
    int[] in_angle = new int[12];

    for(i1 = 0; i1 < 12; i1++) {
        if(i1 < 4 || i1 > 7) continue;
        zn = 0.0;
        in[0] = inp[i1];
        in[1] = inp[12+i1];
        if(in[0] > MAX_KOD/8*3) {
            if(in[1] < MAX_KOD/8)
                in[1] += MAX_KOD/2;
        } else
        if(in[0] < MAX_KOD/8) {
            if(in[1] > MAX_KOD/8*3)
                in[1] -= MAX_KOD/2;
        }
        // strange code !!! rethink!!!
        zn = 2.0 * ( ((double)in[0] - (double)in[1]) / 12.0 *
                     (12.0 - (double)i1) + (double)in[1]);
        in_angle[i1] = (int)zn;
        if(in_angle[i1] >= MAX_KOD)
          in_angle[i1] -= MAX_KOD;
        else if(in_angle[i1] < 0)
          in_angle[i1] += MAX_KOD;
          //!!!!!!!!!!!!!!!
      }

      for(i1 = 0; i1 < 4; i1++)
          in_angle[i1] = 2*inp[i1];
      for(i1 = 7; i1 < 12; i1++)
        in_angle[i1] = 2*inp[i1];

      for(i1 = 0; i1 < 6; i1++) {
        ugl[0] = ((double)(in_angle[2*i1]))*_ED;
        ugl[1] = ((double)(in_angle[2*i1+1]))*_ED;
        // ugl[2] - �����
        ugl[2] = norm( (ugl[0]+ugl[1])/2.0 );
        //if(ugl[2] >= 360.0)
        //  ugl[2] -= 360.0;
        // ugl[3] - �������� �� �������
        ugl[3] = norm( (ugl[0]-ugl[1])/2.0 );
        //if(ugl[3] < 0.0)
        //  ugl[3] += 360.0;

        ugl[0] = norm( (double)tabl[i1]*_ED - 90.0 );
        //if(ugl[0] < 0.0)
        //  ugl[0] += 360.0; ;
        ugl[1] = norm( (double)tabl[i1]*_ED + 90.0 );
        //if(ugl[1] >= 360.0)
        //  ugl[1] -= 360.0; ;

        if(ugl[0] < ugl[1] && ugl[0] < ugl[2] && ugl[2] < ugl[1]) ;
        else if(ugl[0] > ugl[1] && (ugl[0] < ugl[2] || ugl[2] < ugl[1])) ;
          else {
            ugl[3] = norm( ugl[3] - 180.0 );
            ugl[2] = norm( ugl[2] - 180.0 );
            //if(ugl[3] < 0.0)
            //  ugl[3] += 360.0; ;
            //if(ugl[2] < 0.0)
            //  ugl[2] += 360.0; ;
          }
          angls[6 + i1] = ugl[2];  // !!!
          angls[12 + i1] = ugl[3]; // !!!
          // ��������� ��������� ������� Az
          if( i1 == 2 ) {
              angls[12 + i1] = norm(360.0 - angls[12 + i1]);
          }
          ugl[0] = ugl[3];

          //   ���� ���������
          i = ( int ) ( ugl[0] / _ED16 );
          if ( i < ( NUMB_TAR - 1 ) ) {
              koef = tabl[i1 + 1 + 7 * ( i + 2 )] - 1024;
              delt[1] = ( double )koef / 60.0;
              ugl[1] = ( double )i * _ED16;
              koef = tabl[i1 + 1 + 7 * ( i + 1 + 2 )] - 1024;
              delt[2] = ( double )koef / 60.0;
              ugl[2] = ( double ) ( i + 1 ) * _ED16;
          } else {
              koef = tabl[i1 + 1 + 7 * ( i + 2 )] - 1024;
              delt[1] = ( double )koef / 60.0;
              ugl[1] = ( double )i * _ED16;
              koef = tabl[i1 + 1 + 7 * 2] - 1024;
              delt[2] = ( double )koef / 60.0;
              ugl[2] = 360.0;
          }

          delt[0] = ( delt[2] - delt[1] ) / ( ugl[2] - ugl[1] ) *
              ( ugl[0] - ugl[1] ) + delt[1];
          if ( i1 < 3 ) {
              ugl[0] = 360.0 - ugl[0];
              delt[0] = -delt[0];
          }
          ugl[0] = norm( ugl[0] + delt[0] - ( double )tabl[7 + i1] * _ED );
          /*
          ugl[0] += delt[0];

          while(ugl[0] < 0.0)
            ugl[0] += 360.0;
          while(ugl[0] >= 360.0)
            ugl[0] -= 360.0;

            // ���� �������� ����
          ugl[1] = (double)tabl[7+i1]*_ED;

          ugl[0] -= ugl[1];
          if(ugl[0] < 0.0)
            ugl[0] += 360.0;
                */
          angls[i1] = dat[i1] = ugl[0] + 0.00001;
          if(dat[i1] >= 180.0)
            dat[i1] -= 360.0;
          dat[i1] *= _PI/180.0;
        }


    //       �����������  ������������������ ���������� �������
    for(j = 0; j < 2; j++)
    {
//      ������ ������������� �������� ������
//      Az ������������, ������ �� 3 ��������
      if((Math.abs(dat[0]) < _PI45  || Math.abs(dat[0]) > _PI135) &&
         ((Math.abs(dat[1]) > _PI45) && (Math.abs(dat[1]) < _PI135)))
        {  if(Math.abs(dat[1]) > _HALF_PI)
            out[0] = Math.atan2(-Math.sin(dat[0])/Math.cos(dat[0])*Math.sin(dat[1])/Math.cos(dat[1]), 1.0);
          else out[0] = Math.atan2( Math.sin(dat[0])/Math.cos(dat[0])*Math.sin(dat[1])/Math.cos(dat[1]),-1.0);
     //                  out[1] = atan2(1.0,-sin(dat[1])/cos(dat[1])*cos(out[0]));
   if((dat[0] >= 0.0  && dat[0] < _HALF_PI) ||
      (dat[0] >= -_PI && dat[0] < -_HALF_PI))
     out[1] = Math.atan2(Math.sin(dat[0])/Math.cos(dat[0]),Math.sin(out[0]));
   else out[1] =-Math.atan2(Math.sin(dat[0])/Math.cos(dat[0]),-Math.sin(out[0]));
   kl = 6;
 }

//      Ax ������������, ������ �� 1 ��������
     else  if((Math.abs(dat[1]) < _PI45  || Math.abs(dat[1]) > _PI135) &&
                 ((Math.abs(dat[2]) > _PI45) && (Math.abs(dat[2]) < _PI135)))
                     {  out[0] = _HALF_PI + dat[2];
                        out[1] = Math.atan2(1.0,-Math.sin(dat[1])/Math.cos(dat[1])*Math.cos(out[0]));
                        kl = 0;
                     }

//      Ay ������������, ������ �� 2 ��������
     else    {  out[0] = _HALF_PI + dat[2];
                if((dat[0] >= 0.0  && dat[0] < _HALF_PI) ||
                       (dat[0] >= -_PI && dat[0] < -_HALF_PI))
                                        out[1] = Math.atan2(Math.sin(dat[0])/Math.cos(dat[0]), Math.sin(out[0]));
                               else out[1] =-Math.atan2(Math.sin(dat[0])/Math.cos(dat[0]),-Math.sin(out[0]));
                        kl = 3;
             }

//      ����������� ����������� ��������� �� Az ��� ������ �� 3 - 177 ����
            if(out[1] > 0.04 && out[1] < 3.10) out[0] = _HALF_PI + dat[2]; ;
            if(out[0] >= _PI) out[0] -= 2.0*_PI; ;

//      ������ ������������� �������� ���������� ���� �����
        //      Hxy ������������, ������ �� 3 ��������
        if((Math.abs(dat[4]) <  _PI45  || Math.abs(dat[4]) > _PI135) &&
              ((Math.abs(dat[5]) >  _PI45) && (Math.abs(dat[5]) <= _PI135)))
               {  zn = Math.cos(out[1]) * Math.cos(out[0])*Math.cos(dat[5])/Math.sin(dat[5])-
                         Math.cos(out[1])*Math.sin(out[0])*Math.sin(dat[4])/Math.cos(dat[4])+
                         Math.sin(out[1]);
                  ch=-Math.sin(out[0])*Math.cos(dat[5])/Math.sin(dat[5])-
                      Math.cos(out[0])*Math.sin(dat[4])/Math.cos(dat[4]);
                  if(dat[5] >= 0.0 && dat[5] < _PI)
                                                 out[2] = Math.atan2(-ch,-zn);
                                            else out[2] = Math.atan2( ch, zn);
                  if(Math.abs(dat[5]) > _HALF_PI)
                                        b[0] = Math.atan2(-Math.sin(dat[4])/Math.cos(dat[4])*Math.sin(dat[5])/Math.cos(dat[5]), 1.0);
                               else b[0] = Math.atan2( Math.sin(dat[4])/Math.cos(dat[4])*Math.sin(dat[5])/Math.cos(dat[5]),-1.0);
                  b[1] = Math.atan2(1.0,-Math.sin(dat[5])/Math.cos(dat[5])*Math.cos(b[0]));
               }

//      Hyz ������������, ������ �� 1 ��������
     else  if((Math.abs(dat[5]) < _PI45 || Math.abs(dat[5]) >= _PI135) &&
                 ((Math.abs(dat[3]) > _PI45) && (Math.abs(dat[3]) < _PI135)))
               {  zn = Math.cos(out[1])*Math.cos(out[0])-
                       Math.cos(out[1])*Math.sin(out[0])*Math.cos(dat[3])/Math.sin(dat[3])+
                   Math.sin(out[1])*Math.sin(dat[5])/Math.cos(dat[5]);
                  ch = -Math.sin(out[0])-Math.cos(out[0])*Math.cos(dat[3])/Math.sin(dat[3]);
                  if(dat[3] >= 0.0 && dat[3] < _PI)
                                                 out[2] = Math.atan2(-ch,-zn);
                                            else out[2] = Math.atan2( ch, zn);
                  b[0] = _HALF_PI + dat[3];
                  b[1] = Math.atan2(1.0,-Math.sin(dat[5])/Math.cos(dat[5])*Math.cos(b[0]));
                  kl += 1;
               }

//      Hzx ������������, ������ �� 2 ��������
     else  {  zn=Math.cos(out[1])*Math.cos(out[0])*Math.sin(dat[3])/Math.cos(dat[3])-
                 Math.cos(out[1])*Math.sin(out[0])+
                         Math.sin(out[1])*Math.cos(dat[4])/Math.sin(dat[4]);
                  ch=-Math.sin(out[0])*Math.sin(dat[3])/Math.cos(dat[3])-
                      Math.cos(out[0]);
                  if(dat[4] >= 0.0 && dat[4] < _PI)
                                                 out[2] = Math.atan2(-ch,-zn);
                                            else out[2] = Math.atan2( ch, zn);
                  b[0] = _HALF_PI + dat[3];
                  if((dat[4] >= 0.0 && dat[4] < _HALF_PI) ||
                         (dat[4] >= -_PI && dat[4] < -_HALF_PI))
                                   b[1] =  Math.atan2(Math.sin(dat[4])/Math.cos(dat[4]), Math.sin(b[0]));
                          else b[1] = -Math.atan2(Math.sin(dat[4])/Math.cos(dat[4]),-Math.sin(b[0]));
                  kl += 2;
           }

         if(j == 0)
           { //       ����������� �������� �������� G � H
               h[0] = -Math.sin( out[1] ) * Math.cos( out[0] );
               h[1] = Math.sin( out[1] ) * Math.sin( out[0] );
               h[2] = Math.cos( out[1] );
               h[3] = -Math.sin( b[1] ) * Math.cos( b[0] );
               h[4] = Math.sin( b[1] ) * Math.sin( b[0] );
               h[5] = Math.cos( b[1] );

//       ��������� ���������� ����������� ��������� Az
               b[2] = ( double )tabl[6] * _PI / 180.0 / 60.0;
               b[3] = ( double )tabl[13] * _PI / 180.0 / 60.0;
               ch = h[0] + b[2] * h[2];
               zn = h[1] - b[3] * h[2];
               dat[2] = Math.atan2( ch, zn );
           } ;
         }
         out[0] -= _PI / 2;
         for ( i = 0; i < 3; i++ ) {
             //out[i] *= 180.0/_PI;
             out[i] = norm( out[i] * 180.0 / _PI );
             //  while(out[i] < 0.0) out[i] += 360.0; ;
             //  while(out[i] >= 360.0) out[i] -= 360.0;
             dat[i] = out[i];
         }
         return kl;

  }
  public int norm(int value) {
      while( value < 0 ) value += MAX_KOD;
      while( value >= MAX_KOD ) value -= MAX_KOD;
      return value;
  }

  public double norm( double value ) {
      while( value < 0 ) value += 360.0;
      while( value >= 360.0 ) value -= 360.0;
      return value;
  }
}
