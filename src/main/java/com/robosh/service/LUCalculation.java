package com.robosh.service;

import com.robosh.entity.SystemOfLinearEquations;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class LUCalculation {
    private SystemOfLinearEquations matrix;
    private int size;
    private int numberOfProcessors;
    private double[][] matrixL;
    private double[][] matrixU;
    private double[] matrixY;
    private double[] matrixX;

    private LUCalculation(SystemOfLinearEquations matrix) {
        this.matrix = matrix;
        size = matrix.getSize();
        numberOfProcessors = Runtime.getRuntime().availableProcessors();
        calculateLU();
        calculateY();
        calculateX();
    }

    public static LUCalculation from(SystemOfLinearEquations matrix) {
        return new LUCalculation(matrix);
    }

    private void calculateLU() {

        matrixU = copyArray(matrix.getMatrixA());
        matrixL = new double[size][size];

        Thread[] thread = new Thread[numberOfProcessors];
        numberOfProcessors = Math.min(size, numberOfProcessors);
        int startIndex = 1;

        for (int i = 1; i < numberOfProcessors; i++) {

            thread[i] = new Thread(calculateLU(size / numberOfProcessors * i,
                    i == numberOfProcessors - 1 ? size : size / numberOfProcessors * (i + 1)));
            thread[i].start();
        }
    }

    private void calculateY() {
        matrixY = new double[size];
        double[] matrixB = matrix.getMatrixB();
        double sum;
        matrixY[0] = matrixB[0];
        for (int i = 1; i < size; i++) {
            sum = 0.;
            for (int j = 0; j < i; j++) {
                sum += matrixY[j] * matrixL[i][j];
            }
            matrixY[i] = (matrixB[i] - sum) / matrixL[i][i];
        }
    }

    private void calculateX() {
        matrixX = new double[size];
        double sum;
        matrixX[size - 1] = matrixY[size - 1] / matrixU[size - 1][size - 1];
        for (int i = size - 2; i >= 0; i--) {
            sum = 0.;
            for (int j = size - 1; j >= i; j--) {
                sum += matrixX[j] * matrixU[i][j];
            }
            matrixX[i] = (matrixY[i] - sum) / matrixU[i][i];
        }
    }

    public double[][] multipleMatrix(double[][] matrixA, double[][] matrixB) {
        double[][] result = new double[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                for (int k = 0; k < size; k++)
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
        return result;
    }

    private double[][] copyArray(double[][] arrayToCopy) {
        double[][] result = new double[arrayToCopy.length][arrayToCopy.length];
        for (int i = 0; i < arrayToCopy.length; i++) {
            result[i] = Arrays.copyOf(arrayToCopy[i], arrayToCopy.length);
        }
        return result;
    }

    private Runnable calculateLU(int startIndex, int endIndex) {
        return () -> {
            for (int k = startIndex; k < endIndex; k++) {
                for (int i = k - 1; i < size; i++) {
                    for (int j = i; j < size; j++) {
                        matrixL[j][i] = matrixU[j][i] / matrixU[i][i];
                    }
                }

                for (int i = k; i < size; i++) {
                    for (int j = k - 1; j < size; j++) {
                        matrixU[i][j] = matrixU[i][j] - matrixL[i][k - 1] * matrixU[k - 1][j];
                    }
                }
            }
        };
    }
}

