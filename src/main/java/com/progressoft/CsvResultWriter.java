package com.progressoft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvResultWriter {

    private CsvResultWriter() {
    }

    public static void write(Path path, List<List<String>> rows) throws IOException {
        Files.writeString(path, toCsv(rows));
    }

    public static String toCsv(List<List<String>> rows) {
        StringBuilder csv = new StringBuilder();
        for (List<String> row : rows) {
            List<String> values = new ArrayList<>();
            for (String value : row) {
                values.add(escape(value));
            }
            csv.append(String.join(",", values)).append("\n");
        }
        return csv.toString();
    }

    // A value with a comma, quote or new line must be put in quotes, and any quote inside doubled,
    // otherwise programs like Excel would split it into the wrong columns.
    private static String escape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
