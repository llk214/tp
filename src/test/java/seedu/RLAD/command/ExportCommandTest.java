package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportCommandTest {

    @TempDir
    Path tempDir;

    private TransactionManager tm;

    private Ui uiWith(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return new Ui();
    }

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
    }

    @Test
    void execute_withTransactions_createsFile() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Lunch"));
        tm.addTransaction(new Transaction("debit", "transport", 3.50,
                LocalDate.of(2026, 3, 16), "Bus"));

        Path outFile = tempDir.resolve("out.csv");
        ExportCommand cmd = new ExportCommand(outFile.toString());
        cmd.execute(tm, uiWith(""));

        assertTrue(Files.exists(outFile));
        List<String> lines = Files.readAllLines(outFile);
        assertEquals(3, lines.size());
        assertEquals("HashID,Type,Category,Amount,Date,Description", lines.get(0));
    }

    @Test
    void execute_emptyTransactions_noFileCreated() throws RLADException {
        Path outFile = tempDir.resolve("out.csv");
        ExportCommand cmd = new ExportCommand(outFile.toString());
        cmd.execute(tm, uiWith(""));
        assertFalse(Files.exists(outFile));
    }

    @Test
    void execute_specialCharsInDescription_exportedCorrectly() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 25.00,
                LocalDate.of(2026, 1, 1), "Meal, \"fancy\" place"));

        Path outFile = tempDir.resolve("special.csv");
        ExportCommand cmd = new ExportCommand(outFile.toString());
        cmd.execute(tm, uiWith(""));

        List<String> lines = Files.readAllLines(outFile);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("\"Meal, \"\"fancy\"\" place\""));
    }

    @Test
    void execute_defaultFilename_usesDateBasedName() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 10.00,
                LocalDate.of(2026, 1, 1), "Test"));

        ExportCommand cmd = new ExportCommand("");
        cmd.execute(tm, uiWith(""));

        String expectedName = "transactions_"
                + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";
        Path created = Paths.get(expectedName);
        try {
            assertTrue(Files.exists(created));
        } finally {
            Files.deleteIfExists(created);
        }
    }

    @Test
    void execute_dataDirectoryInPath_throwsException() {
        Path outFile = tempDir.resolve("data").resolve("backup.csv");
        ExportCommand cmd = new ExportCommand(outFile.toString());
        assertThrows(RLADException.class, () -> cmd.execute(tm, uiWith("")));
    }

    @Test
    void execute_missingParentDirectory_userConfirms_createsDirectoryAndFile() throws Exception {
        tm.addTransaction(new Transaction("debit", "food", 10.00,
                LocalDate.of(2026, 1, 1), "Test"));

        Path newDir = tempDir.resolve("exports");
        Path outFile = newDir.resolve("backup.csv");
        assertFalse(Files.isDirectory(newDir));

        ExportCommand cmd = new ExportCommand(outFile.toString());
        cmd.execute(tm, uiWith("y\n"));

        assertTrue(Files.isDirectory(newDir));
        assertTrue(Files.exists(outFile));
    }

    @Test
    void execute_missingParentDirectory_userDeclines_cancelsExport() throws Exception {
        tm.addTransaction(new Transaction("debit", "food", 10.00,
                LocalDate.of(2026, 1, 1), "Test"));

        Path newDir = tempDir.resolve("exports");
        Path outFile = newDir.resolve("backup.csv");

        ExportCommand cmd = new ExportCommand(outFile.toString());
        cmd.execute(tm, uiWith("n\n"));

        assertFalse(Files.exists(outFile));
    }

    @Test
    void hasValidArgs_always_returnsTrue() {
        assertEquals(true, new ExportCommand("").hasValidArgs());
        assertEquals(true, new ExportCommand("backup.csv").hasValidArgs());
    }
}
