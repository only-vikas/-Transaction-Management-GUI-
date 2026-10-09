package com.banking.ui;

import com.banking.model.Account;
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
 * View displaying all accounts in a searchable table with delete functionality.
 */
public class AllAccountsView {

    private final BankingService bankingService;
    private final Runnable onRefresh;

    public AllAccountsView(BankingService bankingService, Runnable onRefresh) {
        this.bankingService = bankingService;
        this.onRefresh = onRefresh;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(32));
        container.getStyleClass().add("view-container");

        // ── Page Header ──────────────────────────────────────
        VBox headerSection = new VBox(4);
        Label pageTitle = new Label("All Accounts");
        pageTitle.getStyleClass().add("page-title");
        Label pageSubtitle = new Label("View and manage all bank accounts");
        pageSubtitle.getStyleClass().add("page-subtitle");
        headerSection.getChildren().addAll(pageTitle, pageSubtitle);

        // ── Search Bar ───────────────────────────────────────
        HBox searchBar = new HBox(12);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by account holder name...");
        searchField.getStyleClass().add("search-input");
        searchField.setPrefWidth(350);

        Label countLabel = new Label(bankingService.getTotalAccounts() + " accounts");
        countLabel.getStyleClass().add("count-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchBar.getChildren().addAll(searchField, spacer, countLabel);

        // ── Accounts Table ───────────────────────────────────
        TableView<Account> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setMinHeight(400);

        TableColumn<Account, String> idCol = new TableColumn<>("Account ID");
        idCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAccountId()));
        idCol.setPrefWidth(110);

        TableColumn<Account, String> holderCol = new TableColumn<>("Account Holder");
        holderCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAccountHolder()));
        holderCol.setPrefWidth(180);

        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAccountType().getDisplayName()));
        typeCol.setPrefWidth(100);

        TableColumn<Account, Number> balanceCol = new TableColumn<>("Balance ($)");
        balanceCol.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getBalance()));
        balanceCol.setPrefWidth(120);

        TableColumn<Account, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAccountDetails()));
        detailsCol.setPrefWidth(250);

        TableColumn<Account, String> dateCol = new TableColumn<>("Created");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFormattedCreatedAt()));
        dateCol.setPrefWidth(160);

        TableColumn<Account, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑 Delete");

            {
                deleteBtn.getStyleClass().add("btn-danger-small");
                deleteBtn.setOnAction(e -> {
                    Account account = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Account");
                    confirm.setHeaderText("Delete " + account.getAccountHolder() + "'s account?");
                    confirm.setContentText("Account ID: " + account.getAccountId() +
                            "\nBalance: $" + String.format("%.2f", account.getBalance()) +
                            "\n\nThis action cannot be undone.");

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            bankingService.deleteAccount(account.getAccountId());
                            if (onRefresh != null) onRefresh.run();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        table.getColumns().addAll(idCol, holderCol, typeCol, balanceCol, detailsCol, dateCol, actionCol);

        // Load data
        List<Account> allAccounts = bankingService.getAllAccounts();
        table.getItems().addAll(allAccounts);

        // Search functionality
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<Account> filtered = bankingService.searchAccounts(newText);
            table.getItems().clear();
            table.getItems().addAll(filtered);
            countLabel.setText(filtered.size() + " accounts");
        });

        // Empty state
        if (allAccounts.isEmpty()) {
            table.setPlaceholder(new Label("No accounts created yet. Go to 'Create Account' to get started!"));
        }

        container.getChildren().addAll(headerSection, searchBar, table);
        return container;
    }
}
