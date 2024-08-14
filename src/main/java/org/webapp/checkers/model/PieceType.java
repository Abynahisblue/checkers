package org.webapp.checkers.model;


public enum PieceType {
    RED(1),
    BLACK(-1);

    private final int moveDir;

    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }

    public int getMoveDir() {
        return moveDir;
    }
}

