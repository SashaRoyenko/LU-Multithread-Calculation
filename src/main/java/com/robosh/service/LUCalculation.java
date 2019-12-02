package com.robosh.service;

import com.robosh.entity.SystemOfLinearEquations;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        numberOfProcessors = Math.min(size, Runtime.getRuntime().availableProcessors());
        startCalculation();
    }

    public static LUCalculation from(SystemOfLinearEquations matrix) {
        return new LUCalculation(matrix);
    }

    private void calculateLU() {
        matrixU = copyArray(matrix.getMatrixA());
        matrixL = new double[size][size];

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
    }

    private void calculateY() {
        matrixY = new double[size];
        matrixY[0] = matrix.getMatrixB()[0];
        double sum;
        for (int i = 1; i < size; i++) {
            sum = 0.;
            for (int j = 0; j < i; j++) {
                sum += matrixY[j] * matrixL[i][j];
            }
            matrixY[i] = (matrix.getMatrixB()[i] - sum) / matrixL[i][i];
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

    private void startCalculation() {
        calculateLU();
        calculateY();
        calculateX();
    }

    @SneakyThrows
    private double[][] copyArray(double[][] arrayToCopy) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfProcessors);
        double[][] result = new double[arrayToCopy.length][arrayToCopy.length];
        int startIndex;
        int endIndex;
        for (int i = 0; i < numberOfProcessors; i++) {
            startIndex = size / numberOfProcessors * i;
            endIndex = size / numberOfProcessors * i;
            executorService.execute(copyArray(arrayToCopy, result, startIndex, endIndex));
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        return result;
    }

    private Runnable copyArray(double[][] arrayToCopy, double[][] result, int startIndex, int endIndex) {
        return () ->
        {
            for (int i = startIndex; i <= endIndex; i++) {
                result[i] = Arrays.copyOf(arrayToCopy[i], arrayToCopy.length);
            }
        };
    }
}

