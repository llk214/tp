package seedu.rlad;

import seedu.rlad.command.AddCommand;
import seedu.rlad.command.Command;
import seedu.rlad.command.DeleteCommand;
import seedu.rlad.command.HelpCommand;
import seedu.rlad.command.ListCommand;
import seedu.rlad.command.ModifyCommand;
import seedu.rlad.command.SummarizeCommand;
import seedu.rlad.exception.RLADException;
import seedu.rlad.budget.BudgetCommand;

public class Parser {

    public Parser() {

    }

    // We let RLAD deal with the error catching directly
    // TODO: better input sanitization for null or empty commands
    public static String[] parseCommand(String command) throws RLADException {
        String commandToParse = command.trim();
        if (commandToParse.isEmpty()) {
            throw new RLADException("Empty commands are invalid");
        }

        String[] parts = commandToParse.split("\\s+", 2);
        String action = parts[0].toLowerCase();

        // allow the command where arguments are not required to exit gracefully
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        if (!isValidAction(action)) {
            throw new RLADException("Unknown command: " + action + ". Type 'help' for options.");
        }

        // forces argument invocation
        if (requiresArguments(action) && arguments.isEmpty()) {
            throw new RLADException("The '" + action + "' command requires arguments (e.g., --command).");
        }

        return new String[]{action, arguments};
    }

    private static boolean isValidAction(String action) {
        return action.matches("add|delete|modify|list|summarize|help|exit|budget");
    }

    private static boolean requiresArguments(String action) {
        return action.matches("add|delete|modify|budget");
    }

    public static Command parse(String input) throws RLADException {
        String[] parts = parseCommand(input); // Your existing logic to split into 2 parts
        String action = parts[0].toLowerCase();
        String arguments = parts[1];

        // The Parser acts as a "Factory"
        switch (action) {
        case "add":
            return new AddCommand(arguments);
        case "delete":
            return new DeleteCommand(arguments);
        case "list":
            return new ListCommand(arguments);
        case "help":
            return new HelpCommand(arguments);
        case "summarize":
            return new SummarizeCommand(arguments);
        case "modify":
            return new ModifyCommand(action, arguments);
        case "budget":
            return new BudgetCommand(arguments);
        default:
            throw new RLADException("Unknown command: " + action);
        }
    }
}
