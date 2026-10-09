package com.banking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base class for all bank accounts.
 * <p>
 * Demonstrates OOP principles:
 * <ul>
 *   <li><b>Abstraction</b> — defines the contract that all accounts must follow</li>
 *   <li><b>Encapsulation</b> — private fields with controlled access</li>
 *   <li><b>Inheritance</b> — subclasses extend this to add specialized behavior</li>
 *   <li><b>Polymorphism</b> — {@link #withdraw(double)} is abstract and behaves differently per type</li>
 * </ul>
 */
public abstract class Account {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String accountId;
    private String accountHolder;
    private double balance;
    private final AccountType accountType;
    private final LocalDateTime createdAt;
    private final List<Transaction> transactions;

    /**
     * Constructs a new account.
     *
     * @param accountHolder  the name of the account holder
     * @param initialDeposit the opening balance (must be >= 0)
     * @param accountType    the type of this account
     * @throws IllegalArgumentException if initialDeposit is negative
     */
    protected Account(String accountHolder, double initialDeposit, AccountType accountType) {
        if (accountHolder == null || accountHolder.isBlank()) {
            throw new IllegalArgumentException("Account holder name cannot be empty");
        }
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }

        this.accountId = "ACC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.accountHolder = accountHolder;
        this.balance = initialDeposit;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();

        if (initialDeposit > 0) {
            addTransaction(TransactionType.DEPOSIT, initialDeposit, "Initial deposit");
        }
    }

    // ── Public Operations ────────────────────────────────────

    /**
     * Deposits money into this account.
     *
     * @param amount the amount to deposit (must be positive)
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance += amount;
        addTransaction(TransactionType.DEPOSIT, amount, "Deposit");
    }

    /**
     * Withdraws money from this account.
     * Each account type implements its own withdrawal rules.
     *
     * @param amount the amount to withdraw (must be positive)
     * @throws IllegalArgumentException if amount is invalid or exceeds allowed limits
     */
    public abstract void withdraw(double amount);

    // ── Protected Helpers ────────────────────────────────────

    /**
     * Processes a withdrawal by deducting the amount and recording the transaction.
     * Called by subclasses after validation.
     */
    protected void processWithdrawal(double amount, String description) {
        this.balance -= amount;
        addTransaction(TransactionType.WITHDRAWAL, amount, description);
    }

    /**
     * Records a transaction against this account.
     */
    protected void addTransaction(TransactionType type, double amount, String description) {
        transactions.add(new Transaction(this.accountId, type, amount, this.balance, description));
    }

    /**
     * Directly sets the balance. Used internally by subclasses (e.g., interest application).
     */
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getAccountId() {
        return accountId;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        if (accountHolder == null || accountHolder.isBlank()) {
            throw new IllegalArgumentException("Account holder name cannot be empty");
        }
        this.accountHolder = accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getFormattedCreatedAt() {
        return createdAt.format(FORMATTER);
    }

    /**
     * Returns an unmodifiable view of the transaction history.
     */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Returns a summary string describing the account type and its special properties.
     * Subclasses should override to include type-specific details.
     */
    public abstract String getAccountDetails();

    @Override
    public String toString() {
        return String.format("[%s] %s — %s | Balance: $%.2f",
                accountId, accountHolder, accountType, balance);
    }
}
