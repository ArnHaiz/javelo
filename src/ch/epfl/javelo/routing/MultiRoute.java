package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * class representing an itinerary composed of multiple itineraries.
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final List<Edge> edges;
    private final List<PointCh> points;
    private final double[] positionList;
    private final double length;

    /**
     * public constructor of the MultiRoute class
     * initializing an itinerary composed of the routes in <code>segments</code>.
     *
     * @param segments a list of routes
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(segments.size() != 0);

        this.segments = List.copyOf(segments);
        var tempPoints = new ArrayList<PointCh>();
        double tempLength = 0;
        var tempEdges = new ArrayList<Edge>();

        tempPoints.add(segments.get(0).points().get(0));

        for (Route route : this.segments) {
            tempPoints.addAll(route.points().subList(1, route.points().size()));
            tempLength += route.length();
            tempEdges.addAll(route.edges());
        }

        positionList = new double[segments.size() + 1];
        positionList[0] = 0;

        for (int i = 1; i < segments.size(); i++) {
            positionList[i] = positionList[i - 1] + segments.get(i).length();
        }

        points = List.copyOf(tempPoints);
        length = tempLength;
        edges = List.copyOf(tempEdges);
    }

    private int indexOf(double position) {
        int index = Arrays.binarySearch(positionList, position);
        if (index >= 0) {
            return index;
        } else {
            index = Math.abs(index) - 2;
            return index == segments.size() ? index - 1 : index;
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length);
        int index = 0;

        for (Route route : segments) {
            if (position <= 0) break;
            index += route.indexOfSegmentAt(position) + 1;
            //index += Integer.max(route.indexOfSegmentAt(position), position < route.length() ? 0 : 1) ;
            position -= route.length();
        }

        return index - 1;
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
        double atLength = 0;

        for (Route route : segments) {
            //closestPoint = closestPoint.min(route.pointClosestTo(point));
            if (closestPoint.distanceToReference() >= route.pointClosestTo(point).distanceToReference()) {
                closestPoint = route.pointClosestTo(point).withPositionShiftedBy(atLength);
            }
            atLength += route.length();
        }
        return closestPoint;
        //TODO yes
        /* return segments.stream()
                .map(r -> r.pointClosestTo(point))
                .min(Comparator.comparingDouble(RoutePoint::distanceToReference))
                .orElse(RoutePoint.NONE);
    */
    }
}
