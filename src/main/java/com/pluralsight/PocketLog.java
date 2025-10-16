package com.pluralsight;

// Import necessary classes
import java.io.File;  // Tool for file operations
import java.io.FileNotFoundException; // Tool for file operations
import java.io.FileWriter;  // Tool used for file writing
import java.io.IOException;  // Tool used for general file I/O errors
import java.time.LocalDate;  // Tool to work with dates
import java.time.format.DateTimeFormatter;  // Tool to define the date format
import java.util.ArrayList;  // Tools for storing lists of transactions
import java.util.Scanner;  // Tools used for user input and file reading

public class PocketLog {

    // Core Application Constants
    private static final String FILENAME = "transactions.csv";
    private static final Scanner menuScanner = new Scanner(System.in);

    //  The main entry point
    public static void main(String[] args) {

        System.out.println("Welcome to PocketLog!");

        boolean running = true;

        while (running) {

            displayMainMenu();

            System.out.print("Enter your choice: ");
            String choice = menuScanner.nextLine().trim().toUpperCase();

            // Navigation logic using the switch statement
            switch (choice) {
                case "D":
                    recordTransaction("DEPOSIT");
                    break;
                case "P":
                    recordTransaction("PAYMENT");
                    break;
                case "L":
                    // Calling the fully implemented Ledger function
                    displayLedger();
                    break;
                case "S":
                    // Placeholder for Custom Search
                    System.out.println("-> S: Going to Custom Search screen (Not implemented)...");
                    break;
                case "X":
                    running = false; // Exit the loop
                    break;
                default:
                    System.out.println(" Invalid option. Please enter D, P, L, S, or X.");
                    break;
            }

            if (running) {
                System.out.println("\nPress ENTER to return to the main menu...");
                menuScanner.nextLine();
            }
        }

        menuScanner.close();
        System.out.println("Application closed. Goodbye!");
    }

    // The Menu Display
    private static void displayMainMenu() {
        System.out.println("\n--- POCKETLOG MAIN MENU ---");
        System.out.println("D) Add Deposit");
        System.out.println("P) Make Payment (Debit)");
        System.out.println("L) Ledger (View all transactions)");
        System.out.println("S) Custom Search");
        System.out.println("X) Exit");
    }

    // Record Transaction
    private static void recordTransaction(String type) {
        System.out.println("\n--- RECORD NEW " + type + " ---");

        try {
            // Gather Input
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String date = menuScanner.nextLine();
            System.out.print("Enter Time (HH:MM): ");
            String time = menuScanner.nextLine();
            System.out.print("Enter Description: ");
            String description = menuScanner.nextLine();
            System.out.print("Enter Vendor/Source: ");
            String vendor = menuScanner.nextLine();
            System.out.print("Enter Amount (e.g., 50.00): ");
            String stringAmount = menuScanner.nextLine();

            // Process Amount
            double amount = Double.parseDouble(stringAmount);

            if (type.equals("PAYMENT")) {
                if (amount > 0) {
                    amount = amount * -1;
                }
            }

            // Create Object
            Transaction newTransaction = new Transaction(date, time, description, vendor, amount);

            // File Writing
            try {
                FileWriter writer = new FileWriter(FILENAME, true);
                writer.write(newTransaction.toString());
                writer.write("\n");
                writer.close();

                System.out.println("\n Success! " + type + " recorded.");
                System.out.println("   -> " + newTransaction.toString());

            } catch (IOException e) {
                System.out.println("\n ERROR: Could not write to the transaction file.");
                System.out.println("   Details: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("\n ERROR: Invalid amount entered. Please use a number (e.g., 50.00).");
        }
    }

    // Display Ledger
    private static void displayLedger() {
        boolean runningLedger = true;

        final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String searchVendor = "";

        while (runningLedger) {
            System.out.println("\n--- LEDGER SUB-MENU ---");
            System.out.println("A) All Transactions");
            System.out.println("D) Deposits (Income > 0)");
            System.out.println("P) Payments (Expenses < 0)");
            System.out.println("V) Vendor Search");
            System.out.println("M) Month-to-Date (MTD) Report");
            System.out.println("H) Home (Return to Main Menu)");
            System.out.print("Enter your filter choice: ");

            String filterChoice = menuScanner.nextLine().trim().toUpperCase();

            if (filterChoice.equals("H")) {
                runningLedger = false;
                continue;
            }

            if (filterChoice.equals("V")) {
                System.out.print("Enter the Vendor/Source name to search: ");
                searchVendor = menuScanner.nextLine().trim();
            }

            boolean isMTD = filterChoice.equals("M");
            LocalDate startOfMonth = null;
            LocalDate today = null;

            if (isMTD) {
                today = LocalDate.now();

                startOfMonth = today.withDayOfMonth(1);
                System.out.printf("\n--- Showing Transactions from %s to %s ---\n", startOfMonth, today);
            }

            if (!filterChoice.equals("A") && !filterChoice.equals("D") && !filterChoice.equals("P")) {
                System.out.println(" Invalid filter choice. Please enter A, D, P, or H.");
                System.out.println("Press ENTER to continue...");
                menuScanner.nextLine();
                continue;
            }

            // Read All Transactions into a list
            ArrayList<String> transactions = new ArrayList<>();
            try {
                File file = new File(FILENAME);
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {
                    transactions.add(fileScanner.nextLine());
                }
                fileScanner.close();

                if (transactions.isEmpty()) {
                    System.out.println("\nNo transactions found. Use D or P to add a record.");
                    continue;
                }

                // Display Header
                System.out.println("\n------------------------------------------");
                System.out.println("DATE|TIME|DESCRIPTION|VENDOR|AMOUNT");
                System.out.println("------------------------------------------");

                // Loop Backwards to filter and display
                for (int i = transactions.size() - 1; i >= 0; i--) {
                    String transactionLine = transactions.get(i);
                    String[] parts = transactionLine.split("\\|");

                    String vendor = parts[3];
                    String amountString = parts[4];
                    double amount = Double.parseDouble(amountString);

                    boolean shouldDisplay = false;

                    if (filterChoice.equals("A")) {
                        shouldDisplay = true;
                    } else if (filterChoice.equals("D") && amount > 0) {
                        shouldDisplay = true; // Deposits are positive
                    } else if (filterChoice.equals("P") && amount < 0) {
                        shouldDisplay = true; // Payments are negative
                    } else if (filterChoice.equals("V") && vendor.equalsIgnoreCase(searchVendor)) {
                        shouldDisplay = true;
                    }

                    if (shouldDisplay) {
                        System.out.println(transactionLine);
                    }
                }

            } catch (FileNotFoundException e) {
                System.out.println("\n ERROR: The transaction file (" + FILENAME + ") was not found.");
                System.out.println("Please enter a Deposit (D) or Payment (P) first to create the file.");
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("\n FATAL ERROR: Data file appears corrupted. Cannot read transaction amounts.");
            }

            System.out.println("------------------------------------------");
            System.out.println("Press ENTER to view another filter, or H to go home.");
            menuScanner.nextLine();
        }
    }
}