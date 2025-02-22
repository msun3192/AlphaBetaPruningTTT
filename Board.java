// Matthew Sun
// Mr. Paige
// AI
// 11/18/24
public class Board {

    public static final int N = Coordinate.N;
    private long X = 0;
    private long O = 0;
    public static long[] corners = { // need to change if board size changes(for some reason)
            Bit.set(0,Coordinate.position(0,0,0)),
            Bit.set(0,Coordinate.position(3,0,0)),
            Bit.set(0,Coordinate.position(0,3,0)),
            Bit.set(0,Coordinate.position(3,3,0)),
            Bit.set(0,Coordinate.position(0,0,3)),
            Bit.set(0,Coordinate.position(3,0,3)),
            Bit.set(0,Coordinate.position(0,3,3)),
            Bit.set(0,Coordinate.position(3,3,3)),
            };
    public static long[] centers = { // need to change if board size changes(for some reason)
            Bit.set(0,Coordinate.position(1,1,1)),
            Bit.set(0,Coordinate.position(2,1,1)),
            Bit.set(0,Coordinate.position(1,2,1)),
            Bit.set(0,Coordinate.position(2,2,1)),
            Bit.set(0,Coordinate.position(1,1,2)),
            Bit.set(0,Coordinate.position(2,1,2)),
            Bit.set(0,Coordinate.position(1,2,2)),
            Bit.set(0,Coordinate.position(2,2,2)),
    };
    public Board(long X, long O){
        this.X = X;
        this.O = O;
    }
    public Board(){}

    public Player get(int position) {
        if(Bit.isSet(X,position)){
            return Player.X;
        }else if(Bit.isSet(O,position)){
            return Player.O;
        }else{
            return Player.EMPTY;
        }
    }

    public Player get(int x, int y, int z) {
        return get(Coordinate.position(x,y,z));
    }

    public Board set(int position, Player player) {
        Board newBoard = new Board(X,O);
        // check if bit is set
        if(!Bit.isSet(X,position) && !Bit.isSet(O,position)){
            if(player == Player.X) {
                newBoard.X = Bit.set(X, position);
            }
            if(player == Player.O) {
                newBoard.O = Bit.set(O, position);
            }
            return newBoard;
        }
        return null;
    }

    
    // Construct a Board from a string representation.
    // Should be an inverse function of toString().

    public static Board valueOf(String s) {
        Board board = new Board(0,0);
        int position = 0;

        for (int i= 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case 'x':
                case 'X':
                    board.set(position++, Player.X);
                    break;

                case 'o':
                case 'O':
                    board.set(position++, Player.O);
                    break;

                case '.':
                    position++;
                    break;

                case ' ':
                case '|':
                    break;

                default:
                    throw new IllegalArgumentException("Invalid player: " + c);
            }
        }
        return board;
    }


    // Image & printing functions.

    @Override
    public String toString() {
        String result = "";
        String separator = "";

        for (int position = 0; position < 64; position++) {
            result += separator;
            result += this.get(position).toString();
            if ((position+1) % 16 == 0) {
                separator = " | ";
            } else if ((position+1) % 4 == 0) {
                separator = " ";
            } else {
                separator = "";
            }
        }
        return result;
    }


    public void print() {
        for (int y = N-1; y >= 0; y--) {
            for (int z = 0; z < N; z++) {
                for (int x = 0; x < N; x++) {
                    System.out.print(this.get(x, y, z));
                }
                System.out.print("    ");
            }
            System.out.println();
        }
    }
    public boolean hasWon(Player player){
        long playerBoard;
        if(player.equals(Player.X)){
            playerBoard = X;
        }else{
            playerBoard = O;
        }
        Line[] lines = Line.lines;
        for (Line line: lines) {
            long positions =  line.positions();
            if((playerBoard & positions) == positions){
                return true;
            }
        }
        return false;
    }
    public Player winner(){
        if(hasWon(Player.X)){
            return Player.X;
        }else if(hasWon(Player.O)){
            return Player.O;
        }else{
            return Player.EMPTY;
        }
    }

    public Line winningLine(){
        Line[] lines = Line.lines;
        for (Line line: lines) {
            long positions =  line.positions();
            if((X & positions) == positions || (O & positions) == positions){
                return line;
            }
        }
        return null;
    }

    public boolean isValid(){
        // check that X and Os are relatively similar(1 or 0 apart)
        if(Math.abs(Bit.countOnes(X) - Bit.countOnes(O)) > 1){
            return false;
        }
        // check if both have won
        return !hasWon(Player.X) || !hasWon(Player.O);
    }
    public Player turn(){
        int xs = Bit.countOnes(X);
        int os = Bit.countOnes(O);
        if(xs > os){
            return Player.O;
        }else{
            return Player.X;
        }
    }
    public boolean isComplete(){
        return ~(X | O) == 0;}
    public boolean isEmpty(int position){
        return get(position).equals(Player.EMPTY);
    }
    public int boardEvaluation(int ply){
        // Positive is X favored, Negative is O favored
        Player turn = turn();
        // 1. Check if someone won
        int xRating = 0;
        int oRating = 0;
        if(hasWon(Player.X)){
            return  10000;
        }
        if (hasWon(Player.O)) {
            return - 10000;
        }

        xRating += 1000 * unblockedAnythings(Player.X,3);
        oRating -= 1000 * unblockedAnythings(Player.O,3);
        // 2. Check unblocked two in a rows for each player
        xRating += 100 * unblockedAnythings(Player.X,2);
        oRating -= 100 * unblockedAnythings(Player.O,2);
        // 3. Check unblocked one in a rows
        xRating += 10 * unblockedAnythings(Player.X,1);
        oRating -= 10 * unblockedAnythings(Player.O,1);
        // 4. Check for strategic positioning
        xRating += 5 * strategicPositions(Player.X);
        oRating -= 5 * strategicPositions(Player.O);
        if(turn == Player.X){
            oRating*=2;
        }
        if(turn == Player.O){
            xRating*=2;
        }
        return (xRating + oRating);
    }
    public int simpleBoardEval(){
        if(hasWon(Player.X)){
            return 1;
        } else if (hasWon(Player.O)) {
            return -1;
        }
        return 0;
    }
    public int unblockedAnythings(Player player, int c){
        long p;
        long g;
        if(player.equals(Player.X)){
            p = X;
            g = O;
        }else{
            p = O;
            g = X;
        }
        Line[] lines = Line.lines;
        int count = 0;
        for (Line line: lines) {
            long positions = line.positions();
            if(Bit.countOnes(p & positions) == c && (g & positions) == 0){
                count++;
            }
        }
        return count;
    }
    public int strategicPositions(Player player){
        long p;
        if(player.equals(Player.X)){
            p = X;
        }else{
            p = O;
        }
        int count = 0;
        for (long corner: corners) {
            if((p & corner) != 0) {count++;}
        }
        for (long center: centers) {
            if((p & center) != 0) {count++;}
        }
        return count;
    }
    public Board next(int position){
        return this.set(position,turn());
    }
//    public int forks(){
//        //generate all lines with intersection
//        Line[] lines = Line.lines;
//        for (Line line1: lines) {
//            for (Line line2: lines) {
//
//            }
//        }
//    }
//    public int Ls(){
//
//    }
}
