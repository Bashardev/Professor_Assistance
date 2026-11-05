package com.progressoft;

import java.io.IOException;
import java.util.List;

// Anything that can give the program its list of students: a CSV file, a database, an API...
public interface StudentSource {
    List<Student> load() throws IOException;
}
