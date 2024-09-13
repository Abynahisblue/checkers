package org.webapp.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MainApplication extends Application {

    private static final String FXML_PATH = "/org/webapp/checkers/draught-view.fxml";
    private static final String WINDOW_TITLE = "Draughts Game";

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlUrl = getClass().getResource(FXML_PATH);
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found: " + FXML_PATH);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle(WINDOW_TITLE);
            primaryStage.show();
        } catch (IOException e) {
            handleException("Error loading FXML", e);
        }
    }

    private void handleException(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
        // Here you could add code to display an error dialog to the user
    }

    public static void main(String[] args) {
        launch(args);
    }
}
