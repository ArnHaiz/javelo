package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * record class representing a single edge of an itinerary.
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     * returns an edge with the specified values.
     *
     * @param graph
     * @param edgeId
     * @param fromNodeId
     * @param toNodeId
     * @return an edge with the specified values.
     */
    /*public Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return null;
    }*/
}
