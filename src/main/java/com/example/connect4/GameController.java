package com.example.connect4;

import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.FileChooser;
import java.io.File;

public class GameController {
    private GameData gameData;
    private GameView gameView;
    private AI aiPlayer;
    private String whoPlaysFirst;
    private String difficulty;

    public GameController(GameData gameData) {
        this.gameData = gameData;
        this.gameView = new GameView(this::showRestartConfirmAlert, this::showNewGameConfirmAlert, this::saveGame);
        attachEventListeners();
    }

    private void attachEventListeners() {
        for (int i = 0; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                Circle circle = gameView.getCircle(i, j);

                // Remove previous listeners to avoid duplicates
                circle.setOnMouseClicked(null);
                circle.setOnMouseEntered(null);
                circle.setOnMouseExited(null);

                // Attach new listeners
                circle.setOnMouseClicked(this::handleClick);
                circle.setOnMouseEntered(this::handleHoverEnter);
                circle.setOnMouseExited(this::handleHoverExit);
            }
        }
    }

    public GameView getGameView() {
        return gameView;
    }
    public GameData getGameData() { return gameData; }

    public void setAiPlayer(AI aiPlayer) {
        this.aiPlayer = aiPlayer;
    }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setWhoPlaysFirst(String player) {
        whoPlaysFirst = player;
    }

    public void aiMakeMove() {
        int aiColumn = aiPlayer.chooseColumn(gameData.getBoard());
        int aiRow = gameData.placePiece(aiColumn, GameConfig.AI);
        if (aiRow != GameConfig.INVALID) {
            gameData.logMove("AI", aiColumn, aiRow); // Log the move
            gameView.updateUI(aiRow, aiColumn, GameConfig.AI); // Update UI of table
            if (gameData.checkWin(aiRow, aiColumn)) {
                showWinAlert("Lose"); // AI wins
            }
        }
    }

    private void handleClick(MouseEvent event) {
        Circle clickedCircle = (Circle) event.getSource();

        // Player's turn
        int column = GridPane.getColumnIndex(clickedCircle);
        int row = gameData.placePiece(column, GameConfig.PLAYER);

        if (row != GameConfig.INVALID) {
            gameData.logMove("PLAYER", column, row); // Log the move
            gameView.updateUI(row, column, GameConfig.PLAYER); // Update UI of table

            if (gameData.checkWin(row, column)) {
                showWinAlert("Win"); // Player wins
                return;
            }

            if (gameData.checkDraw()) {
                showWinAlert("Draw"); // Draw
                return;
            }

            // AI's turn
            aiMakeMove();
        }
        else { // Case when column is full
            showAlert("Column Full", "This column is full. Please choose another column.");
        }
    }

    private void handleHoverEnter(MouseEvent event) {
        Circle hoveredCircle = (Circle) event.getSource();
        int column = GridPane.getColumnIndex(hoveredCircle);
        int row = gameData.findEmptyRow(column);
        if (row != -1) {
            gameView.getCircle(row, column).setFill(GameConfig.COLOR_PLAYER);
        }
    }

    private void handleHoverExit(MouseEvent event) {
        Circle exitedCircle = (Circle) event.getSource();
        int column = GridPane.getColumnIndex(exitedCircle);
        int row = gameData.findEmptyRow(column);
        if (row != -1) {
            gameView.getCircle(row, column).setFill(GameConfig.COLOR_CIRCLE);
        }
    }

    private void showWinAlert(String result) {
        disableBoardInteraction();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(result.equals("Draw") ? "It's a draw!" : "You " + result + "!");
        alert.setContentText("Would you like to play again?");
        ButtonType restartButton = new ButtonType("Play Again");
        ButtonType newGameButton = new ButtonType("New Game");
        ButtonType exitButton = new ButtonType("Exit");

        alert.getButtonTypes().setAll(restartButton, newGameButton, exitButton);

        Optional<ButtonType> decision = alert.showAndWait();
        if (decision.isPresent() && decision.get() == restartButton) {
            gameData.restartGame();
            gameView.restartUI();
            enableBoardInteraction();
            if (whoPlaysFirst.equals("AI")) {
                Platform.runLater(() -> { aiMakeMove(); });
            }
        }
        else if (decision.isPresent() && decision.get() == newGameButton) {
            gameData.restartGame();
            gameView.restartUI();
            enableBoardInteraction();
            GameSettingsManager gameSettingsManager = new GameSettingsManager();
            Platform.runLater(() -> gameSettingsManager.showSettingsWindow((Stage) gameView.getRoot().getScene().getWindow()));
        }
        else if (decision.isPresent() && decision.get() == exitButton) {
            Platform.exit();
        }
    }

    private void showRestartConfirmAlert() {
        disableBoardInteraction();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Restart Game");
        alert.setContentText("Are you sure you want to restart?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> decision = alert.showAndWait();
        if (decision.isPresent() && decision.get() == yesButton) {
            gameData.restartGame();
            gameView.restartUI();
            enableBoardInteraction();
            if (whoPlaysFirst.equals("AI")) {
                Platform.runLater(() -> { aiMakeMove(); });
            }
        }
        else {
            enableBoardInteraction();
        }
    }

    private void showNewGameConfirmAlert() {
        disableBoardInteraction();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Start a New Game?");
        alert.setContentText("Are you sure you want to start a new game?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> decision = alert.showAndWait();
        if (decision.isPresent() && decision.get() == yesButton) {
            gameData.restartGame();
            gameView.restartUI();
            enableBoardInteraction();
            GameSettingsManager gameSettingsManager = new GameSettingsManager();
            Platform.runLater(() -> gameSettingsManager.showSettingsWindow((Stage) gameView.getRoot().getScene().getWindow()));
        }
        else {
            enableBoardInteraction();
        }
    }

    public void showExitConfirmation() {
        disableBoardInteraction();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText(null); // No header text
        alert.setContentText("Do you want to save your game before exiting?");

        ButtonType buttonSave = new ButtonType("Save");
        ButtonType buttonDontSave = new ButtonType("Don't Save");
        ButtonType buttonCancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(buttonSave, buttonDontSave, buttonCancel);

        Optional<ButtonType> decision = alert.showAndWait();

        if (decision.isPresent()) {
            if (decision.get() == buttonSave) {
                saveGame();
                Platform.exit();
            }
            else if (decision.get() == buttonDontSave) {
                Platform.exit();
            }
            else enableBoardInteraction();
        }
        else enableBoardInteraction();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void disableBoardInteraction() {
        for (int i = 0; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                gameView.getCircle(i, j).setDisable(true);
            }
        }
    }

    private void enableBoardInteraction() {
        for (int i = 0; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                gameView.getCircle(i, j).setDisable(false);
            }
        }
    }

    // Save the game state to a file
    public void saveGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showSaveDialog(gameView.getRoot().getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(whoPlaysFirst + " plays first");
                writer.newLine();
                writer.write("Difficulty: " + difficulty);
                writer.newLine();
                writer.write("Log of moves:");
                writer.newLine();
                List<GameData.Move> moves = gameData.getMoveLog();
                for (GameData.Move move : moves) {
                    writer.write(move.player + ": column " + move.column + ", row " + move.row);
                    writer.newLine();
                }
            } catch (IOException e) {
                // Handle exception (show an alert to the user or log it)
                e.printStackTrace();
                showAlert("Save Error", "Failed to save the game: " + e.getMessage());
            }
        }
    }
}
