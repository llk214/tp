package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.TransactionSorter;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Implements the core filtering engine for transactions.
 * This command serves two purposes:
 * 1. Acts as a standalone "filter" command for searching transactions.
 * 2. Provides the shared buildPredicate() logic used by ListCommand, DeleteCommand, and SummarizeCommand.
 *
 * Supported flags: --type, --category, --amount (with operators), --date, --date-from, --date-to.
 * Supported operators for --amount: -gt, -gte, -eq, -lt, -leq.
 * Multiple flags are combined using Predicate.and() to narrow results.
 */
public class FilterCommand extends Command {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String sortField;
    private String sortDirection;

    public FilterCommand(String rawArgs) {
        super(rawArgs);
        parseSortOverride(rawArgs);
    }

    private void parseSortOverride(String rawArgs) {
        this.sortField = "";
        this.sortDirection = "asc";
        if (rawArgs == null || rawArgs.isEmpty()) {
            return;
        }
        String[] tokens = rawArgs.trim().split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("--sort") && i + 1 < tokens.length) {
                this.sortField = tokens[i + 1].toLowerCase();
                if (i + 2 < tokens.length && TransactionSorter.isValidDirection(tokens[i + 2].toLowerCase())) {
                    this.sortDirection = tokens[i + 2].toLowerCase();
                }
            }
        }
    }

    /**
     * Builds a combined Predicate from raw argument flags.
     * This is the "Single Source of Truth" for filtering logic across the app.
     * Other commands (ListCommand, DeleteCommand, SummarizeCommand) call this method
     * to reuse the same filtering behaviour.
     *
     * Tokenizes the rawArgs string by whitespace, then iterates through each token:
     * - If a "--flag" is found, the next token(s) are read as its value.
     * - For --amount, an optional operator (-gt, -gte, -eq, -lt, -leq) can appear before the value.
     * - If no operator is given for --amount, defaults to -eq (exact match).
     * - Multiple flags are chained with Predicate.and() to form a combined filter.
     *
     * @param rawArgs The raw argument string (e.g., "--type debit --amount -gt 200")
     * @return A Predicate that matches transactions meeting all specified criteria.
     *         Returns a predicate that always returns true if rawArgs is null or empty.
     * @throws RLADException if an unknown flag, invalid value, or missing value is encountered
     */
    public static Predicate<Transaction> buildPredicate(String rawArgs) {
        //If no filter arguments provided, return a predicate that matches everything
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            return t -> true;
        }

        //Start with a predicate that matches all transactions, then narrow down with .and()
        Predicate<Transaction> predicate = t -> true;
        String[] tokens = rawArgs.trim().split("\\s+");
        int i = 0;

        //Loop through each token to find flags and their values
        while (i < tokens.length) {
            String token = tokens[i];

            switch (token) {
            case "--type":
                //Next token is the type value (credit or debit)
                i++;
                predicate = predicate.and(buildTypePredicate(getRequiredValue(tokens, i, "--type")));
                i++;
                break;
            case "--category":
                //Next token is the category name to match
                i++;
                predicate = predicate.and(buildCategoryPredicate(getRequiredValue(tokens, i, "--category")));
                i++;
                break;
            case "--amount":
                /*
                 * Amount supports comparison operators: -gt, -gte, -eq, -lt, -leq.
                 * If the next token is an operator, use it; otherwise default to -eq.
                 * This allows both "filter --amount 100" and "filter --amount -gt 100".
                 * Multiple --amount flags can be chained for range queries,
                 * e.g., "--amount -gte 50 --amount -lt 150" filters amounts in [50, 150).
                 */
                i++;
                String operator = "-eq"; //Default operator is exact equality
                if (i < tokens.length && isOperator(tokens[i])) {
                    operator = tokens[i];
                    i++;
                }
                String amountStr = getRequiredValue(tokens, i, "--amount");
                predicate = predicate.and(buildAmountPredicate(operator, amountStr));
                i++;
                break;
            case "--date":
                //Next token is the exact date to match (yyyy-MM-dd format)
                i++;
                String dateStr = getRequiredValue(tokens, i, "--date");
                predicate = predicate.and(buildExactDatePredicate(dateStr));
                i++;
                break;
            case "--date-from":
                //Next token is the start date (inclusive) for range filtering
                i++;
                String fromStr = getRequiredValue(tokens, i, "--date-from");
                predicate = predicate.and(buildDateFromPredicate(fromStr));
                i++;
                break;
            case "--date-to":
                //Next token is the end date (inclusive) for range filtering
                i++;
                String toStr = getRequiredValue(tokens, i, "--date-to");
                predicate = predicate.and(buildDateToPredicate(toStr));
                i++;
                break;
            case "--sort":
                //Skip --sort and its value(s) — handled separately by sort logic
                i++;
                if (i < tokens.length && !tokens[i].startsWith("--")) {
                    i++;
                    if (i < tokens.length && TransactionSorter.isValidDirection(tokens[i].toLowerCase())) {
                        i++;
                    }
                }
                break;
            default:
                throw new RLADException("Unknown filter flag: " + token);
            }
        }

        return predicate;
    }

    /**
     * Retrieves the value token at the given index, ensuring it exists and is not another flag.
     *
     * @param tokens The array of all tokens from the raw argument string
     * @param index The index of the expected value token
     * @param flag The flag name, used in the error message if the value is missing
     * @return The value token at the specified index
     * @throws RLADException if the index is out of bounds or the token starts with "--"
     */
    private static String getRequiredValue(String[] tokens, int index, String flag) {
        if (index >= tokens.length || tokens[index].startsWith("--")) {
            throw new RLADException("Missing value for " + flag);
        }
        return tokens[index];
    }

    /**
     * Checks if a token is a valid comparison operator for --amount filtering.
     *
     * @param token The token to check
     * @return true if the token is one of -gt, -gte, -eq, -lt, -leq
     */
    private static boolean isOperator(String token) {
        return token.equals("-gt") || token.equals("-gte") || token.equals("-eq")
                || token.equals("-lt") || token.equals("-leq");
    }

    /**
     * Builds a predicate that filters by transaction type (credit or debit).
     * Comparison is case-insensitive.
     *
     * @param type The type string to match against
     * @return A Predicate matching transactions of the given type
     * @throws RLADException if the type is not "credit" or "debit"
     */
    private static Predicate<Transaction> buildTypePredicate(String type) {
        String lowerType = type.toLowerCase();
        if (!lowerType.equals("credit") && !lowerType.equals("debit")) {
            throw new RLADException("Invalid --type value. Must be 'credit' or 'debit'.");
        }
        return t -> t.getType().equalsIgnoreCase(lowerType);
    }

    /**
     * Builds a predicate that filters by transaction category.
     * Comparison is case-insensitive. Transactions with null category will not match.
     *
     * @param category The category string to match against
     * @return A Predicate matching transactions with the given category
     */
    private static Predicate<Transaction> buildCategoryPredicate(String category) {
        String lowerCategory = category.toLowerCase();
        return t -> t.getCategory() != null && t.getCategory().equalsIgnoreCase(lowerCategory);
    }

    /**
     * Builds a predicate that filters by transaction amount using a comparison operator.
     * Parses the amount string to a double and applies the specified operator.
     *
     * @param operator The comparison operator (-gt, -gte, -eq, -lt, -leq)
     * @param amountStr The amount value as a string (e.g., "100.50")
     * @return A Predicate matching transactions whose amount satisfies the comparison
     * @throws RLADException if the amount string is not a valid number
     */
    private static Predicate<Transaction> buildAmountPredicate(String operator, String amountStr) {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount: " + amountStr + ". Please enter a valid number.");
        }

        switch (operator) {
        case "-gt":
            return t -> t.getAmount() > amount;
        case "-gte":
            return t -> t.getAmount() >= amount;
        case "-eq":
            //Uses Double.compare to avoid floating-point precision issues
            return t -> Double.compare(t.getAmount(), amount) == 0;
        case "-lt":
            return t -> t.getAmount() < amount;
        case "-leq":
            return t -> t.getAmount() <= amount;
        default:
            throw new RLADException("Unknown operator: " + operator);
        }
    }

    /**
     * Parses a date string into a LocalDate object using the yyyy-MM-dd format.
     *
     * @param dateStr The date string to parse
     * @param flag The flag name, used in the error message if parsing fails
     * @return The parsed LocalDate
     * @throws RLADException if the date string does not match yyyy-MM-dd format
     */
    private static LocalDate parseDate(String dateStr, String flag) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date for " + flag + ": " + dateStr
                    + ". Please use yyyy-MM-dd format.");
        }
    }

    /**
     * Builds a predicate that matches transactions on an exact date.
     *
     * @param dateStr The date string to match (yyyy-MM-dd)
     * @return A Predicate matching transactions on the specified date
     */
    private static Predicate<Transaction> buildExactDatePredicate(String dateStr) {
        LocalDate date = parseDate(dateStr, "--date");
        return t -> t.getDate().isEqual(date);
    }

    /**
     * Builds a predicate that matches transactions on or after the given date (inclusive).
     *
     * @param dateStr The start date string (yyyy-MM-dd)
     * @return A Predicate matching transactions with date >= the specified date
     */
    private static Predicate<Transaction> buildDateFromPredicate(String dateStr) {
        LocalDate from = parseDate(dateStr, "--date-from");
        return t -> !t.getDate().isBefore(from);
    }

    /**
     * Builds a predicate that matches transactions on or before the given date (inclusive).
     *
     * @param dateStr The end date string (yyyy-MM-dd)
     * @return A Predicate matching transactions with date <= the specified date
     */
    private static Predicate<Transaction> buildDateToPredicate(String dateStr) {
        LocalDate to = parseDate(dateStr, "--date-to");
        return t -> !t.getDate().isAfter(to);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // Step 1: Get all transactions from the manager
        ArrayList<Transaction> allTransactions = transactions.getTransactions();

        if (allTransactions.isEmpty()) {
            ui.showResult("Your wallet is empty! Use 'add' to record a transaction.");
            return;
        }

        // Step 2: Build predicate from filter arguments and apply to transactions
        Predicate<Transaction> predicate = buildPredicate(rawArgs);
        ArrayList<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (predicate.test(t)) {
                filtered.add(t);
            }
        }

        // Step 3: Display results or inform user if no matches found
        if (filtered.isEmpty()) {
            ui.showResult("No transactions match your filter criteria.");
            return;
        }

        // Step 4: Apply sorting (--sort override > global sort > insertion order)
        if (!sortField.isEmpty() && TransactionSorter.isValidSortField(sortField)) {
            filtered = TransactionSorter.sort(filtered, sortField, sortDirection);
        } else {
            String globalField = transactions.getGlobalSortField();
            if (!globalField.isEmpty()) {
                filtered = TransactionSorter.sort(filtered, globalField,
                        transactions.getGlobalSortDirection());
            }
        }

        for (Transaction t : filtered) {
            ui.showResult(t.toString());
        }
    }

    @Override
    public boolean hasValidArgs() {
        //Filter requires at least one flag to be provided
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
