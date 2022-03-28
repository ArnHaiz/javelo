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
 * class representing an itinerary composed of multiple itineraries.
 */
public final class MultiRoute implements Route{
    private final List<Route> segments;
    private final double[] positionList;
    private final  List<PointCh> points;
    private final double length;
    private final ArrayList<Edge> edges;

    /**
     * public constructor of the MultiRoute class
     * initializing an itinerary composed of the routes in <code>segments</code>.
     *
     * @param segments
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(segments.size() != 0);

        this.segments = List.copyOf(segments);
        positionList = new double[segments.size() + 1];

        var tempPoints = new ArrayList<PointCh>();
        tempPoints.add(segments.get(0).points().get(0));
        double tempLength = 0;
        ArrayList<Edge> tempEdges = new ArrayList<>();

        //TODO correct to add a second loop to consider segments that are multiRoutes.
        for (int i = 0; i < segments.size(); i++) {
            tempPoints.add(segments.get(i).points().get(segments.get(i).points().size() - 1));
            tempLength += segments.get(i).length();
            tempEdges.addAll(segments.get(i).edges());

            if (i == 0) {
                positionList[i] = 0;
            } else {
                positionList[i] = positionList[i - 1] + segments.get(i).length();
            }
        }

        points = List.copyOf(tempPoints);
        length = tempLength;
        edges = tempEdges;
    }

    //TODO correct method to be able to iterate on lists of mutliroutes.
    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length);
        double currentDistance = 0;
        int rightEdgeId = Math2.clamp(0, Math.abs(Arrays.binarySearch(positionList, position)) - 2, segments.size());
        return rightEdgeId + segments.get(rightEdgeId).indexOfSegmentAt(position - positionList[rightEdgeId]);
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
        int rightEdgeId = Math2.clamp(0, Math.abs(Arrays.binarySearch(positionList, position)) - 2, segments.size());
        return segments.get(rightEdgeId).pointAt(position - positionList[rightEdgeId]);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length);
        int rightEdgeId = Math2.clamp(0, Math.abs(Arrays.binarySearch(positionList, position)) - 2, segments.size());
        return segments.get(rightEdgeId).elevationAt(position - positionList[rightEdgeId]);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length);
        int rightEdgeId = Math2.clamp(0, Math.abs(Arrays.binarySearch(positionList, position)) - 2, segments.size());
        return segments.get(rightEdgeId).nodeClosestTo(position - positionList[rightEdgeId]);
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        double minDistance = Double.MAX_VALUE;

        for (Route segment : segments) {
            if (minDistance > segment.pointClosestTo(point).distanceToReference()) {
                closestPoint = segment.pointClosestTo(point);
            }
        }
        return closestPoint;
    }
}
