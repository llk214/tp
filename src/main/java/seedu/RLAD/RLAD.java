package seedu.RLAD;

import java.util.Scanner;
import java.util.ArrayList;

public class RLAD {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ArrayList<Double> income = new ArrayList<>();
        ArrayList<Double> expenses = new ArrayList<>();
        ArrayList<String> itemType = new ArrayList<>();
        Logo.printRLAD();

        boolean isRunning = true;

        while(isRunning)
        {
            System.out.println("Enter command (add, list, bye):");
            String command = in.nextLine().trim();

            switch (command){
            case "add":
                new addTransaction(income, expenses, itemType, in);
                break;
            case "list":
                new TransactionList(income, expenses, itemType);
                break;
            case "bye":
                System.out.println("Exiting application. Goodbye!");
                isRunning = false;
                break;
            default:
                System.out.println("Unknown command. Please try again.");
            }
        }

        in.close();
    }
}
