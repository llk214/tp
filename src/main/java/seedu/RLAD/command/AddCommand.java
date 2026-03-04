package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

public class AddCommand extends Command {
    public AddCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // TODO: Use a tokenizer or regex to extract --type, --amount, --category, --date, and --description.
        // TODO: Validate that mandatory fields (--type, --amount, --date) are present.
        // TODO: Convert the amount string to double and date string to LocalDate.
        // TODO: Create a new Transaction object and add it via transactions.addTransaction().
        // TODO: Provide success feedback to the user via ui.showResult().
        ui.showResult("AddCommand logic will be implemented here.");
    }

    @Override
    public boolean hasValidArgs() {
        // TODO: Check if rawArgs contains the required flags to prevent runtime RLADExceptions.
        return true;
    }
}
