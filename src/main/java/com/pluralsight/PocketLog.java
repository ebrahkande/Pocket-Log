package com.pluralsight;

// Import necessary classes
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class PocketLog {

    private static final Scanner menuScanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 1. MAIN ENTRY POINT
    public static void main(String[] args) {

        System.out.println("Welcome to PocketLog CLI!");

        boolean running = true;

        while (running) {

            displayMainMenu();

            System.out.print("Enter your choice: ");
            String choice = menuScanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "D":
                    recordTransaction("DEPOSIT");
                    break;
                case "P":
                    recordTransaction("PAYMENT");
                    break;
                case "L":
                    displayLedger();
                    break;
                case "S":
                    customSearch();
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid option. Please enter D, P, L, S, or X.");
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

    // 2. PRIMARY HELPER METHOD (Menu Display)
    private static void displayMainMenu() {
        System.out.println("\n--- POCKETLOG MAIN MENU ---");
        System.out.println("D) Add Deposit");
        System.out.println("P) Make Payment (Debit)");
        System.out.println("L) Ledger (View all transactions)");
        System.out.println("S) Custom Search");
        System.out.println("X) Exit");
    }

    // 3. CORE ACTION METHOD (Record Transaction)
    private static void recordTransaction(String type) {
        System.out.println("\n--- RECORD NEW " + type + " ---");

        try {
            // 1. Gather Input
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

            // 2. Process Amount
            double amount = Double.parseDouble(stringAmount);

            if (type.equals("PAYMENT")) {
                if (amount > 0) {
                    amount = amount * -1;
                }
            }

            // 3. Create Object
            Transaction newTransaction = new Transaction(date, time, description, vendor, amount);

            // 4. File Writing (DELEGATED)
            if (FinanceManager.saveTransaction(newTransaction)) {
                System.out.println("\n✅ Success! " + type + " recorded.");
                System.out.println("   -> " + newTransaction.toString());
            } else {
                System.out.println("\n❌ ERROR: Could not write to the transaction file.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\n❌ ERROR: Invalid amount entered. Please use a number (e.g., 50.00).");
        }
    }

    // 4. CORE ACTION METHOD (Display Ledger)
    private static void displayLedger() {
        boolean runningLedger = true;
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

            // MTD Setup
            boolean isMTD = filterChoice.equals("M");
            LocalDate startOfMonth = null;
            LocalDate today = null;

            if (isMTD) {
                today = LocalDate.now();
                startOfMonth = today.withDayOfMonth(1);
                System.out.printf("\n--- Showing Transactions from %s to %s ---\n", startOfMonth, today);
            }

            // Validation
            if (!filterChoice.equals("A") && !filterChoice.equals("D") && !filterChoice.equals("P") && !filterChoice.equals("V") && !filterChoice.equals("M")) {
                System.out.println("❌ Invalid filter choice. Please enter A, D, P, V, M, or H.");
                System.out.println("Press ENTER to continue...");
                menuScanner.nextLine();
                continue;
            }

            // 1. Read All Transactions (DELEGATED)
            List<Transaction> transactions = FinanceManager.loadTransactions();

            if (transactions.isEmpty()) {
                System.out.println("\nNo transactions found. Use D or P to add a record.");
                continue;
            }

            // 2. Display Header
            System.out.println("\n------------------------------------------");
            System.out.println("DATE|TIME|DESCRIPTION|VENDOR|AMOUNT");
            System.out.println("------------------------------------------");

            // 3. Loop BACKWARDS and filter (Using Transaction object methods)
            try {
                for (int i = transactions.size() - 1; i >= 0; i--) {
                    Transaction t = transactions.get(i);

                    boolean shouldDisplay = false;

                    // Apply Filters (Using Transaction helpers)
                    if (filterChoice.equals("A")) {
                        shouldDisplay = true;
                    } else if (filterChoice.equals("D") && t.isDeposit()) {
                        shouldDisplay = true;
                    } else if (filterChoice.equals("P") && t.isPayment()) {
                        shouldDisplay = true;
                    } else if (filterChoice.equals("V") && t.getVendor().equalsIgnoreCase(searchVendor)) {
                        shouldDisplay = true;
                    }

                    // Apply MTD Filter
                    else if (isMTD) {
                        LocalDate transactionDate = LocalDate.parse(t.getDate(), DATE_FORMATTER);

                        if (!transactionDate.isBefore(startOfMonth) && !transactionDate.isAfter(today)) {
                            shouldDisplay = true;
                        }
                    }

                    if (shouldDisplay) {
                        System.out.println(t.toString());
                    }
                }
            } catch (Exception e) {
                System.out.println("\n❌ ERROR: An unexpected error occurred while displaying ledger.");
            }

            System.out.println("------------------------------------------");
            System.out.println("Press ENTER to view another filter, or H to go home.");
            menuScanner.nextLine();
        }
    }

    // 5. CORE ACTION METHOD (Custom Search)
    private static void customSearch() {
        System.out.println("\n--- CUSTOM SEARCH (Optional Filters) ---");

        // --- 1. Collect Filters ---
        System.out.println("\n--- TEXT FILTERS ---");
        System.out.print("Enter Vendor/Source name (leave blank to skip): ");
        String vendorFilter = menuScanner.nextLine().trim().toUpperCase();

        System.out.print("Enter keyword in Description (leave blank to skip): ");
        String descriptionFilter = menuScanner.nextLine().trim().toUpperCase();

        System.out.println("\n--- AMOUNT RANGE ---");
        System.out.print("Enter MINIMUM Amount (0 or blank to skip): $");
        String minAmountStr = menuScanner.nextLine().trim();

        System.out.print("Enter MAXIMUM Amount (0 or blank to skip): $");
        String maxAmountStr = menuScanner.nextLine().trim();

        System.out.println("\n--- DATE RANGE ---");
        System.out.print("Enter Start Date (YYYY-MM-DD, blank to skip): ");
        String startDateStr = menuScanner.nextLine().trim();

        System.out.print("Enter End Date (YYYY-MM-DD, blank to skip): ");
        String endDateStr = menuScanner.nextLine().trim();

        // --- 2. Prepare Filters for Logic ---

        double minAmount = 0.0;
        double maxAmount = Double.MAX_VALUE;
        LocalDate startDate = null;
        LocalDate endDate = null;

        try {
            if (!minAmountStr.isEmpty()) {
                minAmount = Double.parseDouble(minAmountStr);
            }
            if (!maxAmountStr.isEmpty() && Double.parseDouble(maxAmountStr) > 0) {
                maxAmount = Double.parseDouble(maxAmountStr);
            }

            if (!startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
            }
            if (!endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
            }

        } catch (NumberFormatException e) {
            System.out.println("\n❌ ERROR: Invalid number entered for amount. Returning to main menu.");
            return;
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("\n❌ ERROR: Invalid date format. Please use YYYY-MM-DD. Returning to main menu.");
            return;
        }

        // --- 3. Perform Filtering (Logic uses Transaction objects) ---

        List<Transaction> transactions = FinanceManager.loadTransactions();
        List<Transaction> results = new ArrayList<>();

        if (transactions.isEmpty()) {
            System.out.println("No transactions to search.");
            return;
        }

        try {
            for (Transaction t : transactions) {

                boolean passesAllFilters = true;

                // 1. Vendor Filter
                if (!vendorFilter.isEmpty() && !t.getVendor().toUpperCase().contains(vendorFilter)) {
                    passesAllFilters = false;
                }

                // 2. Description Filter
                if (passesAllFilters && !descriptionFilter.isEmpty() && !t.getDescription().toUpperCase().contains(descriptionFilter)) {
                    passesAllFilters = false;
                }

                // 3. Amount Range Filter
                if (passesAllFilters && (t.getAmount() < minAmount || t.getAmount() > maxAmount)) {
                    passesAllFilters = false;
                }

                // 4. Date Range Filter
                if (passesAllFilters && (startDate != null || endDate != null)) {
                    LocalDate transactionDate = LocalDate.parse(t.getDate(), DATE_FORMATTER);

                    if (startDate != null && transactionDate.isBefore(startDate)) {
                        passesAllFilters = false;
                    }
                    if (passesAllFilters && endDate != null && transactionDate.isAfter(endDate)) {
                        passesAllFilters = false;
                    }
                }

                if (passesAllFilters) {
                    results.add(t);
                }
            }

        } catch (Exception e) {
            System.out.println("\n❌ FATAL ERROR during search: Data is corrupted. Cannot continue.");
            return;
        }

        // --- 4. Display Results ---

        System.out.println("\n--- Search Results (" + results.size() + " matches) ---");
        if (results.isEmpty()) {
            System.out.println("No transactions matched all criteria.");
        } else {
            System.out.println("------------------------------------------");
            System.out.println("DATE|TIME|DESCRIPTION|VENDOR|AMOUNT");
            System.out.println("------------------------------------------");

            // Loop backwards (newest first) through the results list
            for (int i = results.size() - 1; i >= 0; i--) {
                System.out.println(results.get(i).toString());
            }
        }
    }
}