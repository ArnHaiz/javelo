package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class SingleRoute implements Route {
    private final List<Edge> edges;

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(edges.size() != 0);
        this.edges = edges;
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        //TODO edges.stream().mapToDouble(Edge::length).sum();
        double sum = 0;
        for (Edge edge : edges) {
            sum += edge.length();
        }
        return sum;
    }

    @Override
    public List<Edge> edges() {
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for (Edge edge : edges) {
            points.add(edge.fromPoint());
            points.add(edge.toPoint());
        }
        return points;
    }

    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        return null;
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        double atLength = 0;
        int i = 0;
        while (atLength < position) {
            atLength += edges.get(i).length();
            ++i;
        }
        return edges.get(i).elevationAt(atLength - position);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        double atLength = 0;
        int i = 0;
        while (atLength < position) {
            atLength += edges.get(i).length();
            ++i;
        }
        return atLength - position < edges.get(i).length() ? edges.get(i).fromNodeId() : edges.get(i).toNodeId();
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
