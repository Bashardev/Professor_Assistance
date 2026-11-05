package com.progressoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentCsvParserTest {

    private static final String HEADER = "name,class,grade";

    private ByteArrayOutputStream output;
    private StudentCsvParser parser;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        parser = new StudentCsvParser(new PrintStream(output));
    }

    @Test
    void parsesValidLines() {
        List<Student> students = parser.parse(List.of(HEADER, "Ali,A,85", "Sara,B,92"));

        assertEquals(List.of(
                new StudentRecord("Ali", "A", 85),
                new StudentRecord("Sara", "B", 92)), students);
    }

    @Test
    void headerOnlyGivesNoStudents() {
        assertTrue(parser.parse(List.of(HEADER)).isEmpty());
    }

    @Test
    void emptyFileGivesNoStudents() {
        assertTrue(parser.parse(List.of()).isEmpty());
    }

    @Test
    void trimsSpacesAroundValues() {
        assertEquals(List.of(new StudentRecord("Ali", "A", 85)),
                parser.parse(List.of(HEADER, "  Ali ,  A , 85  ")));
    }

    @Test
    void skipsBlankLinesSilently() {
        assertEquals(1, parser.parse(List.of(HEADER, "", "   ", "Ali,A,85")).size());
        assertEquals("", output.toString());
    }

    @Test
    void skipsLinesWithWrongNumberOfColumnsAndSaysWhich() {
        List<Student> students = parser.parse(List.of(HEADER, "Ali,A", "Sara,B,92,x", "Omar,C,70"));

        assertEquals(List.of(new StudentRecord("Omar", "C", 70)), students);
        String out = output.toString();
        assertTrue(out.contains("Line 2 skipped: expected 3 columns -> Ali,A"));
        assertTrue(out.contains("Line 3 skipped: expected 3 columns -> Sara,B,92,x"));
    }

    @Test
    void skipsLinesWhereGradeIsNotANumberAndSaysWhich() {
        List<Student> students = parser.parse(List.of(HEADER, "Ali,A,eighty", "Omar,C,70"));

        assertEquals(List.of(new StudentRecord("Omar", "C", 70)), students);
        assertTrue(output.toString().contains("Line 2 skipped: grade is not a number -> Ali,A,eighty"));
    }

    @Test
    void acceptsBoundaryGrades() {
        List<Student> students = parser.parse(List.of(HEADER, "Ali,A,0", "Sara,B,100"));

        assertEquals(0, students.get(0).Sgrade());
        assertEquals(100, students.get(1).Sgrade());
    }
}
