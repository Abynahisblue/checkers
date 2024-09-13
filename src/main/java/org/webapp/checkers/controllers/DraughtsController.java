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

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void initialize() {
        setupBoard();
        boardPane.setOnMouseClicked(this::handleClick);
    }


    private void setupBoard() {
        if (boardPane.getChildren().isEmpty()) {
            // First-time setup (board creation)
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    Tile tile = new Tile((x + y) % 2 == 0, x, y, this);
                    board[x][y] = tile;
                    boardPane.add(tile, x, y);
                }
            }
        }

        // Reset game state, reusing tiles
        selectedPiece = null;
        isPlayer1Turn = true;
        clearBoardPieces();
        addPieces();
        statusLabel.setText("Player 1's Turn");
    }

    private void clearBoardPieces() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                board[x][y].setPiece(null);
            }
        }
    }

    private void addPieces() {
        int redCount = 0;
        int whiteCount = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if ((x + y) % 2 != 0) {
                    if (y < 4 && redCount < 20) {
                        Piece piece = new Piece(PieceType.RED, board[x][y]);
                        board[x][y].setPiece(piece);
                        redCount++;
                    } else if (y >= 6 && whiteCount < 20) {
                        Piece piece = new Piece(PieceType.BLACK, board[x][y]);
                        board[x][y].setPiece(piece);
                        whiteCount++;
                    }
                }
            }
        }
    }

    @FXML

    public void handleClick(MouseEvent event) {
        int x = (int) (event.getX() / TILE_SIZE);
        int y = (int) (event.getY() / TILE_SIZE);
        if (!isValidTile(x, y)) return;

        Tile clickedTile = board[x][y];

        if (selectedPiece != null) {
            MoveResult result = tryMove(selectedPiece, clickedTile);
            if (result.getType() != MoveType.NONE) {
                makeMove(selectedPiece, clickedTile, result);

                if (result.getType() == MoveType.FLY_OVER_CAPTURE && canCaptureAgain(selectedPiece)) {
                    clearHighlights();
                    highlightPossibleMoves();
                    return;
                } else {
                    clearHighlights();
                    selectedPiece = null;
                    switchTurns();
                }
            } else {
                selectedPiece = null;
                clearHighlights();
            }
        } else {
            if (clickedTile.hasPiece() && isCorrectPlayerTurn(clickedTile.getPiece())) {
                selectedPiece = clickedTile.getPiece();
                clearHighlights();
                highlightPossibleMoves();
            } else {
                // Clear any erroneous selection if the clicked tile is not a valid piece
                clearHighlights();
                selectedPiece = null;
            }
        }
    }


    public MoveResult tryMove(Piece piece, Tile targetTile) {
        int dx = targetTile.getX() - piece.getTile().getX();
        int dy = targetTile.getY() - piece.getTile().getY();

        if (Math.abs(dx) == 1 && dy == piece.getPieceType().getMoveDir() && !targetTile.hasPiece()) {
            return new MoveResult(MoveType.NORMAL);
        }

        if (Math.abs(dx) == 2 && Math.abs(dy) == 2) {
            int midX = (targetTile.getX() + piece.getTile().getX()) / 2;
            int midY = (targetTile.getY() + piece.getTile().getY()) / 2;
            Tile midTile = board[midX][midY];

            if (midTile.hasPiece() && midTile.getPiece().getPieceType() != piece.getPieceType() && !targetTile.hasPiece()) {
                return new MoveResult(MoveType.FLY_OVER_CAPTURE, midTile.getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    public void makeMove(Piece piece, Tile targetTile, MoveResult result) {
        piece.getTile().setPiece(null);
        targetTile.setPiece(piece);
        piece.setTile(targetTile);

        if (result.getType() == MoveType.FLY_OVER_CAPTURE) {
            Tile capturedTile = result.getCapturedPiece().getTile();
            capturedTile.setPiece(null);

            if (canCaptureAgain(piece)) {
                highlightPossibleMoves();
                selectedPiece = piece;
                clearHighlights();
                return;
            } else {
                selectedPiece = null;
            }
        }
    }

    private boolean canCaptureAgain(Piece piece) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int forwardMoveDir = piece.getPieceType().getMoveDir();

        // Combine checks for forward and backward moves in one loop
        int[][] directions = {
                {2, 2 * forwardMoveDir}, {-2, 2 * forwardMoveDir},
                {2, -2 * forwardMoveDir}, {-2, -2 * forwardMoveDir}
        };

        for (int[] dir : directions) {
            if (isValidTile(x + dir[0], y + dir[1]) &&
                    isValidCaptureMove(x + dir[0], y + dir[1], x + dir[0] / 2, y + dir[1] / 2)) {
                return true;
            }
        }

        return false;
    }


    private boolean isValidCaptureMove(int targetX, int targetY, int midX, int midY) {
        if (isValidTile(targetX, targetY) && isValidTile(midX, midY)) {
            Tile targetTile = board[targetX][targetY];
            Tile midTile = board[midX][midY];

            return !targetTile.hasPiece() &&
                    midTile.hasPiece() &&
                    midTile.getPiece().getPieceType() != selectedPiece.getPieceType();
        }
        return false;
    }

    private void highlightPossibleMoves() {
        if (selectedPiece == null) return;

        int x = selectedPiece.getTile().getX();
        int y = selectedPiece.getTile().getY();
        int moveDir = selectedPiece.getPieceType().getMoveDir();

        highlightMove(x + 1, y + moveDir);
        highlightMove(x - 1, y + moveDir);
        highlightCaptureMove(x + 2, y + 2 * moveDir, x + 1, y + moveDir);
        highlightCaptureMove(x - 2, y + 2 * moveDir, x - 1, y + moveDir);
    }

    private void highlightMove(int targetX, int targetY) {
        if (isValidTile(targetX, targetY) && !board[targetX][targetY].hasPiece()) {
            board[targetX][targetY].setStyle("-fx-background-color: lightgreen;");
        }
    }


    private void highlightTileIfValid(int x, int y) {
        if (isValidTile(x, y)) {
            Tile tile = board[x][y];
            if (!tile.hasPiece()) {
                tile.setStyle("-fx-background-color: lightgreen;");
            }
        }
    }

    private void highlightCaptureMove(int targetX, int targetY, int midX, int midY) {
        if (isValidTile(targetX, targetY)) {
            Tile targetTile = board[targetX][targetY];
            Tile midTile = board[midX][midY];

            if (!targetTile.hasPiece() && midTile.hasPiece() && midTile.getPiece().getPieceType() != selectedPiece.getPieceType()) {
                targetTile.setStyle("-fx-background-color: lightblue;");
            }
        }
    }

    private void clearHighlights() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = board[x][y];
                if (tile.getStyle().contains("lightgreen") || tile.getStyle().contains("lightblue")) {
                    tile.setStyle(tile.isDark() ? "-fx-background-color: #D2B48C;" : "-fx-background-color: beige;");
                }
            }
        }
    }


    public void switchTurns() {
        isPlayer1Turn = !isPlayer1Turn;
        statusLabel.setText(isPlayer1Turn ? "Player 1's Turn" : "Player 2's Turn");

        selectedPiece = null;
        clearHighlights();
    }

    private boolean isCorrectPlayerTurn(Piece piece) {
        boolean isRed = piece.getPieceType() == PieceType.RED;
        boolean isBlack = piece.getPieceType() == PieceType.BLACK;

        return (isPlayer1Turn && isRed) || (!isPlayer1Turn && isBlack);
    }

    @FXML
    public void restartGame() {
        clearBoardPieces();

        selectedPiece = null;

        setupBoard();
    }

    private boolean isValidTile(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }


}
