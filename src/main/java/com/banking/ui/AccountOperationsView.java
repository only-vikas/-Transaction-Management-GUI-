package com.banking.ui;

import com.banking.model.Account;
import com.banking.model.SavingsAccount;
import com.banking.model.CheckingAccount;
import com.banking.service.BankingService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * View for performing deposit and withdrawal operations on accounts.
 */
public class AccountOperationsView {

    private final BankingService bankingService;
    private final Runnable onRefresh;

    public AccountOperationsView(BankingService bankingService, Runnable onRefresh) {
        this.bankingService = bankingService;
        this.onRefresh = onRefresh;
    }

    public Node getView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(32));
        container.getStyleClass().add("view-container");

        // ── Page Header ──────────────────────────────────────
        VBox headerSection = new VBox(4);
        Label pageTitle = new Label("Account Operations");
        pageTitle.getStyleClass().add("page-title");
        Label pageSubtitle = new Label("Deposit, withdraw, or apply interest to accounts");
        pageSubtitle.getStyleClass().add("page-subtitle");
        headerSection.getChildren().addAll(pageTitle, pageSubtitle);

        List<Account> accounts = bankingService.getAllAccounts();

        if (accounts.isEmpty()) {
            VBox emptyState = new VBox(8);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(60));
            emptyState.getStyleClass().add("empty-state");

            Label emptyIcon = new Label("🏦");
            emptyIcon.setStyle("-fx-font-size: 48px;");
            Label emptyText = new Label("No accounts found");
            emptyText.getStyleClass().add("empty-text");
            Label emptyHint = new Label("Create an account first to perform operations.");
            emptyHint.getStyleClass().add("empty-hint");

            emptyState.getChildren().addAll(emptyIcon, emptyText, emptyHint);
            container.getChildren().addAll(headerSection, emptyState);
            return container;
        }

        // ── Account Selector ─────────────────────────────────
        VBox selectorCard = new VBox(12);
        selectorCard.setPadding(new Insets(24));
        selectorCard.getStyleClass().add("form-card");
        selectorCard.setMaxWidth(600);

        Label selectLabel = new Label("Select Account");
        selectLabel.getStyleClass().add("form-label");

        ComboBox<String> accountSelector = new ComboBox<>();
        accountSelector.getStyleClass().add("form-combo");
        accountSelector.setMaxWidth(Double.MAX_VALUE);
        accountSelector.setPromptText("Choose an account...");

        for (Account account : accounts) {
            accountSelector.getItems().add(
                    account.getAccountId() + " — " + account.getAccountHolder() +
                            " (" + account.getAccountType() + ") | $" +
                            String.format("%.2f", account.getBalance()));
        }

        // ── Account Details ──────────────────────────────────
        VBox detailsBox = new VBox(8);
        detailsBox.getStyleClass().add("details-box");
        detailsBox.setPadding(new Insets(16));
        detailsBox.setVisible(false);
        detailsBox.setManaged(false);

        Label detailsTitle = new Label();
        detailsTitle.getStyleClass().add("details-title");
        Label detailsInfo = new Label();
        detailsInfo.getStyleClass().add("details-info");
        Label detailsBalance = new Label();
        detailsBalance.getStyleClass().add("details-balance");

        detailsBox.getChildren().addAll(detailsTitle, detailsInfo, detailsBalance);

        selectorCard.getChildren().addAll(selectLabel, accountSelector, detailsBox);

        // ── Operations Cards ─────────────────────────────────
        HBox opsRow = new HBox(16);
        opsRow.setAlignment(Pos.TOP_LEFT);

        // Deposit Card
        VBox depositCard = createOperationCard("💰 Deposit", "deposit", accountSelector);
        // Withdraw Card
        VBox withdrawCard = createOperationCard("💸 Withdraw", "withdraw", accountSelector);
        // Interest Card (only for savings)
        VBox interestCard = new VBox(16);
        interestCard.setPadding(new Insets(24));
        interestCard.getStyleClass().add("form-card");
        interestCard.setPrefWidth(280);

        Label interestTitle = new Label("📈 Apply Interest");
        interestTitle.getStyleClass().add("section-title");

        Label interestInfo = new Label("Apply annual interest rate to savings accounts");
        interestInfo.getStyleClass().add("form-hint");
        interestInfo.setWrapText(true);

        Label interestStatus = new Label();
        interestStatus.getStyleClass().add("status-label");
        interestStatus.setWrapText(true);

        Button interestBtn = new Button("Apply Interest");
        interestBtn.getStyleClass().add("btn-accent");
        interestBtn.setMaxWidth(Double.MAX_VALUE);
        interestBtn.setDisable(true);

        interestCard.getChildren().addAll(interestTitle, interestInfo, interestStatus, interestBtn);

        HBox.setHgrow(depositCard, Priority.ALWAYS);
        HBox.setHgrow(withdrawCard, Priority.ALWAYS);
        HBox.setHgrow(interestCard, Priority.ALWAYS);

        opsRow.getChildren().addAll(depositCard, withdrawCard, interestCard);

        // ── Account Selection Handler ────────────────────────
        accountSelector.setOnAction(e -> {
            String selected = accountSelector.getValue();
            if (selected != null) {
                String accountId = selected.split(" — ")[0].trim();
                try {
                    Account account = bankingService.getAccount(accountId);

                    detailsTitle.setText(account.getAccountHolder());
                    detailsInfo.setText(account.getAccountDetails());
                    detailsBalance.setText(String.format("Balance: $%,.2f", account.getBalance()));
                    detailsBox.setVisible(true);
                    detailsBox.setManaged(true);

                    // Enable/disable interest button
                    boolean isSavings = account instanceof SavingsAccount;
                    interestBtn.setDisable(!isSavings);
                    if (!isSavings) {
                        interestStatus.setText("Interest only applies to savings accounts");
                        interestStatus.getStyleClass().removeAll("status-success", "status-error");
                    } else {
                        interestStatus.setText("");
                    }

                } catch (Exception ex) {
                    detailsBox.setVisible(false);
                    detailsBox.setManaged(false);
                }
            }
        });

        // Interest button handler
        interestBtn.setOnAction(e -> {
            String selected = accountSelector.getValue();
            if (selected != null) {
                String accountId = selected.split(" — ")[0].trim();
                try {
                    bankingService.applyInterest(accountId);
                    Account account = bankingService.getAccount(accountId);
                    interestStatus.setText("✅ Interest applied! New balance: $" +
                            String.format("%.2f", account.getBalance()));
                    interestStatus.getStyleClass().removeAll("status-success", "status-error");
                    interestStatus.getStyleClass().add("status-success");
                    detailsBalance.setText(String.format("Balance: $%,.2f", account.getBalance()));
                    refreshSelector(accountSelector, accounts);
                } catch (Exception ex) {
                    interestStatus.setText("❌ " + ex.getMessage());
                    interestStatus.getStyleClass().removeAll("status-success", "status-error");
                    interestStatus.getStyleClass().add("status-error");
                }
            }
        });

        container.getChildren().addAll(headerSection, selectorCard, opsRow);
        return container;
    }

    private VBox createOperationCard(String title, String operation, ComboBox<String> accountSelector) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.getStyleClass().add("form-card");
        card.setPrefWidth(280);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        Label amountLabel = new Label("Amount ($)");
        amountLabel.getStyleClass().add("form-label");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.getStyleClass().add("form-input");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        String btnStyle = operation.equals("deposit") ? "btn-success" : "btn-warning";
        Button actionBtn = new Button(operation.equals("deposit") ? "Deposit" : "Withdraw");
        actionBtn.getStyleClass().add(btnStyle);
        actionBtn.setMaxWidth(Double.MAX_VALUE);

        actionBtn.setOnAction(e -> {
            String selected = accountSelector.getValue();
            if (selected == null) {
                showStatus(statusLabel, "Please select an account first", false);
                return;
            }

            String accountId = selected.split(" — ")[0].trim();
            String amountText = amountField.getText().trim();

            if (amountText.isEmpty()) {
                showStatus(statusLabel, "Please enter an amount", false);
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                if (operation.equals("deposit")) {
                    bankingService.deposit(accountId, amount);
                } else {
                    bankingService.withdraw(accountId, amount);
                }

                Account account = bankingService.getAccount(accountId);
                showStatus(statusLabel,
                        String.format("✅ %s $%.2f successful! Balance: $%.2f",
                                operation.equals("deposit") ? "Deposited" : "Withdrew",
                                amount, account.getBalance()),
                        true);
                amountField.clear();

                // Refresh the selector to show updated balance
                refreshSelector(accountSelector, bankingService.getAllAccounts());

            } catch (NumberFormatException ex) {
                showStatus(statusLabel, "Invalid amount. Enter a number.", false);
            } catch (IllegalArgumentException ex) {
                showStatus(statusLabel, "❌ " + ex.getMessage(), false);
            }
        });

        card.getChildren().addAll(titleLabel, amountLabel, amountField, statusLabel, actionBtn);
        return card;
    }

    private void refreshSelector(ComboBox<String> selector, List<Account> accounts) {
        String currentSelection = selector.getValue();
        String currentId = currentSelection != null ? currentSelection.split(" — ")[0].trim() : null;

        selector.getItems().clear();
        int selectedIndex = -1;
        int i = 0;
        for (Account account : accounts) {
            String item = account.getAccountId() + " — " + account.getAccountHolder() +
                    " (" + account.getAccountType() + ") | $" +
                    String.format("%.2f", account.getBalance());
            selector.getItems().add(item);
            if (account.getAccountId().equals(currentId)) {
                selectedIndex = i;
            }
            i++;
        }
        if (selectedIndex >= 0) {
            selector.getSelectionModel().select(selectedIndex);
        }
    }

    private void showStatus(Label label, String message, boolean success) {
        label.setText(message);
        label.getStyleClass().removeAll("status-success", "status-error");
        label.getStyleClass().add(success ? "status-success" : "status-error");
    }
}
