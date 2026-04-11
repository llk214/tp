package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.storage.CsvStorageManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Exports all transactions to a CSV file.
 * Syntax: export [filename]
 * If no filename is given, defaults to transactions_YYYY-MM-DD.csv in the current directory.
 */
public class ExportCommand extends Command {

    /**
     * Creates a new ExportCommand.
     *
     * @param rawArgs the raw argument string (optional filename)
     */
    public ExportCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        String filename = rawArgs == null ? "" : rawArgs.trim();
        if (filename.isEmpty()) {
            filename = "transactions_"
                    + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";
        }

        ArrayList<Transaction> txns = transactions.getTransactions();
        if (txns.isEmpty()) {
            ui.showResult("No transactions to export.");
            return;
        }

        CsvStorageManager.exportToCsv(txns, filename);
        ui.showResult("Exported " + txns.size() + " transactions to: " + filename);
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
