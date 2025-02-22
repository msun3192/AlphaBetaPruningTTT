// Matthew Sun
// Mr. Paige
// AI
// 11/18/24
public enum Player {

    X,
    O,
    EMPTY {
        @Override
        public String toString() {
            return ".";
        }
    };

    public Player other() {
        switch (this) {
            case X: return O;
            case O: return X;
            default: return EMPTY;
        }
    }

    public static Player valueOf(char c) {
        switch(c) {
            case 'x':
            case 'X':
                return X;

            case 'o':
            case 'O':
                return O;

            default:
                return EMPTY;
        }
    }
}
