package Minesweeper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A class to represent the Minesweeper game
 */
public class Minesweeper extends Application {

    /**
     * The grid of tiles for the board
     */
    private GridPane gridPane = null;

    /**
     * The number of rows in the board
     */
    private static int rows = 0;

    /**
     * The number of columns in the board
     */
    private static int columns = 0;
    
    /**
     * The number of mines on the board
     */
    private int mines = 0;

    /**
     * The set to keep track of all the flagged tiles
     */
    private HashSet<String> flaggedTiles = new HashSet<>();

    /**
     * The set to keep track of all visited (broken) tiles
     */
    private HashSet<String> visitedTiles = new HashSet<>();

    /**
     * The board containing all the mines
     */
    private int[][] mineBoard = new int[rows][columns];

    /**
     * A boolean flag to check for the first move of the game
     */
    private boolean firstMove = true;

    /**
     * A boolean flag to check if the game has ended
     */
    private boolean gameHasEnded = false;

    /**
     * A method to set the number of rows of the board
     * @param numRows the number of rows of the board
     */
    public static void setRows(int numRows) {
        rows = numRows;
    }

    /**
     * A method to set the number of columns of the board
     * @param numColumns the number of columns of the board
     */
    public static void setColumns(int numColumns) {
        columns = numColumns;
    }

