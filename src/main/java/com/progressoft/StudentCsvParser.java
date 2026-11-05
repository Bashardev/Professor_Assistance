package com.progressoft;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StudentCsvParser {

    private final PrintStream out;   // where messages about skipped lines are printed

    public StudentCsvParser(PrintStream out) {
        this.out = out;
    }

    public List<Student> parse(List<String> lines) {
        List<Student> students = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {           // i = 1 so the header is skipped
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;                   // skip blank lines

            String[] parts = line.split(",");
            if (parts.length != 3) {
                out.println("Line " + (i + 1) + " skipped: expected 3 columns -> " + line);
                continue;
            }
            String name = parts[0].trim();
            String sClass = parts[1].trim();
            try {
                int grade = Integer.parseInt(parts[2].trim());
                students.add(new StudentRecord(name, sClass, grade));
            } catch (NumberFormatException e) {
                out.println("Line " + (i + 1) + " skipped: grade is not a number -> " + line);
            }
        }
        return students;
    }
}
