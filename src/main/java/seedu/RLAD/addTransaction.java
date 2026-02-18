package seedu.RLAD;

import java.util.ArrayList;
import java.util.Scanner;

public class addTransaction {
    private static int expNum = 1;

    public addTransaction (ArrayList<Double> income, ArrayList<Double> expenses, ArrayList<String> itemType, Scanner scanner) {
        System.out.print("Monthly income: $");
        double incomeValue = Double.parseDouble(scanner.nextLine());
        income.add(incomeValue);

        while(true) {
            System.out.print("Expense #" + expNum + ": $");
            double expenseValue = Double.parseDouble(scanner.nextLine());
            if (expenseValue == 0) {
                break;  //Exits the do-while loop if there are no/no more transactions to make
            }
            expenses.add(expenseValue); //Adds expense amount to the back of the expenses array

            System.out.print("Item type #" + expNum + ": ");
            String itemTypeValue = scanner.nextLine();
            itemType.add(itemTypeValue); //Adds item type to the back of the category array
            expNum++;
        }
    }
}