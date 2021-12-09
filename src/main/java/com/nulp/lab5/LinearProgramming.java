package com.nulp.lab5;

public class LinearProgramming {
    private static final double EPSILON = 1.0E-10;
    private double[][] a;   // tableaux
    private int m;          // number of constraints
    private int n;          // number of original variables

    private int[] basis;

    public LinearProgramming(double[][] A, double[] b, double[] c) {
        m = b.length;
        n = c.length;
        for (int i = 0; i < m; i++)
            if (!(b[i] >= 0)) throw new IllegalArgumentException("RHS must be nonnegative");

        a = new double[m+1][n+m+1];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                a[i][j] = A[i][j];
        for (int i = 0; i < m; i++)
            a[i][n+i] = 1.0;
        for (int j = 0; j < n; j++)
            a[m][j] = c[j];
        for (int i = 0; i < m; i++)
            a[i][m+n] = b[i];

        basis = new int[m];
        for (int i = 0; i < m; i++)
            basis[i] = n + i;

        solve();

        assert check(A, b, c);
    }

    private void solve() {
        while (true) {

            int q = bland();
            if (q == -1) break;  // optimal

            int p = minRatioRule(q);
            if (p == -1) throw new ArithmeticException("Linear program is unbounded");

            // pivot
            pivot(p, q);

            basis[p] = q;
        }
    }

    private int bland() {
        for (int j = 0; j < m+n; j++)
            if (a[m][j] > 0) return j;
        return -1;  // optimal
    }

    private int minRatioRule(int q) {
        int p = -1;
        for (int i = 0; i < m; i++) {
            // if (a[i][q] <= 0) continue;
            if (a[i][q] <= EPSILON) continue;
            else if (p == -1) p = i;
            else if ((a[i][m+n] / a[i][q]) < (a[p][m+n] / a[p][q])) p = i;
        }
        return p;
    }

    private void pivot(int p, int q) {

        for (int i = 0; i <= m; i++)
            for (int j = 0; j <= m+n; j++)
                if (i != p && j != q) a[i][j] -= a[p][j] * a[i][q] / a[p][q];

        // zero out column q
        for (int i = 0; i <= m; i++)
            if (i != p) a[i][q] = 0.0;

        // scale row p
        for (int j = 0; j <= m+n; j++)
            if (j != q) a[p][j] /= a[p][q];
        a[p][q] = 1.0;
    }

    public double value() {
        return -a[m][m+n];
    }

    public double[] primal() {
        double[] x = new double[n];
        for (int i = 0; i < m; i++)
            if (basis[i] < n) x[basis[i]] = a[i][m+n];
        return x;
    }

    public double[] dual() {
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            y[i] = -a[m][n+i];
        return y;
    }


    private boolean isPrimalFeasible(double[][] A, double[] b) {
        double[] x = primal();

        // check that x >= 0
        for (int j = 0; j < x.length; j++) {
            if (x[j] < 0.0) {
                System.out.println("x[" + j + "] = " + x[j] + " is negative");
                return false;
            }
        }

        // check that Ax <= b
        for (int i = 0; i < m; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            if (sum > b[i] + EPSILON) {
                System.out.println("not primal feasible");
                System.out.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    private boolean isDualFeasible(double[][] A, double[] c) {
        double[] y = dual();

        for (int i = 0; i < y.length; i++) {
            if (y[i] < 0.0) {
                System.out.println("y[" + i + "] = " + y[i] + " is negative");
                return false;
            }
        }

        for (int j = 0; j < n; j++) {
            double sum = 0.0;
            for (int i = 0; i < m; i++) {
                sum += A[i][j] * y[i];
            }
            if (sum < c[j] - EPSILON) {
                System.out.println("not dual feasible");
                System.out.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    private boolean isOptimal(double[] b, double[] c) {
        double[] x = primal();
        double[] y = dual();
        double value = value();

        // check that value = cx = yb
        double value1 = 0.0;
        for (int j = 0; j < x.length; j++)
            value1 += c[j] * x[j];
        double value2 = 0.0;
        for (int i = 0; i < y.length; i++)
            value2 += y[i] * b[i];
        if (Math.abs(value - value1) > EPSILON || Math.abs(value - value2) > EPSILON) {
            System.out.println("value = " + value + ", cx = " + value1 + ", yb = " + value2);
            return false;
        }

        return true;
    }

    private boolean check(double[][]A, double[] b, double[] c) {
        return isPrimalFeasible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
    }

}