package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.TransactionSorter;
import seedu.RLAD.Ui;

import java.util.ArrayList;

/**
 * Displays all transactions with optional sort override.
 * Uses the global sort order by default.
 * A --sort flag temporarily overrides the global sort for this command only.
 *
 * Example usage:
 *   list                -> Show all transactions (global sort)
 *   list --sort amount  -> Override: sort by amount ascending
 *   list --sort date desc -> Override: sort by date descending
 */
public class ListCommand extends Command {
    private String sortField;
    private String sortDirection;

    public ListCommand(String rawArgs) {
        super(rawArgs);
        parseSortArgs(rawArgs);
    }

    private void parseSortArgs(String rawArgs) {
        this.sortField = "";
        this.sortDirection = "";
        if (rawArgs == null || rawArgs.isEmpty()) {
            return;
        }
        String[] tokens = rawArgs.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("--sort") && i + 1 < tokens.length) {
                this.sortField = tokens[i + 1].toLowerCase();
                if (i + 2 < tokens.length && TransactionSorter.isValidDirection(tokens[i + 2].toLowerCase())) {
                    this.sortDirection = tokens[i + 2].toLowerCase();
                } else {
                    this.sortDirection = "asc";
                }
            }
        }
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        ArrayList<Transaction> results = transactions.getTransactions();

        if (results.isEmpty()) {
            ui.showResult("Your wallet is empty! Use 'add' to record a transaction.");
            return;
        }

        results = applySorting(results, transactions);

        for (Transaction transaction : results) {
            ui.showResult(transaction.toString());
        }
    }

    /**
     * Applies sorting: uses --sort override if provided, otherwise falls back to global sort.
     */
    private ArrayList<Transaction> applySorting(ArrayList<Transaction> results,
                                                TransactionManager transactions) {
        if (!sortField.isEmpty()) {
            return TransactionSorter.sort(results, sortField, sortDirection);
        }
        String globalField = transactions.getGlobalSortField();
        if (!globalField.isEmpty()) {
            return TransactionSorter.sort(results, globalField, transactions.getGlobalSortDirection());
        }
        return results;
    }

    @Override
    public boolean hasValidArgs() {
        if (rawArgs == null || rawArgs.isEmpty()) {
            return true;
        }
        if (!sortField.isEmpty() && !TransactionSorter.isValidSortField(sortField)) {
            return false;
        }
        return true;
    }
}
