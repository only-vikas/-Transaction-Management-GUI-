package com.banking.model;

/**
 * Savings account with interest rate and minimum balance requirement.
 * <p>
 * Inherits from {@link Account} and overrides {@link #withdraw(double)}
 * to enforce minimum balance rules — a key demonstration of polymorphism.
 */
public class SavingsAccount extends Account {

    private double interestRate;
    private final double minimumBalance;

    /**
     * Creates a new savings account.
     *
     * @param accountHolder  the account holder's name
     * @param initialDeposit the opening balance
     * @param interestRate   annual interest rate as a percentage (e.g., 2.5 for 2.5%)
     * @param minimumBalance the minimum balance that must be maintained
     */
    public SavingsAccount(String accountHolder, double initialDeposit,
                          double interestRate, double minimumBalance) {
        super(accountHolder, initialDeposit, AccountType.SAVINGS);
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (minimumBalance < 0) {
            throw new IllegalArgumentException("Minimum balance cannot be negative");
        }
        if (initialDeposit < minimumBalance) {
            throw new IllegalArgumentException(
                    String.format("Initial deposit ($%.2f) must be >= minimum balance ($%.2f)",
                            initialDeposit, minimumBalance));
        }
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
    }

    /**
     * Withdraws money, ensuring the balance does not drop below the minimum.
     *
     * @param amount the amount to withdraw
     * @throws IllegalArgumentException if the withdrawal would violate minimum balance
     */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        double availableForWithdrawal = getBalance() - minimumBalance;
        if (amount > availableForWithdrawal) {
            throw new IllegalArgumentException(
                    String.format("Cannot withdraw $%.2f. Available (after min balance $%.2f): $%.2f",
                            amount, minimumBalance, Math.max(0, availableForWithdrawal)));
        }
        processWithdrawal(amount, "Withdrawal");
    }

    /**
     * Applies the annual interest rate to the current balance and records it.
     */
    public void applyInterest() {
        double interest = getBalance() * (interestRate / 100.0);
        if (interest > 0) {
            setBalance(getBalance() + interest);
            addTransaction(TransactionType.INTEREST, interest,
                    String.format("Interest applied at %.2f%%", interestRate));
        }
    }

    // ── Getters & Setters ────────────────────────────────────

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        this.interestRate = interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    @Override
    public String getAccountDetails() {
        return String.format("Savings Account | Interest: %.2f%% | Min Balance: $%.2f",
                interestRate, minimumBalance);
    }
}
