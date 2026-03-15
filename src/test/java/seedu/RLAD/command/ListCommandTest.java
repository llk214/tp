package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ListCommandTest {
    private TransactionManager tm;
    private Ui ui;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        tm = new TransactionManager();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        InputStream emptyInput = new java.io.ByteArrayInputStream(new byte[0]);
        System.setIn(emptyInput);
        ui = new Ui();
        outputStream.reset();
    }

    @Test
    public void execute_emptyList_showsEmptyMessage() {
        ListCommand cmd = new ListCommand("");
        cmd.execute(tm, ui);
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Your wallet is empty"));
    }

    @Test
    public void execute_withTransactions_showsAll() {
        tm.addTransaction(new Transaction("debit", "food", 10.00,
                LocalDate.of(2026, 1, 15), "Lunch"));
        tm.addTransaction(new Transaction("credit", "salary", 3000.00,
                LocalDate.of(2026, 1, 1), "Pay"));

        ListCommand cmd = new ListCommand("");
        cmd.execute(tm, ui);
        String output = outputStream.toString();
        assertTrue(output.contains("Lunch"));
        assertTrue(output.contains("Pay"));
    }

    @Test
    public void execute_withSortOverride_sortsByAmount() {
        tm.addTransaction(new Transaction("debit", "food", 50.00,
                LocalDate.of(2026, 1, 15), "Expensive"));
        tm.addTransaction(new Transaction("debit", "food", 5.00,
                LocalDate.of(2026, 1, 10), "Cheap"));

        ListCommand cmd = new ListCommand("--sort amount");
        cmd.execute(tm, ui);
        String output = outputStream.toString();
        int cheapIdx = output.indexOf("Cheap");
        int expensiveIdx = output.indexOf("Expensive");
        assertTrue(cheapIdx < expensiveIdx, "Cheap should appear before Expensive when sorted by amount asc");
    }

    @Test
    public void execute_withSortOverrideDesc_sortsByAmountDesc() {
        tm.addTransaction(new Transaction("debit", "food", 50.00,
                LocalDate.of(2026, 1, 15), "Expensive"));
        tm.addTransaction(new Transaction("debit", "food", 5.00,
                LocalDate.of(2026, 1, 10), "Cheap"));

        ListCommand cmd = new ListCommand("--sort amount desc");
        cmd.execute(tm, ui);
        String output = outputStream.toString();
        int cheapIdx = output.indexOf("Cheap");
        int expensiveIdx = output.indexOf("Expensive");
        assertTrue(expensiveIdx < cheapIdx, "Expensive should appear before Cheap when sorted by amount desc");
    }

    @Test
    public void execute_globalSortApplied_whenNoOverride() {
        tm.setGlobalSort("amount", "asc");
        tm.addTransaction(new Transaction("debit", "food", 50.00,
                LocalDate.of(2026, 1, 15), "Expensive"));
        tm.addTransaction(new Transaction("debit", "food", 5.00,
                LocalDate.of(2026, 1, 10), "Cheap"));

        ListCommand cmd = new ListCommand("");
        cmd.execute(tm, ui);
        String output = outputStream.toString();
        int cheapIdx = output.indexOf("Cheap");
        int expensiveIdx = output.indexOf("Expensive");
        assertTrue(cheapIdx < expensiveIdx, "Global sort should apply when no override");
    }

    @Test
    public void execute_sortOverride_overridesGlobalSort() {
        tm.setGlobalSort("amount", "asc");
        tm.addTransaction(new Transaction("debit", "food", 50.00,
                LocalDate.of(2026, 1, 15), "Expensive"));
        tm.addTransaction(new Transaction("debit", "food", 5.00,
                LocalDate.of(2026, 1, 10), "Cheap"));

        // Override with desc
        ListCommand cmd = new ListCommand("--sort amount desc");
        cmd.execute(tm, ui);
        String output = outputStream.toString();
        int cheapIdx = output.indexOf("Cheap");
        int expensiveIdx = output.indexOf("Expensive");
        assertTrue(expensiveIdx < cheapIdx, "Override should take precedence over global sort");
    }

    @Test
    public void hasValidArgs_noArgs_returnsTrue() {
        ListCommand cmd = new ListCommand("");
        assertTrue(cmd.hasValidArgs());
    }

    @Test
    public void hasValidArgs_validSortField_returnsTrue() {
        ListCommand cmd = new ListCommand("--sort amount");
        assertTrue(cmd.hasValidArgs());
    }

    @Test
    public void hasValidArgs_invalidSortField_returnsFalse() {
        ListCommand cmd = new ListCommand("--sort name");
        assertFalse(cmd.hasValidArgs());
    }
}
