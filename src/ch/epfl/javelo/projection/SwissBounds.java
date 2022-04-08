package ch.epfl.javelo.projection;

/**
 * @author Hervé Sérandour (328233)
 * <p>
 * class representing swiss bounds.
 */
public final class SwissBounds {
    /**
     * constant representing the minimal east value.
     */
    public static final double MIN_E = 2485000;
    /**
     * constant representing the maximal east value.
     */
    public static final double MAX_E = 2834000;
    /**
     * constant representing the minimal north value.
     */
    public static final double MIN_N = 1075000;
    /**
     * constant representing the maximal north value.
     */
    public static final double MAX_N = 1296000;
    /**
     * constant representing the swiss width.
     */
    public static final double WIDTH = MAX_E - MIN_E;
    /**
     * constant representing the swiss height.
     */
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * return true iff the coordinates given are in the swiss bounds.
     *
     * @param e : east coordinate
     * @param n : north coordinate
     * @return tru iff the coordinates given are in the swiss bounds.
     */
    public static boolean containsEN(double e, double n) {
        return (e >= MIN_E) && (e <= MAX_E) && (n >= MIN_N) && (n <= MAX_N);
    }
}
