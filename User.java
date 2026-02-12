import java.util.Scanner;

/**
 * The User class represents a human player in the game.
 * It extends the abstract {@link Agent} class and provides an implementation for getting the player's move.
 */
public class User extends Agent{

    private Scanner scanner = new Scanner(System.in);
    private int[] move = new int[2];

    /**
     * Prompts the user to select a move on the board.
     * Ensures the move is within bounds (1-3 for rows and columns) and the selected spot is not already taken.
     *
     * @param board the current state of the game board.
     * @param turn the current turn number.
     * @return an array of two integers representing the row and column of the user's move.
     */
    @Override
    public int[] getPlayMove(int[][] board, int turn) {
        boolean emptySpot = false;
        while(!emptySpot) {
            do {
                System.out.println("please choose row 1-3: ");
                move[0] = scanner.nextInt() - 1;

            } while (move[0] < 0 || move[0] > 2);

            do {
                System.out.println("please choose col 1-3: ");
                move[1] = scanner.nextInt() - 1;

            } while (move[1] < 0 || move[1] > 2);
            if(board[move[0]][move[1]] == -1)
                emptySpot = true;
            else
                System.out.println("this spot is taken, try again");
        }

        return move;
    }
}
