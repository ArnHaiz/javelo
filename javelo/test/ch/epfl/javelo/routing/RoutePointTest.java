package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.*;

public class RoutePointTest {

    @Test
    public void verifyNONEWorks() {
        assertNull(RoutePoint.NONE.point());
        assertEquals(RoutePoint.NONE.position(), NaN);
        assertEquals(RoutePoint.NONE.distanceToReference(), POSITIVE_INFINITY);
    }

    @Test
    public void withPositionShiftedByWorksWithPositiveDistance() {
        RoutePoint thisPoint = new RoutePoint(new PointCh(2500000, 1250000), 100, 0);
        assertEquals(thisPoint.position() + 12, thisPoint.withPositionShiftedBy(12).position());
    }

    @Test
    public void withPositionShiftedByWorksWithNegativeDistance() {
        RoutePoint thisPoint = new RoutePoint(new PointCh(2500000, 1250000), 100, 0);
        assertEquals(thisPoint.position() - 12, thisPoint.withPositionShiftedBy(-12).position());
    }

    @Test
    public void withPositionShiftedByWorksWithNullDistance() {
        RoutePoint thisPoint = new RoutePoint(new PointCh(2500000, 1250000), 100, 0);
        assertEquals(thisPoint.position(), thisPoint.withPositionShiftedBy(0).position());
    }

    @Test
    public void minOneWorksWithDifferentDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 1);
        assertEquals(rp2, rp1.min(rp2));
    }

    @Test
    public void minOneWorksWithSameDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        assertEquals(rp2, rp1.min(rp2));
    }

    @Test
    public void minOneWorksWithNullDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 0);
        assertEquals(rp2, rp1.min(rp2));
    }

    @Test
    public void minOneWorksWithNegativeDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, -4);
        assertEquals(rp2, rp1.min(rp2));
    }

    @Test
    public void minOneWorksWithRP1smaller() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 1);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        assertEquals(rp1, rp1.min(rp2));
    }

    @Test
    public void minTwoWorksWithDifferentDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 1);
        assertEquals(rp2.point(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).point());
        assertEquals(rp2.position(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).position());
        assertEquals(rp2.distanceToReference(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).distanceToReference());
    }

    @Test
    public void minTwoWorksWithSameDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        assertEquals(rp2.point(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).point());
        assertEquals(rp2.position(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).position());
        assertEquals(rp2.distanceToReference(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).distanceToReference());
    }

    @Test
    public void minTwoWorksWithNullDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 0);
        assertEquals(rp2.point(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).point());
        assertEquals(rp2.position(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).position());
        assertEquals(rp2.distanceToReference(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).distanceToReference());
    }

    @Test
    public void minTwoWorksWithNegativeDistanceToReference() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, -4);
        assertEquals(rp2.point(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).point());
        assertEquals(rp2.position(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).position());
        assertEquals(rp2.distanceToReference(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).distanceToReference());
    }

    @Test
    public void minTwoWorksWithRP1Smaller() {
        RoutePoint rp1 = new RoutePoint(new PointCh(2500000, 1250000), 100, 1);
        RoutePoint rp2 = new RoutePoint(new PointCh(2500000, 1250000), 100, 10);
        assertEquals(rp1.point(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).point());
        assertEquals(rp1.position(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).position());
        assertEquals(rp1.distanceToReference(),
                rp1.min(rp2.point(), rp2.position(), rp2.distanceToReference()).distanceToReference());
    }
}
