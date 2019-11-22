package com.robosh;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        double[][] matrixA = new double[][]{
                {-1., 0., 3.},
                {2., 5., 4.},
                {7., 10., -10.}
        };

        double[] matrixB = new double[]{0, 3, -4};

        SystemOfLinearEquations matrix = new SystemOfLinearEquations(3, matrixA, matrixB);
        LUCalculation calculation = LUCalculation.from(matrix);

        double[][] L = calculation.calculateL();
        double[][] U = calculation.calculateU();
        double[] Y = calculation.calculateY();
        double[] X = calculation.calculateX();

        System.out.println("L : " + Arrays.deepToString(L));
        System.out.println("U: " + Arrays.deepToString(U));
        System.out.println("Y: " + Arrays.toString(Y));
        System.out.println("X: " + Arrays.toString(X));
        System.out.println(Arrays.deepEquals(calculation.multipleMatrix(L, U), matrixA));
    }
}
