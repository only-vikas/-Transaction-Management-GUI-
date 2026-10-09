package com.banking.database;

import com.banking.model.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory database for storing and retrieving bank accounts.
 * <p>
 * Implements the <b>Singleton</b> pattern to ensure a single, shared
 * data store across the application.
 */
public class AccountDatabase {

    private static AccountDatabase instance;

    private final Map<String, Account> accounts;

    /**
     * Private constructor — enforces singleton access via {@link #getInstance()}.
     */
    private AccountDatabase() {
        this.accounts = new LinkedHashMap<>();
    }

    /**
     * Returns the single shared instance of the database.
     */
    public static synchronized AccountDatabase getInstance() {
        if (instance == null) {
            instance = new AccountDatabase();
        }
        return instance;
    }

    /**
     * Resets the database (useful for testing).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    // ── CRUD Operations ──────────────────────────────────────

    /**
     * Adds an account to the database.
     *
     * @param account the account to add
     * @throws IllegalArgumentException if an account with the same ID already exists
     */
    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (accounts.containsKey(account.getAccountId())) {
            throw new IllegalArgumentException("Account with ID " + account.getAccountId() + " already exists");
        }
        accounts.put(account.getAccountId(), account);
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the unique account identifier
     * @return the account, or null if not found
     */
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    /**
     * Removes an account from the database.
     *
     * @param accountId the ID of the account to remove
     * @return true if the account was removed, false if it didn't exist
     */
    public boolean removeAccount(String accountId) {
        return accounts.remove(accountId) != null;
    }

    /**
     * Returns an unmodifiable list of all accounts.
     */
    public List<Account> getAllAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts.values()));
    }

    /**
     * Returns the total number of accounts.
     */
    public int getAccountCount() {
        return accounts.size();
    }

    /**
     * Checks whether an account with the given ID exists.
     */
    public boolean accountExists(String accountId) {
        return accounts.containsKey(accountId);
    }

    /**
     * Searches accounts by holder name (case-insensitive partial match).
     */
    public List<Account> searchByHolder(String query) {
        if (query == null || query.isBlank()) {
            return getAllAccounts();
        }
        String lowerQuery = query.toLowerCase();
        return accounts.values().stream()
                .filter(a -> a.getAccountHolder().toLowerCase().contains(lowerQuery))
                .toList();
    }
}
