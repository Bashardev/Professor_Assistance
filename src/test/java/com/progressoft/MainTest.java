package com.progressoft;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @TempDir
    Path tempDir;

    private Path writeCsv(String content) throws IOException {
        return Files.writeString(tempDir.resolve("marks.csv"), content);
    }

    private static String run(String... typedLines) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Main.run(new Scanner(String.join("\n", typedLines) + "\n"), new PrintStream(output));
        return output.toString();
    }

    @Test
    void loadsTheFileListsStudentsAndOpensMenu() throws IOException {
        Path csv = writeCsv("name,class,grade\nAli,A,85\nLina,B,60\n");

        String out = run(csv.toString(), "7");

        assertTrue(out.contains("Loaded 2 students:"));
        assertTrue(out.contains("Ali | A | 85"));
        assertTrue(out.contains("Lina | B | 60"));
        assertTrue(out.contains("1- Summary"));
        assertTrue(out.contains("Goodbye!"));
    }

    @Test
    void mainReadsFromKeyboardAndPrintsToScreen() throws IOException {
        Path csv = writeCsv("name,class,grade\nAli,A,85\n");
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream((csv + "\n7\n").getBytes()));
            System.setOut(new PrintStream(output));
            Main.main();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertTrue(output.toString().contains("Goodbye!"));
    }
}