    /**
     * A method to place mines throughout the board
     * @param row the row of the tile that was broken
     * @param column the column of the tile that was broken
     */
    public void placeMines(int row, int column) {
        Random rand = new Random();
        int[][] surroundingTiles = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        HashSet<String> visitedTiles = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{row, column});
        int[] current = null;
        while(queue.size() > 0) {
            current = queue.poll();
            if (current[0] == row && current[1] == column) {
                this.mineBoard[row][column] = -1;
                visitedTiles.add(current[0] + "," + current[1]);
            }
            else if (current.length == 3) {
                this.mineBoard[current[0]][current[1]] = 0;
                visitedTiles.add(current[0] + "," + current[1]);
            }
            else if (rand.nextInt(5) == 1) {
                mineBoard[current[0]][current[1]] = 9;
                this.mines++;
            }
            else {
                mineBoard[current[0]][current[1]] = 0;
            }
            for (int[] tile : surroundingTiles) {
                String tilePosition = (current[0] + tile[0]) + "," + (current[1] + tile[1]);
                if (current[0] + tile[0] >= 0 && current[0] + tile[0] < rows && current[1] + tile[1] >= 0 && current[1] + tile[1] < columns && visitedTiles.add(tilePosition)) {
                    if (current[0] == row && current[1] == column) {
                        queue.add(new int[]{current[0] + tile[0], current[1] + tile[1], -1});
                    }
                    else {
                        queue.add(new int[]{current[0] + tile[0], current[1] + tile[1]});
                    }
                }
            }
        }
    }
    
    /**
     * A method to place the number of surrounding mines on the board
     */
    public void placeNumbers() {
        int count = 0;
        int[][] surroundingTiles = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!(this.mineBoard[i][j] == (9))) {
                    for (int[] tile : surroundingTiles) {
                        try {
                            if (this.mineBoard[i + tile[0]][j + tile[1]] == 9) {
                                count++;
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                    if (count != 0) {
                        this.mineBoard[i][j] = count;
                    }
                    count = 0;
                }
            }
        }
    }

    /**
     * A method to break the tiles on the board
     * @param row the row of the tile that was broken
     * @param column the column of the tile that was broken
     */
    public void breakTiles(int row, int column) {
    int[][] surroundingTiles = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{row, column});
        int[] current = null;
        while (queue.size() > 0) {
            current = queue.poll();
            if (this.mineBoard[current[0]][current[1]] == 9) {
                this.gameHasEnded = true;
                return;
            }
            else if (this.mineBoard[current[0]][current[1]] > 0) {
                updateTile(this.mineBoard[current[0]][current[1]], current[1] + 1, current[0] + 1);
                visitedTiles.add(current[0] + "," + current[1]);
            }
            else {
                updateTile(this.mineBoard[current[0]][current[1]], current[1] + 1, current[0] + 1);
                visitedTiles.add(current[0] + "," + current[1]);
                for (int[] tile : surroundingTiles) {
                    String tilePosition = (current[0] + tile[0]) + "," + (current[1] + tile[1]);
                    boolean inBounds = current[0] + tile[0] >= 0 && current[0] + tile[0] < rows && current[1] + tile[1] >= 0 && current[1] + tile[1] < columns;
                    if (inBounds && !visitedTiles.contains(tilePosition) && !flaggedTiles.contains(tilePosition)) {
                        if (tile[0] == 0 || tile[1] == 0 || this.mineBoard[current[0] + tile[0]][current[1] + tile[1]] > 0) {
                            queue.add(new int[]{current[0] + tile[0], current[1] + tile[1]});
                            visitedTiles.add(tilePosition);
                        }
                    }
                }
            }
        }
    }

    /**
     * A method to update the tiles when updating the board
     */
    public void updateTile(int numMines, int column, int row) {
        Region rect = new Region();
        rect.setStyle("-fx-background-color: #A9A9A9; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: gray; -fx-min-width: 50; -fx-min-height:50; -fx-max-width:50; -fx-max-height: 50;");
        Text text = new Text();
        text.setStyle("-fx-font-weight: bold");
        if (numMines == 9) {
            rect.setStyle("-fx-background-color: #FF0000; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: gray; -fx-min-width: 50; -fx-min-height:50; -fx-max-width:50; -fx-max-height: 50;");
            text.setText("M");
            text.setFill(Color.BLACK);
        }
        else if (numMines > 0) {
            text.setText(Integer.toString(numMines));
            switch (numMines) {
                case 1:
                    text.setFill(Color.BLUE);
                    break;
                case 2:
                    text.setFill(Color.GREEN);
                    break;
                case 3:
                    text.setFill(Color.RED);
                    break;
                case 4:
                    text.setFill(Color.DARKBLUE);
                    break;
                case 5:
                    text.setFill(Color.MAROON);
                    break;
                case 6:
                    text.setFill(Color.DARKCYAN);
                    break;
                case 7:
                    text.setFill(Color.BLACK);
                    break;
                case 8:
                    text.setFill(Color.GRAY);
                    break;
            }
        }
        else {
            text.setText("");
        }
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(rect, text);
        gridPane.add(stackPane, column, row);
    }

    /**
     * A method to set up the board
     * @param row the row of the tile that was broken
     * @param column the column of the tile that was broken
     */
    public void setUpBoard(int row, int column) {
        this.placeMines(row, column);
        this.placeNumbers();
    }

    /**
     * A method to update the board when a tile is broken
     * @param row the row of the tile that was broken
     * @param column the column of the tile that was broken
     */
    public void updateBoard(int row, int column) {
        if (this.mineBoard[row][column] == 9) {
            updateTile(this.mineBoard[row][column], column + 1, row + 1);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (this.mineBoard[i][j] == 9) {
                        updateTile(9, column + 1, row + 1);
                    }
                }
            }
            this.gameHasEnded = true;
            System.out.println("You dug a mine.");
            System.out.println("Game Over.");
        }
        else {
            this.breakTiles(row, column);
        }
    }

    /**
     * A method to initialize the game
     * @param row the row of the tile that was broken
     * @param column the column of the tile that was broken
     */
    public void initializeGame(int row, int column) {
        this.setUpBoard(row, column);
        this.updateBoard(row, column);
    }
    
    /**
     * A method to build the Minesweeper board
     * @param primaryStage the main window
     */
    public void start(Stage primaryStage) {
        gridPane = new GridPane();
        for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
                Button button = new Button();
                EventHandler<MouseEvent> mineSweeperAction = new MineSweeperAction();
                button.setMinHeight(50);
                button.setMinWidth(50);
                button.setOnMouseClicked(mineSweeperAction);
                gridPane.add(button, i + 1, j + 1);
			}
		}
        BorderPane borderPane = new BorderPane(gridPane);
        borderPane.setCenter(gridPane);
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }

    /**
     * A class to represent an action performed on the Minesweeper board
     */
    public class MineSweeperAction implements EventHandler<MouseEvent> {

        /**
         * A method to handle a mouse event that either breaks or flags a tile
         * @param e the mouse event
         */
        @Override
        public void handle(MouseEvent e) {
            if (gameHasEnded) {
                return;
            }
            Button button = (Button)e.getSource();
            if(e.getButton().equals(MouseButton.PRIMARY)) {
                if (button.getText().equals("F")) {
                    return;
                }
                else if (!firstMove) {
                    updateBoard(GridPane.getRowIndex(button) - 1, GridPane.getColumnIndex(button) - 1);
                }
                else if (firstMove) {
                    initializeGame(GridPane.getRowIndex(button) - 1, GridPane.getColumnIndex(button) - 1);
                    firstMove = false;
                }
            }
            else if (e.getButton().equals(MouseButton.SECONDARY)) {
                if (button.getText().equals("")) {
                    button.setText("F");
                    button.setStyle("-fx-font-weight: bold");
                    flaggedTiles.add((GridPane.getRowIndex(button) - 1) + "," + (GridPane.getColumnIndex(button) - 1));
                }
                else {
                    button.setText("");
                }
            }
            if (visitedTiles.size() == rows * columns - mines) {
                System.out.println("Congratulations!");
                System.out.println("You win!");
                gameHasEnded = true;
            }
        }
        
    }

    /**
     * The main method of the Minesweeper game
     */
    public static void main(String[] args) {
        Scanner settings = new Scanner(System.in);
        boolean isValidOption = false;
        boolean isValidSize = false;
        System.out.println("Welcome to Minesweeper!");
        do {
            System.out.println("If you want to play the game, type 'Play'. If you want to use custom settings, type 'Custom'.");
            String option = settings.nextLine();
            if (option.equals("Play")) {
                isValidOption = true;
                setRows(10);
                setColumns(10);
            }
            else if (option.equals("Custom")) {
                isValidOption = true;
                System.out.println("Please enter the board size.");
                do {
                    try {
                        System.out.println("Rows:");
                        int rows = settings.nextInt();
                        System.out.println("Columns:");
                        int columns = settings.nextInt();
                        if (rows > 0 && columns > 0) {
                            isValidSize = true;
                            setRows(rows);
                            setColumns(columns);
                        }
                        else {
                            System.out.println("Please enter a larger board size. The minimum board size is 1 x 1.");
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Please enter the size in a valid format.");
                        settings.next();
                    }
                } while(!isValidSize);
            }
        } while(!isValidOption);
        settings.close();
        Application.launch(args);
	}

}