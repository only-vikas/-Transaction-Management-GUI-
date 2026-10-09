package com.banking.ui;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.service.BankingService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Dashboard view showing summary cards and recent transactions.
 */
public class DashboardView {

    private final BankingService bankingService;
    private final Runnable onRefresh;

    public DashboardView(BankingService bankingService, Runnable onRefresh) {
        this.bankingService = bankingService;
        this.onRefresh = onRefresh;
    }

    public Node getView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(32));
        container.getStyleClass().add("view-container");

        // ── Page Header ──────────────────────────────────────
        VBox headerSection = new VBox(4);
        Label pageTitle = new Label("Dashboard");
        pageTitle.getStyleClass().add("page-title");
        Label pageSubtitle = new Label("Overview of your banking system");
        pageSubtitle.getStyleClass().add("page-subtitle");
        headerSection.getChildren().addAll(pageTitle, pageSubtitle);

        // ── Summary Cards ────────────────────────────────────
        HBox cardsRow = new HBox(16);
        cardsRow.setAlignment(Pos.CENTER_LEFT);

        int totalAccounts = bankingService.getTotalAccounts();
        double totalBalance = bankingService.getTotalBalance();
        int totalTransactions = bankingService.getTotalTransactions();

        long savingsCount = bankingService.getAllAccounts().stream()
                .filter(a -> a.getAccountType().name().equals("SAVINGS")).count();
        long checkingCount = totalAccounts - savingsCount;

        Node accountsCard = createSummaryCard("📊", "Total Accounts",
                String.valueOf(totalAccounts), "card-blue");
        Node balanceCard = createSummaryCard("💰", "Total Balance",
                String.format("$%,.2f", totalBalance), "card-green");
        Node transactionsCard = createSummaryCard("📄", "Transactions",
                String.valueOf(totalTransactions), "card-gold");
        Node typesCard = createSummaryCard("🏦", "Account Types",
                savingsCount + " Savings · " + checkingCount + " Checking", "card-purple");

        HBox.setHgrow(accountsCard, Priority.ALWAYS);
        HBox.setHgrow(balanceCard, Priority.ALWAYS);
        HBox.setHgrow(transactionsCard, Priority.ALWAYS);
        HBox.setHgrow(typesCard, Priority.ALWAYS);

        cardsRow.getChildren().addAll(accountsCard, balanceCard, transactionsCard, typesCard);

        // ── Recent Transactions Table ────────────────────────
        VBox recentSection = new VBox(12);
        Label recentTitle = new Label("📜 Recent Transactions");
        recentTitle.getStyleClass().add("section-title");

        List<Transaction> recentTransactions = bankingService.getAllTransactions();
        if (recentTransactions.size() > 10) {
            recentTransactions = recentTransactions.subList(0, 10);
        }

        if (recentTransactions.isEmpty()) {
            VBox emptyState = new VBox(8);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(40));
            emptyState.getStyleClass().add("empty-state");

            Label emptyIcon = new Label("📭");
            emptyIcon.setStyle("-fx-font-size: 48px;");
            Label emptyText = new Label("No transactions yet");
            emptyText.getStyleClass().add("empty-text");
            Label emptyHint = new Label("Create an account and start making transactions!");
            emptyHint.getStyleClass().add("empty-hint");

            emptyState.getChildren().addAll(emptyIcon, emptyText, emptyHint);
            recentSection.getChildren().addAll(recentTitle, emptyState);
        } else {
            TableView<Transaction> table = createTransactionTable(recentTransactions);
            VBox.setVgrow(table, Priority.ALWAYS);
            recentSection.getChildren().addAll(recentTitle, table);
        }

        // ── Accounts Overview ────────────────────────────────
        VBox accountsSection = new VBox(12);
        Label accountsTitle = new Label("📋 Accounts Overview");
        accountsTitle.getStyleClass().add("section-title");

        List<Account> accounts = bankingService.getAllAccounts();
        if (!accounts.isEmpty()) {
            FlowPane accountCards = new FlowPane(12, 12);
            for (Account account : accounts) {
                accountCards.getChildren().add(createAccountMiniCard(account));
            }
            accountsSection.getChildren().addAll(accountsTitle, accountCards);
        }

        VBox.setVgrow(recentSection, Priority.ALWAYS);
        container.getChildren().addAll(headerSection, cardsRow, recentSection);
        if (!accounts.isEmpty()) {
            container.getChildren().add(accountsSection);
        }

        return container;
    }

    private Node createSummaryCard(String icon, String title, String value, String colorClass) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.getStyleClass().addAll("summary-card", colorClass);
        card.setMinWidth(180);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    private Node createAccountMiniCard(Account account) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("account-mini-card");
        card.setPrefWidth(240);

        Label nameLabel = new Label(account.getAccountHolder());
        nameLabel.getStyleClass().add("mini-card-name");

        Label typeLabel = new Label(account.getAccountType().getDisplayName() + " • " + account.getAccountId());
        typeLabel.getStyleClass().add("mini-card-type");

        Label balanceLabel = new Label(String.format("$%,.2f", account.getBalance()));
        balanceLabel.getStyleClass().add("mini-card-balance");

        card.getChildren().addAll(nameLabel, typeLabel, balanceLabel);
        return card;
    }

    @SuppressWarnings("unchecked")
    private TableView<Transaction> createTransactionTable(List<Transaction> transactions) {
        TableView<Transaction> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Transaction, String> idCol = new TableColumn<>("Transaction ID");
        idCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTransactionId()));
        idCol.setPrefWidth(120);

        TableColumn<Transaction, String> accountCol = new TableColumn<>("Account");
        accountCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAccountId()));
        accountCol.setPrefWidth(100);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getType().getDisplayName()));
        typeCol.setPrefWidth(100);

        TableColumn<Transaction, Number> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getAmount()));
        amountCol.setPrefWidth(100);

        TableColumn<Transaction, Number> balanceCol = new TableColumn<>("Balance After");
        balanceCol.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getBalanceAfter()));
        balanceCol.setPrefWidth(110);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFormattedTimestamp()));
        dateCol.setPrefWidth(160);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescription()));
        descCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, accountCol, typeCol, amountCol, balanceCol, dateCol, descCol);
        table.getItems().addAll(transactions);
        table.setPrefHeight(Math.min(350, 50 + transactions.size() * 35));

        return table;
    }
}
