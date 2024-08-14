package org.webapp.checkers.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.webapp.checkers.model.Piece;
import org.webapp.checkers.model.PieceType;
import org.webapp.checkers.model.Tile;

public class DraughtsController {

    @FXML
    private GridPane boardPane;

    @FXML
    private Label statusLabel;

    private static final int TILE_SIZE = 100;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private final Tile[][] board = new Tile[WIDTH][HEIGHT];

    @FXML
    public void initialize() {
        setupBoard();
    }

    private void setupBoard() {
        boardPane.getChildren().clear();  // Clear the current board
        int redCount = 0;
        int whiteCount = 0;

        // Initialize the board and pieces
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                boardPane.add(tile, x, y);

                if ((x + y) % 2 != 0) { // Only place pieces on dark tiles
                    if (y < 4 && redCount < 20) {
                        Piece piece = new Piece(PieceType.RED, x, y);
                        tile.setPiece(piece);
                        redCount++;
                    } else if (y >= 4 && whiteCount < 20) {
                        Piece piece = new Piece(PieceType.WHITE, x, y);
                        tile.setPiece(piece);
                        whiteCount++;
                    }
                }
            }
        }

        statusLabel.setText("Player 1's Turn");
    }

    @FXML
    private void restartGame() {
        setupBoard();
    }
}
