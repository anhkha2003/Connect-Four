package com.example.connect4;

import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;

import java.util.ArrayList;

public class GameView {
    private ArrayList<ArrayList<Circle>> circles;
    private GridPane gridPane;
    private StackPane root;
    private Button restartButton;
    private Button newGameButton;
    private Button saveButton;

    public GameView(Runnable onRestart, Runnable onNewGame, Runnable onSave) {
        gridPane = new GridPane();
        root = new StackPane();
        initializeBackground();
        initializeGridPane();
        createRestartButton(onRestart);
        createNewGameButton(onNewGame);
        createSaveButton(onSave);

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(25);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(restartButton, newGameButton, saveButton);

        vbox.getChildren().addAll(gridPane, buttonContainer);
        VBox.setMargin(buttonContainer, new Insets(20, 0, 0, 0)); // Top, Right, Bottom, Left
        root.getChildren().add(vbox);
    }

    private void initializeBackground() {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/background.jpg"));
        ImageView backgroundView = new ImageView(backgroundImage);

        backgroundView.fitWidthProperty().bind(root.widthProperty());
        backgroundView.fitHeightProperty().bind(root.heightProperty());
        backgroundView.setPreserveRatio(false);
        root.getChildren().add(backgroundView);
    }

    private void initializeGridPane() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(GameConfig.CELL_SPACING);
        gridPane.setVgap(GameConfig.CELL_SPACING);

        circles = new ArrayList<>();

        for (int i = 0; i < GameConfig.ROWS; i++) {
            ArrayList<Circle> circleRow = new ArrayList<>();
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                Circle circle = new Circle(GameConfig.CELL_SIZE, GameConfig.COLOR_CIRCLE);
                circle.setStroke(GameConfig.COLOR_STROKE);
                GridPane.setHalignment(circle, HPos.CENTER);
                gridPane.add(circle, j, i);
                circleRow.add(circle);
            }
            circles.add(circleRow);
        }
        root.getChildren().add(gridPane);
    }

    private void createRestartButton(Runnable onRestart) {
        restartButton = new Button("Restart Game");
        restartButton.getStyleClass().add("button");
        restartButton.setOnAction(e -> onRestart.run());
        restartButton.setPrefWidth(150);
        restartButton.setPrefHeight(30);
    }

    private void createNewGameButton(Runnable onNewGame) {
        newGameButton = new Button("New Game");
        newGameButton.getStyleClass().add("button");
        newGameButton.setOnAction(e -> onNewGame.run());
        newGameButton.setPrefWidth(150);
        newGameButton.setPrefHeight(30);
    }

    private void createSaveButton(Runnable onSave) {
        saveButton = new Button("Save Game");
        saveButton.getStyleClass().add("button");
        saveButton.setOnAction(e -> onSave.run());
        saveButton.setPrefWidth(150);
        saveButton.setPrefHeight(30);
    }

    public StackPane getRoot() {
        return root;
    }

    public Circle getCircle(int row, int col) {
        return circles.get(row).get(col);
    }

    public void updateUI(int row, int column, int player) {
        if (row != GameConfig.INVALID) {
            Color color = (player == GameConfig.PLAYER) ? GameConfig.COLOR_PLAYER : GameConfig.COLOR_AI;
            getCircle(row, column).setFill(color);
        }
    }

    public void restartUI() {
        for (int i = 0; i < GameConfig.ROWS; i++) {
            for (int j = 0; j < GameConfig.COLUMNS; j++) {
                getCircle(i, j).setFill(GameConfig.COLOR_CIRCLE);
            }
        }
    }
}
