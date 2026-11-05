package com.progressoft;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class MenuTest {

    // Class A: 85, 92, 70  (mean 82.33, std dev 9.18)
    // Class B: 60, 80      (mean 70,    std dev 10)
    // All:     85, 92, 70, 60, 80 (mean 77.4, median 80, variance 127.04, std dev 11.27)
    private static final List<Student> STUDENTS = List.of(
            new StudentRecord("Ali", "A", 85),
            new StudentRecord("Sara", "A", 92),
            new StudentRecord("Omar", "A", 70),
            new StudentRecord("Lina", "B", 60),
            new StudentRecord("Zaid", "B", 80));

    // Runs the menu as if the user typed each line, and returns everything it printed.
    private static String run(List<Student> students, String... typedLines) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Scanner in = new Scanner(String.join("\n", typedLines) + "\n");
        new Menu(students, in, new PrintStream(output)).show();
        return output.toString();
    }

    // Counts how many times "part" appears in "text".
    private static int count(String text, String part) {
        int count = 0;
        int index = text.indexOf(part);
        while (index != -1) {
            count++;
            index = text.indexOf(part, index + part.length());
        }
        return count;
    }

    // ---------- menu loop ----------

    @Test
    void showsAllOptions() {
        String out = run(STUDENTS, "7");

        assertTrue(out.contains("1- Summary"));
        assertTrue(out.contains("2- Summary for a specific class"));
        assertTrue(out.contains("3- Display Z-scores"));
        assertTrue(out.contains("4- Display Z-scores for specific class"));
        assertTrue(out.contains("5- Categorize students"));
        assertTrue(out.contains("6- Categorize students in a specific class"));
        assertTrue(out.contains("7- Exit"));
    }

    @Test
    void option7SaysGoodbyeAndStops() {
        String out = run(STUDENTS, "7");

        assertTrue(out.endsWith("Goodbye!" + System.lineSeparator()));
        assertEquals(1, count(out, "Choose an option"));
    }

    @Test
    void invalidChoicesShowErrorAndMenuRepeats() {
        String out = run(STUDENTS, "9", "abc", "", "7");

        assertEquals(3, count(out, "Invalid choice, please enter a number from 1 to 7."));
        assertEquals(4, count(out, "Choose an option"));
    }

    @Test
    void choiceWithSpacesAroundIsAccepted() {
        assertTrue(run(STUDENTS, "  7  ").contains("Goodbye!"));
    }

    // ---------- option 1 / 2: summary ----------

    @Test
    void option1PrintsSummaryOfAllStudents() {
        String out = run(STUDENTS, "1", "n", "7");

        assertTrue(out.contains("Median: 80.00"));
        assertTrue(out.contains("Variance: 127.04"));
        assertTrue(out.contains("Standard Deviation: 11.27"));
        assertTrue(out.contains("Total count: 5"));
    }

    @Test
    void option1WithNoStudentsSaysSo() {
        String out = run(List.of(), "1", "7");

        assertTrue(out.contains("No students to summarize."));
        assertFalse(out.contains("Do you want to save"));
    }

    @Test
    void option2PrintsSummaryOfChosenClassOnly() {
        String out = run(STUDENTS, "2", "b", "n", "7");

        assertTrue(out.contains("Median: 70.00"));
        assertTrue(out.contains("Variance: 100.00"));
        assertTrue(out.contains("Standard Deviation: 10.00"));
        assertTrue(out.contains("Total count: 2"));
    }

    @Test
    void unknownClassAsksAgain() {
        String out = run(STUDENTS, "2", "Z", "B", "n", "7");

        assertTrue(out.contains("No students found in class \"Z\". Try again."));
        assertEquals(2, count(out, "Enter the class name: "));
        assertTrue(out.contains("Total count: 2"));
    }

    // ---------- option 3 / 4: Z-scores ----------

    @Test
    void option3PrintsOneRowPerStudentWithZScore() {
        String out = run(STUDENTS, "3", "n", "7");

        assertTrue(out.contains("Name         Class  Grade   Z-score"));
        assertTrue(out.contains("Ali          A      85         0.67"));
        assertTrue(out.contains("Lina         B      60        -1.54"));
        for (Student st : STUDENTS) {
            assertTrue(out.contains(st.Sname()), st.Sname() + " missing");
        }
    }

    @Test
    void option3WithNoStudentsSaysSo() {
        String out = run(List.of(), "3", "7");

        assertTrue(out.contains("No students to show Z-scores for."));
        assertFalse(out.contains("Do you want to save"));
    }

    @Test
    void option4UsesTheMeanOfTheChosenClass() {
        String out = run(STUDENTS, "4", "B", "n", "7");

        assertTrue(out.contains("Lina         B      60        -1.00"));
        assertTrue(out.contains("Zaid         B      80         1.00"));
        assertFalse(out.contains("Ali"));
    }

    // ---------- option 5 / 6: categorize ----------

    @Test
    void option5CategorizesAllStudentsAndPrintsSummary() {
        String out = run(STUDENTS, "5", "1", "-1", "n", "7");

        assertTrue(out.contains("Name         Class  Grade   Z-score  Category"));
        assertTrue(out.contains("Sara         A      92         1.30  ELITE"));
        assertTrue(out.contains("Ali          A      85         0.67  PASSED"));
        assertTrue(out.contains("Lina         B      60        -1.54  FAILED"));
        assertTrue(out.contains("Median: 80.00"));
        assertTrue(out.contains("Variance: 127.04"));
        assertTrue(out.contains("Standard Deviation: 11.27"));
        assertTrue(out.contains("Elite students count: 1"));
        assertTrue(out.contains("Passed students count: 3"));
        assertTrue(out.contains("Failed students count: 1"));
        assertTrue(out.contains("Passing score: 66.13"));
        assertTrue(out.contains("Elite score: 88.67"));
    }

    @Test
    void option6CategorizesOnlyTheChosenClass() {
        String out = run(STUDENTS, "6", "B", "1", "-1", "n", "7");

        assertTrue(out.contains("Lina         B      60        -1.00  FAILED"));
        assertTrue(out.contains("Zaid         B      80         1.00  ELITE"));
        assertFalse(out.contains("Ali"));
        assertTrue(out.contains("Elite students count: 1"));
        assertTrue(out.contains("Passed students count: 0"));
        assertTrue(out.contains("Failed students count: 1"));
        assertTrue(out.contains("Passing score: 60.00"));
        assertTrue(out.contains("Elite score: 80.00"));
    }

    @Test
    void option5WithNoStudentsSaysSoWithoutAskingForLimits() {
        String out = run(List.of(), "5", "7");

        assertTrue(out.contains("No students to categorize."));
        assertFalse(out.contains("Enter the Z-score"));
        assertFalse(out.contains("Do you want to save"));
    }

    @Test
    void limitThatIsNotANumberIsAskedAgain() {
        String out = run(STUDENTS, "5", "abc", "1", "-1", "n", "7");

        assertTrue(out.contains("\"abc\" is not a number. Try again."));
        assertEquals(2, count(out, "Enter the Z-score for ELITE"));
    }

    @Test
    void failedLimitMustBeLowerThanEliteLimit() {
        String out = run(STUDENTS, "5", "1", "1", "2", "-1", "n", "7");

        assertEquals(2, count(out, "The FAILED limit must be lower than the ELITE limit (1.0)."));
        assertEquals(3, count(out, "Enter the Z-score for FAILED"));
    }

    // ---------- saving the result to a CSV file ----------

    @TempDir
    Path tempDir;

    @Test
    void fullWordNoAlsoMeansDoNotSave() {
        String out = run(STUDENTS, "1", " No ", "7");

        assertFalse(out.contains("Enter the path for the result CSV file"));
        assertFalse(out.contains("Please answer y or n."));
    }

    @Test
    void answerThatIsNotYesOrNoIsAskedAgain() {
        String out = run(STUDENTS, "1", "maybe", "N", "7");

        assertTrue(out.contains("Please answer y or n."));
        assertEquals(2, count(out, "Do you want to save"));
    }

    @Test
    void summaryIsSavedAsCsv() throws IOException {
        Path file = tempDir.resolve("summary.csv");

        String out = run(STUDENTS, "1", "y", file.toString(), "7");

        assertTrue(out.contains("Result saved to: " + file));
        assertEquals("""
                Metric,Value
                Median,80.00
                Variance,127.04
                Standard Deviation,11.27
                Total count,5
                """, Files.readString(file));
    }

    @Test
    void zScoresAreSavedAsCsv() throws IOException {
        Path file = tempDir.resolve("z.csv");

        run(STUDENTS, "4", "B", "yes", file.toString(), "7");

        assertEquals("""
                Name,Class,Grade,Z-score
                Lina,B,60,-1.00
                Zaid,B,80,1.00
                """, Files.readString(file));
    }

    @Test
    void categoriesAreSavedWithTheirSummary() throws IOException {
        Path file = tempDir.resolve("categories.csv");

        run(STUDENTS, "6", "B", "1", "-1", "Y", file.toString(), "7");

        assertEquals("""
                Name,Class,Grade,Z-score,Category
                Lina,B,60,-1.00,FAILED
                Zaid,B,80,1.00,ELITE

                Metric,Value
                Median,70.00
                Variance,100.00
                Standard Deviation,10.00
                Elite students count,1
                Passed students count,0
                Failed students count,1
                Passing score,60.00
                Elite score,80.00
                """, Files.readString(file));
    }

    @Test
    void badSavePathIsAskedAgain() throws IOException {
        Path file = tempDir.resolve("summary.csv");

        String out = run(STUDENTS, "1", "y", "summary.txt", file.toString(), "7");

        assertTrue(out.contains("The file must be a .csv file."));
        assertEquals(2, count(out, "Enter the path for the result CSV file: "));
        assertTrue(Files.exists(file));
    }

    @Test
    void existingFileIsKeptWhenUserSaysNoToOverwrite() throws IOException {
        Path existing = Files.writeString(tempDir.resolve("old.csv"), "keep me\n");
        Path other = tempDir.resolve("new.csv");

        String out = run(STUDENTS, "1", "y", existing.toString(), "n", other.toString(), "7");

        assertTrue(out.contains("The file already exists. Overwrite it? (y/n): "));
        assertEquals("keep me\n", Files.readString(existing));
        assertTrue(Files.readString(other).startsWith("Metric,Value"));
    }

    @Test
    void existingFileIsReplacedWhenUserSaysYesToOverwrite() throws IOException {
        Path existing = Files.writeString(tempDir.resolve("old.csv"), "old\n");

        run(STUDENTS, "1", "y", existing.toString(), "y", "7");

        assertTrue(Files.readString(existing).startsWith("Metric,Value"));
    }

    @Test
    void errorWhileSavingIsReportedAndMenuContinues() throws IOException {
        Path readOnly = Files.writeString(tempDir.resolve("locked.csv"), "old\n");
        readOnly.toFile().setWritable(false);
        assumeFalse(Files.isWritable(readOnly), "cannot make a read-only file here (running as root?)");

        String out = run(STUDENTS, "1", "y", readOnly.toString(), "y", "7");

        assertTrue(out.contains("Could not save the file: "));
        assertTrue(out.contains("Goodbye!"));
        assertEquals("old\n", Files.readString(readOnly));
    }
}
