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
 * <p>
 * class representing an itinerary composed of multiple itineraries.
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double[] positionList;
    private final List<PointCh> points;
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
        var tempPoints = new ArrayList<PointCh>();
        double tempLength = 0;
        ArrayList<Edge> tempEdges = new ArrayList<>();

        for (Route tempSegment : segments) {
            tempPoints.addAll(tempSegment.points());
            tempLength += tempSegment.length();
            tempEdges.addAll(tempSegment.edges());
        }

        positionList = new double[segments.size() + 1];
        positionList[0] = 0;

        for (int i = 1; i < segments.size(); i++) {
            positionList[i] = positionList[i - 1] + segments.get(i).length();
        }

        points = List.copyOf(tempPoints);
        length = tempLength;
        edges = tempEdges;
    }

    private int indexOf(double position) {
        int index = Arrays.binarySearch(positionList, position);
        if (index >= 0) {
            return index;
        } else {
            index = Math.abs(index) - 2;
            return index == segments.size() ? --index : index;
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length);
        return indexOf(position);
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
        return segments.get(rightEdgeId).pointAt(position - positionList[rightEdgeId]);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length);
        int rightEdgeId = indexOf(position);
        return segments.get(rightEdgeId).elevationAt(position - positionList[rightEdgeId]);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length);
        int rightEdgeId = indexOf(position);
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
