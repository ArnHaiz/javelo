package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * interface representing an itinerary.
 */
public interface Route {
    /**
     * returns the index of the segment at the given <code>position</code>.
     *
     * @param position
     * @return the index of the segment at the given <code>position</code>.
     */
    int indexOfSegmentAt(double position);

    /**
     * returns the length of the itinerary in meters.
     *
     * @return the lengrh of the itinerary in meters.
     */
    double length();

    /**
     * returns a complete list of the edges of the whole itinerary.
     *
     * @return a list containing all the edges of the itinerary.
     */
    List<Edge> edges();

    /**
     * returns a list containing all the points at extremities of all the edges of the itinerary.
     *
     * @return a list of all the points at extremities of the edges of the itinerary.
     */
    List<PointCh> points();

    /**
     * returns the point at the given <code>position</code>.
     *
     * @param position
     * @return the point at the given <code>position</code>.
     */
    PointCh pointAt(double position);

    /**
     * returns the elevation at the given <code>position</code>.
     *
     * @param Position
     * @return the elevation at the given <code>position</code>.
     */
    double elevationAt(double Position);

    /**
     * returns the id of the node contained in the itinerary closest to the given <code>position</code>.
     *
     * @param position
     * @return the id of the node contained in the itinerary closest to the given <code>position</code>.
     */
    int nodeClosestTo(double position);

    /**
     * returns the <code>RoutePoint</code> closest to the given <code>point</code>.
     *
     * @param point
     * @return the <code>RoutePoint</code> closest to the given <code>point</code>.
     */
    RoutePoint pointClosestTo(PointCh point);
}
