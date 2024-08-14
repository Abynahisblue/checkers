module org.webapp.checkers {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.webapp.checkers to javafx.fxml, javafx.graphics;
    // Export the package where your FXML controller is located
    exports org.webapp.checkers.controllers to javafx.fxml;

    // Open the package for reflection used by FXML
    opens org.webapp.checkers.controllers to javafx.fxml;

    // Open the package for reflection used by FXML
    exports org.webapp.checkers;
}