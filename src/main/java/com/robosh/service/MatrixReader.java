package com.robosh.service;


import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.robosh.entity.SystemOfLinearEquations;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class MatrixReader {
    private MatrixReader() {

    }

    public static SystemOfLinearEquations readMatrixFromExternalFile(String fileName) {
        try (Scanner inputSource = new Scanner(new BufferedReader(new FileReader(fileName)))) {

            String[] line = inputSource.nextLine().split(" ");

            int size = line.length - 1;
            double[][] matrixA = new double[size][size];
            double[] matrixB = new double[size];

            for (int i = 0; i < size; i++) {
                matrixA[0][i] = Double.parseDouble(line[i]);
            }

            matrixB[0] = Double.parseDouble(line[size]);

            while (inputSource.hasNextLine()) {
                for (int i = 1; i < size; i++) {
                    line = inputSource.nextLine().split(" ");
                    matrixB[i] = Double.parseDouble(line[size]);
                    for (int j = 0; j < size; j++) {
                        matrixA[i][j] = Double.parseDouble(line[j]);
                    }
                }
            }
            return new SystemOfLinearEquations(size, matrixA, matrixB);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
        return new SystemOfLinearEquations();
    }

    public static SystemOfLinearEquations readMatrixFromInternalFile(String filepath) {
        List<double[]> dynamicMatrix = Lists.newArrayList();

        try {
            String content = Resources.toString(
                    Resources.getResource(filepath),
                    StandardCharsets.UTF_8
            );

            Arrays.stream(content.split("\n"))
                    .forEach(line -> dynamicMatrix.add(
                            Arrays.stream(line.split(" "))
                                    .mapToDouble(Double::parseDouble)
                                    .toArray()
                    ));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        double[][] matrix = dynamicMatrix.toArray(new double[0][]);
        int size = matrix.length;
        double[][] matrixA = new double[size][size];
        double[] matrixB = new double[size];
        for (int i = 0; i < size; i++) {
            matrixB[i] = matrix[i][size];
            matrixA[i] = Arrays.copyOf(matrix[i], size);
        }
        return new SystemOfLinearEquations(size, matrixA, matrixB);
    }


}
