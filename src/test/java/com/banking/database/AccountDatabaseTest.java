package com.banking.database;

import com.banking.model.Account;
import com.banking.model.CheckingAccount;
import com.banking.model.SavingsAccount;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AccountDatabase}.
 * Validates singleton behavior, CRUD operations, and search.
 */
class AccountDatabaseTest {

    private AccountDatabase db;

    @BeforeEach
    void setUp() {
        AccountDatabase.resetInstance();
        db = AccountDatabase.getInstance();
    }

    @AfterEach
    void tearDown() {
        AccountDatabase.resetInstance();
    }

    @Nested
    @DisplayName("Singleton Pattern")
    class Singleton {

        @Test
        @DisplayName("should return same instance")
        void shouldReturnSameInstance() {
            AccountDatabase db2 = AccountDatabase.getInstance();
            assertSame(db, db2);
        }

        @Test
        @DisplayName("should return new instance after reset")
        void shouldReturnNewInstanceAfterReset() {
            AccountDatabase.resetInstance();
            AccountDatabase db2 = AccountDatabase.getInstance();
            assertNotSame(db, db2);
        }
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOps {

        @Test
        @DisplayName("should add and retrieve an account")
        void shouldAddAndRetrieve() {
            SavingsAccount account = new SavingsAccount("Alice", 1000.0, 2.5, 100.0);
            db.addAccount(account);

            Account retrieved = db.getAccount(account.getAccountId());
            assertNotNull(retrieved);
            assertEquals("Alice", retrieved.getAccountHolder());
        }

        @Test
        @DisplayName("should reject null account")
        void shouldRejectNull() {
            assertThrows(IllegalArgumentException.class, () -> db.addAccount(null));
        }

        @Test
        @DisplayName("should reject duplicate account ID")
        void shouldRejectDuplicate() {
            SavingsAccount account = new SavingsAccount("Alice", 1000.0, 2.5, 100.0);
            db.addAccount(account);
            assertThrows(IllegalArgumentException.class, () -> db.addAccount(account));
        }

        @Test
        @DisplayName("should return null for non-existent account")
        void shouldReturnNullForMissing() {
            assertNull(db.getAccount("NONEXISTENT"));
        }

        @Test
        @DisplayName("should remove an account")
        void shouldRemoveAccount() {
            SavingsAccount account = new SavingsAccount("Alice", 1000.0, 2.5, 100.0);
            db.addAccount(account);

            assertTrue(db.removeAccount(account.getAccountId()));
            assertNull(db.getAccount(account.getAccountId()));
            assertEquals(0, db.getAccountCount());
        }

        @Test
        @DisplayName("should return false when removing non-existent account")
        void shouldReturnFalseForMissingRemove() {
            assertFalse(db.removeAccount("NONEXISTENT"));
        }

        @Test
        @DisplayName("should list all accounts")
        void shouldListAllAccounts() {
            db.addAccount(new SavingsAccount("Alice", 1000.0, 2.5, 100.0));
            db.addAccount(new CheckingAccount("Bob", 500.0, 200.0));

            List<Account> accounts = db.getAllAccounts();
            assertEquals(2, accounts.size());
        }

        @Test
        @DisplayName("should track account count")
        void shouldTrackCount() {
            assertEquals(0, db.getAccountCount());
            db.addAccount(new SavingsAccount("Alice", 1000.0, 2.5, 100.0));
            assertEquals(1, db.getAccountCount());
        }

        @Test
        @DisplayName("should check account existence")
        void shouldCheckExistence() {
            SavingsAccount account = new SavingsAccount("Alice", 1000.0, 2.5, 100.0);
            db.addAccount(account);

            assertTrue(db.accountExists(account.getAccountId()));
            assertFalse(db.accountExists("NOPE"));
        }
    }

    @Nested
    @DisplayName("Search")
    class Search {

        @Test
        @DisplayName("should search by holder name (case-insensitive)")
        void shouldSearchByName() {
            db.addAccount(new SavingsAccount("Alice Johnson", 1000.0, 2.5, 100.0));
            db.addAccount(new CheckingAccount("Bob Smith", 500.0, 200.0));
            db.addAccount(new SavingsAccount("Alice Cooper", 800.0, 1.5, 50.0));

            List<Account> results = db.searchByHolder("alice");
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("should return all when search is blank")
        void shouldReturnAllForBlankSearch() {
            db.addAccount(new SavingsAccount("Alice", 1000.0, 2.5, 100.0));
            db.addAccount(new CheckingAccount("Bob", 500.0, 200.0));

            assertEquals(2, db.searchByHolder("").size());
            assertEquals(2, db.searchByHolder(null).size());
        }
    }
}
