package org.webapp.checkers.model;


public class MoveResult {
    private MoveType type;
    private Piece capturedPiece;

    public MoveResult(MoveType type) {
        this.type = type;
    }

    public MoveResult(MoveType type, Piece capturedPiece) {
        this.type = type;
        this.capturedPiece = capturedPiece;
    }

    public MoveType getType() {
        return type;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}
