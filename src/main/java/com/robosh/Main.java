package com.robosh;

import com.robosh.entity.SystemOfLinearEquations;
import com.robosh.service.LUCalculation;
import com.robosh.service.MatrixReader;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Scanner;

@Slf4j
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String fileName = scanner.next();
        SystemOfLinearEquations systemOfLinearEquations = MatrixReader.readMatrixFromExternalFile(fileName);
        LUCalculation luCalculation = LUCalculation.from(systemOfLinearEquations);
        System.out.println("Matrix U: ");
        showTwoDimensionalMatrix(luCalculation.getMatrixU());
        System.out.println("Matrix L: ");
        showTwoDimensionalMatrix(luCalculation.getMatrixL());
        System.out.println("Matrix Y: " + Arrays.toString(luCalculation.getMatrixY()));
        System.out.println("Matrix X: " + Arrays.toString(luCalculation.getMatrixX()));
    }

    private static void showTwoDimensionalMatrix(double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(String.format("%4.3f | ",matrix[i][j]));
            }
            System.out.println();
        }
    }
}
