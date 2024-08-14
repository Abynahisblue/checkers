package org.webapp.checkers.model;


public class MoveResult {
    private MoveType type;
    private Piece piece;

    public MoveResult(MoveType type) {
        this(type, null);
    }

    public MoveResult(MoveType type, Piece piece) {
        this.type = type;
        this.piece = piece;
    }

    public MoveType getType() {
        return type;
    }

    public Piece getPiece() {
        return piece;
    }
}

