package com.banking.ui;

import com.banking.model.AccountType;
import com.banking.service.BankingService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Form view for creating new Savings or Checking accounts.
 */
public class CreateAccountView {

    private final BankingService bankingService;
    private final Runnable onRefresh;

    public CreateAccountView(BankingService bankingService, Runnable onRefresh) {
        this.bankingService = bankingService;
        this.onRefresh = onRefresh;
    }

    public Node getView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(32));
        container.getStyleClass().add("view-container");

        // ── Page Header ──────────────────────────────────────
        VBox headerSection = new VBox(4);
        Label pageTitle = new Label("Create Account");
        pageTitle.getStyleClass().add("page-title");
        Label pageSubtitle = new Label("Open a new savings or checking account");
        pageSubtitle.getStyleClass().add("page-subtitle");
        headerSection.getChildren().addAll(pageTitle, pageSubtitle);

        // ── Form Card ────────────────────────────────────────
        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(28));
        formCard.getStyleClass().add("form-card");
        formCard.setMaxWidth(550);

        // Account Type Selection
        Label typeLabel = new Label("Account Type");
        typeLabel.getStyleClass().add("form-label");

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton savingsRadio = new RadioButton("💰 Savings Account");
        savingsRadio.setToggleGroup(typeGroup);
        savingsRadio.setSelected(true);
        savingsRadio.getStyleClass().add("form-radio");
        savingsRadio.setUserData(AccountType.SAVINGS);

        RadioButton checkingRadio = new RadioButton("💳 Checking Account");
        checkingRadio.setToggleGroup(typeGroup);
        checkingRadio.getStyleClass().add("form-radio");
        checkingRadio.setUserData(AccountType.CHECKING);

        HBox typeBox = new HBox(20, savingsRadio, checkingRadio);
        typeBox.setAlignment(Pos.CENTER_LEFT);

        // Common Fields
        Label nameLabel = new Label("Account Holder Name");
        nameLabel.getStyleClass().add("form-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");
        nameField.getStyleClass().add("form-input");

        Label depositLabel = new Label("Initial Deposit ($)");
        depositLabel.getStyleClass().add("form-label");
        TextField depositField = new TextField();
        depositField.setPromptText("e.g., 1000.00");
        depositField.getStyleClass().add("form-input");

        // Savings-specific fields
        VBox savingsFields = new VBox(12);
        Label interestLabel = new Label("Interest Rate (%)");
        interestLabel.getStyleClass().add("form-label");
        TextField interestField = new TextField("2.5");
        interestField.setPromptText("e.g., 2.5");
        interestField.getStyleClass().add("form-input");

        Label minBalanceLabel = new Label("Minimum Balance ($)");
        minBalanceLabel.getStyleClass().add("form-label");
        TextField minBalanceField = new TextField("100.00");
        minBalanceField.setPromptText("e.g., 100.00");
        minBalanceField.getStyleClass().add("form-input");

        savingsFields.getChildren().addAll(interestLabel, interestField, minBalanceLabel, minBalanceField);

        // Checking-specific fields
        VBox checkingFields = new VBox(12);
        Label overdraftLabel = new Label("Overdraft Limit ($)");
        overdraftLabel.getStyleClass().add("form-label");
        TextField overdraftField = new TextField("500.00");
        overdraftField.setPromptText("e.g., 500.00");
        overdraftField.getStyleClass().add("form-input");

        checkingFields.getChildren().addAll(overdraftLabel, overdraftField);
        checkingFields.setVisible(false);
        checkingFields.setManaged(false);

        // Toggle fields based on type
        typeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                AccountType selectedType = (AccountType) newToggle.getUserData();
                boolean isSavings = selectedType == AccountType.SAVINGS;
                savingsFields.setVisible(isSavings);
                savingsFields.setManaged(isSavings);
                checkingFields.setVisible(!isSavings);
                checkingFields.setManaged(!isSavings);
            }
        });

        // Status message
        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        // Submit Button
        Button submitBtn = new Button("✅ Create Account");
        submitBtn.getStyleClass().add("btn-primary");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showStatus(statusLabel, "Please enter the account holder name.", false);
                    return;
                }

                double initialDeposit = parseDouble(depositField.getText(), "Initial deposit");

                AccountType type = (AccountType) typeGroup.getSelectedToggle().getUserData();

                if (type == AccountType.SAVINGS) {
                    double rate = parseDouble(interestField.getText(), "Interest rate");
                    double minBal = parseDouble(minBalanceField.getText(), "Minimum balance");
                    bankingService.createSavingsAccount(name, initialDeposit, rate, minBal);
                } else {
                    double overdraft = parseDouble(overdraftField.getText(), "Overdraft limit");
                    bankingService.createCheckingAccount(name, initialDeposit, overdraft);
                }

                showStatus(statusLabel,
                        "✅ " + type.getDisplayName() + " account created successfully for " + name + "!",
                        true);

                // Clear form
                nameField.clear();
                depositField.clear();

                if (onRefresh != null) {
                    // Small delay so user sees success message
                }

            } catch (IllegalArgumentException ex) {
                showStatus(statusLabel, "❌ " + ex.getMessage(), false);
            } catch (Exception ex) {
                showStatus(statusLabel, "❌ Error: " + ex.getMessage(), false);
            }
        });

        // ── Separator ────────────────────────────────────────
        Separator separator = new Separator();
        separator.getStyleClass().add("form-separator");

        formCard.getChildren().addAll(
                typeLabel, typeBox,
                separator,
                nameLabel, nameField,
                depositLabel, depositField,
                savingsFields,
                checkingFields,
                statusLabel,
                submitBtn
        );

        container.getChildren().addAll(headerSection, formCard);
        return container;
    }

    private double parseDouble(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        try {
            double value = Double.parseDouble(text.trim());
            if (value < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number");
        }
    }

    private void showStatus(Label label, String message, boolean success) {
        label.setText(message);
        label.getStyleClass().removeAll("status-success", "status-error");
        label.getStyleClass().add(success ? "status-success" : "status-error");
    }
}
