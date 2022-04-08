package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * class representing a single route composed of a list of edges.
 */
public final class SingleRoute implements Route {
    private final List<Edge> edges;
    private final double[] positionList;
    private final List<PointCh> points;
    private  final double length;

    /**
     * public constructor of the <code>SingleRoute</code> class.
     *
     * @param edges a list of type edges
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(edges.size() != 0);

        //TODO edges.stream().mapToDouble(Edge::length).sum();

        this.edges = List.copyOf(edges);
        positionList = new double[edges().size()];

        var tempPoints = new ArrayList<PointCh>();
        tempPoints.add(edges.get(0).fromPoint());
        double tempLength = 0;

        for (int i = 0; i < edges().size(); ++i) {
            tempPoints.add(edges.get(i).toPoint());
            tempLength += edges.get(i).length();

            if (i == 0)
                positionList[i] = 0;
            else {
                positionList[i] = positionList[i - 1] + edges.get(i-1).length();
            }
        }

        points = List.copyOf(tempPoints);
        length = tempLength;
    }

    private int indexOf(double position) {
        int index = Arrays.binarySearch(positionList, position);
        if (index >= 0) {
            return index;
        } else {
            index = Math.abs(index) - 2;
            return index == edges.size() ? index - 1 : index;
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        return length;
    }

    @Override
    public List<Edge> edges() {
        return edges;
    }

    @Override
    public List<PointCh> points() {
        return points;
    }

    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length);
        int rightEdgeId = indexOf(position);

        return edges.get(rightEdgeId).pointAt(position - positionList[rightEdgeId]);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length);
        int rightEdgeId = indexOf(position);

        return edges.get(rightEdgeId).elevationAt(position - positionList[rightEdgeId]);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length);
        int rightEdgeId = indexOf(position);
        Edge edge = edges.get(rightEdgeId);

        return position - positionList[rightEdgeId] <
                edge.length() / 2.0 ?
                edge.fromNodeId() :
                edge.toNodeId();
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        PointCh closestPoint;
        double minDistance = Double.MAX_VALUE;
        double tempDistance = 0;
        double totalDistance = 0;
        PointCh thisPoint = null;

        for (Edge edge : edges) {
            closestPoint = edge.pointAt(Math2.clamp(0, edge.positionClosestTo(point), edge.length()));
            double thisDistance = closestPoint.distanceTo(point);

            if (minDistance > thisDistance) {
                minDistance = thisDistance;
                thisPoint = closestPoint;
                totalDistance += tempDistance + edge.fromPoint().distanceTo(closestPoint);
                tempDistance = closestPoint.distanceTo(edge.toPoint());
            } else {
                tempDistance += edge.length();
            }
        }

        return new RoutePoint(thisPoint, totalDistance, minDistance);
    }
}
