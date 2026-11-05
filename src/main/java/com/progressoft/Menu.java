package com.progressoft;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Menu {

    private final List<Student> students;
    private final Scanner in;        // where the user's answers come from
    private final PrintStream out;   // where everything is printed

    public Menu(List<Student> students, Scanner in, PrintStream out) {
        this.students = students;
        this.in = in;
        this.out = out;
    }

    public void show() {
        boolean running = true;
        while (running) {

            out.println();
            out.println("1- Summary");
            out.println("2- Summary for a specific class");
            out.println("3- Display Z-scores");
            out.println("4- Display Z-scores for specific class");
            out.println("5- Categorize students");
            out.println("6- Categorize students in a specific class");
            out.println("7- Exit");
            out.print("Choose an option (1-7): ");

            String choice = in.nextLine().trim();
            // Each option prints its result and returns it as rows, so the user can save it.
            switch (choice) {
                case "1" -> offerToSave(summary(students));
                case "2" -> offerToSave(summary(chooseClass()));
                case "3" -> offerToSave(zScores(students));
                case "4" -> offerToSave(zScores(chooseClass()));
                case "5" -> offerToSave(categorize(students));
                case "6" -> offerToSave(categorize(chooseClass()));
                case "7" -> {
                    out.println("Goodbye!");
                    running = false;
                }
                default -> out.println("Invalid choice, please enter a number from 1 to 7.");
            }
        }
    }

    private List<List<String>> summary(List<Student> students) {
        if (students.isEmpty()) {
            out.println("No students to summarize.");
            return List.of();
        }

        List<Integer> grades = gradesOf(students);
        double median = Statistics.median(grades);
        double variance = Statistics.variance(grades);
        double stdDev = Statistics.standardDeviation(grades);

        out.printf("Median: %.2f%n", median);
        out.printf("Variance: %.2f%n", variance);
        out.printf("Standard Deviation: %.2f%n", stdDev);
        out.println("Total count: " + grades.size());

        return List.of(
                List.of("Metric", "Value"),
                List.of("Median", number(median)),
                List.of("Variance", number(variance)),
                List.of("Standard Deviation", number(stdDev)),
                List.of("Total count", String.valueOf(grades.size())));
    }

    private List<List<String>> zScores(List<Student> students) {
        if (students.isEmpty()) {
            out.println("No students to show Z-scores for.");
            return List.of();
        }

        List<Integer> grades = gradesOf(students);
        double mean = Statistics.mean(grades);
        double stdDev = Statistics.standardDeviation(grades);

        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Name", "Class", "Grade", "Z-score"));

        out.printf("%-12s %-6s %-6s %8s%n", "Name", "Class", "Grade", "Z-score");
        for (Student s : students) {
            double z = Statistics.zScore(s.Sgrade(), mean, stdDev);
            out.printf("%-12s %-6s %-6d %8.2f%n", s.Sname(), s.Sclass(), s.Sgrade(), z);
            rows.add(List.of(s.Sname(), s.Sclass(), String.valueOf(s.Sgrade()), number(z)));
        }
        return rows;
    }

    private List<List<String>> categorize(List<Student> students) {
        if (students.isEmpty()) {
            out.println("No students to categorize.");
            return List.of();
        }

        // The professor decides the limits each time.
        double eliteLimit = readNumber("Enter the Z-score for ELITE (e.g. 1.0): ");
        double failedLimit = readNumber("Enter the Z-score for FAILED (e.g. -1.0): ");
        while (failedLimit >= eliteLimit) {
            out.println("The FAILED limit must be lower than the ELITE limit (" + eliteLimit + ").");
            failedLimit = readNumber("Enter the Z-score for FAILED (e.g. -1.0): ");
        }

        List<Integer> grades = gradesOf(students);
        double mean = Statistics.mean(grades);
        double median = Statistics.median(grades);
        double variance = Statistics.variance(grades);
        double stdDev = Statistics.standardDeviation(grades);
        double passingScore = Statistics.gradeAtZ(failedLimit, mean, stdDev);
        double eliteScore = Statistics.gradeAtZ(eliteLimit, mean, stdDev);

        // How many students are in each category.
        int eliteCount = 0;
        int passedCount = 0;
        int failedCount = 0;

        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Name", "Class", "Grade", "Z-score", "Category"));

        out.printf("%-12s %-6s %-6s %8s  %s%n", "Name", "Class", "Grade", "Z-score", "Category");
        for (Student s : students) {
            double z = Statistics.zScore(s.Sgrade(), mean, stdDev);
            Category category = Category.of(z, eliteLimit, failedLimit);
            if (category == Category.ELITE) {
                eliteCount++;
            } else if (category == Category.PASSED) {
                passedCount++;
            } else {
                failedCount++;
            }
            out.printf("%-12s %-6s %-6d %8.2f  %s%n",
                    s.Sname(), s.Sclass(), s.Sgrade(), z, category);
            rows.add(List.of(s.Sname(), s.Sclass(), String.valueOf(s.Sgrade()), number(z), category.name()));
        }

        out.println();
        out.printf("Median: %.2f%n", median);
        out.printf("Variance: %.2f%n", variance);
        out.printf("Standard Deviation: %.2f%n", stdDev);
        out.println("Elite students count: " + eliteCount);
        out.println("Passed students count: " + passedCount);
        out.println("Failed students count: " + failedCount);
        out.printf("Passing score: %.2f%n", passingScore);
        out.printf("Elite score: %.2f%n", eliteScore);

        rows.add(List.of());   // blank line between the students and the summary
        rows.add(List.of("Metric", "Value"));
        rows.add(List.of("Median", number(median)));
        rows.add(List.of("Variance", number(variance)));
        rows.add(List.of("Standard Deviation", number(stdDev)));
        rows.add(List.of("Elite students count", String.valueOf(eliteCount)));
        rows.add(List.of("Passed students count", String.valueOf(passedCount)));
        rows.add(List.of("Failed students count", String.valueOf(failedCount)));
        rows.add(List.of("Passing score", number(passingScore)));
        rows.add(List.of("Elite score", number(eliteScore)));
        return rows;
    }

    // Asks if the user wants the result in a CSV file, and saves it if so.
    // An empty result (no students) has nothing to save, so nothing is asked.
    private void offerToSave(List<List<String>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        if (!askYesNo("Do you want to save the result to a CSV file? (y/n): ")) {
            return;
        }
        while (true) {
            out.print("Enter the path for the result CSV file: ");
            String typed = in.nextLine();
            String error = CsvPathValidator.checkOutput(typed);   // null means "no error"
            if (error != null) {
                out.println(error);
                continue;
            }
            Path path = Path.of(CsvPathValidator.clean(typed));
            if (Files.exists(path) && !askYesNo("The file already exists. Overwrite it? (y/n): ")) {
                continue;   // ask for another path
            }
            try {
                CsvResultWriter.write(path, rows);
                out.println("Result saved to: " + path);
            } catch (IOException e) {
                out.println("Could not save the file: " + e.getMessage());
            }
            return;
        }
    }

    // Keeps asking until the user answers y / yes / n / no (any upper or lower case).
    private boolean askYesNo(String question) {
        while (true) {
            out.print(question);
            String answer = in.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            out.println("Please answer y or n.");
        }
    }

    // Numbers in the CSV always use a dot and 2 decimals (e.g. 80.00), whatever the computer's language.
    // In some languages %.2f would write 80,00, and the comma would break the CSV columns.
    private static String number(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static List<Integer> gradesOf(List<Student> students) {
        List<Integer> grades = new ArrayList<>();
        for (Student s : students) {
            grades.add(s.Sgrade());
        }
        return grades;
    }

    // Keeps asking until the user types a valid number.
    private double readNumber(String message) {
        while (true) {
            out.print(message);
            String text = in.nextLine().trim();
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                out.println("\"" + text + "\" is not a number. Try again.");
            }
        }
    }

    // Asks for a class name and returns only the students in that class.
    private List<Student> chooseClass() {
        while (true) {
            out.print("Enter the class name: ");
            String className = in.nextLine().trim();

            List<Student> inClass = new ArrayList<>();
            for (Student s : students) {
                if (s.Sclass().equalsIgnoreCase(className)) {
                    inClass.add(s);
                }
            }
            if (!inClass.isEmpty()) {
                return inClass;
            }
            out.println("No students found in class \"" + className + "\". Try again.");
        }
    }
}
