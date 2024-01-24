package com.example.connect4;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameSettingsManager gameSettingsManager = new GameSettingsManager();
        gameSettingsManager.showSettingsWindow(primaryStage);
    }
}