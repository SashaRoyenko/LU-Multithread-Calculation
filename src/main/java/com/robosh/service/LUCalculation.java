package com.robosh.service;

import com.robosh.entity.SystemOfLinearEquations;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class LUCalculation {
    private SystemOfLinearEquations matrix;
    private int size;
    private int numberOfProcessors;
    private double[][] matrixL;
    private double[][] matrixU;
    private double[] matrixY;
    private double[] matrixX;
    private ReentrantLock reentrantLock;
    private LUCalculation(SystemOfLinearEquations matrix) {
        this.matrix = matrix;
        size = matrix.getSize();
        numberOfProcessors = Math.min(size, Runtime.getRuntime().availableProcessors());
        startCalculation();
    }

    public static LUCalculation from(SystemOfLinearEquations matrix) {
        return new LUCalculation(matrix);
    }

    @SneakyThrows
    private void calculateLU() {

        matrixU = copyArray(matrix.getMatrixA());
        matrixL = new double[size][size];
        reentrantLock = new ReentrantLock();
        Thread[] thread = new Thread[numberOfProcessors - 1];
        int startIndex;
        int endIndex;
        for (int i = 0; i < numberOfProcessors - 1; i++) {
            startIndex = size / numberOfProcessors * i + 1;
            endIndex = i == numberOfProcessors - 1 ? size : size / numberOfProcessors * (i + 1);
            thread[i] = new Thread(calculateLU(startIndex,
                    endIndex));
            thread[i].start();

        }
        for (int i = 0; i < numberOfProcessors - 1; i++) {
            thread[i].join();
        }
    }

    @SneakyThrows
    private void calculateY() {
        matrixY = new double[size];
        Thread[] thread = new Thread[numberOfProcessors - 1];
        System.err.println("Processors: " + (numberOfProcessors - 1));
        int startIndex;
        int endIndex;
        matrixY[0] = matrix.getMatrixB()[0];
        for (int i = 0; i < numberOfProcessors - 1; i++) {
            startIndex = size / numberOfProcessors * i + 1;
            endIndex = i == numberOfProcessors - 1 ? size : size / numberOfProcessors * (i + 1);
            thread[i] = new Thread(calculateY(startIndex,
                    endIndex));
            thread[i].start();

        }
        for (int i = 0; i < numberOfProcessors - 1; i++) {
            thread[i].join();
        }
    }

    @SneakyThrows
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

    @SneakyThrows
    private void startCalculation() {
//        ExecutorService threadPool = Executors.newFixedThreadPool(3);
//        threadPool.execute(this::calculateLU);
//        threadPool.execute(this::calculateY);
//        threadPool.execute(this::calculateX);
//        threadPool.shutdown();
        calculateLU();
        calculateY();
        calculateX();
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

    private Runnable calculateY(int startIndex, int endIndex) {
        return () -> {
            double sum;
            for (int i = startIndex; i <= endIndex; i++) {
                sum = 0.;
                for (int j = 0; j < i; j++) {
                    sum += matrixY[j] * matrixL[i][j];
                }
                matrixY[i] = (matrix.getMatrixB()[i] - sum) / matrixL[i][i];
            }
        };
    }

    private Runnable calculateX(int startIndex, int endIndex) {
        return () -> {
            double sum;
            for (int i = endIndex - 2; i >= startIndex; i--) {
                sum = 0.;
                for (int j = size - 1; j >= i; j--) {
                    sum += matrixX[j] * matrixU[i][j];
                }
                matrixX[i] = (matrixY[i] - sum) / matrixU[i][i];
            }
        };
    }

    private Runnable calculateLU(int startIndex, int endIndex) {
        return () -> {
            for (int k = startIndex; k <= endIndex; k++) {
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

