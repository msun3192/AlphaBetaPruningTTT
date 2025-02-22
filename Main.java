// Matthew Sun
// Mr. Paige
// AI
// 11/18/24
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Board[] boards = new Board[32];
    private static int current = 0;

    private static class Redo extends Exception {}
    private static class Undo extends Exception {}
    public static class Done extends Exception {}

    private static boolean isValid(int coordinate) {
        return coordinate >= 0 && coordinate <= 3;
    }

    private static int getCoordinate(String prompt) throws Redo, Undo, Done {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            switch (s.toLowerCase()) {
                case "":
                case "redo": // Restart entering this move
                    throw new Redo();

                case "undo": // Go back to previous board position
                    throw new Undo();

                case "done":
                case "exit": // Quit the program
                    throw new Done();

                default:
                    try {
                        int value = Integer.parseInt(s);
                        if (isValid(value)) return value;
                    } catch (NumberFormatException e) {
                    }
                    System.err.println("Invalid coordinate: " + s);
            }
        }
    }

    public static Board getYourMove(Board board) throws Done {

        // Prompt the user to enter a move as XYZ coordinates
        // Check the entry for validity (retry if invalid)
        // Returns an updated board with the user's move.

        // Entering a blank line or "redo" for any coordinate
        // will restart the with entering the X coordinate.

        // Entering "undo" will back up the game state to
        // the previous move entered by the user.

        // Entering "done" will quit the game.

        boards[current++] = board;
        System.out.println("Your move");
        while (true) {
            try {
                int x = getCoordinate("Enter X: ");
                int y = getCoordinate("Enter Y: ");
                int z = getCoordinate("Enter Z: ");
                int position = Coordinate.position(x, y, z);
                if (board.isEmpty(position)) return board.next(position);
                System.err.println("Position is not empty");

            } catch (Redo e) {
                // Try again

            } catch (Undo e) {
                // Revert to previous board configuration
                if (current > 0) {
                    current -= 2;
                    board = boards[current];
                    board.print();
                } else {
                    System.err.println("Start of game:");
                    board.print();
                }
            }
        }
    }

    private static int getMyMove(Board board, int plies) {
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Integer> bestMoves = new ArrayList<>();
        for (int pos = 0; pos < Coordinate.NCubed; pos++){
            // check if spot is occupied by O
            Board next = board.set(pos, Player.X);
            if(next == null){
                continue;
            }
            int score = minimax(next, plies-1, Integer.MIN_VALUE,Integer.MAX_VALUE,false);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(pos);
            }
            if(score == bestScore){
                bestMoves.add(pos);
            }
            // add best move tracker
        }
        int bestPos = bestMoves.get((int)(Math.random()*bestMoves.size()));
        return bestPos;
    }
    public static int minimax(Board board, int depth, int alpha, int beta, boolean max){
        // since we run this when it is our turn, need to run max first
        if (depth <= 0 || board.isComplete()) return board.boardEvaluation(depth);

        int value;

        if(max){
            value = Integer.MIN_VALUE;
            for (int pos = 0; pos < Coordinate.NCubed; pos++){
                Board next = board.set(pos, Player.X);
                if(next == null){
                    continue;
                }
                value = Integer.max(value,minimax(next,depth-1,alpha,beta,false));
                if (value > beta) {
                    break;
                }
                alpha = Integer.max(alpha,value);
            }
        }else{
            value = Integer.MAX_VALUE;
            for (int pos = 0; pos < Coordinate.NCubed; pos++){
                Board next = board.set(pos, Player.O);
                if(next == null){
                    continue;
                }
                value = Integer.min(value,minimax(next,depth-1,alpha,beta,true));
                if (value < alpha) {
                    break;
                }
                beta = Integer.min(beta,value);
            }
        }
        return value;
    }
    public static int alphabeta(Board board, int depth, int alpha, int beta, boolean max){
        // since we run this when it is our turn, need to run max first
        if (depth <= 0 || board.isComplete()) return board.boardEvaluation(depth);

        int value;
        ArrayList<Board> validMoves = new ArrayList<>(); //todo
        for (int pos = 0; pos < Coordinate.NCubed; pos++) {
            Board next = board.set(pos, Player.X);
            if (next == null) {
                continue;
            }
            validMoves.add(next);
        }


        if(max){
            value = Integer.MIN_VALUE;
            for (int pos = 0; pos < Coordinate.NCubed; pos++){
                Board next = board.set(pos, Player.X);
                if(next == null){
                    continue;
                }
                value = Integer.max(value,minimax(next,depth-1,alpha,beta,false));
                if (value > beta) {
                    break;
                }
                alpha = Integer.max(alpha,value);
            }
        }else{
            value = Integer.MAX_VALUE;
            for (int pos = 0; pos < Coordinate.NCubed; pos++){
                Board next = board.set(pos, Player.O);
                if(next == null){
                    continue;
                }
                value = Integer.min(value,minimax(next,depth-1,alpha,beta,true));
                if (value < alpha) {
                    break;
                }
                beta = Integer.min(beta,value);
            }
        }
        return value;
    }

    public static void main(String[] args) {
        Board board = new Board();
        boolean first = true;
        int plies = 4;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-first":
                    first = true;
                    break;

                case "-second":
                    first = false;
                    break;

                case "-plies":
                    try {
                        arg = args[++i];
                        plies = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid value for -plies: " + arg);
                    } catch (IndexOutOfBoundsException e) {
                        System.err.println("No value for -plies");
                    }
                    break;

                default:
                    System.err.println("Invalid option: " + arg);
            }
        }

        boolean me = first;
        do {
            if (me) {
                int position = getMyMove(board, plies);
                board = board.next(position);
                System.out.println("My move: " + Coordinate.toString(position));
                board.print();
            } else {
                try {
                    board = getYourMove(board);
                    board.print();
                } catch (Done e) {
                    return; // Game abandonned
                }
            }
            me = !me;
        } while (!board.isComplete() && board.winner() == Player.EMPTY);

        switch (board.winner()) {
            case X:
                System.out.println(first ? "I won" : "You won");
                System.out.println(board.winningLine());
                break;

            case O:
                System.out.println(first ? "You won" : "I won");
                System.out.println(board.winningLine());
                break;

            default:
                System.out.println("Tie");
                break;
        }
    }
}
