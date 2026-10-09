package com.banking;

import com.banking.service.BankingService;
import com.banking.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main JavaFX Application class.
 * Sets up the primary stage with the MainView and loads styling.
 */
public class App extends Application {

    private static final double MIN_WIDTH = 1100;
    private static final double MIN_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        BankingService bankingService = new BankingService();
        seedSampleData(bankingService);

        MainView mainView = new MainView(bankingService);

        Scene scene = new Scene(mainView.getRoot(), MIN_WIDTH, MIN_HEIGHT);

        // Load CSS
        String css = getClass().getResource("/com/banking/styles/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("🏦 Transaction Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        System.out.println("=================================================");
        System.out.println("🏦 Banking Transaction Management System Launched");
        System.out.println("=================================================");
        System.out.println("Active Accounts: " + bankingService.getTotalAccounts());
        System.out.printf("Total Vault Balance: $%,.2f%n", bankingService.getTotalBalance());
        System.out.println("Total Transactions: " + bankingService.getTotalTransactions());
        System.out.println("Status: GUI Window opened successfully.");
        System.out.println("=================================================");
    }

    private void seedSampleData(BankingService service) {
        if (service.getAllAccounts().isEmpty()) {
            var sa = service.createSavingsAccount("Alice Smith", 5000.00, 4.5, 500.00);
            var ca = service.createCheckingAccount("Bob Jones", 2500.00, 1000.00);
            service.deposit(sa.getAccountId(), 1200.00);
            service.withdraw(ca.getAccountId(), 350.00);
            service.applyInterest(sa.getAccountId());
            service.transfer(sa.getAccountId(), ca.getAccountId(), 300.00);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
