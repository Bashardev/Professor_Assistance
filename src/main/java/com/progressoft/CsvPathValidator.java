package com.progressoft;

import java.nio.file.Files;
import java.nio.file.Path;

public class CsvPathValidator {

    private CsvPathValidator() {
    }

    // Removes spaces and surrounding quotes,
    public static String clean(String typed) {
        String text = typed.trim();
        if (text.startsWith("\"")) {
            text = text.substring(1);
        }
        if (text.endsWith("\"")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }


    // Returns the error message, or null if the path is fine.
    public static String check(String typed) {
        String pathText = clean(typed);

        String nameError = checkName(pathText);
        if (nameError != null) {
            return nameError;
        }
        Path path = Path.of(pathText);
        if (!Files.isRegularFile(path)) {
            return "File not found: " + path;
        }
        return null;
    }

    // Returns the error message, or null if the path is fine. checking the file is it available .
    public static String checkOutput(String typed) {
        String pathText = clean(typed);

        String nameError = checkName(pathText);
        if (nameError != null) {
            return nameError;
        }
        Path path = Path.of(pathText);
        if (Files.isDirectory(path)) {
            return "That is a folder, not a file: " + path;
        }
        Path folder = path.getParent();
        if (folder != null && !Files.isDirectory(folder)) {
            return "Folder not found: " + folder;
        }
        return null;
    }

    private static String checkName(String pathText) {
        if (pathText.isEmpty()) {
            return "Please enter a path.";
        }
        if (!pathText.toLowerCase().endsWith(".csv")) {
            return "The file must be a .csv file.";
        }
        return null;
    }
}
