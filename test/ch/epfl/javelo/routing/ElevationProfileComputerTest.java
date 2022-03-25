/*
package ch.epfl.my;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.RepeatedTest;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static ch.epfl.my.GraphTest.lausanne;
import static ch.epfl.my.TestUtile.*;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {


    @RepeatedTest(10)
    void elevationProfileNormal() {

        final int nombreSamples = 500;
        Route route;

        List<Edge> edges = new ArrayList<>();
        //Edge[] edges = new Edge[2];

        Graph graph = lausanne();


        //choisi
        PointCh pointChChoisi = new PointCh(2_530_000, 1_155_000);
        //random
        PointCh pointChAleatoire = new PointCh(eLaus(),nLaus());


        PointCh pointChDepart = pointChAleatoire;
        int fromNodeId = graph.nodeClosestTo(pointChDepart, 1000);

        if(fromNodeId>0) {

            int arreteSortantes = graph.nodeOutDegree(fromNodeId);
            if (arreteSortantes > 0) {

                System.out.println("noeud trouvé");
                int toEdgeId = graph.nodeOutEdgeId(fromNodeId, r(0,arreteSortantes-1));

                int toNodeId = graph.edgeTargetNodeId(toEdgeId);

                PointCh pointChArrive = graph.nodePoint(toNodeId);

                double length = pointChArrive.distanceTo(pointChDepart);

                float[] samples = new float[]{1,2, Float.NaN,Float.NaN,5, 6};
                DoubleUnaryOperator profile = Functions.sampled(samples, length);

                float[] samples2 = new float[]{6, 7, 8, 9, 10, 11, 12};
                DoubleUnaryOperator profile2 = Functions.sampled(samples2, length);

                float[] samples3 = new float[]{12, 8, 4, 2, 0, 15, 42};
                DoubleUnaryOperator profile3 = Functions.sampled(samples3, length);

                System.out.println("length " + length);
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile));
                edges.add(new Edge(toNodeId, fromNodeId, pointChArrive, pointChDepart, length, profile2));
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile3));

                route = new SingleRoute(edges);

                ElevationProfile elevationProfile = elevationProfile(route, (3*length/(nombreSamples)));

                assertFalse(hasNull(elevationProfile, 3*length));

            }else {
                System.out.println("pas d'arrête sortante trouvée");
            }

        }else {
            System.out.println("pas de noeud trouvé");
        }
    }

    @RepeatedTest(100)
    void elevationProfileNormalaleatoire() {

        final int nombreSamples = 500;
        Route route;

        List<Edge> edges = new ArrayList<>();
        //Edge[] edges = new Edge[2];

        Graph graph = lausanne();


        //choisi
        PointCh pointChChoisi = new PointCh(2_530_000, 1_155_000);
        //random
        PointCh pointChAleatoire = new PointCh(eLaus(),nLaus());


        PointCh pointChDepart = pointChAleatoire;
        int fromNodeId = graph.nodeClosestTo(pointChDepart, 1000);

        if(fromNodeId>0) {

            int arreteSortantes = graph.nodeOutDegree(fromNodeId);
            if (arreteSortantes > 0) {

                System.out.println("noeud trouvé");
                int toEdgeId = graph.nodeOutEdgeId(fromNodeId, r(0,arreteSortantes-1));

                int toNodeId = graph.edgeTargetNodeId(toEdgeId);

                PointCh pointChArrive = graph.nodePoint(toNodeId);

                double length = pointChArrive.distanceTo(pointChDepart);

                DoubleUnaryOperator profile = graph.edgeProfile(toEdgeId);


                System.out.println("length " + length);
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile));
                route = new SingleRoute(edges);

                ElevationProfile elevationProfile = elevationProfile(route, (length/(nombreSamples)));

                assertFalse(hasNull(elevationProfile, length));

            }else {
                System.out.println("pas d'arrête sortante trouvée");
            }

        }else {
            System.out.println("pas de noeud trouvé");
        }
    }


    @Test
    void elevationNullDebut(){

        final int nombreSamples = 500;
        final double deltaPrecis = 1e-3;
        final double deltaGlobal = 1;


        Route route;

        List<Edge> edges = new ArrayList<>();
        //Edge[] edges = new Edge[2];

        Graph graph = lausanne();


        //choisi
        PointCh pointChChoisi = new PointCh(2_530_000, 1_155_000);
        //random
        PointCh pointChAleatoire = new PointCh(eLaus(),nLaus());


        PointCh pointChDepart = pointChChoisi;
        int fromNodeId = graph.nodeClosestTo(pointChDepart, 1000);

        if(fromNodeId>0) {

            int arreteSortantes = graph.nodeOutDegree(fromNodeId);
            if (arreteSortantes > 0) {

                System.out.println("noeud trouvé");
                int toEdgeId = graph.nodeOutEdgeId(fromNodeId, r(0,arreteSortantes-1));

                int toNodeId = graph.edgeTargetNodeId(toEdgeId);

                PointCh pointChArrive = graph.nodePoint(toNodeId);

                double length = pointChArrive.distanceTo(pointChDepart);

                float[] samples = new float[]{Float.NaN, Float.NaN, Float.NaN,Float.NaN,5, 6};
                DoubleUnaryOperator profile = Functions.sampled(samples, length);

                float[] samples2 = new float[]{6, 7, 8, 9, 10, 11, 12};
                DoubleUnaryOperator profile2 = Functions.sampled(samples2, length);

                float[] samples3 = new float[]{12, 8, 4, 2, 3, 15, 23};
                DoubleUnaryOperator profile3 = Functions.sampled(samples3, length);

                System.out.println("length " + length);
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile));
                edges.add(new Edge(toNodeId, fromNodeId, pointChArrive, pointChDepart, length, profile2));
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile3));

                route = new SingleRoute(edges);

                ElevationProfile elevationProfile= elevationProfile(route, (3*length/(nombreSamples)));

                assertEquals(23,elevationProfile.maxElevation(), deltaPrecis);
                assertEquals(2,elevationProfile.minElevation(), deltaGlobal);
                assertEquals(length*3,elevationProfile.length(),deltaPrecis);
                assertEquals(7 + 21, elevationProfile.totalAscent(), deltaGlobal);
                assertEquals(12 - 3, elevationProfile.totalDescent(), deltaGlobal);


                assertFalse(hasNull(elevationProfile, 3*length));

            }else {
                System.out.println("pas d'arrête sortante trouvée");
            }

        }else {
            System.out.println("pas de noeud trouvé");
        }
    }


    @Test
    void elevationNullMilieu(){

        final int nombreSamples = 500;
        final double deltaPrecis = 1e-3;
        final double deltaGlobal = 1;


        Route route;

        List<Edge> edges = new ArrayList<>();
        //Edge[] edges = new Edge[2];

        Graph graph = lausanne();


        //choisi
        PointCh pointChChoisi = new PointCh(2_530_000, 1_155_000);
        //random
        PointCh pointChAleatoire = new PointCh(eLaus(),nLaus());


        PointCh pointChDepart = pointChChoisi;
        int fromNodeId = graph.nodeClosestTo(pointChDepart, 1000);

        if(fromNodeId>0) {

            int arreteSortantes = graph.nodeOutDegree(fromNodeId);
            if (arreteSortantes > 0) {

                System.out.println("noeud trouvé");
                int toEdgeId = graph.nodeOutEdgeId(fromNodeId, r(0,arreteSortantes-1));

                int toNodeId = graph.edgeTargetNodeId(toEdgeId);

                PointCh pointChArrive = graph.nodePoint(toNodeId);

                double length = pointChArrive.distanceTo(pointChDepart);

                float[] samples = new float[]{Float.NaN,5, 6};
                DoubleUnaryOperator profile = Functions.sampled(samples, length);

                float[] samples2 = new float[]{6, Float.NaN, 8, Float.NaN, Float.NaN, Float.NaN, Float.NaN};
                DoubleUnaryOperator profile2 = Functions.sampled(samples2, length);

                float[] samples3 = new float[]{12, 8, 4, Float.NaN, 0, 15, Float.NaN};
                DoubleUnaryOperator profile3 = Functions.sampled(samples3, length);

                System.out.println("length " + length);
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile));
                edges.add(new Edge(toNodeId, fromNodeId, pointChArrive, pointChDepart, length, profile2));
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile3));

                route = new SingleRoute(edges);

                ElevationProfile elevationProfile= elevationProfile(route, (3*length/(nombreSamples)));



                assertFalse(hasNull(elevationProfile, 3*length));

            }else {
                System.out.println("pas d'arrête sortante trouvée");
            }

        }else {
            System.out.println("pas de noeud trouvé");
        }
    }



    @Test
    void elevationNullDebut2(){

        final int nombreSamples = 500;
        final double deltaPrecis = 1e-3;
        final double deltaGlobal = 1;


        Route route;

        List<Edge> edges = new ArrayList<>();
        //Edge[] edges = new Edge[2];

        Graph graph = lausanne();


        //choisi
        PointCh pointChChoisi = new PointCh(2_530_000, 1_155_000);
        //random
        PointCh pointChAleatoire = new PointCh(eLaus(),nLaus());


        PointCh pointChDepart = pointChChoisi;
        int fromNodeId = graph.nodeClosestTo(pointChDepart, 1000);

        if(fromNodeId>0) {

            int arreteSortantes = graph.nodeOutDegree(fromNodeId);
            if (arreteSortantes > 0) {

                System.out.println("noeud trouvé");
                int toEdgeId = graph.nodeOutEdgeId(fromNodeId, r(0,arreteSortantes-1));

                int toNodeId = graph.edgeTargetNodeId(toEdgeId);

                PointCh pointChArrive = graph.nodePoint(toNodeId);

                double length = pointChArrive.distanceTo(pointChDepart);

                float[] samples = new float[]{Float.NaN,5, 6};
                DoubleUnaryOperator profile = Functions.sampled(samples, length);

                float[] samples2 = new float[]{6, 7, 8, 9, 10, 11, 12};
                DoubleUnaryOperator profile2 = Functions.sampled(samples2, length);

                float[] samples3 = new float[]{12, 8, 4, 2, 2, 15, 23};
                DoubleUnaryOperator profile3 = Functions.sampled(samples3, length);

                System.out.println("length " + length);
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile));
                edges.add(new Edge(toNodeId, fromNodeId, pointChArrive, pointChDepart, length, profile2));
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile3));

                route = new SingleRoute(edges);

                ElevationProfile elevationProfile= elevationProfile(route, (3*length/(nombreSamples)));

                assertEquals(23,elevationProfile.maxElevation(), deltaPrecis);
                assertEquals(2,elevationProfile.minElevation(), deltaPrecis);
                assertEquals(length*3,elevationProfile.length(),deltaPrecis);
                assertEquals(7 + 21, elevationProfile.totalAscent(), deltaGlobal);
                assertEquals(10, elevationProfile.totalDescent(), deltaGlobal);


                assertFalse(hasNull(elevationProfile, 3*length));

            }else {
                System.out.println("pas d'arrête sortante trouvée");
            }

        }else {
            System.out.println("pas de noeud trouvé");
        }
    }



    @Test
    void elevationNullFin(){

        final int nombreSamples = 5000;
        final double deltaPrecis = 1e-3;
        final double deltaGlobal = 1;


        Route route;

        List<Edge> edges = new ArrayList<>();
        //Edge[] edges = new Edge[2];

        Graph graph = lausanne();


        //choisi
        PointCh pointChChoisi = new PointCh(2_530_000, 1_155_000);
        //random
        PointCh pointChAleatoire = new PointCh(eLaus(),nLaus());


        PointCh pointChDepart = pointChChoisi;
        int fromNodeId = graph.nodeClosestTo(pointChDepart, 1000);

        if(fromNodeId>0) {

            int arreteSortantes = graph.nodeOutDegree(fromNodeId);
            if (arreteSortantes > 0) {

                System.out.println("noeud trouvé");
                int toEdgeId = graph.nodeOutEdgeId(fromNodeId, r(0,arreteSortantes-1));

                int toNodeId = graph.edgeTargetNodeId(toEdgeId);

                PointCh pointChArrive = graph.nodePoint(toNodeId);

                double length = pointChArrive.distanceTo(pointChDepart);

                float[] samples = new float[]{22, 10, 5,12,5, 6};
                DoubleUnaryOperator profile = Functions.sampled(samples, length);

                float[] samples2 = new float[]{6, 7, 8, 9, 10, 11, 12};
                DoubleUnaryOperator profile2 = Functions.sampled(samples2, length);

                float[] samples3 = new float[]{12, 8, 4, 2, -10, 15, Float.NaN};
                DoubleUnaryOperator profile3 = Functions.sampled(samples3, length);

                System.out.println("length " + length);
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile));
                edges.add(new Edge(toNodeId, fromNodeId, pointChArrive, pointChDepart, length, profile2));
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile3));

                route = new SingleRoute(edges);

                ElevationProfile elevationProfile= elevationProfile(route, (3*length/(nombreSamples)));

                assertEquals(22,elevationProfile.maxElevation(), deltaPrecis);
                assertEquals(-10,elevationProfile.minElevation(), deltaGlobal);
                assertEquals(length*3,elevationProfile.length(),deltaPrecis);
                assertEquals(7 + 7 + 25, elevationProfile.totalAscent(), deltaGlobal);
                assertEquals(17 + 7 +22, elevationProfile.totalDescent(), deltaGlobal);


                assertFalse(hasNull(elevationProfile, 3*length));

            }else {
                System.out.println("pas d'arrête sortante trouvée");
            }

        }else {
            System.out.println("pas de noeud trouvé");
        }
    }
    @Test
    void elevationNullFin2(){

        final int nombreSamples = 500;
        final double deltaPrecis = 1e-3;
        final double deltaGlobal = 1;


        Route route;

        List<Edge> edges = new ArrayList<>();
        //Edge[] edges = new Edge[2];

        Graph graph = lausanne();


        //choisi
        PointCh pointChChoisi = new PointCh(2_530_000, 1_155_000);
        //random
        PointCh pointChAleatoire = new PointCh(eLaus(),nLaus());


        PointCh pointChDepart = pointChChoisi;
        int fromNodeId = graph.nodeClosestTo(pointChDepart, 1000);

        if(fromNodeId>0) {

            int arreteSortantes = graph.nodeOutDegree(fromNodeId);
            if (arreteSortantes > 0) {

                System.out.println("noeud trouvé");
                int toEdgeId = graph.nodeOutEdgeId(fromNodeId, r(0,arreteSortantes-1));

                int toNodeId = graph.edgeTargetNodeId(toEdgeId);

                PointCh pointChArrive = graph.nodePoint(toNodeId);

                double length = pointChArrive.distanceTo(pointChDepart);

                float[] samples = new float[]{22, 10, 5,12,5, 6};
                DoubleUnaryOperator profile = Functions.sampled(samples, length);

                float[] samples2 = new float[]{6, 7, 8, 9, 10, 11, 12};
                DoubleUnaryOperator profile2 = Functions.sampled(samples2, length);

                float[] samples3 = new float[]{12, 8, 4, 2, Float.NaN, Float.NaN, Float.NaN};
                DoubleUnaryOperator profile3 = Functions.sampled(samples3, length);

                System.out.println("length " + length);
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile));
                edges.add(new Edge(toNodeId, fromNodeId, pointChArrive, pointChDepart, length, profile2));
                edges.add(new Edge(fromNodeId, toNodeId, pointChDepart, pointChArrive, length, profile3));

                route = new SingleRoute(edges);

                ElevationProfile elevationProfile= elevationProfile(route, (3*length/(nombreSamples)));

                assertEquals(22,elevationProfile.maxElevation(), deltaPrecis);
                assertEquals(2,elevationProfile.minElevation(), deltaGlobal);
                assertEquals(length*3,elevationProfile.length(),deltaPrecis);
                assertEquals(7 + 7, elevationProfile.totalAscent(), deltaGlobal);
                assertEquals(17 + 7 + 10, elevationProfile.totalDescent(), deltaGlobal);


                assertFalse(hasNull(elevationProfile, 3*length));



            }else {
                System.out.println("pas d'arrête sortante trouvée");
            }

        }else {
            System.out.println("pas de noeud trouvé");
        }
    }


    @Test
    void aNull(){
        double length = 10;
        ElevationProfile elevationProfile = new ElevationProfile(length, new float[]{Float.NaN, 10, 7});
        assertTrue(hasNull(elevationProfile, length));
    }

    private final static boolean hasNull(ElevationProfile profile, double length){
        final int nombreTest = 1000;
        boolean hasNull = false;


        for(int i=0;i<nombreTest;i++){
            double position = length*i/nombreTest;
            double height = profile.elevationAt(position);
            if(Double.isNaN(height) || !Double.isFinite(height)){
                hasNull=true;
            }
        }
        return hasNull;
    }
}*/
