package com.robosh;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class LUCalculation {
    private SystemOfLinearEquations matrix;
    private int size;
    private int numberOfProcessors;

    private LUCalculation(SystemOfLinearEquations matrix) {
        this.matrix = matrix;
        size = matrix.getSize();
        numberOfProcessors = Runtime.getRuntime().availableProcessors();
    }

    public static LUCalculation from(SystemOfLinearEquations matrix) {
        return new LUCalculation(matrix);
    }

    public double[][] calculateL() {

        double[][] matrixU = copyArray(matrix.getMatrixA());
        double[][] matrixL = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                matrixL[j][i] = matrixU[j][i] / matrixU[i][i];
            }
        }

        for (int k = 1; k < size; k++) {
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

        return matrixL;
    }

    public double[][] calculateU() {

        double[][] matrixU = copyArray(matrix.getMatrixA());
        double[][] matrixL = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                matrixL[j][i] = matrixU[j][i] / matrixU[i][i];
            }
        }

        for (int k = 1; k < size; k++) {
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
        return matrixU;
    }

    public double[] calculateY() {
        double[] matrixY = new double[size];
        double[][] matrixL = calculateL();
        double[] matrixB = matrix.getResultB();
        double sum;
        matrixY[0] = matrixB[0];
        for (int i = 1; i < size; i++) {
            sum = 0.;
            for (int j = 0; j < i; j++) {
                sum += matrixY[j] * matrixL[i][j];
            }
            matrixY[i] = (matrixB[i] - sum) / matrixL[i][i];
        }
        return matrixY;
    }

    public double[] calculateX() {
        double[] matrixX = new double[size];
        double[] matrixY = calculateY();
        double[][] matrixU = calculateU();
        double sum;
        matrixX[size - 1] = matrixY[size - 1] / matrixU[size - 1][size - 1];
        for (int i = size - 2; i >= 0; i--) {
            sum = 0.;
            for (int j = size - 1; j >= i; j--) {
                sum += matrixX[j] * matrixU[i][j];
            }
            matrixX[i] = (matrixY[i] - sum) / matrixU[i][i];
        }
        return matrixX;
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

    public double[] getResult() {
        return new double[size];
    }
}
