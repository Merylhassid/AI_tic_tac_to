//the default limit tree is 7 and the default exploration rate is 0.
import java.util.Scanner;

public class Game {
    private static final int SIZE = 3; // The size of the game board
    private static int xCounter = 0; // Counter for X wins
    private static int oCounter = 0; // Counter for O wins
    private static int tCounter = 0; // Counter for games ending after 100 moves

    private static int xWonTurn = 0;

    private static int oWonTurn = 0;


    /**
     * Entry point for the game. Displays a menu and handles user interaction.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        Agent user = new User();
        int number;
        Agent agentQL = new QLearningAgent('X');
        Agent agentMM = new MinMaxAgent('X');
        Agent agentMM2 = new MinMaxAgent('O');


        while(loop) {
            System.out.print("choose 1 for 1000 games of Q-learning algorithm against Min-Max algorithm" +
                    "\nchoose 2 for play against Min-Max algorithm" +
                    "\nchoose 3 for play against Q-learning algorithm" +
                    "\nchoose 4 for change exploration rate for QL algorithm" +
                    "\nchoose 5 for change limit for MiniMax algorithm" +
                    "\nchoose 6 for exit\n");
            number = scanner.nextInt();

            switch (number) {
                case 1:
                    int gameNumber = 1;
                    while (gameNumber <= 1000) {
                        System.out.println("game number " + gameNumber + ":\n----------------\n");
                        game(agentQL, agentMM2);
                        gameNumber++;
                    }

                    System.out.println("X won " + xCounter + " times with avg turn of " + ((double)xWonTurn)/xCounter + " per game\n" +
                            "O won " + oCounter + " times with avg turn of " + ((double)oWonTurn)/oCounter + " per game\n" +
                            "the game stop after 100 turns " + tCounter + " times\n" +
                            "total avg turns per game = " +((double)(xWonTurn+oWonTurn))/(xCounter+oCounter));


                    ((QLearningAgent) agentQL).writeData();
                    xCounter = 0;
                    oCounter = 0;
                    xWonTurn = 0;
                    oWonTurn = 0;
                    tCounter = 0;
                    break;

                case 2:
                    game(agentMM, user);
                    System.out.println("enter 1 for return menu or any integer to exit: ");
                    number = scanner.nextInt();
                    if (number != 1)
                        loop = false;
                    break;

                case 3:
                    game(agentQL, user);
                    System.out.println("enter 1 for return menu or any integer to exit: ");
                    number = scanner.nextInt();
                    if (number != 1)
                        loop = false;
                    break;

                case 4:
                    System.out.println("enter the new exploration rate(number between 0 - 1): ");
                    ((QLearningAgent) agentQL).changeRandom(scanner.nextDouble());
                    break;

                case 5:
                    System.out.println("enter the new limit(number between 1 - 10): ");
                    int newLimit = scanner.nextInt();
                    ((MinMaxAgent) agentMM).changeLimit(newLimit);
                    break;

                case 6:
                    loop = false;
                    break;

                default:
                    System.out.println("choose only 1 or 2 or 3 or 4");

            }
        }
    }

    /**
     * Simulates a single game between two agents.
     *
     * @param xPlayer the agent playing as X.
     * @param oPlayer the agent playing as O.
     */
    private static void game(Agent xPlayer, Agent oPlayer){

        int[][] board = new int[SIZE][SIZE], oldBoard = new int[SIZE][SIZE];
        restartBoard(board);
        int turn = 0;
        boolean gameOver = false;
        int[] playMove;


        printBoard(board);

        while (!gameOver){
            if(turn % 2 == 0){
                playMove = xPlayer.getPlayMove(board, turn);
                oldBoard = xPlayer.copy(board);
            }
            else{
                playMove = oPlayer.getPlayMove(board, turn);
                oldBoard = oPlayer.copy(board);
            }

            if(turn >= 6){
                deleteOldest(board, turn);
            }
            updateBoard(board, playMove, turn);
            gameOver = checkGameOver(board);

            if(xPlayer instanceof QLearningAgent)
                ((QLearningAgent)xPlayer).updateQTable(oldBoard, playMove, turn, getReward(gameOver, turn),board);
            else if(oPlayer instanceof QLearningAgent)
                ((QLearningAgent)oPlayer).updateQTable(oldBoard, playMove, turn, getReward(gameOver, turn),board);

            printBoard(board);
            if(gameOver){
                if(turn % 2 == 0) {
                    System.out.println("X WON THE GAME AFTER " + (turn+1) + " TURNS");
                    xCounter++;
                    xWonTurn += turn + 1;
                }
                else {
                    System.out.println("O WON THE GAME AFTER " + (turn+1) + " TURNS");
                    oCounter++;
                    oWonTurn += turn + 1;
                }
            }

            turn++;

            if(turn >= 100){
                tCounter++;
                gameOver = true;
            }
        }

    }

