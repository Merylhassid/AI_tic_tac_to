import java.util.ArrayList;
import java.util.List;

/**
 * The MinMaxAgent class represents an AI player for the game that uses the Minimax algorithm
 * to determine the best move. It extends the {@link Agent} abstract class and provides an
 * implementation for selecting moves for both players ('X' and 'O').
 */
public class MinMaxAgent extends Agent{

    /**
     * Constructs a MinMaxAgent for the specified player.
     *
     * @param player the character representing the player ('X' or 'O').
     */
    public MinMaxAgent(char player){
        this.player = player;
    }

    private final int EMPTY = -1;
    private final char player;
    private static int limit = 7;

    /**
     * Determines the best move for the current player using the Minimax algorithm.
     *
     * @param board the current state of the game board.
     * @param turn the current turn number.
     * @return an array of two integers representing the row and column of the best move.
     */
    public int[] getPlayMove(int[][] board, int turn){
        if(player == 'X')
            return getPlayMoveForX(board, turn);
        else
            return getPlayMoveForO(board, turn);
    }

    /**
     * Determines the best move for player 'O' using the Minimax algorithm.
     *
     * @param realBoard the current state of the game board.
     * @param turn the current turn number.
     * @return an array of two integers representing the row and column of the best move.
     */
    public int[] getPlayMoveForO(int[][] realBoard, int turn){
        int bestScore = Integer.MAX_VALUE;
        int[] bestMove = {-1, -1};
        int[][] board = copy(realBoard);

        for (int[] move : getAvailableMoves(board)) {
            // בצע את המהלך
            board[move[0]][move[1]] = turn;
            int score = minimax(copy(board), true, turn + 1, limit);
            board[move[0]][move[1]] = EMPTY;

            if (score < bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Determines the best move for player 'X' using the Minimax algorithm.
     *
     * @param realBoard the current state of the game board.
     * @param turn the current turn number.
     * @return an array of two integers representing the row and column of the best move.
     */
    public int[] getPlayMoveForX(int[][] realBoard, int turn){
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};
        int[][] board = copy(realBoard);

        for (int[] move : getAvailableMoves(board)) {
            // בצע את המהלך
            board[move[0]][move[1]] = turn;
            int score = minimax(copy(board), false, turn + 1, limit);
            board[move[0]][move[1]] = EMPTY;

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }


    /**
     * Minimax algorithm for evaluating the best move.
     *
     * @param board the current state of the game board.
     * @param isXTurn true if it's player 'X's turn, false otherwise.
     * @param turn the current turn number.
     * @param limit the depth limit for the algorithm.
     * @return the score of the evaluated move.
     */
    private int minimax(int[][] board, boolean isXTurn, int turn, int limit){
        if(limit == 0)
            return 0;
        if(turn >= 6)
            deleteOldest(board, turn - 1);
        Integer winner = checkWinner(board);
        if(winner != null){
            if(winner % 2 == 0)
                return limit;
            else
                return limit * (-1);
        }
        if(isXTurn) {
            int score, bestScore = Integer.MIN_VALUE;
            for (int[] move : getAvailableMoves(board)) {
                board[move[0]][move[1]] = turn;
                score = minimax(copy(board), !isXTurn, turn + 1, limit - 1);
                board[move[0]][move[1]] = EMPTY;
                if (score > bestScore)
                    bestScore = score;
            }
            return bestScore;
        }
        else{
            int score, bestScore = Integer.MAX_VALUE;
            for (int[] move : getAvailableMoves(board)) {
                board[move[0]][move[1]] = turn;
                score = minimax(copy(board), !isXTurn, turn + 1, limit - 1);
                board[move[0]][move[1]] = EMPTY;
                if (score < bestScore)
                    bestScore = score;
            }
            return bestScore;
        }
    }


    /**
     * Retrieves all available moves on the board.
     *
     * @param board the current state of the game board.
     * @return a list of available moves, where each move is an array of two integers (row and column).
     */
    private List<int[]> getAvailableMoves(int[][] board) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }


    /**
     * Checks for a winner on the board.
     *
     * @param board the current state of the game board.
     * @return the turn number of the winner, or null if there is no winner.
     */
    private Integer checkWinner(int[][] board) {
        // Check rows, columns, and diagonals
        for (int i = 0; i < 3; i++) {
            if (isWinningLine(board[i][0], board[i][1], board[i][2])) return board[i][0];
            if (isWinningLine(board[0][i], board[1][i], board[2][i])) return board[0][i];
        }
        if (isWinningLine(board[0][0], board[1][1], board[2][2])) return board[0][0];
        if (isWinningLine(board[0][2], board[1][1], board[2][0])) return board[0][2];

        return null;
    }

    /**
     * Deletes the oldest move from the board when the turn count exceeds 6.
     *
     * @param board the current state of the game board.
     * @param turn the turn number to delete.
     */
    private static void deleteOldest(int[][] board, int turn){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board[i][j] == turn - 6 && board[i][j] != -1){
                    board[i][j] = -1;
                    break;
                }
            }
        }
    }

    /**
     * Checks if three cells form a winning line.
     *
     * @param a the value of the first cell.
     * @param b the value of the second cell.
     * @param c the value of the third cell.
     * @return true if the three cells form a winning line, false otherwise.
     */
    private boolean isWinningLine(int a, int b, int c) {
        return a != EMPTY && a % 2 == b % 2 && b % 2 == c % 2;
    }

    public void changeLimit(int l){
        if(l > 10 || l < 1)
            return;
        limit = l;
    }
}
