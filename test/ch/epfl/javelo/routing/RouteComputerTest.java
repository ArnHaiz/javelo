package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import ch.epfl.test.KmlPrinter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
public class RouteComputerTest {

    //verifier que les tests fonctionnent sur https://map.geo.admin.ch/ en mettant les fichiers .kml

    private final static double E_EPFL = 2_533_040.0;
    private final static double N_EPFL = 1_152_130.5;


    private final static double E_RENENS = 2_534_186.0;
    private final static double N_RENENS = 1_154_244.0;

    private final static double E_MALOMBRE = 2_500_809.0;
    private final static double N_MALOMBRE = 1_116_773.0;

    private final static double E_COURROUX =2_595_550.0;
    private final static double N_COURROUX = 1_246_620.0;

    private final static double LAT_ST_SULPICE = Math.toRadians(46.50865);
    private final static double LON_ST_SULPICE = Math.toRadians(6.55804);



    private final static int MAX_DISTANCE = 10000;

    @Test
    void test1(){
        Graph g = null;
        try {
            g = Graph.loadFrom(Path.of("lausanne"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);


            Route r = rc.bestRouteBetween(159049, 117669);
            KmlPrinter.write("javelo1.kml", r);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }


        Path path = Path.of("javelo1.kml");
        Path expected = Path.of("route_prof.kml");
        try {
            assertTrue(compareFile(expected, path));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

    }

    private static boolean compareFile(Path path1, Path path2) throws IOException {
        //https://www.baeldung.com/java-compare-files
        // 5. Using Memory Mapped Files

        try (RandomAccessFile randomAccessFile1 = new RandomAccessFile(path1.toFile(), "r");
             RandomAccessFile randomAccessFile2 = new RandomAccessFile(path2.toFile(), "r")) {

            FileChannel ch1 = randomAccessFile1.getChannel();
            FileChannel ch2 = randomAccessFile2.getChannel();
            if (ch1.size() != ch2.size()) {
                return false;
            }
            long size = ch1.size();
            MappedByteBuffer m1 = ch1.map(FileChannel.MapMode.READ_ONLY, 0L, size);
            MappedByteBuffer m2 = ch2.map(FileChannel.MapMode.READ_ONLY, 0L, size);

            return m1.equals(m2);
        }
    }


    @Test
    void test2(){
        Graph g = null;
        try {
            g = Graph.loadFrom(Path.of("lausanne"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);

            PointCh pEpfl = new PointCh(E_EPFL, N_EPFL);
            PointCh pRenes = new PointCh(E_RENENS, N_RENENS);

            int nEpfl = g.nodeClosestTo(pEpfl, MAX_DISTANCE);
            int nRenens = g.nodeClosestTo(pRenes, MAX_DISTANCE);

            Route r = rc.bestRouteBetween(nRenens, nEpfl);
            KmlPrinter.write("javelo2.kml", r);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public static Route bestRoutePoint(PointCh depart, PointCh arrive){
        Graph g = null;
        Route r = null;
        try {
            g = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);

            int nDepart = g.nodeClosestTo(depart, MAX_DISTANCE);
            int nArrive = g.nodeClosestTo(arrive, MAX_DISTANCE);

            if(nDepart == -1 || nArrive == -1){
                System.out.println("pas de noeud de depart ou arrive trouve " + nDepart + "," + nArrive);
            }

            r = rc.bestRouteBetween(nDepart, nArrive);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return r;
    }

    @Test
    void cheminAltLon(){
        Graph g = null;
        try {
            g = Graph.loadFrom(Path.of("lausanne"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);


            double e = Ch1903.e(LON_ST_SULPICE, LAT_ST_SULPICE);
            double n = Ch1903.n(LON_ST_SULPICE, LAT_ST_SULPICE);
            PointCh st_sulpice = new PointCh(e, n);


            PointCh pEpfl = new PointCh(E_EPFL, N_EPFL);
            PointCh pRenes = new PointCh(E_RENENS, N_RENENS);

            int nEpfl = g.nodeClosestTo(pEpfl, MAX_DISTANCE);
            int nRenens = g.nodeClosestTo(st_sulpice, MAX_DISTANCE);

            Route r = rc.bestRouteBetween(nRenens, nEpfl);
            KmlPrinter.write("javelo3.kml", r);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    void cheminAltLon2(){
        Graph g = null;
        try {
            g = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);


            PointCh pMalombre = new PointCh(E_MALOMBRE, N_MALOMBRE);
            PointCh pCourroux = new PointCh(E_COURROUX, N_COURROUX);

            int nMalombre = g.nodeClosestTo(pMalombre, MAX_DISTANCE);
            int nCourroux = g.nodeClosestTo(pCourroux, MAX_DISTANCE);

            Route r = rc.bestRouteBetween(nCourroux, nMalombre);
            KmlPrinter.write("javelo4.kml", r);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void cheminAltLon3(){
        Graph g = null;
        try {
            g = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);


            long t0 = System.nanoTime();
            Route r = rc.bestRouteBetween(2046055, 2694240);
            System.out.printf("Itinéraire calculé en %d ms\n", (System.nanoTime() - t0) / 1_000_000);


            KmlPrinter.write("javelo5.kml", r);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}