package ch.epfl.javelo;

/**
 * @author Arnaud Haizmann (329072)
 *
 * Math helper class.
 */
public final class Math2 {
    /**
     * Constructor of the non-instantiable Math2 class.
     */
    private Math2() {
    }

    /**
     * Returns the ceil value of x divided by y.
     *
     * @param x the dividend
     * @param y the divisor
     * @throws IllegalArgumentException if x is strictly less than 0 or y is less
     *                                  than or equal to 0
     * @return the ceil value.
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }

    /**
     * Returns the y-value of the point on the line going through (0,y0) and (1,y1).
     *
     * @param y0 the y-value at x=0
     * @param y1 the y-value at x=1
     * @param x  x-value to interpolate
     * @return y-value at position x on the line passing by y0 and y1.
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * Clamps the given value v between the given minimum and maximum integer
     * values.
     *
     * @param min the minimum value to compare against
     * @param v   the value to be clamped inside the range
     * @param max the maximum value to compare against
     * @throws IllegalArgumentException if the minimum is strictly bigger than the
     *                                  maximum
     * @return the value clamped between the min and max values.
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        return (v <= min) ? min : ((v >= max) ? max : v);
    }

    /**
     * Clamps the given value v between the given minimum and maximum float values.
     *
     * @param min the minimum value to compare against
     * @param v   the value to be clamped inside the range
     * @param max the maximum value to compare against
     * @throws IllegalArgumentException if the minimum is strictly bigger than the
     *                                  maximum
     * @return the value clamped inside the min and max values.
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(min <= max);
        return (v <= min) ? min : ((v >= max) ? max : v);
    }

    /**
     * Calculates the hyperbolic sinus of the given input.
     *
     * @param x the input
     * @return the hyperbolic sinus of the argument.
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(Math.pow(x, 2) + 1));
    }

    /**
     * Calculates the dot product for two-dimensional vectors.
     *
     * @param uX the x component of vector u
     * @param uY the y component of vector u
     * @param vX the x component of vector v
     * @param vY the y component of vector v
     * @return the dot product of vectors u and v given by their respective
     *         components.
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return Math.fma(uX, vX, uY * vY);
    }

    /**
     * Calculates the squared norm of the vector given by its x and y components.
     *
     * @param uX the x component of the vector
     * @param uY the y component of the vector
     * @return the squared norm of the vector from its components.
     */
    public static double squaredNorm(double uX, double uY) {
        return Math.pow(uX, 2) + Math.pow(uY, 2);
    }

    /**
     * Calculates the norm of the vector given by its x and y components.
     *
     * @param uX the x component of the vector
     * @param uY the y component of the vector
     * @return the norm of the vector from its components.
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Returns the length of the projection of the vector from point A to point P
     * on the vector from point A to point B.
     *
     * @param aX the x component of point A
     * @param aY the y component of point A
     * @param bX the x component of point B
     * @param bY the y component of point B
     * @param pX the x component of point P
     * @param pY the y component of point P
     * @return the length of the projection.
     */
    public static double    projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;
        return dotProduct(uX, uY, vX, vY) / norm(vX, vY);
    }
}
