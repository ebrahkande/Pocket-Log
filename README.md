## Project Overview

PocketLog is a Java-based command-line interface application designed for basic personal finance tracking. It allows users to record deposits, payments, and view transaction history using various filtering and reporting tools.

This project was built to demonstrate proficiency in:
* Core Java programming concepts (loops, switches, methods).
* **Object-Oriented Programming (OOP)**, specifically **Encapsulation** and **Separation of Concerns**.
* File I/O operations (reading and writing CSV files).
* Handling complex input validation (dates, amounts).

* ## Key Features

* **Transaction Logging (D/P):** Record Deposits (positive amounts) and Payments (negative amounts), with automatic sign correction for expenses.
* **File Persistence:** All transactions are saved to and loaded from a local `transactions.csv` file.
* **Ledger Filtering (L):** View transactions filtered by All, Deposits, Payments, Vendor Search, and **Month-to-Date (MTD)** reports.
* **Custom Search (S):** Utilize a powerful search screen to apply **multiple filters simultaneously**, including:
    * Vendor Name (Case-insensitive)
    * Description Keyword
    * Minimum/Maximum Amount Range
    * Start/End Date Range
 
    * ## Object-Oriented Design (OOP)

The application adheres to the principle of **Separation of Concerns** by dividing responsibilities across three dedicated classes:

| Class | Primary Role | OOP Principle Demonstrated |
| :--- | :--- | :--- |
| **PocketLog.java** | Handles all **User Interface (UI)** and **Application Flow**. It takes user input, calls the `FinanceManager`, and displays results. | Application Entry Point / Controller |
| **Transaction.java** | The **Data Model** (Blueprint). It holds the private fields (date, amount, vendor, etc.) and provides **Getters** and helper methods (`isDeposit()`). | Encapsulation / Data Integrity |
| **FinanceManager.java** | Handles all low-level **File Input/Output (I/O)** and data parsing. It contains `loadTransactions()` and `saveTransaction()`. | Separation of Concerns / Data Access Layer |

## How to Compile and Run

This project requires a standard Java Development Kit (JDK) environment (Java 8 or later).

1.  **Save Files:** Ensure all three files (`PocketLog.java`, `Transaction.java`, and `FinanceManager.java`) are in the same directory.
2.  **Compile:** Open your command line or terminal in that directory and compile the files:
    ```bash
    javac PocketLog.java Transaction.java FinanceManager.java
    ```
3.  **Run:** Execute the main class:
    ```bash
    java PocketLog
    ```
    *(The application will automatically create a `transactions.csv` file upon the first Deposit or Payment.)*

    ## Final Testing Scenario

The application passed a full system integrity check, validating the persistence and filtering capabilities:

* Confirmed deposits and payments are saved correctly (positive/negative sign enforced).
* **Ledger (L)** successfully filtered by All, Deposits, Payments, Vendor, and Month-to-Date.
* **Custom Search (S)** successfully executed complex queries combining Vendor, Amount Range, and Date Range filters, validating the use of the refactored `Transaction` Getters and Helpers.
