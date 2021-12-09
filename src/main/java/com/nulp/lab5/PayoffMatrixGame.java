package com.nulp.lab5;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PayoffMatrixGame {
    private static final double EPSILON = 1E-8;

    private final int m;            // number of rows
    private final int n;            // number of columns
    private LinearProgramming lp;
    private double constant;

    public PayoffMatrixGame(double[][] payoff) {
        m = payoff.length;
        n = payoff[0].length;

        double[] c = new double[n];
        double[] b = new double[m];
        double[][] A = new double[m][n];
        for (int i = 0; i < m; i++)
            b[i] = 1.0;
        for (int j = 0; j < n; j++)
            c[j] = 1.0;

        constant = Double.POSITIVE_INFINITY;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (payoff[i][j] < constant)
                    constant = payoff[i][j];

        if (constant <= 0) constant = -constant + 1;
        else constant = 0;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                A[i][j] = payoff[i][j] + constant;

        lp = new LinearProgramming(A, b, c);
        assert certifySolution(payoff);
    }

    public double value() {
        return 1.0 / scale() - constant;
    }

    private double scale() {
        double[] x = lp.primal();
        double sum = 0.0;
        for (int j = 0; j < n; j++)
            sum += x[j];
        return sum;
    }

    public double[] row() {
        double scale = scale();
        double[] x = lp.primal();
        for (int j = 0; j < n; j++)
            x[j] /= scale;
        return x;
    }

    public double[] column() {
        double scale = scale();
        double[] y = lp.dual();
        for (int i = 0; i < m; i++)
            y[i] /= scale;
        return y;
    }

    private boolean isPrimalFeasible() {
        double[] x = row();
        double sum = 0.0;
        for (int j = 0; j < n; j++) {
            if (x[j] < 0) {
                System.out.println("row vector not a probability distribution");
                System.out.printf("    x[%d] = %f\n", j, x[j]);
                return false;
            }
            sum += x[j];
        }
        if (Math.abs(sum - 1.0) > EPSILON) {
            System.out.println("row vector x[] is not a probability distribution");
            System.out.println("    sum = " + sum);
            return false;
        }
        return true;
    }

    private boolean isDualFeasible() {
        double[] y = column();
        double sum = 0.0;
        for (int i = 0; i < m; i++) {
            if (y[i] < 0) {
                System.out.println("column vector y[] is not a probability distribution");
                System.out.printf("    y[%d] = %f\n", i, y[i]);
                return false;
            }
            sum += y[i];
        }
        if (Math.abs(sum - 1.0) > EPSILON) {
            System.out.println("column vector not a probability distribution");
            System.out.println("    sum = " + sum);
            return false;
        }
        return true;
    }

    private boolean isNashEquilibrium(double[][] payoff) {
        double[] x = row();
        double[] y = column();
        double value = value();

        double opt1 = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < m; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += payoff[i][j] * x[j];
            }
            if (sum > opt1) opt1 = sum;
        }
        if (Math.abs(opt1 - value) > EPSILON) {
            System.out.println("Optimal value = " + value);
            System.out.println("Optimal best response for column player = " + opt1);
            return false;
        }

        double opt2 = Double.POSITIVE_INFINITY;
        for (int j = 0; j < n; j++) {
            double sum = 0.0;
            for (int i = 0; i < m; i++) {
                sum += payoff[i][j] * y[i];
            }
            if (sum < opt2) opt2 = sum;
        }
        if (Math.abs(opt2 - value) > EPSILON) {
            System.out.println("Optimal value = " + value);
            System.out.println("Optimal best response for row player = " + opt2);
            return false;
        }


        return true;
    }

    private boolean certifySolution(double[][] payoff) {
        return isPrimalFeasible() && isDualFeasible() && isNashEquilibrium(payoff);
    }


    private static void calculate(String description, double[][] payoff) {
        System.out.println();
        System.out.println(description);
        System.out.println("------------------------------------");
        int m = payoff.length;
        int n = payoff[0].length;
        PayoffMatrixGame zerosum = new PayoffMatrixGame(payoff);
        double[] x = zerosum.row();
        double[] y = zerosum.column();

        System.out.print("x[] = [");
        for (int j = 0; j < n - 1; j++)
            System.out.printf("%8.4f, ", x[j]);
        System.out.printf("%8.4f]\n", x[n - 1]);

        System.out.print("y[] = [");
        for (int i = 0; i < m - 1; i++)
            System.out.printf("%8.4f, ", y[i]);
        System.out.printf("%8.4f]\n", y[m - 1]);
        System.out.println("value =  " + zerosum.value());

    }

    public static void main(String[] args) {
        List<String[]> lines = readLinesFromFile();
        double[][] payoff = lines.stream().map(line -> Arrays.stream(line).mapToDouble(Double::valueOf).toArray()).toArray(double[][]::new);
        calculate("A and B players payoff matrix", payoff);
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = PayoffMatrixGame.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }

    }

    private static List<String[]> readLinesFromFile() {
        try {
            List<String> allLines = Files.readAllLines(getFileFromResource("lab_5_input.txt").toPath());
            return allLines.stream().map(firstLine -> firstLine.split(" ")).collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}