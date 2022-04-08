package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * record class representing a single edge of an itinerary.
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * returns an edge with the specified values.
     *
     * @param graph      the graph of the edge
     * @param edgeId     the id of the edge
     * @param fromNodeId the id of the starting node of the edge
     * @param toNodeId   the id of the ending node of the edge
     * @return an edge with the specified values.
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(
                fromNodeId, toNodeId,
                graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * returns the <code>position</code> on the edge closest to the given point.
     *
     * @param point the reference point
     * @return the <code>position</code> on the edge closest to the given point.
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(
                fromPoint.e(), fromPoint.n(),
                toPoint.e(), toPoint.n(),
                point.e(), point.n());
    }

    /**
     * returns the point at position <code>position</code> on the edge.
     *
     * @param position the position at which to look for the point
     * @return the point at position <code>position</code> on the edge.
     */
    public PointCh pointAt(double position) {
        return new PointCh(
                Math2.interpolate(fromPoint.e(), toPoint.e(), position / length),
                Math2.interpolate(fromPoint.n(), toPoint.n(), position / length));
    }

    /**
     * returns the elevation at position <code>position</code> on the edge.
     *
     * @param position the position at which to look for the elevation
     * @return the elevation at position <code>position</code> on the edge.
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
