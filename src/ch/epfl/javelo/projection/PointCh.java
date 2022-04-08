package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * @author Arnaud Haizmann (329072)
 *
 * Class (record) representing a point in the swiss coordinate system.
 */
public record PointCh(double e, double n) {
    /**
     * public constructor for a point in swiss coordinates.
     *
     * @param e the east coordinate
     * @param n the north coordinate
     * @throws IllegalArgumentException if the coordinates given to the constructor
     *                                  are not inside Switzerland.
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Calculates the squared distance between the current point and the given
     * one.
     *
     * @param that the point to which we calculate the squared distance
     * @return the squared distance separating the current and given points.
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e - this.e, that.n - this.n);
    }

    /**
     * Calculates the distance between the current point and the given one.
     *
     * @param that the point to which we calculate the distance
     * @return the distance separating the current and given points.
     */
    public double distanceTo(PointCh that) {
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * Returns the longitude of the point in swiss coordinates.
     *
     * @return the longitude in swiss coordinates.
     */
    public double lon() {
        return Ch1903.lon(this.e, this.n);
    }

    /**
     * Returns the latitude of the point in swiss coordinates.
     *
     * @return the latitude in swiss coordinates.
     */
    public double lat() {
        return Ch1903.lat(this.e, this.n);
    }
}
