package org.webapp.checkers.model;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends Region {
    private PieceType pieceType;
    private int x, y;
    private Tile tile;
    private Circle visual;

    public Piece(PieceType pieceType, Tile tile) {
        this.pieceType = pieceType;
        this.tile = tile;

        visual = new Circle(30); // Example size
        visual.setFill(pieceType == PieceType.RED ? Color.RED : Color.BLACK);
        this.getChildren().add(visual); // Add the circle to the piece
    }

    public Piece(PieceType pieceType, int x, int y) {
        this.pieceType = pieceType;
        this.x = x;
        this.y = y;
    }

    public Circle getVisual() {
        return visual;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
