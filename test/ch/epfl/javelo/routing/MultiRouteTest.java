package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.test.KmlPrinter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {

    // Points used to make routes
    /*private final static double E_EPFL = 2_533_040.0;
    private final static double N_EPFL = 1_152_130.5;
    PointCh epfl = new PointCh(E_EPFL, N_EPFL);

    private final static double E_RENENS = 2_534_186.0;
    private final static double N_RENENS = 1_154_244.0;
    PointCh renens = new PointCh(E_RENENS, N_RENENS);

    private final static double E_MALOMBRE = 2_500_809.0;
    private final static double N_MALOMBRE = 1_116_773.0;
    PointCh malombre = new PointCh(E_MALOMBRE, N_MALOMBRE);

    private final static double E_COURROUX =2_595_550.0;
    private final static double N_COURROUX = 1_246_620.0;
    PointCh courroux = new PointCh(E_COURROUX, N_COURROUX);

    //PointCh ouchy = new PointCh(Ch1903.e(), Ch1903.n());
    //PointCh sauvablin = new PointCh(Ch1903.e(), Ch1903.n());

    // Making the graph and loading data
    final Graph lsn = lausanne();

    // Empty list to test the constructor
    List<Route> emptyRouteList = new ArrayList<>();

    // Routes created with RouteComputer
    List<Route> routesList = new ArrayList<>();
    SingleRoute routep1 = (SingleRoute) RouteComputerTestProf.bestRoutePoint(epfl, renens);
    SingleRoute routep2 = (SingleRoute) RouteComputerTestProf.bestRoutePoint(renens, malombre);
    SingleRoute routep3 = (SingleRoute) RouteComputerTestProf.bestRoutePoint(malombre, courroux);

    List<Route> exRoutesList = new ArrayList<>();
    PointCh chateauOuchy = new PointCh(2537738.1, 1150924.3);
    PointCh tourSauvablin = new PointCh(2538620.4, 1154103.8);
    Route givenRoute = RouteComputerTestProf.bestRoutePoint(chateauOuchy, tourSauvablin);

    public MultiRoute routeMaker() {
        routesList.add(routep1);
        routesList.add(routep2);
        routesList.add(routep3);
        return new MultiRoute(routesList);
    }

    public MultiRoute givenRouteMaker() {
        exRoutesList.add(givenRoute);
        return new MultiRoute(exRoutesList);
    }

    // NOT the 3.5 example route!!! (is 2.1)
    @Test
    void algoGivesCorrectPath() throws IOException {
        try {
            MultiRoute mr = givenRouteMaker();
            KmlPrinter.write("route_prof.kml", mr);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public static Graph lausanne(){
        Graph graph = null;
        Path basePath = Path.of("ch_west");
        try {
            graph = Graph.loadFrom(basePath);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return graph;
    }

    @Test
    void segmentListCantBeEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            MultiRoute empty = new MultiRoute(emptyRouteList);
        });
    }

    @Test
    void multiRouteSegmentsAreCopiedToEnsureImmutability() {
        var immutableEdges = verticalEdges(10);
        var mutableEdges = new ArrayList<>(immutableEdges);
        var sr = new SingleRoute(mutableEdges);
        var routes = new ArrayList<Route>();
        routes.add(sr);
        var route = new MultiRoute(routes);
        mutableEdges.clear();
        assertEquals(immutableEdges, route.edges());
    }

    @Test
    void indexOfSegmentAtWorksOnGivenData() {
        MultiRoute mr = routeMaker();
        var expected1 = 0;
        var actual1 = mr.indexOfSegmentAt(0);
        assertEquals(expected1, actual1);

        var expected2 = 1;
        var actual2 = mr.indexOfSegmentAt(routep1.length()+1);
        assertEquals(expected2, actual2);

        double shiftSeg1 = newRandom().nextDouble(1, routep2.length()-1);
        System.out.println("Shift on 2nd route: " + shiftSeg1);
        var expected22 = 1;
        var actual22 = mr.indexOfSegmentAt(routep1.length()+shiftSeg1);
        assertEquals(expected22, actual22);

        var expected3 = 2;
        var actual3 = mr.indexOfSegmentAt(mr.length()-1);
        assertEquals(expected3, actual3);

        double shiftSeg2 = newRandom().nextDouble(1, routep3.length()-1);
        System.out.println("Shift on 3rd route: " + shiftSeg2);
        var expected33 = 2;
        var actual33 = mr.indexOfSegmentAt(routep1.length() + routep2.length() + shiftSeg2);
        assertEquals(expected33, actual33);
    }

    @Test
    void lengthWorksOnGivenData() {
        MultiRoute mr = routeMaker();
        var expected = routep1.length() + routep2.length() + routep3.length();
        var actual = mr.length();
        assertEquals(expected, actual);
    }

    @Test
    void edgesWorksOnGivenData() {
        MultiRoute mr = routeMaker();
        var expected = new ArrayList<>();
        expected.addAll(routep1.edges());
        expected.addAll(routep2.edges());
        expected.addAll(routep3.edges());
        var actual = mr.edges();
        assertEquals(expected, actual);
    }

    @Test
    void pointsWorksOnGivenData() {
        MultiRoute mr = routeMaker();
        // tout doux: faire
    }

    @Test
    void doublonsAreRemoved() {
        List<Route> rl = new ArrayList<>();
        rl.add(routep1);
        rl.add(routep1);
        MultiRoute mr = new MultiRoute(rl);
        var expected = routep1.points().size();
        var actual = mr.points().size();
        assertEquals(expected, actual);

        /* LORIC ADRIAN
         * Vous aussi celui-là échoue? Je crois que c'est normal vu que notre itinéraire passe
         * plusieurs fois aux mêmes endroits mais jsuis pas sur a 100%
         */
        /*MultiRoute fmr = routeMaker();
        System.out.println(fmr.points().size());
        System.out.println(routep1.points().size());
        System.out.println(routep2.points().size());
        System.out.println(routep3.points().size());
        var expectedf = routep1.points().size()
                        + routep2.points().size() - 1
                        + routep3.points().size() - 1;
        var actualf = mr.points().size();
        assertEquals(expectedf, actualf); // <----- celui-là
    }

    @Test
    void pointAtWorksOnKnownRoute() throws IOException {
        MultiRoute mr = routeMaker();
        KmlPrinter.write("tropcoolInverseDansLeCode.kml", mr);

        double delta = 100;

        boolean print = false;
        if (print) {
            for (Route route : routesList) {
                System.out.println(route.points());
            }
        }

        var expected1 = epfl;
        var actual1 = mr.pointAt(0);
        assertEquals(expected1.n(), actual1.n(), 5);
        assertEquals(expected1.e(), actual1.e(), 5);

        var expected = courroux;
        var actual = mr.pointAt(mr.length());
        assertEquals(expected.n(), actual.n(), 75);
        assertEquals(expected.e(), actual.e(), 160);

        assertEquals(expected1.n(), actual1.n(), delta);
        assertEquals(expected1.e(), actual1.e(), delta);

        var expected2 = renens;
        var actual2 = mr.pointAt(routep1.length());
        assertEquals(expected2.e(), actual2.e(), 25);
        assertEquals(expected2.n(), actual2.n(), 25);

        // https://map.geo.admin.ch/?topic=ech&bgLayer=ch.swisstopo.pixelkarte-grau&lang=fr&E=2532935&N=1152634&zoom=11.134215715337904&crosshair=marker
        double fromEpflToSubway = 593.8;
        var expected3 = new PointCh(2532934.7, 1152634.0);
        var actual3 = mr.pointAt(fromEpflToSubway);
        assertEquals(expected3.e(), actual3.e(), 5);
        assertEquals(expected3.n(), actual3.n(), 5);
    }

    @Test
    void constructorInitializesCorrectArray() {
        MultiRoute mr = routeMaker();

        var expected0 = 0;
        var actual0 = mr.length() - routep3.length() - routep2.length() - routep1.length();

        var expected1 = routep1.length();
        var actual1 = mr.length() - routep3.length() - routep2.length();

        var expected2 = routep1.length() + routep2.length();
        var actual2 = mr.length() - routep3.length();

        var expected3 = routep3.length() + routep2.length() + routep1.length();
        var actual3 = mr.length();

        assertEquals(expected0, actual0);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
    }

    @Test
    void elevationAtWorksOnKnownData() {
        MultiRoute mr = routeMaker();
        var expectedEpfl = 395.8;

        ElevationProfile elevationProfile = elevationProfile(mr, 1);
        var actualEpfl = elevationProfile.elevationAt(0);
        assertEquals(expectedEpfl, actualEpfl, 1);
        // https://map.geo.admin.ch/?topic=ech&bgLayer=ch.swisstopo.pixelkarte-farbe&layers_visibility=false,false,false,false,false&layers_timestamp=18641231,,,,&lang=en&E=2534187&N=1154247&zoom=12.081753240233422&layers=ch.swisstopo.zeitreihen,ch.bfs.gebaeude_wohnungs_register,ch.bav.haltestellen-oev,ch.swisstopo.swisstlm3d-wanderwege,ch.astra.wanderland-sperrungen_umleitungen&layers_opacity=1,1,1,0.8,0.8&crosshair=marker
        var expectedRenens = 413;
        var actualRenens = mr.elevationAt(routep1.length());
        assertEquals(expectedRenens, actualRenens, 2);
    }

    @Test
    void nodeClosestToWorksOnKnownData() {
        MultiRoute mr = routeMaker();
        var expected = lsn.nodeClosestTo(epfl, 10);
        var actual = mr.nodeClosestTo(0);
        System.out.println("point at expected: " + lsn.nodePoint(expected));
        System.out.println("point at actual: " + lsn.nodePoint(actual));
        assertEquals(expected, actual);
    }

    @Test
    void pointClosestToWorksOnGivenData() {
        MultiRoute mr = routeMaker();
    }

    @Test
    void indexOfSegmentMultiRouteWorks() {
        List<Route> mr1l = new ArrayList<>();
        mr1l.add(routep1);
        mr1l.add(routep2);
        List<Route> mr2l = new ArrayList<>();
        mr2l.add(routep3);
        MultiRoute mr1 = new MultiRoute(mr1l);
        MultiRoute mr2 = new MultiRoute(mr2l);
        List<Route> mrl = new ArrayList<>();
        mrl.add(mr1);
        mrl.add(mr2);
        MultiRoute mr = new MultiRoute(mrl);

        //System.out.println(mr.indexOfSegmentAt(routep1.length() + 10));

        List<Route> mrtriplel = new ArrayList<>();
        mrtriplel.add(routep1);
        mrtriplel.add(routep2);
        mrtriplel.add(routep3);
        MultiRoute mrtriple = new MultiRoute(mrtriplel);
        List<Route> mrtripleinl = new ArrayList<>();
        mrtripleinl.add(mrtriple);
        MultiRoute mrtriplein = new MultiRoute(mrtripleinl);
        System.out.println(mrtriplein.indexOfSegmentAt(0));//
        System.out.println(mrtriplein.indexOfSegmentAt(routep1.length() + 1));
        System.out.println(mrtriplein.indexOfSegmentAt(routep1.length() + routep2.length() + 1));
        System.out.println(mrtriplein.indexOfSegmentAt(mrtriplein.length()));

        List<Route> mrtripleindoublel = new ArrayList<>();
        mrtripleindoublel.add(mrtriplein);
        mrtripleindoublel.add(mrtriplein);
        MultiRoute mrtripleindouble = new MultiRoute(mrtripleindoublel);

        var expected0 = 0;
        var actual0 = mrtriplein.indexOfSegmentAt(0);
        assertEquals(expected0, actual0);

        var expected1 = 1;
        var actual1 = mrtriplein.indexOfSegmentAt(routep1.length() + 1);
        assertEquals(expected1, actual1);

        var expected2 = 2;
        var actual2 = mrtriplein.indexOfSegmentAt(routep1.length() + routep2.length() + 1);
        assertEquals(expected2, actual2);

        // Double multiroute
        var expected3 = 3;
        var actual3 = mrtripleindouble.indexOfSegmentAt(mrtriplein.length()+1);
        assertEquals(expected3, actual3);

        var expected4 = 3.5;
        var actual4 = mrtripleindouble.indexOfSegmentAt(mrtriplein.length() + routep1.length());
        assertEquals(expected4, actual4, 0.5);

        var expect5 = 5;
        var actual5 = mrtripleindouble.indexOfSegmentAt(mrtripleindouble.length());
        assertEquals(expect5, actual5);
    }




    // Teacher's methods
    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    private static List<Edge> verticalEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
            var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
            edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, x -> Double.NaN));
        }
        return Collections.unmodifiableList(edges);
    }*/
}