package com.progressoft;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryTest {

    // Limits used in every row: ELITE at z >= 1.0, FAILED at z <= -1.0
    @ParameterizedTest(name = "z = {0} -> {1}")  // The Z-score and the expected Value .
    @CsvSource({
            " 2.5, ELITE",
            " 1.0, ELITE",
            " 0.99, PASSED",
            " 0.0, PASSED",
            "-0.99, PASSED",
            "-1.0, FAILED",
            "-3.0, FAILED"
    })
    void categoryDependsOnZScoreAndLimits(double z, Category expected) {
        assertEquals(expected, Category.of(z, 1.0, -1.0));
    }
}
