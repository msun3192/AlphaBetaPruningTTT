// Matthew Sun
// Mr. Paige
// AI
// 11/18/24
public class TTT {
    public static void main(String[] args) {
        StringBuilder board = new StringBuilder();
        for (String arg : args){
            board.append(arg);
        }
        Board b = Board.valueOf(board.toString());
        if(b.isValid()){
            System.out.println("Turn: " + b.turn());
        }else{
            System.out.println("Invalid Board");
            return;
        }

        boolean xWon = b.hasWon(Player.X);
        boolean oWon = b.hasWon(Player.O);
        if(xWon){
            System.out.println("X wins");
            System.out.println(b.winningLine());
        }else if(oWon){
            System.out.println("O wins");
            System.out.println(b.winningLine());
        }else{
            // check if game is complete or not
            if(b.isComplete()){
                System.out.println("Game over");
            }else{
                System.out.println("Game incomplete");
            }
        }
        System.out.println(b);



    }
}