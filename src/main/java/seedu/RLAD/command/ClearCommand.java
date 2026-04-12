package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Clears all transactions from storage after user confirmation.
 */
public class ClearCommand extends Command {

    private static final HashSet<String> VALID_FLAGS = new HashSet<>(Arrays.asList("force"));

    /**
     * Creates a new ClearCommand.
     *
     * @param rawArgs the raw argument string
     */
    public ClearCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException(getInvalidArgsMessage());
        }
        Map<String, String> flags = FilterCommand.parseFlags(rawArgs);
        boolean forceMode = flags.keySet().stream()
                .anyMatch(f -> f.equalsIgnoreCase("force"));

        int count = transactions.getTransactionCount();
        if (count == 0) {
            ui.showResult("No transactions to clear.");
            return;
        }

        if (!forceMode) {
            boolean confirmed = ui.askConfirmation(
                    "WARNING: This will permanently delete all " + count + " transactions.\n"
                            + "This action cannot be undone.");
            if (!confirmed) {
                ui.showResult("Clear cancelled.");
                return;
            }
        }

        transactions.clearAllTransactions();
        ui.showResult("Cleared " + count + " transactions.");
    }

    @Override
    public boolean hasValidArgs() {
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            return true;
        }
        Map<String, String> flags = FilterCommand.parseFlags(rawArgs);
        for (String flag : flags.keySet()) {
            if (!VALID_FLAGS.contains(flag.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an error message for invalid arguments.
     */
    public String getInvalidArgsMessage() {
        return "Invalid argument. Usage: clear [--force]";
    }
}
