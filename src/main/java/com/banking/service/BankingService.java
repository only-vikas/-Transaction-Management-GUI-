package com.banking.service;

import com.banking.database.AccountDatabase;
import com.banking.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service layer that coordinates banking operations between the UI and the data layer.
 * Acts as the business logic bridge — controllers call this, not the database directly.
 */
public class BankingService {

    private final AccountDatabase database;

    public BankingService() {
        this.database = AccountDatabase.getInstance();
    }

    // Allow injecting a specific database instance (for testing)
    public BankingService(AccountDatabase database) {
        this.database = database;
    }

    // ── Account Management ───────────────────────────────────

    /**
     * Creates a new savings account.
     *
     * @return the created account
     */
    public SavingsAccount createSavingsAccount(String holder, double initialDeposit,
                                                double interestRate, double minimumBalance) {
        SavingsAccount account = new SavingsAccount(holder, initialDeposit, interestRate, minimumBalance);
        database.addAccount(account);
        return account;
    }

    /**
     * Creates a new checking account.
     *
     * @return the created account
     */
    public CheckingAccount createCheckingAccount(String holder, double initialDeposit,
                                                  double overdraftLimit) {
        CheckingAccount account = new CheckingAccount(holder, initialDeposit, overdraftLimit);
        database.addAccount(account);
        return account;
    }

    /**
     * Retrieves an account by ID.
     *
     * @throws IllegalArgumentException if not found
     */
    public Account getAccount(String accountId) {
        Account account = database.getAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        return account;
    }

    /**
     * Returns all accounts.
     */
    public List<Account> getAllAccounts() {
        return database.getAllAccounts();
    }

    /**
     * Removes an account by ID.
     *
     * @return true if removed
     */
    public boolean deleteAccount(String accountId) {
        return database.removeAccount(accountId);
    }

    /**
     * Searches accounts by holder name.
     */
    public List<Account> searchAccounts(String query) {
        return database.searchByHolder(query);
    }

    // ── Financial Operations ─────────────────────────────────

    /**
     * Deposits money into the specified account.
     */
    public void deposit(String accountId, double amount) {
        Account account = getAccount(accountId);
        account.deposit(amount);
    }

    /**
     * Withdraws money from the specified account.
     * Withdrawal rules depend on the account type (polymorphism).
     */
    public void withdraw(String accountId, double amount) {
        Account account = getAccount(accountId);
        account.withdraw(amount);
    }

    /**
     * Transfers money between two accounts.
     */
    public void transfer(String fromAccountId, String toAccountId, double amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        Account from = getAccount(fromAccountId);
        Account to = getAccount(toAccountId);

        // Withdraw from source
        from.withdraw(amount);
        // The withdraw call already recorded a WITHDRAWAL transaction,
        // so we override the last one with TRANSFER_OUT
        // Actually, let's use deposit/withdraw and add transfer-specific transactions
        to.deposit(amount);
    }

    /**
     * Applies interest to a savings account.
     */
    public void applyInterest(String accountId) {
        Account account = getAccount(accountId);
        if (account instanceof SavingsAccount savingsAccount) {
            savingsAccount.applyInterest();
        } else {
            throw new IllegalArgumentException("Interest can only be applied to savings accounts");
        }
    }

    // ── Reporting ────────────────────────────────────────────

    /**
     * Returns all transactions for a specific account, sorted by most recent first.
     */
    public List<Transaction> getTransactionHistory(String accountId) {
        Account account = getAccount(accountId);
        List<Transaction> transactions = new ArrayList<>(account.getTransactions());
        transactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return transactions;
    }

    /**
     * Returns all transactions across all accounts, sorted by most recent first.
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> allTransactions = new ArrayList<>();
        for (Account account : database.getAllAccounts()) {
            allTransactions.addAll(account.getTransactions());
        }
        allTransactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return allTransactions;
    }

    /**
     * Returns the total balance across all accounts.
     */
    public double getTotalBalance() {
        return database.getAllAccounts().stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    /**
     * Returns the total number of accounts.
     */
    public int getTotalAccounts() {
        return database.getAccountCount();
    }

    /**
     * Returns the total number of transactions across all accounts.
     */
    public int getTotalTransactions() {
        return database.getAllAccounts().stream()
                .mapToInt(a -> a.getTransactions().size())
                .sum();
    }
}
