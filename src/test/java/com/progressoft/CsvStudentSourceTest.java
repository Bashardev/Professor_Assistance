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

class CsvStudentSourceTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private List<Student> load(String... typedLines) throws IOException {
        Scanner in = new Scanner(String.join("\n", typedLines) + "\n");
        StudentSource source = new CsvStudentSource(in, new PrintStream(output));
        return source.load();
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

    @Test
    void loadsStudentsFromTheFileTheUserTypes() throws IOException {
        Path csv = Files.writeString(tempDir.resolve("marks.csv"), "name,class,grade\nAli,A,85\nLina,B,60\n");

        List<Student> students = load(csv.toString());

        assertEquals(List.of(new StudentRecord("Ali", "A", 85), new StudentRecord("Lina", "B", 60)), students);
        assertTrue(output.toString().contains("File accepted: " + csv));
    }

    @Test
    void asksAgainUntilThePathIsValid() throws IOException {
        Path csv = Files.writeString(tempDir.resolve("marks.csv"), "name,class,grade\nAli,A,85\n");
        Path missing = tempDir.resolve("nope.csv");

        List<Student> students = load(missing.toString(), csv.toString());

        String out = output.toString();
        assertTrue(out.contains("File not found: " + missing));
        assertEquals(2, count(out, "Enter the path of the marks CSV file: "));
        assertEquals(1, students.size());
    }

    @Test
    void acceptsPathWrittenInQuotes() throws IOException {
        Path csv = Files.writeString(tempDir.resolve("marks.csv"), "name,class,grade\nAli,A,85\n");

        assertEquals(1, load("\"" + csv + "\"").size());
    }
}
