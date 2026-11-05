package com.progressoft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statistics {

    private Statistics() {
        // only static methods, so no objects are needed
    }

    public static double mean(List<Integer> grades) {
        double sum = 0;
        for (int g : grades) sum += g;
        return sum / grades.size();
    }

    // Middle value after sorting; with an even count, the average of the two middle values.
    public static double median(List<Integer> grades) {
        List<Integer> sorted = new ArrayList<>(grades);   // copy, so the original order is kept
        Collections.sort(sorted);
        int n = sorted.size();
        if (n % 2 == 1) {
            return sorted.get(n / 2);
        }
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }

    // Average of the squared distances from the mean (population variance).
    public static double variance(List<Integer> grades) {
        double m = mean(grades);
        double sum = 0;
        for (int g : grades) sum += (g - m) * (g - m);
        return sum / grades.size();
    }

    public static double standardDeviation(List<Integer> grades) {
        return Math.sqrt(variance(grades));
    }

    // How many standard deviations a grade is above (+) or below (-) the mean.
    // If every grade is the same, stdDev is 0; nobody is above or below, so the Z-score is 0.
    public static double zScore(int grade, double mean, double stdDev) {
        if (stdDev == 0) {
            return 0;
        }
        return (grade - mean) / stdDev;
    }

    // The opposite of zScore: turns a Z-score limit back into a grade.
    public static double gradeAtZ(double z, double mean, double stdDev) {
        return mean + z * stdDev;
    }
}
