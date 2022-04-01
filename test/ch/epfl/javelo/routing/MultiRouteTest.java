package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiRouteTest {
    ArrayList<Edge> edgeList = new ArrayList<>();
    Route routeOne;
    Route singleRouteOne;
    Route singleRouteTwo;
    ArrayList<Route> singleRoutesOne = new ArrayList<>();
    Route multiRouteOne;
    Route singleRouteThree;
    ArrayList<Route> routes = new ArrayList<>();
    Route multiRouteTwo;
    Route routeThree;
    ArrayList<Route> testRoutes = new ArrayList<>();
    Route testRoute;
    {
        for (int i = 0; i < 7; i++) {
            edgeList.add(new Edge(i,
                    i + 1,
                    new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N + i),
                    new PointCh(SwissBounds.MIN_E + i + 1, SwissBounds.MIN_N + i + 1),
                    1,
                    Functions.constant(1)));
        }

        routeOne = new SingleRoute(edgeList.subList(0, 2));
        singleRouteOne = new SingleRoute(edgeList.subList(2, 4));
        singleRouteTwo = new SingleRoute(edgeList.subList(4, 5));
        singleRoutesOne.add(singleRouteOne);
        singleRoutesOne.add(singleRouteTwo);
        multiRouteOne = new MultiRoute(singleRoutesOne);
        singleRouteThree = new SingleRoute(edgeList.subList(5, 6));
        routes.add(multiRouteOne);
        routes.add(singleRouteThree);
        multiRouteTwo = new MultiRoute(routes);
        routeThree = new SingleRoute(edgeList.subList(6, 7));
        testRoutes.add(routeOne);
        testRoutes.add(multiRouteTwo);
        testRoutes.add(routeThree);
        testRoute = new MultiRoute(testRoutes);
    }


    @Test
    public void indexOfSegmentWorks() {
        assertEquals(0, testRoute.indexOfSegmentAt(-10));
        assertEquals(0, testRoute.indexOfSegmentAt(0));
        assertEquals(0, testRoute.indexOfSegmentAt(0.9));
        assertEquals(0, testRoute.indexOfSegmentAt(1));
        assertEquals(0, testRoute.indexOfSegmentAt(1.00001));
        assertEquals(1, testRoute.indexOfSegmentAt(4.5));
        assertEquals(2, testRoute.indexOfSegmentAt(10));
        assertEquals(2, testRoute.indexOfSegmentAt(7));
    }

    @Test
    public void lengthWorks() {
        assertEquals(7, testRoute.length());
    }

    @Test
    public void edgesWorks() {
        assertEquals(edgeList, testRoute.edges());
    }

    @Test
    public void elevationAtWorks() {
        assertEquals(edgeList.get(6).elevationAt(0.5), testRoute.elevationAt(testRoute.length() - 0.5));
    }
}
