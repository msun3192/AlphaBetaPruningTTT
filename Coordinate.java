// Matthew Sun
// Mr. Paige
// AI
// 11/18/24
public class Coordinate {

    // A class to facilitate conversion between positions and XYZ coordinates.

    public static final int N = 4;
    public static final int NSquared = N * N;
    public static final int NCubed = N * N * N;

    public static int getX(int position) {
        return (position%16)/4;
    }

    public static int getY(int position) {
        return position%4;
    }

    public static int getZ(int position) {
        return position/16;
    }

    public static int position(int x, int y, int z) {
        //zero based
        return 4*x + y + 16 * z;
    }

    public static String toString(int position) {
        return "X: " + getX(position) + " Y: " + getY(position) + " Z: " + getZ(position);
    }
}
