package ch.epfl.javelo.projection;

/**
 * Helper class containing constants and methods about the limits of
 * Switzerland.
 * 
 * @author Arnaud Haizmann (329072)
 * @author Florian Kolly (328313)
 */
public final class SwissBounds {
    /**
     * Smallest east coordinate in Switzerland in swiss coordinates.
     */
    public static final double MIN_E = 2_485_000;
    /**
     * Biggest east coordinate in Switzerland in swiss coordinates.
     */
    public static final double MAX_E = 2_834_000;
    /**
     * Smallest north coordinate in Switzerland in swiss coordinates.
     */
    public static final double MIN_N = 1_075_000;
    /**
     * Biggest north coordinate in Switzerland in swiss coordinates.
     */
    public static final double MAX_N = 1_296_000;
    /**
     * Width of Switzerland (in meters).
     */
    public static final double WIDTH = MAX_E - MIN_E;
    /**
     * Height of Switzerland (in meters).
     */
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Constructor for the non-instantiable SwissBounds class.
     */
    private SwissBounds() {
    }

    /**
     * Checks if the given swiss coordinates are inside the swiss boundaries.
     * 
     * @param e the east coordinate
     * @param n the north coordinate
     * @return true if the coordinates are in Switzerland, false otherwise
     */
    public static boolean containsEN(double e, double n) {
        return e >= MIN_E && e <= MAX_E && n >= MIN_N && n <= MAX_N;
    }
}
