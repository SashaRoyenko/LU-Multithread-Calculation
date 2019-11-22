package com.robosh;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class SystemOfLinearEquations {
    private double[][] matrixA;
    private int size;
    private double[] resultB;

    public SystemOfLinearEquations(int size) {
        this.size = size;
    }

    public SystemOfLinearEquations(int size, double[][] matrixA, double[] resultB) {
        this.matrixA = matrixA;
        this.size = size;
        this.resultB = resultB;
    }
}
