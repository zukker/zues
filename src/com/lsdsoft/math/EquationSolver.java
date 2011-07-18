package com.lsdsoft.math;

public class EquationSolver {
    /**
     * Решение линейного уравнения вида A[1]*x+A[0] = 0
     * @param A
     * @return
     */
    static public double[] SolveLineEquation(double[] A) {
        double[] roots = null;
        if(A[1] != 0) { // решение есть
            roots = new double[1];
            roots[0] = -A[0]/A[1];
        }
        return roots;
    }
        
    /**
     * Решение квадратного уравнения вида A[2]*x^2+A[1]*x+A[0] = 0
     * @param A 
     * @return корни, null - если корней нет
     */
    static public double[] SolveQuadraticEquation(double[] A) {
        double[] roots = null;
        if(A[2] == 0) {
            return SolveLineEquation(A);
        }
        // вычисление дискриминанта
        double D2 = A[1]*A[1] - 4* A[2]*A[0];
        if( D2 > 0 )  { // корней 2
            roots = new double[2];
            double D = Math.sqrt(D2);
            roots[0] = (-A[1] - D) / (2 * A[2]);
            roots[1] = (-A[1] + D) / (2 * A[2]);
        } else
        if ( D2 == 0 ) { // корень один
            roots = new double[1];
            roots[0] = -A[1]/(2 * A[2]);
        }
        return roots;
    }
    /**
     * Решение кубичского полинома вида A[3]*x^3+A[2]*x^2+A[1]*x+A[0]=0;
     * методом Виета-Кардано
     * @param A - коэфициенты полинома
     * @return корни уравнения
     */
    static public double[] SolveCubicEquation(double[] A) {
        double[] roots = null;
        double[] X = new double[3];
        int rc = 1; // количество корней
        if(A.length >= 4) {
            if(A[3] == 0) { // квадратное уравнение
                return SolveQuadraticEquation(A);
            }
            //roots = new double[3];
            double q, r, r2, q3;
            double a = A[2]/A[3];
            double b = A[1]/A[3];
            double c = A[0]/A[3];
            
            q = (a * a - 3. * b) / 9.;
            r = (a * (2. * a * a - 9. * b) + 27. * c) / 54.;
            r2 = r * r;
            q3 = q * q * q;
            if (r2 < q3) {
                double t = Math.acos(r / Math.sqrt(q3));
                a /= 3.;
                q = -2. * Math.sqrt(q);
                X[0] = q * Math.cos(t / 3.) - a;
                X[1] = q * Math.cos((t + Math.PI * 2) / 3.) - a;
                X[2] = q * Math.cos((t - Math.PI * 2) / 3.) - a;
                rc = 3;
            } else {
                double aa, bb;
                if (r <= 0.)
                    r = -r;
                aa = -Math.pow(r + Math.sqrt(r2 - q3), 1. / 3.);
                if (aa != 0.)
                    bb = q / aa;
                else
                    bb = 0.;
                a /= 3.;
                q = aa + bb;
                r = aa - bb;
                X[0] = q - a;
                X[1] = (-0.5) * q - a;
                X[2] = (Math.sqrt(3.) * 0.5) * Math.abs(r);
                if (X[2] == 0.) {
                    rc = 2;
                } else {
                    rc = 1;
                }

            }
        }
        roots = new double[rc];
        for(int i = 0; i < rc; roots[i]=X[i],i++);
        return roots;
    }
}

