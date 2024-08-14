package org.webapp.checkers.model;


import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane {
    private Piece piece;

    public Tile(boolean light, int x, int y) {
        Rectangle rectangle = new Rectangle(80, 80);
        rectangle.setFill(light ? Color.BEIGE : Color.BROWN);
        getChildren().add(rectangle);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece != null) {
            getChildren().add(piece);
        } else {
            getChildren().clear();
            Rectangle background = new Rectangle(80, 80);
            background.setFill((getTranslateX() + getTranslateY()) % 2 == 0 ? Color.BEIGE : Color.BROWN);
            getChildren().add(background);
        }
    }
}

