package seedu.RLAD;

import org.junit.jupiter.api.Test;

import seedu.RLAD.command.Command;
import seedu.RLAD.command.FilterCommand;
import seedu.RLAD.command.ListCommand;
import seedu.RLAD.command.SortCommand;
import seedu.RLAD.command.HelpCommand;
import seedu.RLAD.command.AddCommand;
import seedu.RLAD.command.DeleteCommand;
import seedu.RLAD.exception.RLADException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

    @Test
    public void parseCommand_validCommandWithArgs_returnsParts() throws RLADException {
        String[] result = Parser.parseCommand("add --type credit --amount 10");
        assertEquals("add", result[0]);
        assertEquals("--type credit --amount 10", result[1]);
    }

    @Test
    public void parseCommand_commandWithoutArgs_returnsEmptyArgs() throws RLADException {
        String[] result = Parser.parseCommand("list");
        assertEquals("list", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    public void parseCommand_emptyInput_throwsException() {
        assertThrows(RLADException.class, () -> Parser.parseCommand(""));
    }

    @Test
    public void parseCommand_whitespaceOnly_throwsException() {
        assertThrows(RLADException.class, () -> Parser.parseCommand("   "));
    }

    @Test
    public void parseCommand_unknownCommand_throwsException() {
        RLADException ex = assertThrows(RLADException.class,
                () -> Parser.parseCommand("foobar"));
        assertTrue(ex.getMessage().contains("Unknown command"));
    }

    @Test
    public void parseCommand_filterRequiresArgs_throwsException() {
        RLADException ex = assertThrows(RLADException.class,
                () -> Parser.parseCommand("filter"));
        assertTrue(ex.getMessage().contains("requires arguments"));
    }

    @Test
    public void parseCommand_addRequiresArgs_throwsException() {
        assertThrows(RLADException.class, () -> Parser.parseCommand("add"));
    }

    @Test
    public void parseCommand_listNoArgsAllowed_returnsOk() throws RLADException {
        String[] result = Parser.parseCommand("list");
        assertEquals("list", result[0]);
    }

    @Test
    public void parseCommand_sortNoArgsAllowed_returnsOk() throws RLADException {
        String[] result = Parser.parseCommand("sort");
        assertEquals("sort", result[0]);
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws RLADException {
        Command cmd = Parser.parse("list");
        assertInstanceOf(ListCommand.class, cmd);
    }

    @Test
    public void parse_sortCommand_returnsSortCommand() throws RLADException {
        Command cmd = Parser.parse("sort amount desc");
        assertInstanceOf(SortCommand.class, cmd);
    }

    @Test
    public void parse_filterCommand_returnsFilterCommand() throws RLADException {
        Command cmd = Parser.parse("filter --type credit");
        assertInstanceOf(FilterCommand.class, cmd);
    }

    @Test
    public void parse_helpCommand_returnsHelpCommand() throws RLADException {
        Command cmd = Parser.parse("help");
        assertInstanceOf(HelpCommand.class, cmd);
    }

    @Test
    public void parseCommand_caseInsensitive_parsesCorrectly() throws RLADException {
        String[] result = Parser.parseCommand("LIST");
        assertEquals("list", result[0]);
    }

    @Test
    public void parseCommand_leadingTrailingSpaces_trimmed() throws RLADException {
        String[] result = Parser.parseCommand("  list  ");
        assertEquals("list", result[0]);
    }

    // Helper since we don't statically import it
    private static void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
