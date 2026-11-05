package com.progressoft;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsTest {

    private static final double DELTA = 0.01;   // allowed rounding difference for decimals

    @Test
    void meanIsTheAverage() {
        assertEquals(82.33, Statistics.mean(List.of(85, 92, 70)), DELTA);
    }

    @Test
    void meanOfOneGradeIsThatGrade() {
        assertEquals(50, Statistics.mean(List.of(50)), DELTA);
    }

    @Test
    void medianOfOddCountIsTheMiddleValue() {
        assertEquals(85, Statistics.median(List.of(85, 92, 70)), DELTA);
    }

    @Test
    void medianOfEvenCountAveragesTheTwoMiddleValues() {
        assertEquals(77.5, Statistics.median(List.of(85, 92, 70, 60)), DELTA);
    }

    @Test
    void medianDoesNotChangeTheOrderOfTheGivenList() {
        List<Integer> grades = new ArrayList<>(List.of(85, 92, 70));
        Statistics.median(grades);
        assertEquals(List.of(85, 92, 70), grades);
    }

    @Test
    void varianceIsPopulationVariance() {
        assertEquals(84.22, Statistics.variance(List.of(85, 92, 70)), DELTA);
    }

    @Test
    void varianceIsZeroWhenAllGradesAreEqual() {
        assertEquals(0, Statistics.variance(List.of(70, 70, 70)), DELTA);
    }

    @Test
    void standardDeviationIsSquareRootOfVariance() {
        assertEquals(9.18, Statistics.standardDeviation(List.of(85, 92, 70)), DELTA);
    }

    @Test
    void zScoreIsDistanceFromMeanInStandardDeviations() {
        assertEquals(1.0, Statistics.zScore(80, 70, 10), DELTA);
        assertEquals(-2.0, Statistics.zScore(50, 70, 10), DELTA);
        assertEquals(0.0, Statistics.zScore(70, 70, 10), DELTA);
    }

    @Test
    void zScoreIsZeroWhenStandardDeviationIsZero() {
        assertEquals(0.0, Statistics.zScore(70, 70, 0), DELTA);
    }

    @Test
    void gradeAtZTurnsZScoreBackIntoGrade() {
        assertEquals(80, Statistics.gradeAtZ(1.0, 70, 10), DELTA);
        assertEquals(55, Statistics.gradeAtZ(-1.5, 70, 10), DELTA);
    }
}
