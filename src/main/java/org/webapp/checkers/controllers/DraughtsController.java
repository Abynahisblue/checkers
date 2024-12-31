package org.webapp.checkers.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.webapp.checkers.model.*;

public class DraughtsController {
    @FXML
    private GridPane boardPane;

    @FXML
    private Label statusLabel;

    private static final int TILE_SIZE = 80;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private final Tile[][] board = new Tile[WIDTH][HEIGHT];

    private Piece selectedPiece;
    private boolean isPlayer1Turn = true;
    private boolean gameOver = false;

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void initialize() {
        if (boardPane == null) {
            throw new IllegalStateException("BoardPane not properly initialized by FXML loader");
        }
        setupBoard();
        boardPane.setOnMouseClicked(this::handleClick);
    }

    private void setupBoard() {
        // Initialize the board only if it's empty
        if (boardPane.getChildren().isEmpty()) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    Tile tile = new Tile((x + y) % 2 == 0, x, y, this);
                    board[x][y] = tile;
                    boardPane.add(tile, x, y);
                }
            }
        }

        resetGameState();
    }

    private void resetGameState() {
        selectedPiece = null;
        isPlayer1Turn = true;
        gameOver = false;
        clearBoardPieces();
        addPieces();
        updateStatusLabel();
    }

    private void clearBoardPieces() {
        for (Tile[] rows : board) {
            for (Tile tile : rows) {
                tile.setPiece(null);
            }
        }
    }

    private void addPieces() {
        int redCount = 0;
        int blackCount = 0;
        final int MAX_PIECES_PER_PLAYER = 20;

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if ((x + y) % 2 != 0) {
                    if (y < 4 && redCount < MAX_PIECES_PER_PLAYER) {
                        addPiece(x, y, PieceType.RED);
                        redCount++;
                    } else if (y >= 6 && blackCount < MAX_PIECES_PER_PLAYER) {
                        addPiece(x, y, PieceType.BLACK);
                        blackCount++;
                    }
                }
            }
        }
    }

    private void addPiece(int x, int y, PieceType type) {
        Piece piece = new Piece(type, board[x][y]);
        board[x][y].setPiece(piece);
    }

    @FXML
    public void handleClick(MouseEvent event) {
        if (gameOver) return;

        int x = (int) (event.getX() / TILE_SIZE);
        int y = (int) (event.getY() / TILE_SIZE);

        if (!isValidTile(x, y)) return;

        Tile clickedTile = board[x][y];
        boolean capturesAvailable = hasAvailableCaptures();

        if (selectedPiece != null) {
            handleSelectedPieceMove(clickedTile, capturesAvailable);
        } else {
            handlePieceSelection(clickedTile, capturesAvailable);
        }

        checkGameOver();
    }

    private void handleSelectedPieceMove(Tile clickedTile, boolean capturesAvailable) {
        MoveResult result = tryMove(selectedPiece, clickedTile);

        if (capturesAvailable && result.getType() != MoveType.FLY_OVER_CAPTURE) {
            selectedPiece = null;
            clearHighlights();
            return;
        }

        if (result.getType() != MoveType.NONE) {
            makeMove(selectedPiece, clickedTile, result);

            if (result.getType() == MoveType.FLY_OVER_CAPTURE && canCaptureAgain(selectedPiece)) {
                clearHighlights();
                highlightCaptureMoves();
            } else {
                clearHighlights();
                selectedPiece = null;
                switchTurns();
            }
        } else {
            selectedPiece = null;
            clearHighlights();
        }
    }

    private void handlePieceSelection(Tile clickedTile, boolean capturesAvailable) {
        if (clickedTile.hasPiece() && isCorrectPlayerTurn(clickedTile.getPiece())) {
            Piece piece = clickedTile.getPiece();

            // Always check for capture moves, but only enforce them after a capture
            boolean hasCaptures = piece.getLastMoveType() == MoveType.FLY_OVER_CAPTURE &&
                    canCaptureAgain(piece);

            selectedPiece = piece;
            clearHighlights();

            if (hasCaptures) {
                highlightCaptureMoves();
            } else {
                highlightPossibleMoves();
                // Also show capture moves if they're available
                if (capturesAvailable) {
                    highlightCaptureMoves();
                }
            }
        }
    }

    public MoveResult tryMove(Piece piece, Tile targetTile) {
        int dx = targetTile.getX() - piece.getTile().getX();
        int dy = targetTile.getY() - piece.getTile().getY();
        int moveDir = piece.getPieceType().getMoveDir();

        // Normal move
        if (Math.abs(dx) == 1 && dy == moveDir && !targetTile.hasPiece()) {
            return new MoveResult(MoveType.NORMAL);
        }

        // Capture move
        if (Math.abs(dx) == 2 && Math.abs(dy) == 2) {
            int midX = (targetTile.getX() + piece.getTile().getX()) / 2;
            int midY = (targetTile.getY() + piece.getTile().getY()) / 2;
            Tile midTile = board[midX][midY];

            if (midTile.hasPiece() &&
                    midTile.getPiece().getPieceType() != piece.getPieceType() &&
                    !targetTile.hasPiece()) {
                return new MoveResult(MoveType.FLY_OVER_CAPTURE, midTile.getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    public void makeMove(Piece piece, Tile targetTile, MoveResult result) {
        if (result.getType() == MoveType.NONE) return;

        piece.getTile().setPiece(null);
        targetTile.setPiece(piece);
        piece.setTile(targetTile);
        piece.setLastMoveType(result.getType());

        if (result.getType() == MoveType.FLY_OVER_CAPTURE) {
            Piece capturedPiece = result.getCapturedPiece();
            if (capturedPiece != null) {
                capturedPiece.getTile().setPiece(null);
            }
        }
    }

    private boolean hasAvailableCaptures() {
        // We want to check for capture moves regardless of last move type
        for (Tile[] rows : board) {
            for (Tile tile : rows) {
                Piece piece = tile.getPiece();
                if (piece != null && isCorrectPlayerTurn(piece)) {
                    int[][] directions = {{2, 2}, {2, -2}, {-2, 2}, {-2, -2}};
                    int currentX = piece.getTile().getX();
                    int currentY = piece.getTile().getY();

                    for (int[] dir : directions) {
                        int targetX = currentX + dir[0];
                        int targetY = currentY + dir[1];
                        int midX = currentX + dir[0] / 2;
                        int midY = currentY + dir[1] / 2;

                        if (isValidCaptureMove(piece, targetX, targetY, midX, midY)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public boolean canCaptureAgain(Piece piece) {
        // This method is specifically for checking additional captures
        // after a capture move
        if (piece.getLastMoveType() != MoveType.FLY_OVER_CAPTURE) {
            return false;
        }

        int[][] directions = {{2, 2}, {2, -2}, {-2, 2}, {-2, -2}};
        int currentX = piece.getTile().getX();
        int currentY = piece.getTile().getY();

        for (int[] dir : directions) {
            int targetX = currentX + dir[0];
            int targetY = currentY + dir[1];
            int midX = currentX + dir[0] / 2;
            int midY = currentY + dir[1] / 2;

            if (isValidCaptureMove(piece, targetX, targetY, midX, midY)) {
                return true;
            }
        }
        return false;
    }


    private boolean isValidCaptureMove(Piece piece, int targetX, int targetY, int midX, int midY) {
        if (!isValidTile(targetX, targetY) || !isValidTile(midX, midY)) {
            return false;
        }

        Tile targetTile = board[targetX][targetY];
        Tile midTile = board[midX][midY];

        return !targetTile.hasPiece() &&
                midTile.hasPiece() &&
                midTile.getPiece().getPieceType() != piece.getPieceType();
    }

    private void highlightPossibleMoves() {
        if (selectedPiece == null) return;

        int x = selectedPiece.getTile().getX();
        int y = selectedPiece.getTile().getY();
        int moveDir = selectedPiece.getPieceType().getMoveDir();

        highlightMove(x + 1, y + moveDir);
        highlightMove(x - 1, y + moveDir);
    }

    private void highlightCaptureMoves() {
        if (selectedPiece == null) return;

        int x = selectedPiece.getTile().getX();
        int y = selectedPiece.getTile().getY();
        int moveDir = selectedPiece.getPieceType().getMoveDir();

        highlightCaptureMove(x + 2, y + 2, x + 1, y + 1);
        highlightCaptureMove(x - 2, y + 2, x - 1, y + 1);
        highlightCaptureMove(x + 2, y - 2, x + 1, y - 1);
        highlightCaptureMove(x - 2, y - 2, x - 1, y - 1);
    }

    private void highlightMove(int targetX, int targetY) {
        if (isValidTile(targetX, targetY) && !board[targetX][targetY].hasPiece()) {
            board[targetX][targetY].setStyle("-fx-background-color: lightgreen;");
        }
    }

    private void highlightCaptureMove(int targetX, int targetY, int midX, int midY) {
        if (!isValidTile(targetX, targetY) || !isValidTile(midX, midY)) return;

        Tile targetTile = board[targetX][targetY];
        Tile midTile = board[midX][midY];

        if (!targetTile.hasPiece() &&
                midTile.hasPiece() &&
                midTile.getPiece().getPieceType() != selectedPiece.getPieceType()) {
            targetTile.setStyle("-fx-background-color: lightblue;");
        }
    }

    private void clearHighlights() {
        for (Tile[] rows : board) {
            for (Tile tile : rows) {
                if (tile.getStyle().contains("lightgreen") || tile.getStyle().contains("lightblue")) {
                    tile.setStyle(tile.isDark() ? "-fx-background-color: #D2B48C;" : "-fx-background-color: beige;");
                }
            }
        }
    }
    private void checkGameOver() {
        boolean redPiecesExist = false;
        boolean blackPiecesExist = false;

        for (Tile[] rows : board) {
            for (Tile tile : rows) {
                if (tile.hasPiece()) {
                    if (tile.getPiece().getPieceType() == PieceType.RED) {
                        redPiecesExist = true;
                    } else {
                        blackPiecesExist = true;
                    }
                }
            }
        }

        if (!redPiecesExist || !blackPiecesExist) {
            gameOver = true;
            String winner = redPiecesExist ? "Player 1 (Red)" : "Player 2 (Black)";
            statusLabel.setText("Game Over! " + winner + " wins!");
        }
    }

    public void switchTurns() {
        if (hasAvailableCaptures()) return;

        isPlayer1Turn = !isPlayer1Turn;
        updateStatusLabel();
        selectedPiece = null;
        clearHighlights();
    }

    private void updateStatusLabel() {
        if (!gameOver) {
            statusLabel.setText(isPlayer1Turn ? "Player 1's Turn" : "Player 2's Turn");
        }
    }

    private boolean isCorrectPlayerTurn(Piece piece) {
        return (isPlayer1Turn && piece.getPieceType() == PieceType.RED) ||
                (!isPlayer1Turn && piece.getPieceType() == PieceType.BLACK);
    }

    @FXML
    public void restartGame() {
        resetGameState();
    }

    private boolean isValidTile(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
}