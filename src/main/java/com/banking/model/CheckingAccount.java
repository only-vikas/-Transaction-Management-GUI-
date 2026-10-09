package com.banking.model;

/**
 * Checking account with overdraft protection.
 * <p>
 * Inherits from {@link Account} and overrides {@link #withdraw(double)}
 * to allow withdrawals up to the overdraft limit — demonstrating polymorphism.
 */
public class CheckingAccount extends Account {

    private double overdraftLimit;

    /**
     * Creates a new checking account.
     *
     * @param accountHolder  the account holder's name
     * @param initialDeposit the opening balance
     * @param overdraftLimit the maximum amount the balance can go negative
     */
    public CheckingAccount(String accountHolder, double initialDeposit, double overdraftLimit) {
        super(accountHolder, initialDeposit, AccountType.CHECKING);
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Withdraws money, allowing the balance to go negative up to the overdraft limit.
     *
     * @param amount the amount to withdraw
     * @throws IllegalArgumentException if the withdrawal exceeds the overdraft limit
     */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        double totalAvailable = getBalance() + overdraftLimit;
        if (amount > totalAvailable) {
            throw new IllegalArgumentException(
                    String.format("Cannot withdraw $%.2f. Available (incl. $%.2f overdraft): $%.2f",
                            amount, overdraftLimit, totalAvailable));
        }
        processWithdrawal(amount, "Withdrawal");
    }

    // ── Getters & Setters ────────────────────────────────────

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String getAccountDetails() {
        return String.format("Checking Account | Overdraft Limit: $%.2f", overdraftLimit);
    }
}
