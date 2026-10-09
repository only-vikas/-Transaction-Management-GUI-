# 🏦 Transaction Management GUI

A sample banking transaction management system built with **Java** and **JavaFX**, demonstrating core object-oriented programming principles through a polished, modern GUI.

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=flat-square)
![Gradle](https://img.shields.io/badge/Gradle-8.x-green?style=flat-square&logo=gradle)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

## ✨ Features

| Feature | Description |
|---------|-------------|
| **📊 Dashboard** | Real-time overview with summary cards and recent transactions |
| **➕ Create Account** | Open Savings or Checking accounts with custom parameters |
| **💳 Operations** | Deposit, withdraw, and apply interest with live balance updates |
| **📋 All Accounts** | Searchable table of all accounts with delete functionality |
| **📜 Transaction History** | Complete audit trail with account filtering |

## 🏗️ OOP Principles Demonstrated

| Principle | Implementation |
|-----------|---------------|
| **Abstraction** | `Account` abstract class defines the banking contract |
| **Inheritance** | `SavingsAccount` and `CheckingAccount` extend `Account` |
| **Polymorphism** | `withdraw()` enforces different rules per account type |
| **Encapsulation** | Private fields with controlled getter/setter access |
| **Singleton** | `AccountDatabase` ensures a single shared data store |

## 📁 Project Structure

```
src/
├── main/java/com/banking/
│   ├── App.java                    # JavaFX Application entry point
│   ├── Launcher.java               # JAR-compatible launcher
│   ├── model/
│   │   ├── Account.java            # Abstract base class
│   │   ├── SavingsAccount.java     # Interest + minimum balance
│   │   ├── CheckingAccount.java    # Overdraft protection
│   │   ├── Transaction.java        # Immutable transaction record
│   │   ├── AccountType.java        # SAVINGS, CHECKING enum
│   │   └── TransactionType.java    # DEPOSIT, WITHDRAWAL, etc.
│   ├── database/
│   │   └── AccountDatabase.java    # Singleton in-memory store
│   ├── service/
│   │   └── BankingService.java     # Business logic layer
│   └── ui/
│       ├── MainView.java           # Sidebar + content layout
│       ├── DashboardView.java      # Summary cards & recent txns
│       ├── CreateAccountView.java  # Account creation form
│       ├── AccountOperationsView.java  # Deposit/Withdraw/Interest
│       ├── AllAccountsView.java    # Searchable accounts table
│       └── TransactionHistoryView.java # Filtered txn history
├── main/resources/com/banking/styles/
│   └── styles.css                  # Modern JavaFX styling
└── test/java/com/banking/
    ├── model/
    │   ├── SavingsAccountTest.java
    │   └── CheckingAccountTest.java
    ├── database/
    │   └── AccountDatabaseTest.java
    └── service/
        └── BankingServiceTest.java
```

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (JDK)
- **Git**

### Clone & Run

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/transaction-management-gui.git
cd transaction-management-gui

# Run the application (Maven Wrapper)
./mvnw javafx:run           # Linux/macOS
mvnw.cmd javafx:run         # Windows

# Run tests (59 unit tests)
./mvnw test                 # Linux/macOS
mvnw.cmd test               # Windows
```

### Build & Run Executable Fat JAR

```bash
# Package standalone uber-jar
mvnw.cmd package -DskipTests

# Run the shaded JAR directly
java -jar target/transaction-management-gui-1.0.0.jar
```

## 🧪 Testing

The project includes **30+ unit tests** covering:
- Account creation and validation
- Deposit and withdrawal operations
- Savings minimum balance enforcement
- Checking overdraft protection
- Interest application
- Database singleton behavior
- Service layer business logic

```bash
gradlew.bat test
```

## 📸 Screenshots

| Dashboard | Create Account | Operations |
|-----------|---------------|------------|
| Summary cards with real-time stats | Dynamic form for Savings/Checking | Deposit, Withdraw, Apply Interest |

## 🛠️ Tech Stack

- **Java 17+** — Core language
- **JavaFX 21** — GUI framework
- **Gradle** — Build automation
- **JUnit 5** — Testing framework

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

*Built as a demonstration of Java OOP principles and JavaFX GUI development.*
