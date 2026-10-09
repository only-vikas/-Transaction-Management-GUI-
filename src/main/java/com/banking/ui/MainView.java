package com.banking.ui;

import com.banking.service.BankingService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

/**
 * Main application layout with sidebar navigation and dynamic content area.
 */
public class MainView {

    private final BankingService bankingService;
    private final BorderPane root;
    private final StackPane contentArea;

    // Navigation buttons
    private Button activeButton;
    private Button dashboardBtn;
    private Button createAccountBtn;
    private Button operationsBtn;
    private Button allAccountsBtn;
    private Button historyBtn;

    private String currentView = "dashboard";

    public MainView(BankingService bankingService) {
        this.bankingService = bankingService;
        this.root = new BorderPane();
        this.contentArea = new StackPane();

        buildUI();
        navigateTo("dashboard");
    }

    private void buildUI() {
        // ── Sidebar ──────────────────────────────────────────
        VBox sidebar = createSidebar();
        sidebar.getStyleClass().add("sidebar");

        // ── Content Area ─────────────────────────────────────
        contentArea.getStyleClass().add("content-area");
        contentArea.setPadding(new Insets(0));

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("content-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setLeft(sidebar);
        root.setCenter(scrollPane);
        root.getStyleClass().add("root-pane");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setSpacing(4);
        sidebar.setPadding(new Insets(0));

        // ── App Header ──────────────────────────────────────
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 30, 20));
        header.getStyleClass().add("sidebar-header");

        Label logo = new Label("🏦");
        logo.setStyle("-fx-font-size: 36px;");

        Label title = new Label("TransactPro");
        title.getStyleClass().add("sidebar-title");

        Label subtitle = new Label("Banking Management");
        subtitle.getStyleClass().add("sidebar-subtitle");

        header.getChildren().addAll(logo, title, subtitle);

        // ── Navigation Items ────────────────────────────────
        VBox navItems = new VBox(2);
        navItems.setPadding(new Insets(20, 12, 20, 12));

        dashboardBtn = createNavButton("📊", "Dashboard", "dashboard");
        createAccountBtn = createNavButton("➕", "Create Account", "create");
        operationsBtn = createNavButton("💳", "Operations", "operations");
        allAccountsBtn = createNavButton("📋", "All Accounts", "accounts");
        historyBtn = createNavButton("📜", "Transaction History", "history");

        navItems.getChildren().addAll(
                dashboardBtn, createAccountBtn, operationsBtn,
                allAccountsBtn, historyBtn
        );

        // ── Footer ──────────────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox footer = new VBox(4);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));
        footer.getStyleClass().add("sidebar-footer");

        Label version = new Label("v1.0.0");
        version.getStyleClass().add("sidebar-version");

        Label copyright = new Label("© 2024 TransactPro");
        copyright.getStyleClass().add("sidebar-copyright");

        footer.getChildren().addAll(version, copyright);

        sidebar.getChildren().addAll(header, navItems, spacer, footer);
        return sidebar;
    }

    private Button createNavButton(String icon, String text, String viewName) {
        Button button = new Button(icon + "  " + text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(e -> navigateTo(viewName));
        return button;
    }

    /**
     * Navigates to the specified view, updating the content area and active button.
     */
    public void navigateTo(String viewName) {
        this.currentView = viewName;
        contentArea.getChildren().clear();

        // Update active button styles
        updateActiveButton(viewName);

        // Load the appropriate view
        switch (viewName) {
            case "dashboard" -> contentArea.getChildren().add(
                    new DashboardView(bankingService, this::refresh).getView());
            case "create" -> contentArea.getChildren().add(
                    new CreateAccountView(bankingService, this::refresh).getView());
            case "operations" -> contentArea.getChildren().add(
                    new AccountOperationsView(bankingService, this::refresh).getView());
            case "accounts" -> contentArea.getChildren().add(
                    new AllAccountsView(bankingService, this::refresh).getView());
            case "history" -> contentArea.getChildren().add(
                    new TransactionHistoryView(bankingService).getView());
        }
    }

    private void updateActiveButton(String viewName) {
        // Remove active class from previous
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }

        // Add active class to current
        Button newActive = switch (viewName) {
            case "dashboard" -> dashboardBtn;
            case "create" -> createAccountBtn;
            case "operations" -> operationsBtn;
            case "accounts" -> allAccountsBtn;
            case "history" -> historyBtn;
            default -> dashboardBtn;
        };

        newActive.getStyleClass().add("nav-button-active");
        activeButton = newActive;
    }

    /**
     * Refreshes the current view (called after data mutations).
     */
    public void refresh() {
        navigateTo(currentView);
    }

    public BorderPane getRoot() {
        return root;
    }
}
