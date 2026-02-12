/**
 * Abstract class representing a generic agent in the game.
 * An agent can perform moves and handle board-related actions.
 */
public abstract class Agent {
    /**
     * Abstract method that determines the next move of the agent.
     *
     * @param board the current state of the game board.
     * @param turn the current turn number.
     * @return an array of two integers representing the row and column of the move.
     */
    public abstract int[] getPlayMove(int[][] board, int turn);

    /**
     * Creates a deep copy of the given board.
     *
     * @param board the game board to copy.
     * @return a new 2D array that is a copy of the provided board.
     */
    public int[][] copy(int[][] board) {
        int[][] newBoard = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }
}
