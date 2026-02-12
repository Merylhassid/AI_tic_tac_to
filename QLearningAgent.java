import java.io.*;
import java.util.*;

/**
 * This class implements a Q-learning agent that learns how to play a game (e.g., Tic-Tac-Toe)
 * using Q-learning to update its policy based on rewards and state transitions.
 */
public class QLearningAgent extends Agent{

    private char playerType;
    private static final int SIZE = 3;
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.9;
    private static double EXPLORATION_RATE = 0.0; // random play p

    private Map<String, Double> qTable = new HashMap<>();


    /**
     * Constructor that initializes the QLearningAgent with a given player type.
     * It loads the Q-table from a file based on the player's type.
     *
     * @param playerType The type of the player ('X' or 'O')
     */
    public QLearningAgent(char playerType){
        this.playerType = playerType;
        String filePath;
        if(playerType == 'X')
            filePath = "DBForX.txt";
        else
            filePath = "DBForO.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] arr;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                arr = line.split(":");
                qTable.put(arr[0], Double.valueOf(arr[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Determines the next move for the agent based on the current board state.
     * The agent either selects a random move or uses the Q-table to choose the best move.
     *
     * @param board The current game board
     * @param turn The current turn number
     * @return The chosen move as an array of two integers (row, column)
     */
    public int[] getPlayMove(int[][] board, int turn){

        String state = boardToString(board, turn);
        List<int[]> availableMoves = getAvailableMoves(board);

        // בחירה אקראית או על בסיס הטבלה
        if (Math.random() < EXPLORATION_RATE) {
            return availableMoves.get(new Random().nextInt(availableMoves.size()));
        }

        // מציאת המהלך עם הערך הגבוה ביותר בטבלת Q
        double bestValue = Double.NEGATIVE_INFINITY;
        int[] bestMove = null;

        for (int[] move : availableMoves) {
            String nextState = simulateState(board, move, turn);
            double value = qTable.getOrDefault(nextState, 0.0);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove != null ? bestMove : availableMoves.get(0);

    }

    /**
     * Simulates the next state of the board after a move and returns its string representation.
     *
     * @param board The current game board
     * @param move The move to simulate
     * @param turn The current turn number
     * @return The string representation of the simulated next state
     */
    private String simulateState(int[][] board, int[] move, int turn) {
        int[][] newBoard = simulateMove(board, move, turn);
        return boardToString(newBoard, turn);
    }

    /**
     * Simulates a move on the board and returns the updated board.
     * The move is applied to a copy of the current board.
     *
     * @param board The current game board
     * @param move The move to simulate
     * @param turn The current turn number
     * @return The updated game board after the move
     * */
    private int[][] simulateMove(int[][] board, int[] move, int turn) {
        int[][] newBoard = copy(board);
        newBoard[move[0]][move[1]] = turn;

        int oldestTurn = turn - 6;
        int[] oldestCell = null;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (turn >= 6 && newBoard[i][j] == oldestTurn) {
                    newBoard[i][j] = -1;
                }
            }
        }

        return newBoard;
    }


    /**
     * Returns a list of available moves on the current game board.
     *
     * @param board The current game board
     * @return A list of available moves (each move is represented as an array of two integers)
     */
    private List<int[]> getAvailableMoves(int[][] board) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == -1) {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }


    /**
     * Updates the Q-table based on the current state, the chosen move, and the resulting reward.
     * The Q-value is updated using the Q-learning formula.
     *
     * @param board The current game board
     * @param move The move taken
     * @param turn The current turn number
     * @param reward The reward received after the move
     * @param nextBoard The game board after the move
     */
    public void updateQTable(int[][] board, int[] move, int turn, int reward, int[][] nextBoard) {
        String currentState = boardToString(board, turn);
        String nextState = boardToString(nextBoard, turn+1);

        double currentQ = qTable.getOrDefault(currentState, 0.0);
        double nextMaxQ = qTable.getOrDefault(nextState, 0.0);

        // עדכון הטבלה באמצעות נוסחת Q-Learning
        double updatedQ = currentQ + LEARNING_RATE * (reward + DISCOUNT_FACTOR * nextMaxQ - currentQ);
        qTable.put(currentState, updatedQ);
        //qTable.put(nextState, Double.valueOf(reward));
    }


    /**
     * Converts the game board to a string representation.
     * The string representation is used to store and compare states in the Q-table.
     *
     * @param board The current game board
     * @param turn The current turn number
     * @return The string representation of the game board
     */
    private String boardToString(int[][] board, int turn){
        int[][] copyBoard = copy(board);
        String strBoard = "";
        int minValue = 0;
        if(turn >= 6) {
            minValue = turn - 6;
        }

        for(int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                if(copyBoard[i][j] != -1)
                    copyBoard[i][j] -= minValue;
                strBoard += copyBoard[i][j];
                if(i != 2 || j != 2)
                    strBoard += ",";
            }
        }

        return strBoard;
    }

    /**
     * Writes the Q-table to a file so that it can be loaded later.
     * The file is saved based on the player type (either "DBForX.txt" or "DBForO.txt").
     */
    public void writeData(){
        String filePath;
        if(playerType == 'X')
            filePath = "DBForX.txt";
        else
            filePath = "DBForO.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for(Map.Entry<String, Double> entry : qTable.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeRandom(double p){
        if(p > 1 || p < 0)
            return;
        EXPLORATION_RATE = p;
    }
}
