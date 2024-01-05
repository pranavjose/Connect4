import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.StringTokenizer;


public class MainSceneController {

     @FXML
    private Button loadGame;

    @FXML
    private Button saveGame;


    private List<String> moveLog = new ArrayList<>(); // Move log to record player moves

    @FXML
    private void loadGameClick(MouseEvent event) {  //in FXML use onMouseClicked not onAction
        FileChooser fileChooser = new FileChooser(); //sets up file chooser so user can load in their saved game
        fileChooser.setTitle("Load Game"); //set title of file explorer to Load Game
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt")); //set to .txt format
        File selectedFile = fileChooser.showOpenDialog(new Stage());
    
        if (selectedFile != null) { //if file isnt empty
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) { //use try catch in case smth goes wrong
                // Load the game state from the file
                for (int row = 0; row < 6; row++) { //go row by row
                    String line = reader.readLine();
                    for (int col = 0; col < 7; col++) {
                        gameBoard[row][col] = line.charAt(col);
                        updateCircleColor(row, col);
                    }
                }
    
                // Load the current player
                currentPlayer = reader.readLine().charAt(0);
                updatePlayerTurnText();
    
                // Load the move log
                moveLog.clear(); //clear existing move log
                String move;
                while ((move = reader.readLine()) != null) { //replace with loaded move log
                    moveLog.add(move);
                }
    
                // tests
                
                // printGameBoard();
                // System.out.println("Current player: " + currentPlayer);
                // System.out.println("Move log: " + moveLog);
    
                // Update the circles based on the move log
                updateCirclesFromMoveLog();
    
                // Update the shadow board based on the loaded game state
                updateShadowBoard();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateShadowBoard() {
        // Clear the shadow board and update it based on the game board
        shadowBoard.clear();
        for (int row = 0; row < 6; row++) {
            List<Optional<Character>> rowList = new ArrayList<>();
            for (int col = 0; col < 7; col++) {
                rowList.add(Optional.of(gameBoard[row][col]));
            }
            shadowBoard.add(rowList);
        }
    }

    // private void printGameBoard() { //prints loaded game file for testing
    //     for (int row = 0; row < 6; row++) {
    //         for (int col = 0; col < 7; col++) {
    //             System.out.print(gameBoard[row][col] + " ");
    //         }
    //         System.out.println();
    //     }
    // }


    @FXML
    private void saveGameClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser(); //same initial setup as loadGameClick
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                // write game state to the file
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 7; col++) {
                        writer.write(gameBoard[row][col]);
                    }
                    writer.newLine();
                }

                // save current player
                writer.write(currentPlayer);
                writer.newLine();

