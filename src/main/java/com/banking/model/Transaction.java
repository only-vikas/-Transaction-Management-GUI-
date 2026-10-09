package com.banking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single banking transaction.
 * Immutable value object that records every financial operation.
 */
public class Transaction {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String transactionId;
    private final String accountId;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final String description;
    private final LocalDateTime timestamp;

    /**
     * Creates a new transaction record.
     *
     * @param accountId    the account this transaction belongs to
     * @param type         the type of transaction
     * @param amount       the transaction amount (always positive)
     * @param balanceAfter the account balance after this transaction
     * @param description  a human-readable description
     */
    public Transaction(String accountId, TransactionType type, double amount,
                       double balanceAfter, String description) {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // ── Getters ──────────────────────────────────────────────

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s: $%.2f | Balance: $%.2f | %s",
                transactionId, getFormattedTimestamp(), type, amount, balanceAfter, description);
    }
}
