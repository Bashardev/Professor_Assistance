package com.progressoft;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class CsvStudentSource implements StudentSource {

    private final Scanner in;
    private final PrintStream out;

    public CsvStudentSource(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public List<Student> load() throws IOException {
        String pathText = askForPath();
        List<String> lines = Files.readAllLines(Path.of(pathText));
        return new StudentCsvParser(out).parse(lines);
    }

    private String askForPath() {
        while (true) {
            out.print("Enter the path of the marks CSV file: ");
            String typed = in.nextLine();

            String error = CsvPathValidator.check(typed);   // null means "no error"
            if (error == null) {
                String pathText = CsvPathValidator.clean(typed);
                out.println("File accepted: " + pathText);
                return pathText;
            }

            out.println(error);
        }
    }
}
