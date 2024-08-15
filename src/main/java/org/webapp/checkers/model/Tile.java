package org.webapp.checkers.model;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

        // Set tile size and background color
        setPrefSize(80, 80);
        Rectangle background = new Rectangle(80, 80);
        background.setFill(isDark ? Color.DARKGREEN : Color.BEIGE);
        getChildren().add(background); // Add the background rectangle first

        // Debugging logs to verify tile creation
        System.out.println("Tile created at (" + x + ", " + y + "): " + (isDark ? "Dark" : "Light"));

        // Set the click handler
        setOnMouseClicked(event -> {
            if (piece != null) {
                controller.handleClick(event); // Handle click if there's a piece on this tile
            } else if (controller.getSelectedPiece() != null) {
                MoveResult result = controller.tryMove(controller.getSelectedPiece(), this);
                if (result.getType() != MoveType.NONE) { // Ensure a valid move
                    controller.makeMove(controller.getSelectedPiece(), this, result);
                    controller.switchTurns(); // Switch turns after a successful move
                }
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
        // Remove any existing piece (represented by Circle) on this tile
        getChildren().removeIf(node -> node instanceof Circle);

        // If a new piece is provided, add it to the tile
        if (piece != null) {
            this.piece = piece;
            getChildren().add(piece.getVisual()); // Add the visual representation of the piece
        } else {
            this.piece = null; // Clear the reference if no piece is present
        }
    }

    public boolean isDark() {
        return isDark;
    }
}
