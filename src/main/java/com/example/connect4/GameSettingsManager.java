package com.example.connect4;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class GameSettingsManager {
    private GameController gameController;
    private GameData gameData;

    public GameSettingsManager() {
        gameData = new GameData(GameConfig.ROWS, GameConfig.COLUMNS);
        gameController = new GameController(gameData);
    }

    public void showSettingsWindow(Stage primaryStage) {
        // Create a pane for the background image
        StackPane root = new StackPane();
        root.getStyleClass().add("root");

        // Settings VBox
        VBox settingsLayout = new VBox(15);
        settingsLayout.setAlignment(Pos.CENTER);
        settingsLayout.getStyleClass().add("menu-box");

        // Game Title
        Label gameTitle = new Label("Connect 4");
        gameTitle.setFont(new Font("Arial", 24));
        gameTitle.getStyleClass().add("menu-title");

        // Who Plays First
        Label firstPlayerLabel = new Label("Who plays first?");
        firstPlayerLabel.getStyleClass().add("menu-label");
        ComboBox<String> firstPlayerChoice = new ComboBox<>();
        firstPlayerChoice.getItems().addAll("Player", "AI");
        firstPlayerChoice.setValue("Player"); // Set the default value to "Player"
        firstPlayerChoice.getStyleClass().add("combo-box");

        // Difficulty Level
        Label difficultyLabel = new Label("Select difficulty:");
        difficultyLabel.getStyleClass().add("menu-label");
        ComboBox<String> difficultyChoice = new ComboBox<>();
        difficultyChoice.getItems().addAll("Easy", "Hard");
        difficultyChoice.setValue("Easy"); // Set the default value to "Easy"
        difficultyChoice.getStyleClass().add("combo-box");

        // Start Button
        Button startButton = new Button("Start New Game");
        startButton.getStyleClass().add("menu-button");
        startButton.setOnAction(e -> {
            String firstPlayer = firstPlayerChoice.getValue();
            String difficulty = difficultyChoice.getValue();
            startGame(primaryStage, firstPlayer, difficulty);
        });
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(30);

        // Load button
        Button loadButton = new Button("Load Game");
        loadButton.getStyleClass().add("menu-button");
        loadButton.setOnAction(e -> { loadGame(primaryStage); });
        loadButton.setPrefWidth(200);
        loadButton.setPrefHeight(30);

        // Add all to VBox
        settingsLayout.getChildren().addAll(gameTitle, firstPlayerLabel, firstPlayerChoice, difficultyLabel, difficultyChoice, startButton, loadButton);

        // Add settingsLayout to the root StackPane on top of the background
        root.getChildren().add(settingsLayout);

        // Set up Scene and Window Screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4");
        primaryStage.show();
    }

    private void startGame(Stage primaryStage, String firstPlayer, String difficulty) {
        // Set AI difficulty based on choice
        gameController.setDifficulty(difficulty);
        if (difficulty.equals("Easy")) {
            gameController.setAiPlayer(new RandomAI());
        } else {
            gameController.setAiPlayer(new ThoughtfulAI());
        }

        // Decide who makes the first move
        gameController.setWhoPlaysFirst(firstPlayer);
        if (firstPlayer.equals("AI")) {
            Platform.runLater(() -> { gameController.aiMakeMove(); });
        }

        // Set up Scene and Window Screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(gameController.getGameView().getRoot(), screenBounds.getWidth(), screenBounds.getHeight());
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            gameController.showExitConfirmation();
        });
    }

    // Load the game state from a file
    public void loadGame(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                gameController.getGameData().restartGame(); // Reset the game state
                String line;
                boolean logOfMovesStarted = false;
                String lastPlayer = null;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("AI plays first")) {
                        gameController.setWhoPlaysFirst("AI");
                    }
                    else if (line.startsWith("Player plays first")) {
                        gameController.setWhoPlaysFirst("Player");
                    }
                    else if (line.startsWith("Difficulty:")) {
                        String difficulty = line.split(":")[1].trim();
                        gameController.setDifficulty(difficulty);
                        if (difficulty.equals("Easy")) {
                            gameController.setAiPlayer(new RandomAI());
                        } else {
                            gameController.setAiPlayer(new ThoughtfulAI());
                        }
                    }
                    else if (line.equals("Log of moves:")) {
                        logOfMovesStarted = true;
                    }
                    else if (logOfMovesStarted) {
                        String[] parts = line.split(":")[1].split(",");
                        String player = line.split(":")[0].trim();
                        int column = Integer.parseInt(parts[0].trim().split(" ")[1].trim());
                        int row = Integer.parseInt(parts[1].trim().split(" ")[1].trim());

                        // Apply the move in file
                        gameController.getGameData().applyMove(player, column, row);

                        // Update the UI
                        if (player.equals("PLAYER")) {
                            gameController.getGameView().updateUI(row, column, GameConfig.PLAYER);
                        } else {
                            gameController.getGameView().updateUI(row, column, GameConfig.AI);
                        }

                        lastPlayer = player;
                    }
                }

                // Start the game after loading
                if (lastPlayer != null) {
                    if (lastPlayer.equals("PLAYER")) {
                        // AI plays next move
                        Platform.runLater(() -> { gameController.aiMakeMove(); });
                    }

                    // Set up Scene and Window Screen
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    Scene scene = new Scene(gameController.getGameView().getRoot(), screenBounds.getWidth(), screenBounds.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
                    primaryStage.setMaximized(true);
                    primaryStage.setScene(scene);
                    primaryStage.show();
                    primaryStage.setOnCloseRequest(event -> {
                        event.consume(); // Prevent the window from closing immediately
                        gameController.showExitConfirmation();
                    });
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                // Handle exception (show an alert to the user or log it)
                gameController.showAlert("Load Error", "Failed to load the game: " + e.getMessage());
            }
        }
    }
}
