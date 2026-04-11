package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.storage.CsvStorageManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Path path = Paths.get(filename);
        validateNoDataDirectory(path);

        Path parent = path.getParent();
        if (parent != null && !Files.isDirectory(parent)) {
            boolean create = ui.askYesNo("Directory '" + parent + "' does not exist. Create it?");
            if (!create) {
                ui.showResult("Export cancelled.");
                return;
            }
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new RLADException("Failed to create directory: " + e.getMessage());
            }
        }

        ArrayList<Transaction> txns = transactions.getTransactions();
        if (txns.isEmpty()) {
            ui.showResult("No transactions to export.");
            return;
        }

        CsvStorageManager.exportToCsv(txns, filename);
        ui.showResult("Exported " + txns.size() + " transactions to: " + path.toAbsolutePath());
    }

    /**
     * Validates that no component of the path is the reserved directory name "data".
     *
     * @param path the export path to check
     * @throws RLADException if a path component is named "data"
     */
    private void validateNoDataDirectory(Path path) throws RLADException {
        Path parent = path.getParent();
        if (parent == null) {
            return;
        }
        for (Path component : parent) {
            if (component.toString().equalsIgnoreCase("data")) {
                throw new RLADException("'data' is a reserved directory name used for backups. "
                        + "Please use a different directory name.");
            }
        }
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
