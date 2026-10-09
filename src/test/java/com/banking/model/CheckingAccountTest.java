package com.banking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CheckingAccount}.
 * Validates overdraft behavior and inheritance correctness.
 */
class CheckingAccountTest {

    private CheckingAccount account;

    @BeforeEach
    void setUp() {
        account = new CheckingAccount("Bob Smith", 500.0, 200.0);
    }

    @Nested
    @DisplayName("Account Creation")
    class Creation {

        @Test
        @DisplayName("should create account with correct properties")
        void shouldCreateWithCorrectProperties() {
            assertEquals("Bob Smith", account.getAccountHolder());
            assertEquals(500.0, account.getBalance());
            assertEquals(AccountType.CHECKING, account.getAccountType());
            assertEquals(200.0, account.getOverdraftLimit());
        }

        @Test
        @DisplayName("should auto-generate account ID")
        void shouldGenerateAccountId() {
            assertNotNull(account.getAccountId());
            assertTrue(account.getAccountId().startsWith("ACC-"));
        }

        @Test
        @DisplayName("should reject negative overdraft limit")
        void shouldRejectNegativeOverdraft() {
            assertThrows(IllegalArgumentException.class,
                    () -> new CheckingAccount("Jane", 500.0, -100.0));
        }

        @Test
        @DisplayName("should allow zero initial deposit")
        void shouldAllowZeroDeposit() {
            CheckingAccount zeroAccount = new CheckingAccount("Jane", 0.0, 200.0);
            assertEquals(0.0, zeroAccount.getBalance());
            assertTrue(zeroAccount.getTransactions().isEmpty()); // no initial deposit transaction
        }
    }

    @Nested
    @DisplayName("Deposits")
    class Deposits {

        @Test
        @DisplayName("should increase balance on deposit")
        void shouldIncreaseBalance() {
            account.deposit(300.0);
            assertEquals(800.0, account.getBalance());
        }

        @Test
        @DisplayName("should reject non-positive deposit")
        void shouldRejectInvalidDeposit() {
            assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
            assertThrows(IllegalArgumentException.class, () -> account.deposit(-50));
        }
    }

    @Nested
    @DisplayName("Withdrawals (Polymorphic behavior — Overdraft)")
    class Withdrawals {

        @Test
        @DisplayName("should allow withdrawal within balance")
        void shouldAllowNormalWithdrawal() {
            account.withdraw(300.0);
            assertEquals(200.0, account.getBalance());
        }

        @Test
        @DisplayName("should allow withdrawal into overdraft")
        void shouldAllowOverdraftWithdrawal() {
            account.withdraw(600.0); // 500 - 600 = -100 (within 200 overdraft)
            assertEquals(-100.0, account.getBalance());
        }

        @Test
        @DisplayName("should allow withdrawal up to exact overdraft limit")
        void shouldAllowExactOverdraft() {
            account.withdraw(700.0); // 500 - 700 = -200 (exactly at limit)
            assertEquals(-200.0, account.getBalance());
        }

        @Test
        @DisplayName("should reject withdrawal beyond overdraft limit")
        void shouldRejectExcessiveWithdrawal() {
            assertThrows(IllegalArgumentException.class,
                    () -> account.withdraw(701.0)); // would be -201, exceeds limit
        }

        @Test
        @DisplayName("should record withdrawal transactions")
        void shouldRecordWithdrawalTransaction() {
            account.withdraw(200.0);
            Transaction last = account.getTransactions().get(account.getTransactions().size() - 1);
            assertEquals(TransactionType.WITHDRAWAL, last.getType());
            assertEquals(200.0, last.getAmount());
            assertEquals(300.0, last.getBalanceAfter());
        }

        @Test
        @DisplayName("should reject zero or negative withdrawal")
        void shouldRejectInvalidWithdrawal() {
            assertThrows(IllegalArgumentException.class, () -> account.withdraw(0));
            assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100));
        }
    }

    @Test
    @DisplayName("should return correct account details string")
    void shouldReturnAccountDetails() {
        String details = account.getAccountDetails();
        assertTrue(details.contains("Checking"));
        assertTrue(details.contains("200.00"));
    }
}
