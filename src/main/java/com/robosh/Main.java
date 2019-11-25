package com.robosh;

import com.robosh.entity.SystemOfLinearEquations;
import com.robosh.service.LUCalculation;
import com.robosh.service.MatrixReader;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
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

        System.out.println("L : " + Arrays.deepToString(calculation.getMatrixL()));
        System.out.println("U: " + Arrays.deepToString(calculation.getMatrixU()));
        System.out.println("Y: " + Arrays.toString(calculation.getMatrixY()));
        System.out.println("X: " + Arrays.toString(calculation.getMatrixX()));
        System.out.println(Arrays.deepEquals(calculation.multipleMatrix(calculation.getMatrixL(), calculation.getMatrixU()), matrixA));

        System.out.println(MatrixReader.readMatrixFromExternalFile("C:\\Users\\Oleksandr_Roienko\\IdeaProjects\\Parallel_Calculation\\src\\main\\resources\\matrix"));
        SystemOfLinearEquations fourDimensionalSlau = MatrixReader.readMatrixFromInternalFile("matrix4");
        LUCalculation calculation4 = LUCalculation.from(fourDimensionalSlau);
        System.out.println(Arrays.toString(calculation4.getMatrixX()));
    }
}
