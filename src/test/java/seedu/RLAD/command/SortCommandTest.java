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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SortCommandTest {
    private TransactionManager tm;
    private Ui ui;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        tm = new TransactionManager();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        // Redirect stdin to avoid Scanner blocking in Ui constructor
        InputStream emptyInput = new java.io.ByteArrayInputStream(new byte[0]);
        System.setIn(emptyInput);
        ui = new Ui();
        // Clear constructor output
        outputStream.reset();
    }

    @Test
    public void execute_noArgs_showsNoSortOrder() {
        SortCommand cmd = new SortCommand("");
        cmd.execute(tm, ui);
        String output = outputStream.toString().trim();
        assertTrue(output.contains("No sort order set"));
    }

    @Test
    public void execute_noArgs_showsCurrentSort() {
        tm.setGlobalSort("amount", "asc");
        SortCommand cmd = new SortCommand("");
        cmd.execute(tm, ui);
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Current sort: amount (asc)"));
    }

    @Test
    public void execute_amountAsc_setsSortOrder() {
        SortCommand cmd = new SortCommand("amount");
        cmd.execute(tm, ui);
        assertEquals("amount", tm.getGlobalSortField());
        assertEquals("asc", tm.getGlobalSortDirection());
    }

    @Test
    public void execute_dateDesc_setsSortOrder() {
        SortCommand cmd = new SortCommand("date desc");
        cmd.execute(tm, ui);
        assertEquals("date", tm.getGlobalSortField());
        assertEquals("desc", tm.getGlobalSortDirection());
    }

    @Test
    public void execute_reset_clearsSortOrder() {
        tm.setGlobalSort("amount", "desc");
        SortCommand cmd = new SortCommand("reset");
        cmd.execute(tm, ui);
        assertEquals("", tm.getGlobalSortField());
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Sort order cleared"));
    }

    @Test
    public void hasValidArgs_emptyArgs_returnsTrue() {
        SortCommand cmd = new SortCommand("");
        assertTrue(cmd.hasValidArgs());
    }

    @Test
    public void hasValidArgs_reset_returnsTrue() {
        SortCommand cmd = new SortCommand("reset");
        assertTrue(cmd.hasValidArgs());
    }

    @Test
    public void hasValidArgs_validFieldAndDirection_returnsTrue() {
        SortCommand cmd = new SortCommand("amount desc");
        assertTrue(cmd.hasValidArgs());
    }

    @Test
    public void hasValidArgs_invalidField_returnsFalse() {
        SortCommand cmd = new SortCommand("name");
        assertFalse(cmd.hasValidArgs());
    }

    @Test
    public void hasValidArgs_invalidDirection_returnsFalse() {
        SortCommand cmd = new SortCommand("amount sideways");
        assertFalse(cmd.hasValidArgs());
    }
}
