package com.banking.model;

/**
 * Enum representing the types of transactions that can be performed.
 */
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER_IN("Transfer In"),
    TRANSFER_OUT("Transfer Out"),
    INTEREST("Interest");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
