package com.pluralsight;
// The blueprint for a single financial record

public class Transaction {

    // These store the data for each Transaction object
    private String date;  // Date in YYYY-MM-DD format
    private String time;  // Time in HH:MM format
    private String description;  // Summary of the transaction
    private String vendor;  // The other party involved
    private double amount;  // Monetary value

    // Constructor: Used to create a new Transaction object with all its data.
    public Transaction(String date, String time, String description, String vendor, double amount) {

        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    // Getter Methods
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public double getAmount() {
        return amount;
    }

    // Methods For Filtering
    public boolean isDeposit() {
        return this.amount > 0;
    }

    public boolean isPayment() {
        return this.amount < 0;
    }

    @Override
    public String toString() {
        return date + "|" + time + "|" + description + "|" + vendor + "|" + amount; // pipe-separated format
    }
}
