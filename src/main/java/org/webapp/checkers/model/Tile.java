package org.webapp.checkers.model;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.webapp.checkers.controllers.DraughtsController;

public class Tile extends StackPane {
    private final boolean isDark;
    private final int x;
    private final int y;
    private Piece piece;

    public Tile(boolean isDark, int x, int y, DraughtsController controller) {
        this.isDark = isDark;
        this.x = x;
        this.y = y;

        // Set tile size and color
        setPrefSize(80, 80);
        setStyle(isDark ? "-fx-background-color: darkgreen;" : "-fx-background-color: beige;");

        // Debugging logs to verify tile creation
        System.out.println("Tile created at (" + x + ", " + y + "): " + (isDark ? "Dark" : "Light"));

        // Set the click handler
        setOnMouseClicked(event -> {
            if (piece != null) {
                controller.handleClick(event);
            } else if (controller.getSelectedPiece() != null) {
                MoveResult result = controller.tryMove(controller.getSelectedPiece(), this);
                controller.makeMove(controller.getSelectedPiece(), this, result);
            }
        });
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        getChildren().clear();
        if (piece != null) {
            this.piece = piece;
            getChildren().add(piece.getVisual());
        } else {
            this.piece = null;
        }
    }

    public boolean isDark() {
        return isDark;
    }
}
