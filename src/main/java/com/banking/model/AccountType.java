package com.banking.model;

/**
 * Enum representing the types of bank accounts supported.
 * Demonstrates the use of enums for type-safe constants.
 */
public enum AccountType {
    SAVINGS("Savings"),
    CHECKING("Checking");

    private final String displayName;

    AccountType(String displayName) {
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
