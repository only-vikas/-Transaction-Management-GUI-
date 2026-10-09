package com.banking.service;

import com.banking.database.AccountDatabase;
import com.banking.model.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BankingService}.
 * Validates business logic, account operations, and reporting.
 */
class BankingServiceTest {

    private BankingService service;

    @BeforeEach
    void setUp() {
        AccountDatabase.resetInstance();
        service = new BankingService();
    }

    @AfterEach
    void tearDown() {
        AccountDatabase.resetInstance();
    }

    @Nested
    @DisplayName("Account Creation")
    class AccountCreation {

        @Test
        @DisplayName("should create a savings account")
        void shouldCreateSavingsAccount() {
            SavingsAccount account = service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0);
            assertNotNull(account);
            assertEquals("Alice", account.getAccountHolder());
            assertEquals(AccountType.SAVINGS, account.getAccountType());
            assertEquals(1, service.getTotalAccounts());
        }

        @Test
        @DisplayName("should create a checking account")
        void shouldCreateCheckingAccount() {
            CheckingAccount account = service.createCheckingAccount("Bob", 500.0, 200.0);
            assertNotNull(account);
            assertEquals("Bob", account.getAccountHolder());
            assertEquals(AccountType.CHECKING, account.getAccountType());
        }

        @Test
        @DisplayName("should list all created accounts")
        void shouldListAllAccounts() {
            service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0);
            service.createCheckingAccount("Bob", 500.0, 200.0);

            List<Account> accounts = service.getAllAccounts();
            assertEquals(2, accounts.size());
        }
    }

    @Nested
    @DisplayName("Financial Operations")
    class Operations {

        private String savingsId;
        private String checkingId;

        @BeforeEach
        void createAccounts() {
            savingsId = service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0).getAccountId();
            checkingId = service.createCheckingAccount("Bob", 500.0, 200.0).getAccountId();
        }

        @Test
        @DisplayName("should deposit money")
        void shouldDeposit() {
            service.deposit(savingsId, 500.0);
            assertEquals(1500.0, service.getAccount(savingsId).getBalance());
        }

        @Test
        @DisplayName("should withdraw from savings (respecting minimum)")
        void shouldWithdrawFromSavings() {
            service.withdraw(savingsId, 500.0);
            assertEquals(500.0, service.getAccount(savingsId).getBalance());
        }

        @Test
        @DisplayName("should withdraw from checking (allowing overdraft)")
        void shouldWithdrawFromChecking() {
            service.withdraw(checkingId, 600.0);
            assertEquals(-100.0, service.getAccount(checkingId).getBalance());
        }

        @Test
        @DisplayName("should reject withdrawal that violates account rules")
        void shouldRejectInvalidWithdrawal() {
            // Savings: would go below minimum
            assertThrows(IllegalArgumentException.class,
                    () -> service.withdraw(savingsId, 950.0));
            // Checking: would exceed overdraft
            assertThrows(IllegalArgumentException.class,
                    () -> service.withdraw(checkingId, 800.0));
        }

        @Test
        @DisplayName("should apply interest to savings only")
        void shouldApplyInterest() {
            service.applyInterest(savingsId);
            assertEquals(1025.0, service.getAccount(savingsId).getBalance(), 0.01);

            assertThrows(IllegalArgumentException.class,
                    () -> service.applyInterest(checkingId));
        }

        @Test
        @DisplayName("should transfer between accounts")
        void shouldTransfer() {
            service.transfer(savingsId, checkingId, 200.0);
            assertEquals(800.0, service.getAccount(savingsId).getBalance());
            assertEquals(700.0, service.getAccount(checkingId).getBalance());
        }

        @Test
        @DisplayName("should reject transfer to same account")
        void shouldRejectSelfTransfer() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.transfer(savingsId, savingsId, 100.0));
        }
    }

    @Nested
    @DisplayName("Reporting")
    class Reporting {

        @Test
        @DisplayName("should calculate total balance")
        void shouldCalculateTotalBalance() {
            service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0);
            service.createCheckingAccount("Bob", 500.0, 200.0);
            assertEquals(1500.0, service.getTotalBalance());
        }

        @Test
        @DisplayName("should return all transactions sorted by date")
        void shouldReturnAllTransactions() {
            SavingsAccount sa = service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0);
            service.deposit(sa.getAccountId(), 200.0);
            service.withdraw(sa.getAccountId(), 100.0);

            List<Transaction> transactions = service.getAllTransactions();
            assertEquals(3, transactions.size()); // initial deposit + deposit + withdrawal
        }

        @Test
        @DisplayName("should return transaction history for specific account")
        void shouldReturnAccountTransactions() {
            SavingsAccount sa = service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0);
            service.createCheckingAccount("Bob", 500.0, 200.0);
            service.deposit(sa.getAccountId(), 200.0);

            List<Transaction> history = service.getTransactionHistory(sa.getAccountId());
            assertEquals(2, history.size());
        }

        @Test
        @DisplayName("should count total transactions")
        void shouldCountTotalTransactions() {
            service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0);
            service.createCheckingAccount("Bob", 500.0, 200.0);
            assertEquals(2, service.getTotalTransactions()); // 2 initial deposits
        }

        @Test
        @DisplayName("should search accounts by name")
        void shouldSearchAccounts() {
            service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0);
            service.createCheckingAccount("Bob", 500.0, 200.0);

            assertEquals(1, service.searchAccounts("alice").size());
            assertEquals(2, service.searchAccounts("").size());
        }
    }

    @Test
    @DisplayName("should delete an account")
    void shouldDeleteAccount() {
        String id = service.createSavingsAccount("Alice", 1000.0, 2.5, 100.0).getAccountId();
        assertTrue(service.deleteAccount(id));
        assertEquals(0, service.getTotalAccounts());
    }

    @Test
    @DisplayName("should throw when accessing non-existent account")
    void shouldThrowForMissingAccount() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getAccount("NONEXISTENT"));
    }
}
