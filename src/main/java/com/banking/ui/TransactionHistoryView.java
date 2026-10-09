package com.banking.ui;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.service.BankingService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * View displaying full transaction history with account filtering.
 */
public class TransactionHistoryView {

    private final BankingService bankingService;

    public TransactionHistoryView(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(32));
        container.getStyleClass().add("view-container");

        // ── Page Header ──────────────────────────────────────
        VBox headerSection = new VBox(4);
        Label pageTitle = new Label("Transaction History");
        pageTitle.getStyleClass().add("page-title");
        Label pageSubtitle = new Label("Complete record of all transactions");
        pageSubtitle.getStyleClass().add("page-subtitle");
        headerSection.getChildren().addAll(pageTitle, pageSubtitle);

        // ── Filters ──────────────────────────────────────────
        HBox filterBar = new HBox(16);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filter by Account:");
        filterLabel.getStyleClass().add("form-label");

        ComboBox<String> accountFilter = new ComboBox<>();
        accountFilter.getStyleClass().add("form-combo");
        accountFilter.setPromptText("All Accounts");
        accountFilter.getItems().add("All Accounts");
        for (Account account : bankingService.getAllAccounts()) {
            accountFilter.getItems().add(
                    account.getAccountId() + " — " + account.getAccountHolder());
        }
        accountFilter.setValue("All Accounts");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalLabel = new Label();
        totalLabel.getStyleClass().add("count-label");

        filterBar.getChildren().addAll(filterLabel, accountFilter, spacer, totalLabel);

        // ── Transaction Table ────────────────────────────────
        TableView<Transaction> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setMinHeight(450);

        TableColumn<Transaction, String> idCol = new TableColumn<>("Transaction ID");
        idCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTransactionId()));
        idCol.setPrefWidth(130);

        TableColumn<Transaction, String> accountCol = new TableColumn<>("Account");
        accountCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAccountId()));
        accountCol.setPrefWidth(110);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getType().getDisplayName()));
        typeCol.setPrefWidth(100);

        TableColumn<Transaction, Number> amountCol = new TableColumn<>("Amount ($)");
        amountCol.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getAmount()));
        amountCol.setPrefWidth(110);

        TableColumn<Transaction, Number> balanceCol = new TableColumn<>("Balance After ($)");
        balanceCol.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getBalanceAfter()));
        balanceCol.setPrefWidth(130);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescription()));
        descCol.setPrefWidth(180);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFormattedTimestamp()));
        dateCol.setPrefWidth(160);

        table.getColumns().addAll(idCol, accountCol, typeCol, amountCol, balanceCol, descCol, dateCol);

        // Load all transactions
        List<Transaction> allTransactions = bankingService.getAllTransactions();
        table.getItems().addAll(allTransactions);
        totalLabel.setText(allTransactions.size() + " transactions");

        // Filter handler
        accountFilter.setOnAction(e -> {
            String selected = accountFilter.getValue();
            table.getItems().clear();

            List<Transaction> filtered;
            if (selected == null || selected.equals("All Accounts")) {
                filtered = bankingService.getAllTransactions();
            } else {
                String accountId = selected.split(" — ")[0].trim();
                filtered = bankingService.getTransactionHistory(accountId);
            }

            table.getItems().addAll(filtered);
            totalLabel.setText(filtered.size() + " transactions");
        });

        // Empty state
        if (allTransactions.isEmpty()) {
            table.setPlaceholder(
                    new Label("No transactions yet. Perform deposits or withdrawals to see them here!"));
        }

        container.getChildren().addAll(headerSection, filterBar, table);
        return container;
    }
}