    /**
     * Returns a reward based on the game state and turn.
     *
     * @param gameOver whether the game is over.
     * @param turn the current turn.
     * @return the reward value.
     */
    private static int getReward(boolean gameOver, int turn) {
        if(gameOver && turn % 2 == 0)
            return 1000;
        else if(gameOver && turn % 2 != 0)
            return -1000;
        return -1;
    }

    /**
     * Checks if the game is over by finding a winning line or diagonal.
     *
     * @param board the current state of the board.
     * @return true if the game is over; false otherwise.
     */
    private static boolean checkGameOver(int[][] board){
        for (int i = 0; i < 3; i++) {
            if (isWinningLine(board[i][0], board[i][1], board[i][2]) ||
                    isWinningLine(board[0][i], board[1][i], board[2][i])) {
                return true;
            }
        }

        // Check diagonals
        if (isWinningLine(board[0][0], board[1][1], board[2][2]) ||
                isWinningLine(board[0][2], board[1][1], board[2][0])) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a line contains all even (X) or all odd (O) values and is not empty.
     *
     * @param a the first cell value.
     * @param b the second cell value.
     * @param c the third cell value.
     * @return true if the line is a winning line; false otherwise.
     */
    private static boolean isWinningLine(int a, int b, int c) {
        // Check if all are even (X) or odd (O) and not empty (-1)
        return (a != -1 && a % 2 == b % 2 && b % 2 == c % 2);
    }

    /**
     * Updates the board with the current move.
     *
     * @param board the current state of the board.
     * @param playMove the move to update on the board.
     * @param turn the current turn.
     */
    private static void updateBoard(int[][] board, int[] playMove, int turn){
        if(board[playMove[0]][playMove[1]] == -1)
            board[playMove[0]][playMove[1]] = turn;
        else
            System.out.println("error spot already taken");
    }

    /**
     * Deletes the oldest move on the board (moves older than 6 turns).
     *
     * @param board the current state of the board.
     * @param turn the current turn.
     */
    private static void deleteOldest(int[][] board, int turn){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(board[i][j] == turn - 6 && board[i][j] != -1){
                    board[i][j] = -1;
                    break;
                }
            }
        }
    }

    /**
     * Resets the board by setting all cells to -1.
     *
     * @param board the board to reset.
     */
    private static void restartBoard(int[][] board){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                board[i][j] = -1;
            }
        }
    }

    /**
     * Prints the current state of the board.
     *
     * @param board the board to print.
     */
    private static void printBoard(int[][] board){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(board[i][j] == -1) {
                    System.out.print(" ");
                }
                else if (board[i][j] % 2 == 0) {
                    System.out.print("X");
                }
                else{
                    System.out.print("O");
                }

                if(j != 2){
                    System.out.print("|");
                }
            }

            System.out.println();

            if(i != 2){
                System.out.println("-+-+-");
            }
        }
        System.out.println("\n::::::::::::\n");
    }
}
