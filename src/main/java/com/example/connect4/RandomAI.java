package com.example.connect4;

import java.util.ArrayList;
import java.util.Random;

public class RandomAI extends AI {
    @Override
    public int chooseColumn(ArrayList<ArrayList<Integer>> board) {
        GameData gameData = new GameData(board);
        Random random = new Random();
        int column;
        do {
            column = random.nextInt(GameConfig.COLUMNS);
        } while (gameData.findEmptyRow(column) == GameConfig.INVALID);
        return column;
    }
}
