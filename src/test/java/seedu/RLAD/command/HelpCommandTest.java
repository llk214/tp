package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {

    private TransactionManager tm;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private Ui createUi() {
        System.setIn(new ByteArrayInputStream("".getBytes()));
        return new Ui();
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void execute_noArgs_showsAllCommands() {
        HelpCommand cmd = new HelpCommand("");
        cmd.execute(tm, createUi());
        String output = getOutput();
        assertTrue(output.contains("Available actions:"));
        assertTrue(output.contains("add"));
        assertTrue(output.contains("delete"));
        assertTrue(output.contains("list"));
        assertTrue(output.contains("filter"));
        assertTrue(output.contains("search"));
        assertTrue(output.contains("help"));
        assertTrue(output.contains("exit"));
    }

    @Test
    void execute_nullArgs_showsAllCommands() {
        HelpCommand cmd = new HelpCommand(null);
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Available actions:"));
    }

    @Test
    void execute_addArg_showsAddManual() {
        HelpCommand cmd = new HelpCommand("add");
        cmd.execute(tm, createUi());
        String output = getOutput();
        assertTrue(output.contains("Command: add"));
        assertTrue(output.contains("--type"));
        assertTrue(output.contains("--amount"));
    }

    @Test
    void execute_modifyArg_showsModifyManual() {
        HelpCommand cmd = new HelpCommand("modify");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: modify"));
    }

    @Test
    void execute_deleteArg_showsDeleteManual() {
        HelpCommand cmd = new HelpCommand("delete");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: delete"));
    }

    @Test
    void execute_listArg_showsListManual() {
        HelpCommand cmd = new HelpCommand("list");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: list"));
    }

    @Test
    void execute_summarizeArg_showsSummarizeManual() {
        HelpCommand cmd = new HelpCommand("summarize");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: summarize"));
    }

    @Test
    void execute_exportArg_showsExportManual() {
        HelpCommand cmd = new HelpCommand("export");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: export"));
    }

    @Test
    void execute_importArg_showsImportManual() {
        HelpCommand cmd = new HelpCommand("import");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: import"));
    }

    @Test
    void execute_clearArg_showsClearManual() {
        HelpCommand cmd = new HelpCommand("clear");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: clear"));
    }

    @Test
    void execute_sortArg_showsSortManual() {
        HelpCommand cmd = new HelpCommand("sort");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: sort"));
    }

    @Test
    void execute_budgetArg_showsBudgetManual() {
        HelpCommand cmd = new HelpCommand("budget");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: budget"));
    }

    @Test
    void execute_filterArg_showsFilterManual() {
        HelpCommand cmd = new HelpCommand("filter");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: filter"));
    }

    @Test
    void execute_searchArg_showsSearchManual() {
        HelpCommand cmd = new HelpCommand("search");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: search"));
    }

    @Test
    void execute_helpArg_showsHelpManual() {
        HelpCommand cmd = new HelpCommand("help");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: help"));
    }

    @Test
    void execute_exitArg_showsExitManual() {
        HelpCommand cmd = new HelpCommand("exit");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: exit"));
    }

    @Test
    void execute_unknownCommand_showsError() {
        HelpCommand cmd = new HelpCommand("foobar");
        cmd.execute(tm, createUi());
        String output = getOutput();
        assertTrue(output.contains("Unknown command: foobar"));
        assertTrue(output.contains("Type 'help' to see available commands."));
    }

    @Test
    void execute_caseInsensitive_showsManual() {
        HelpCommand cmd = new HelpCommand("ADD");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: add"));
    }

    @Test
    void execute_extraWhitespace_showsManual() {
        HelpCommand cmd = new HelpCommand("  list  ");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Command: list"));
    }

    @Test
    void hasValidArgs_alwaysTrue() {
        HelpCommand cmd = new HelpCommand("anything");
        assertTrue(cmd.hasValidArgs());
    }
}
