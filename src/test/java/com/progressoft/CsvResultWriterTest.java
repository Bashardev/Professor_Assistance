package com.progressoft;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvResultWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void joinsValuesWithCommasAndRowsWithNewLines() {
        String csv = CsvResultWriter.toCsv(List.of(
                List.of("Name", "Grade"),
                List.of("Ali", "85")));

        assertEquals("Name,Grade\nAli,85\n", csv);
    }

    @Test
    void emptyRowBecomesBlankLine() {
        assertEquals("a\n\nb\n", CsvResultWriter.toCsv(List.of(List.of("a"), List.of(), List.of("b"))));
    }

    @Test
    void noRowsGivesEmptyText() {
        assertEquals("", CsvResultWriter.toCsv(List.of()));
    }

    @Test
    void valueWithCommaIsPutInQuotes() {
        assertEquals("\"Smith, John\",90\n", CsvResultWriter.toCsv(List.of(List.of("Smith, John", "90"))));
    }

    @Test
    void quoteInsideValueIsDoubled() {
        assertEquals("\"Ali \"\"the best\"\"\"\n", CsvResultWriter.toCsv(List.of(List.of("Ali \"the best\""))));
    }

    @Test
    void valueWithNewLineIsPutInQuotes() {
        assertEquals("\"line1\nline2\"\n", CsvResultWriter.toCsv(List.of(List.of("line1\nline2"))));
        assertEquals("\"line1\rline2\"\n", CsvResultWriter.toCsv(List.of(List.of("line1\rline2"))));
    }

    @Test
    void writeCreatesTheFile() throws IOException {
        Path file = tempDir.resolve("result.csv");

        CsvResultWriter.write(file, List.of(List.of("Median", "80.00")));

        assertEquals("Median,80.00\n", Files.readString(file));
    }
}
