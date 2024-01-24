### Connect Four Game ###

### Description ###

This project is a Java and JavaFX implementation of the Connect Four Game where a player competes
against an artificial intelligence (AI) opponent. The game allows for choosing who starts first,
selecting the difficulty level of the AI, and saving and loading the game state and log of moves.

### Features ###

- Single-player gameplay against an AI opponent.
- Two AI modes: Random and Thoughtful, corresponding to Easy and Hard difficulty levels.
- Option to choose who starts first: Player or AI.
- Save and load game functionality, with a log of moves
- Responsive UI with a background image and style components.
- Interactive grid that allows for dropping chips into columns, with warnings for 
invalid positions.
- Ability to restart the current game or start a new game at any time.

### Requirements ###

- Java 11 or higher
- JavaFX SDK (compatible with the project's Java version)

### Setup and Running ###

1. Ensure you have Java and JavaFX installed on your system.
2. Download the ZIP file and extract it to your desired location.
3. Open the project in your preferred IDE.
4. Ensure that the IDE recognizes the source files within the correct package structure. The main
source files are located under "com.example.connect4". If you encounter any "package does not
exist" errors during compilation, it may be due to incorrect package structure setup in your 
IDE or build path.
5. Depending on your IDE, you might need to mark the "src" directory as your source root. 
6. The files besides source codes (image, css, txt) are kept in "resources".
7. Build the project using your IDE's built-in build feature.
8. Locate the "Main.java" file within the "com.example.connect4" and run it to start the game.

### How to play ###

- Upon starting the game, select who should play first and the difficulty level.
- Click "Start New Game" to start the game
- Click on the column where you want to drop your chip.
- Try to connect four chips in a row either horizontally, vertically, or diagonally before the
AI does.
- There will be a warning if you choose an invalid position.

### Save and Load ###

- You can save the game at any point by clicking the "Save Game" button.
- To load a previously saved game, click the "Load Game" button and select the save file.

### Video Demonstration ###

- For a video demonstration of the game and its feature, please visit the following link: 
https://youtu.be/r7mqjTk_V5Q