package com.example.connect4;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class ThoughtfulAI extends AI {
    private class Result {
        public int score;
        public int nextMove;

        public Result(int score, int nextMove) {
            this.score = score;
            this.nextMove = nextMove;
        }
    }

    private static final int WINNING_SCORE = 1000; // Winning state score
    private static final int THREE_IN_A_ROW_SCORE = 7; // Score for three in a row
    private static final int TWO_IN_A_ROW_SCORE = 4; // Score for two in a row

    private Map<ArrayList<ArrayList<Integer>>, Result> memoizeTable = new HashMap<>();

    private static int evaluatePotentialScores(ArrayList<ArrayList<Integer>> board, int player) {
        int score = 0;

        // Count horizontal
        for (int row = 0; row < GameConfig.ROWS; row++) {
            ArrayList<Integer> line = board.get(row);
            score += evaluateLineForPotential(line, player);
        }

        // Count vertical
        for (int col = 0; col < GameConfig.COLUMNS; col++) {
            ArrayList<Integer> line = new ArrayList<>();
            for (int row = 0; row < GameConfig.ROWS; row++) {
                line.add(board.get(row).get(col));
            }
            score += evaluateLineForPotential(line, player);
        }

        // Count diagonal (/)
        for (int col = -GameConfig.ROWS; col < GameConfig.COLUMNS; col++) {
            ArrayList<Integer> line = new ArrayList<>();
            for (int i = 0; i < GameConfig.COLUMNS - col; i++) {
                if (i < GameConfig.ROWS && col + i >= 0) {
                    line.add(board.get(i).get(col + i));
                }
            }
            score += evaluateLineForPotential(line, player);
        }

        // Count diagonal (\)
        for (int col = 0; col < GameConfig.COLUMNS + GameConfig.ROWS - 1; col++) {
            ArrayList<Integer> line = new ArrayList<>();
            for (int i = 0; i <= col; i++) {
                if (i < GameConfig.ROWS && col - i < GameConfig.COLUMNS) {
                    line.add(board.get(i).get(col - i));
                }
            }
            score += evaluateLineForPotential(line, player);
        }

        return score;
    }

    private static int evaluateLineForPotential(ArrayList<Integer> line, int player) {
        int score = 0;
        int consecutive = 0;
        int openEnds = 0;

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i) == player) {
                consecutive++;
            }
            else if (line.get(i) == GameConfig.EMPTY) {
                if (consecutive > 0) {
                    openEnds++;
                    score += getPotentialScore(consecutive, openEnds);
                    consecutive = 0;
                    openEnds = 1; // Current empty cell could be the start of a new sequence
                } else {
                    openEnds = 1; // An empty cell with no current consecutive player pieces
                }
            }
            else if (consecutive > 0) {
                openEnds++;
                score += getPotentialScore(consecutive, openEnds);
                consecutive = 0;
                openEnds = 0;
            }
            else {
                openEnds = 0; // Reset if consecutive run is blocked by opponent's piece
            }
        }

        // Check at the end of the line
        if (consecutive > 0) {
            openEnds++;
            score += getPotentialScore(consecutive, openEnds);
        }

        return score;
    }

    private static int getPotentialScore(int consecutive, int openEnds) {
        if (consecutive == 3) {
            if (openEnds == 2) {
                return THREE_IN_A_ROW_SCORE; // More valuable three since it has two open ends
            }
            else if (openEnds == 1) {
                return THREE_IN_A_ROW_SCORE / 2; // Less valuable since it can only be completed one way
            }
        }
        else if (consecutive == 2) {
            if (openEnds == 2) {
                return TWO_IN_A_ROW_SCORE; // More valuable two since it has two open ends
            }
            else if (openEnds == 1) {
                return TWO_IN_A_ROW_SCORE / 2; // Less valuable since it can only be completed one way
            }
        }
        return 0;
    }

    private int evaluate(ArrayList<ArrayList<Integer>> board) {
        GameData gameData = new GameData(board);

        // Losing state: -100
        for (int i = 0; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                if (board.get(i).get(j) == GameConfig.EMPTY) continue;
                if (gameData.checkWin(i, j)) return -WINNING_SCORE;
            }
        }

        // Draw state: 0
        if (gameData.checkDraw()) return 0;

        return evaluatePotentialScores(board, GameConfig.AI) - evaluatePotentialScores(board, GameConfig.PLAYER);
    }

    // flip table from AI -> PLAYER, PLAYER -> AI to evaluate score
    private ArrayList<ArrayList<Integer>> flip(ArrayList<ArrayList<Integer>> board) {
        ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>(GameConfig.ROWS);
        for (int i = 0; i < GameConfig.ROWS; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                row.add(board.get(i).get(j));
            }
            newBoard.add(row);
        }

        for (int i = 0; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                int value = newBoard.get(i).get(j);
                if (value != GameConfig.EMPTY) {
                    newBoard.get(i).set(j, GameConfig.PLAYER + GameConfig.AI - value);
                }
            }
        }
        return newBoard;
    }

    // Minimax Algorithm to choose the column with best moves in next 7 moves
    private Result dfs(int depth, ArrayList<ArrayList<Integer>> board) {
        int value = evaluate(board);
        if (value == -WINNING_SCORE) return new Result(value + depth, 0);
        if (depth == 7) {
            return new Result(value, 0);
        }
        if (memoizeTable.get(board) != null) return memoizeTable.get(board);

        GameData gameData = new GameData(board);
        Result bestResult = new Result(-WINNING_SCORE * 2, 0);

        ArrayList<Integer> columnResult = new ArrayList<>();
        for (int j = 0; j < GameConfig.COLUMNS; j++) {
            int i = gameData.findEmptyRow(j);
            if (i == GameConfig.INVALID) {
                columnResult.add(GameConfig.INVALID);
                continue;
            }

            ArrayList<ArrayList<Integer>> newBoard = flip(board);
            newBoard.get(i).set(j, GameConfig.PLAYER);
            Result currentResult = dfs(depth + 1, newBoard);

            if (-currentResult.score > bestResult.score) {
                bestResult.score = -currentResult.score;
            }
            columnResult.add(-currentResult.score);
        }

        // Create an ArrayList store all columns that have same best Result
        ArrayList<Integer> bestColumns = new ArrayList<>();
        for (int j = 0; j < GameConfig.COLUMNS; j++) {
            if (columnResult.get(j) == bestResult.score) {
                bestColumns.add(j);
            }
        }

        // Randomly choose the column with best Result
        if (!bestColumns.isEmpty()) {
            Random random = new Random();
            int column = random.nextInt(bestColumns.size());
            bestResult.nextMove = bestColumns.get(column);
        }

        memoizeTable.put(board, bestResult);
        return bestResult;
    }

    @Override
    public int chooseColumn(ArrayList<ArrayList<Integer>> board) {
        memoizeTable.clear();
        Result bestResult = dfs(0, board);
        return bestResult.nextMove;
    }
}
