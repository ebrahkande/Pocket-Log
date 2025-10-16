package com.pluralsight;

// Import necessary tools
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FinanceManager {
    private static final String FILENAME = "transactions.cvs";


    public static Transaction parseTransaction(String csvLine) {
        try {
            // Split by the pipe character
            String[] parts = csvLine.split("\\|");

            if (parts.length != 5) {
                return null;
            }

            // Convert amount string to double
            double amount = Double.parseDouble(parts[4]);

            // Create and return the new Transaction object
            return new Transaction(parts[0], parts[1], parts[2], parts[3], amount);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Catches bad data in the amount field or missing parts
            System.err.println("Skipping malformed transaction line: " + csvLine);
            return null;
        }
    }

    // Loading Function: Reads the entire file and returns a list of Transaction objects.
    public static List<Transaction> loadTransactions() {

        List<Transaction> transactions = new ArrayList<>();

        try {
            File file = new File(FILENAME);
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue; // Skip empty lines

                // Use the parser to convert the line into a Transaction object
                Transaction t = parseTransaction(line);
                if (t != null) {
                    transactions.add(t);
                }
            }
            fileScanner.close();

        } catch (FileNotFoundException e) {
            // File not existing is not an error; we just return an empty list.
        }

        return transactions;
    }

    // Saving Function: Appends a single Transaction object to the file.
    public static boolean saveTransaction(Transaction transaction) {
        try {
            // Use the object's toString() method for the CSV line
            FileWriter writer = new FileWriter(FILENAME, true);
            writer.write(transaction.toString());
            writer.write("\n");
            writer.close();
            return true;
        } catch (IOException e) {
            return false; // Indicate failure
        }
    }
}
