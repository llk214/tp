package seedu.RLAD;

import java.util.ArrayList;

public class TransactionList {

    public TransactionList(ArrayList<Double> income, ArrayList<Double> expenses, ArrayList<String> itemType) {
        System.out.println("_________________________________");
        System.out.println("      TRANSACTION HISTORY        ");
        System.out.println("_________________________________");

        if (income.isEmpty()) {
            System.out.println("Income: $0.00");
        } else {
            // Assuming the last entered income is the current one
            double currentIncome = income.get(income.size() - 1);
            System.out.printf("Monthly Income: $%.2f%n", currentIncome);
        }

        System.out.println("---------------------------------");

        System.out.println("Expenses:");

        if (expenses.isEmpty()) {
            System.out.println("  [No expenses recorded]");
        } else {
            for (int i = 0; i < expenses.size(); i++) {
                String description = itemType.get(i);
                Double amount = expenses.get(i);

                System.out.printf("  %d. %s: $%.2f%n", (i + 1), description, amount);
            }
        }

        if (!income.isEmpty()) {
            double totalIncome = income.get(income.size() - 1);
            double totalExpenses = 0;
            for (double d : expenses) totalExpenses += d;

            System.out.println("---------------------------------");
            System.out.printf("Remaining Balance: $%.2f%n", (totalIncome - totalExpenses));
        }

        System.out.println("_________________________________");
    }
}