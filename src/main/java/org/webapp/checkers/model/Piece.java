package org.webapp.checkers.model;



import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends StackPane {
    private PieceType type;
    private double mouseX, mouseY;
    private double oldX, oldY;
    private boolean isKing = false;

    public Piece(PieceType type, int x, int y) {
        this.type = type;
        move(x, y);

        Circle circle = new Circle(40);
        circle.setFill(type == PieceType.RED ? Color.RED : Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        getChildren().add(circle);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    public void move(int x, int y) {
        oldX = x * 80;
        oldY = y * 80;
        relocate(oldX, oldY);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public PieceType getType() {
        return type;
    }

    public void promoteToKing() {
        isKing = true;
        Circle kingCircle = new Circle(15);
        kingCircle.setFill(Color.GOLD);
        kingCircle.setTranslateY(-5);
        getChildren().add(kingCircle);
    }

    public boolean isKing() {
        return isKing;
    }
}