                // save move log
                for (String move : moveLog) {
                    writer.write(move);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCirclesFromMoveLog() {
        // Update the game board based on the move log
        for (String move : moveLog) {
            // Extract row, col, and player information from the move
            int row = Integer.parseInt(move.substring(0, 1));
            int col = Integer.parseInt(move.substring(2, 3));
            char player = move.charAt(4);
    
            // Update the game board
            gameBoard[row][col] = player;
        }
    
        // Update UI to reflect the changes in the game board
        updateUIFromGameBoard();
    }
    
    private void updateUIFromGameBoard() {
        // Iterate through the game board and update the circles
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                updateCircleColor(row, col);
            }
        }
    }
    



    
    private void updateCircleColor(int row, int col) {
        Circle circle = (Circle) c4grid.getChildren().get(row * 7 + col);
        char currentPlayerSymbol = gameBoard[row][col];
    
        // Check if the circle should be filled with Blue or Red
        if (currentPlayerSymbol == 'B') {
            circle.setFill(Color.BLUE);
        } else if (currentPlayerSymbol == 'R') {
            circle.setFill(Color.RED);
        } else {
            // Empty circle
            circle.setFill(Color.web("#002A22"));
        }
    }
    

    @FXML
    private GridPane c4grid; //7*6 grid that holds all circles 

    @FXML
    private ToggleButton aiToggle; //Button to switch between Thoughtful AI and Random AI

    @FXML
    private TextField playerTurnText; //Shows whose turn it is

    private char[][] gameBoard = new char[6][7]; // Represent the game state
    private List<List<Optional<Character>>> shadowBoard = new ArrayList<>(); // Shadow board for checking winning conditions

    private char currentPlayer = 'B'; // 'B' for Blue, 'R' for Red
    private boolean gameOver = false; //If true, disa

    public void initialize() {
        // Initialize the game board and set initial player turn text
        resetGame();
        updatePlayerTurnText();

        // Add drop shadow to the game board
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.TEAL);
        dropShadow.setRadius(50.0); //cant make 
        dropShadow.setOffsetX(5.0);
        dropShadow.setOffsetY(5.0); 

        c4grid.setEffect(dropShadow);

         // Add background color to the game board for depth
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.DARKGRAY),
        new Stop(1, Color.BLACK));

        BackgroundFill backgroundFill = new BackgroundFill(gradient, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY);
        Background background = new Background(backgroundFill);

        c4grid.setBackground(background);
    }

    private void resetGame() {
        // Clear the grid and reset the game state
        moveLog.clear(); //clear existing move log
        c4grid.getChildren().clear(); //clears all children of grid
        shadowBoard.clear(); //clears shadow
        for (int row = 0; row < 6; row++) {
            List<Optional<Character>> rowList = new ArrayList<>();
            for (int col = 0; col < 7; col++) {
                gameBoard[row][col] = ' '; // Empty space
                rowList.add(Optional.empty());
                Circle circle = createCircle(row, col);
                c4grid.add(circle, col, row);
            }
            shadowBoard.add(rowList);
        }
        currentPlayer = 'B';
        //updatePlayerTurnText();
        gameOver = false;
    }

    private Circle createCircle(int row, int col) {
        // Create a Circle for a specific grid position given row and column
        Circle circle = new Circle(45.0, Color.web("#002A22"));
        circle.setStroke(Color.BLACK);

        // Center the circle within the grid
        GridPane.setHalignment(circle, HPos.CENTER);
        GridPane.setValignment(circle, VPos.CENTER);

        circle.setOnMouseClicked(event -> handleGridClick(col));
        return circle;
    }


    private void handleGridClick(int col) {
        //check if game over
        if(gameOver) {
            return;
        }

        // Check if the column is full
        if (isColumnFull(col)) {
            playerTurnText.setText("Not a valid spot.");
            return; // Do nothing if the column is full
        }

        // User's turn (Blue)
        int userRow = findLowestEmptyRow(col);
        if (userRow != -1) {
            makeMove(userRow, col);
            checkGameStatus(userRow, col);
        }

        // AI's turn (Red) with a delay
        if(!gameOver){PauseTransition pause = new PauseTransition(Duration.seconds(0.5)); // 0.5 sec delay, 1 sec was too high
        pause.setOnFinished(event -> {
            int aiCol = calculateAIMove();
            int aiRow = findLowestEmptyRow(aiCol);
            if (aiRow != -1) {
                makeMove(aiRow, aiCol);
                checkGameStatus(aiRow, aiCol);
            }
        });

        pause.play();}
    }

    private boolean isColumnFull(int col) {
        // Check if the specified column is full
        return findLowestEmptyRow(col) == -1;
    }


    private void makeMove(int row, int col) {
        // Update the game board and shadow board
        gameBoard[row][col] = currentPlayer;
        shadowBoard.get(row).set(col, Optional.of(currentPlayer));

        Circle circle = (Circle) c4grid.getChildren().get(row * 7 + col);
        circle.setFill(currentPlayer == 'B' ? Color.BLUE : Color.RED);

        moveLog.add(row + "," + col + "," + currentPlayer);

        // Switch player turn
        currentPlayer = (currentPlayer == 'B') ? 'R' : 'B';
        updatePlayerTurnText();
        
        // Check for wins using the updated shadow board
        checkGameStatus(row, col);
    }

    private int calculateAIMove() {
        // Check for a winning move
        if(!(aiToggle.isSelected())){ //if on Thoughtful AI
            for (int col = 0; col < 7; col++) {
                int row = findLowestEmptyRow(col);
                if (row != -1) {
                    // Simulate placing a disc and check for a win
                    gameBoard[row][col] = currentPlayer;
                    shadowBoard.get(row).set(col, Optional.of(currentPlayer));
                    if (checkForWin(row, col, shadowBoard)) {
                        // Undo the move
                        gameBoard[row][col] = ' ';
                        shadowBoard.get(row).set(col, Optional.empty());
                        return col;
                    }
                    // Undo the move
                    gameBoard[row][col] = ' ';
                    shadowBoard.get(row).set(col, Optional.empty());
                }
            }

            // Check for a move to block the opponent from winning
            char opponent = (currentPlayer == 'B') ? 'R' : 'B';
            for (int col = 0; col < 7; col++) {
                int row = findLowestEmptyRow(col);
                if (row != -1) {
                    // Simulate placing a disc for the opponent and check for a win
                    gameBoard[row][col] = opponent;
                    shadowBoard.get(row).set(col, Optional.of(opponent));
                    if (checkForWin(row, col, shadowBoard)) {
                        // Undo the move
                        gameBoard[row][col] = ' ';
                        shadowBoard.get(row).set(col, Optional.empty());
                        return col;
                    }
                    // Undo the move
                    gameBoard[row][col] = ' ';
                    shadowBoard.get(row).set(col, Optional.empty());
                }
            }

            // If no winning or blocking move, make a random move
            return (int) (Math.random() * 7);
        }
        else{
            //make a random move, as Random AI is selected
            return (int) (Math.random() * 7);
        }
    }


    private void checkGameStatus(int row, int col) {
        // Check for a win or draw using the shadow board
        if (checkForWin(row, col, shadowBoard)) {
            char winner = (currentPlayer == 'B') ? 'R' : 'B';
            playerTurnText.setText("Player " + winner + " Wins!");
            gameOver = true;
            // Disable further moves or handle game over state
        } else {
            if (isBoardFull()) {
                playerTurnText.setText("It's a Draw!");
                gameOver = true;
                // Disable further moves or handle game over state
            } 
        }
    }


    private int findLowestEmptyRow(int col) {
        // Find the lowest empty row in the selected column
        for (int row = 5; row >= 0; row--) {
            if (gameBoard[row][col] == ' ') {
                return row;
            }
        }
        return -1; // Column is full
    }

    private boolean checkForWin(int row, int col, List<List<Optional<Character>>> board) {
        Optional<Character> currentPlayerSymbol = board.get(row).get(col);
    
        // Check horizontally
        if (checkDirection(board, row, col, 0, 1, currentPlayerSymbol)) {
            return true;
        }
    
        // Check vertically
        if (checkDirection(board, row, col, 1, 0, currentPlayerSymbol)) {
            return true;
        }
    
        // Check diagonally (up-right)
        if (checkDirection(board, row, col, -1, 1, currentPlayerSymbol)) {
            return true;
        }
    
        // Check diagonally (down-right)
        if (checkDirection(board, row, col, 1, 1, currentPlayerSymbol)) {
            return true;
        }
    
        return false;
    }
    
    private boolean checkDirection(List<List<Optional<Character>>> board, int row, int col, int rowDirection, int colDirection, Optional<Character> currentPlayerSymbol) {
        int count = 0;
    
        // Check in the positive direction
        for (int i = 0; i < 4; i++) {
            int newRow = row + i * rowDirection;
            int newCol = col + i * colDirection;
    
            if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 7 && board.get(newRow).get(newCol).equals(currentPlayerSymbol)) {
                count++;
            } else {
                break;
            }
        }
    
        // Check in the negative direction
        for (int i = 1; i < 4; i++) {
            int newRow = row - i * rowDirection;
            int newCol = col - i * colDirection;
    
            if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 7 && board.get(newRow).get(newCol).equals(currentPlayerSymbol)) {
                count++;
            } else {
                break;
            }
        }
    
        return count >= 4;
    }
    
    
    

    private boolean isBoardFull() {
        // Check if the board is full (no more empty spaces)
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (gameBoard[row][col] == ' ') {
                    return false; // There is an empty space, the board is not full
                }
            }
        }
        return true; // No empty spaces, the board is full
    }

    private void updatePlayerTurnText() {
        playerTurnText.setText("Player Turn: " + (currentPlayer == 'B' ? "Blue" : "AI"));
    }

    @FXML
    private void resetClick(MouseEvent event) {
        // Handle the Reset button click
        resetGame();
    }

    @FXML
    private void toggleAI(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        if (toggleButton.isSelected()) {
            aiToggle.setText("Random AI");
        } else {
            aiToggle.setText("Thoughtful AI");
        }
        updatePlayerTurnText(); // Update the player turn text based on the current player
    }


}
