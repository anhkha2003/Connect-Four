package com.example.connect4;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class GameData {
    public static class Move {
        public final String player;  // "PLAYER" or "AI"
        public final int column;     // Column index where the chip was dropped
        public final int row;        // Row index where the chip ended up

        public Move(String player, int column, int row) {
            this.player = player;
            this.column = column;
            this.row = row;
        }

        @Override
        public String toString() {
            return player + ": column " + column + ", row " + row;
        }
    }

    private ArrayList<ArrayList<Integer>> board;
    private final List<Move> moveLog = new ArrayList<>();

    public GameData(int rows, int columns) {
        board = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            ArrayList<Integer> row = new ArrayList<>(Collections.nCopies(columns, GameConfig.EMPTY));
            board.add(row);
        }
        restartGame();
    }

    public GameData(ArrayList<ArrayList<Integer>> board) {
        this.board = board;
    }

    public ArrayList<ArrayList<Integer>> getBoard() {
        return board;
    }

    public int findEmptyRow(int column) {
        for (int row = GameConfig.ROWS - 1; row >= 0; row--) {
            if (board.get(row).get(column) == GameConfig.EMPTY) {
                return row;
            }
        }
        return GameConfig.INVALID;
    }

    public int placePiece(int column, int player) {
        int row = findEmptyRow(column);
        if (row != GameConfig.INVALID) {
            board.get(row).set(column, player);
        }
        return row;
    }

    public boolean checkWin(int row, int column) {
        int player = board.get(row).get(column);

        // Check horizontal
        for (int i = 0; i < GameConfig.COLUMNS - 3; i++) {
            if (board.get(row).get(i) == player
                    && board.get(row).get(i + 1) == player
                    && board.get(row).get(i + 2) == player
                    && board.get(row).get(i + 3) == player) {
                return true;
            }
        }

        // Check vertical
        if (row <= GameConfig.ROWS - 4) {
            if (board.get(row).get(column) == player
                    && board.get(row + 1).get(column) == player
                    && board.get(row + 2).get(column) == player
                    && board.get(row + 3).get(column) == player) {
                return true;
            }
        }

        // Check diagonal
        for (int i = 3; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS - 3; j++) {
                if (board.get(i).get(j) == player
                        && board.get(i - 1).get(j + 1) == player
                        && board.get(i - 2).get(j + 2) == player
                        && board.get(i - 3).get(j + 3) == player) {
                    return true;
                }
            }

            for (int j = 3; j < GameConfig.COLUMNS; j++) {
                if (board.get(i).get(j)== player
                        && board.get(i - 1).get(j - 1) == player
                        && board.get(i - 2).get(j - 2) == player
                        && board.get(i - 3).get(j - 3) == player) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkDraw() {
        for (int i = 0; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                if (board.get(i).get(j) == GameConfig.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public void restartGame() {
        for (ArrayList<Integer> row: board) {
            Collections.fill(row, GameConfig.EMPTY);
        }
        moveLog.clear();
    }

    // Record a move
    public void logMove(String player, int column, int row) {
        moveLog.add(new Move(player, column, row));
    }

    // Get the move log
    public List<Move> getMoveLog() {
        return new ArrayList<>(moveLog);  // Return a copy to prevent external modification
    }

    // Apply the move to the game state
    public void applyMove(String player, int column, int row) {
        int playerInteger = 0;
        if (player.equals("PLAYER")) playerInteger = GameConfig.PLAYER;
        else if (player.equals("AI")) playerInteger = GameConfig.AI;
        placePiece(column, playerInteger);
        logMove(player, column, row);
    }
}
