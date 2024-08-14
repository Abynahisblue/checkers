package org.webapp.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Print the resource path to help with debugging
            System.out.println(Objects.requireNonNull(this.getClass().getResource("/org/webapp/checkers/draught-view.fxml")));

            // Load the FXML file and set up the scene
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(this.getClass().getResource("/org/webapp/checkers/draught-view.fxml")));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Set up the primary stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Draughts Game");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error dialog)
        } catch (NullPointerException e) {
            e.printStackTrace();
            // Handle the null pointer exception (e.g., FXML file not found)
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
