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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
