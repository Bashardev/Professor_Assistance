package com.progressoft;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvPathValidatorTest {

    @TempDir
    Path tempDir;   // JUnit creates an empty folder for each test and deletes it after

    @Test
    void emptyInputAsksForAPath() {
        assertEquals("Please enter a path.", CsvPathValidator.check("   "));
    }

    @Test
    void nonCsvFileIsRejected() throws IOException {
        Path txt = Files.createFile(tempDir.resolve("marks.txt"));

        assertEquals("The file must be a .csv file.", CsvPathValidator.check(txt.toString()));
    }

    @Test
    void missingFileIsRejected() {
        Path missing = tempDir.resolve("missing.csv");

        assertEquals("File not found: " + missing, CsvPathValidator.check(missing.toString()));
    }

    @Test
    void folderNamedLikeCsvIsRejected() throws IOException {
        Path folder = Files.createDirectory(tempDir.resolve("folder.csv"));

        assertEquals("File not found: " + folder, CsvPathValidator.check(folder.toString()));
    }

    @Test
    void existingCsvFileIsAccepted() throws IOException {
        Path csv = Files.createFile(tempDir.resolve("marks.csv"));

        assertNull(CsvPathValidator.check(csv.toString()));
    }

    @Test
    void upperCaseExtensionIsAccepted() throws IOException {
        Path csv = Files.createFile(tempDir.resolve("MARKS.CSV"));

        assertNull(CsvPathValidator.check(csv.toString()));
    }

    @Test
    void cleanRemovesSpacesAndQuotes() {
        assertEquals("/data/marks.csv", CsvPathValidator.clean("  \"/data/marks.csv\"  "));
        assertEquals("/data/marks.csv", CsvPathValidator.clean("/data/marks.csv"));
    }

    @Test
    void quotedPathIsAccepted() throws IOException {
        Path csv = Files.createFile(tempDir.resolve("marks.csv"));

        assertNull(CsvPathValidator.check("\"" + csv + "\""));
    }


    @Test
    void outputEmptyInputAsksForAPath() {
        assertEquals("Please enter a path.", CsvPathValidator.checkOutput("  "));
    }

    @Test
    void outputMustBeACsvFile() {
        assertEquals("The file must be a .csv file.",
                CsvPathValidator.checkOutput(tempDir.resolve("result.txt").toString()));
    }

    @Test
    void outputCannotBeAFolder() throws IOException {
        Path folder = Files.createDirectory(tempDir.resolve("folder.csv"));

        assertEquals("That is a folder, not a file: " + folder,
                CsvPathValidator.checkOutput(folder.toString()));
    }

    @Test
    void outputFolderMustExist() {
        Path missingFolder = tempDir.resolve("missing");

        assertEquals("Folder not found: " + missingFolder,
                CsvPathValidator.checkOutput(missingFolder.resolve("result.csv").toString()));
    }

    @Test
    void outputNewFileInExistingFolderIsAccepted() {
        assertNull(CsvPathValidator.checkOutput(tempDir.resolve("result.csv").toString()));
    }

    @Test
    void outputExistingFileIsAccepted() throws IOException {
        Path file = Files.createFile(tempDir.resolve("result.csv"));

        assertNull(CsvPathValidator.checkOutput(file.toString()));
    }

    @Test
    void outputFileNameWithoutFolderIsAccepted() {
        assertNull(CsvPathValidator.checkOutput("result.csv"));
    }

    @Test
    void outputPathInQuotesIsAccepted() {
        assertNull(CsvPathValidator.checkOutput("\"" + tempDir.resolve("result.csv") + "\""));
    }
}
