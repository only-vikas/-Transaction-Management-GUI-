package com.banking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SavingsAccount}.
 * Validates minimum balance rules, interest application, and inheritance behavior.
 */
class SavingsAccountTest {

    private SavingsAccount account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount("Alice Johnson", 1000.0, 2.5, 100.0);
    }

    @Nested
    @DisplayName("Account Creation")
    class Creation {

        @Test
        @DisplayName("should create account with correct properties")
        void shouldCreateWithCorrectProperties() {
            assertEquals("Alice Johnson", account.getAccountHolder());
            assertEquals(1000.0, account.getBalance());
            assertEquals(AccountType.SAVINGS, account.getAccountType());
            assertEquals(2.5, account.getInterestRate());
            assertEquals(100.0, account.getMinimumBalance());
        }

        @Test
        @DisplayName("should auto-generate an account ID")
        void shouldGenerateAccountId() {
            assertNotNull(account.getAccountId());
            assertTrue(account.getAccountId().startsWith("ACC-"));
        }

        @Test
        @DisplayName("should record initial deposit as a transaction")
        void shouldRecordInitialDeposit() {
            assertFalse(account.getTransactions().isEmpty());
            assertEquals(1, account.getTransactions().size());
            assertEquals(TransactionType.DEPOSIT, account.getTransactions().get(0).getType());
        }

        @Test
        @DisplayName("should reject initial deposit below minimum balance")
        void shouldRejectDepositBelowMinimum() {
            assertThrows(IllegalArgumentException.class,
                    () -> new SavingsAccount("Bob", 50.0, 2.5, 100.0));
        }

        @Test
        @DisplayName("should reject negative interest rate")
        void shouldRejectNegativeInterestRate() {
            assertThrows(IllegalArgumentException.class,
                    () -> new SavingsAccount("Bob", 500.0, -1.0, 100.0));
        }

        @Test
        @DisplayName("should reject blank account holder name")
        void shouldRejectBlankName() {
            assertThrows(IllegalArgumentException.class,
                    () -> new SavingsAccount("", 500.0, 2.5, 100.0));
        }
    }

    @Nested
    @DisplayName("Deposits")
    class Deposits {

        @Test
        @DisplayName("should increase balance on deposit")
        void shouldIncreaseBalance() {
            account.deposit(500.0);
            assertEquals(1500.0, account.getBalance());
        }

        @Test
        @DisplayName("should record deposit transaction")
        void shouldRecordDepositTransaction() {
            account.deposit(500.0);
            assertEquals(2, account.getTransactions().size()); // initial + new
        }

        @Test
        @DisplayName("should reject zero or negative deposit")
        void shouldRejectInvalidDeposit() {
            assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
            assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
        }
    }

    @Nested
    @DisplayName("Withdrawals (Polymorphic behavior)")
    class Withdrawals {

        @Test
        @DisplayName("should allow withdrawal when balance remains above minimum")
        void shouldAllowValidWithdrawal() {
            account.withdraw(500.0);
            assertEquals(500.0, account.getBalance());
        }

        @Test
        @DisplayName("should withdraw exactly to minimum balance")
        void shouldAllowWithdrawalToMinimum() {
            account.withdraw(900.0); // 1000 - 900 = 100 (exactly minimum)
            assertEquals(100.0, account.getBalance());
        }

        @Test
        @DisplayName("should reject withdrawal that would drop below minimum")
        void shouldRejectWithdrawalBelowMinimum() {
            assertThrows(IllegalArgumentException.class, () -> account.withdraw(950.0));
        }

        @Test
        @DisplayName("should reject zero or negative withdrawal")
        void shouldRejectInvalidWithdrawal() {
            assertThrows(IllegalArgumentException.class, () -> account.withdraw(0));
            assertThrows(IllegalArgumentException.class, () -> account.withdraw(-50));
        }
    }

    @Nested
    @DisplayName("Interest Application")
    class Interest {

        @Test
        @DisplayName("should correctly apply interest")
        void shouldApplyInterest() {
            account.applyInterest(); // 1000 * 2.5% = 25
            assertEquals(1025.0, account.getBalance(), 0.01);
        }

        @Test
        @DisplayName("should record interest as a transaction")
        void shouldRecordInterestTransaction() {
            account.applyInterest();
            Transaction last = account.getTransactions().get(account.getTransactions().size() - 1);
            assertEquals(TransactionType.INTEREST, last.getType());
            assertEquals(25.0, last.getAmount(), 0.01);
        }
    }

    @Test
    @DisplayName("should return correct account details string")
    void shouldReturnAccountDetails() {
        String details = account.getAccountDetails();
        assertTrue(details.contains("Savings"));
        assertTrue(details.contains("2.50%"));
    }
}
