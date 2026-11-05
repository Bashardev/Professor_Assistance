package com.progressoft;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Main {

    private Main() {

    }

    static void main() throws IOException {  //THis help to create the in and out the reading and the writing once and the get used by  the other classes .
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner in, PrintStream out) throws IOException {
        StudentSource source = new CsvStudentSource(in, out);   // the only line that knows "CSV"
        List<Student> students = source.load();

        out.println("Loaded " + students.size() + " students:");
        for (Student s : students) {
            out.println(s.Sname() + " | " + s.Sclass() + " | " + s.Sgrade());
        }

        new Menu(students, in, out).show();
    }
}
