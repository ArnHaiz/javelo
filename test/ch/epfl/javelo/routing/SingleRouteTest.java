package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleRouteTest {
    Edge edge1 = new Edge(0,
            1,
            new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
            new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1), 1, Functions.constant(1));
    Edge edge2 = new Edge(1,
            2,
            new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1),
            new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N + 2), 1, Functions.constant(1));
    Edge edge3 = new Edge(2,
            3,
            new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N + 2),
            new PointCh(SwissBounds.MIN_E + 3, SwissBounds.MIN_N + 3), 1, Functions.constant(1));
    List<Edge> edgeListTest = new ArrayList<>(List.of(edge1, edge2, edge3));
    SingleRoute singleRouteTest = new SingleRoute(edgeListTest);

    @Test
    public void indexOfSegmentWorksForAnyInput() {
        assertEquals(0, singleRouteTest.indexOfSegmentAt(1000));
        assertEquals(0, singleRouteTest.indexOfSegmentAt(0));
        assertEquals(0, singleRouteTest.indexOfSegmentAt(-100));
        assertEquals(0, singleRouteTest.indexOfSegmentAt(Double.MAX_VALUE));
        assertEquals(0, singleRouteTest.indexOfSegmentAt(Double.MIN_VALUE));
    }

    @Test
    public void lengthWorksWithSpecificValues() {
        assertEquals(3, singleRouteTest.length());
    }

    @Test
    public void edgesWorksOnSpecificList() {
        for (int i = 0; i < 3; i++) {
            assertEquals(edgeListTest.get(i), singleRouteTest.edges().get(i));
        }
    }

    @Test
    public void pointsWorksOnSpecificValues() {
        for (int i = 0; i < 4; i++) {
            assertEquals(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N + i), singleRouteTest.points().get(i));
        }
    }

    @Test
    public void pointAtWorksOnSpecificValues() {
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRouteTest.pointAt(-100));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), singleRouteTest.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1), singleRouteTest.pointAt(1));
        assertEquals(new PointCh(SwissBounds.MIN_E + 1.5, SwissBounds.MIN_N + 1.5), singleRouteTest.pointAt(1.5));
        assertEquals(new PointCh(SwissBounds.MIN_E + 3, SwissBounds.MIN_N + 3), singleRouteTest.pointAt(10));
        assertEquals(new PointCh(SwissBounds.MIN_E + 1.75, SwissBounds.MIN_N + 1.75), singleRouteTest.pointAt(1.75));
    }

    @Test
    public void elevationAtWorksOnSpecificValues() {

    }

    @Test
    public void nodeClosestToWorksOnSpecificValues() {
        assertEquals(0, singleRouteTest.nodeClosestTo(-100));
        assertEquals(0, singleRouteTest.nodeClosestTo(0));
        assertEquals(1, singleRouteTest.nodeClosestTo(1));
        assertEquals(2, singleRouteTest.nodeClosestTo(1.5));
        assertEquals(3, singleRouteTest.nodeClosestTo(10));
        assertEquals(2, singleRouteTest.nodeClosestTo(1.75));
    }

    @Test
    public void pointClosestToWorksOnSpecificValues() {
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                singleRouteTest.pointClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N)).point());
        assertEquals(new PointCh(SwissBounds.MIN_E + 3, SwissBounds.MIN_N + 3),
                singleRouteTest.pointClosestTo(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N)).point());
        assertEquals(new PointCh(SwissBounds.MIN_E + .707106781, SwissBounds.MIN_N + .7071067812),
                singleRouteTest.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N)).point());
        assertEquals(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1),
                singleRouteTest.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1)).point());
    }
}
