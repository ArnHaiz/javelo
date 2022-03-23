package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * record class representing a point on an itinerary closest to a reference point.
 *
 * @author Arnaud (329072)
 * @author Hervé (328233)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {
    /**
     * constant representing a non-existing point.
     */
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * returns a new RoutePoint with just the position shifted by <code>positionDifference</code>.
     *
     * @param positionDifference
     * @return a new identical point except the position is shifted by <code>positionDifference</code>.
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * returns the point with the smallest <code>distanceToReference</code> from the two
     * without creating a new <code>RoutePoint</code>
     *
     * @param that
     * @return the point with the smallest <code>distanceToReference</code> from the two.
     */
    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference <= that.distanceToReference ? this : that;
    }

    /**
     * returns the point with the smallest <code>distanceToReference</code> from the two
     * by creating a new <code>RoutePoint</code> with the parameters if necessary.
     *
     * @param thatPoint
     * @param thatPosition
     * @param thatDistanceToReference
     * @return a <code>RoutePoint</code> with smallest <code>distanceToReference</code> from the two.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return this.distanceToReference <= thatDistanceToReference ?
                this :
                new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }

}
