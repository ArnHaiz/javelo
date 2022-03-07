package ch.epfl.javelo.projection;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.Test;

public class PointWebMercatorTest {
  private final static double NAPOLEON_TREE_UNSCALED_X = 0.518_275_214_444;
  private final static double NAPOLEON_TREE_UNSCALED_Y = 0.353_664_894_749;
  private final static double NAPOLEON_TREE_ZOOM_19_X = 69_561_722;
  private final static double NAPOLEON_TREE_ZOOM_19_Y = 47_468_099;
  private final static double NAPOLEON_TREE_SCALED_19_X = Math.scalb(NAPOLEON_TREE_ZOOM_19_X, -27);
  private final static double NAPOLEON_TREE_SCALED_19_Y = Math.scalb(NAPOLEON_TREE_ZOOM_19_Y, -27);
  private final static double NAPOLEON_TREE_LON = Math.toRadians(6.5_790_772);
  private final static double NAPOLEON_TREE_LAT = Math.toRadians(46.5_218_976);
  private final static PointWebMercator NAPOLEON_TREE = new PointWebMercator(NAPOLEON_TREE_UNSCALED_X,
      NAPOLEON_TREE_UNSCALED_Y);

  @Test
  public void pointWebMercatorThrows() {
    assertThrows(IllegalArgumentException.class, () -> new PointWebMercator(1.1, 0.6));
    assertThrows(IllegalArgumentException.class, () -> new PointWebMercator(0.2, 1.6));
    assertThrows(IllegalArgumentException.class, () -> new PointWebMercator(-0.2, 0.7));
    assertThrows(IllegalArgumentException.class, () -> new PointWebMercator(0.2, -0.7));
  }

  @Test
  public void ofWorksWithExample() {
    PointWebMercator ofCenter = PointWebMercator.of(19, NAPOLEON_TREE_ZOOM_19_X, NAPOLEON_TREE_ZOOM_19_Y);
    assertEquals(NAPOLEON_TREE_SCALED_19_X, ofCenter.x());
    assertEquals(NAPOLEON_TREE_SCALED_19_Y, ofCenter.y());
  }

  @Test
  public void ofPointChWorks() {
    PointCh pointCh = new PointCh(2_500_000, 1_100_000);
    PointWebMercator webMercator = PointWebMercator.ofPointCh(pointCh);
    // Tolerance of 2 meters
    assertEquals(pointCh.e(), webMercator.toPointCh().e(), 2);
    assertEquals(pointCh.n(), webMercator.toPointCh().n(), 2);
  }

  @Test
  public void xAtZoomLevelWorksWithExample() {
    // Tolerance of 1 meter
    assertEquals(NAPOLEON_TREE_ZOOM_19_X, NAPOLEON_TREE.xAtZoomLevel(19), 1);
    assertEquals(Math.scalb(NAPOLEON_TREE_UNSCALED_X, 8), NAPOLEON_TREE.xAtZoomLevel(0));
  }

  @Test
  public void xAtZoomLevelWorksWithCenteredPoint() {
    assertEquals(0, new PointWebMercator(0, 0).xAtZoomLevel(19));
    assertEquals(0, new PointWebMercator(0, 0).xAtZoomLevel(0));
  }

  @Test
  public void yAtZoomLevelWorksWithExample() {
    // Tolerance of 1 meter
    assertEquals(NAPOLEON_TREE_ZOOM_19_Y, NAPOLEON_TREE.yAtZoomLevel(19), 1);
    assertEquals(Math.scalb(NAPOLEON_TREE_UNSCALED_Y, 8), NAPOLEON_TREE.yAtZoomLevel(0));
  }

  @Test
  public void yAtZoomLevelWorksWithCenteredPoint() {
    assertEquals(0, new PointWebMercator(0, 0).yAtZoomLevel(19));
    assertEquals(0, new PointWebMercator(0, 0).yAtZoomLevel(0));
  }

  @Test
  public void lonWorksWithExample() {
    assertEquals(NAPOLEON_TREE_LON, NAPOLEON_TREE.lon(), 1e-7);
  }

  @Test
  public void latWorksWithExample() {
    assertEquals(NAPOLEON_TREE_LAT, NAPOLEON_TREE.lat(), 1e-7);
  }

  @Test
  public void toPointChReturnsNullForOutsideCoordinates() {
    assertNull(new PointWebMercator(1, 1).toPointCh());
    assertNull(new PointWebMercator(0, 0.5).toPointCh());
    assertNull(new PointWebMercator(0, 0).toPointCh());
    assertNull(new PointWebMercator(0.1, 0.3555852531588708).toPointCh());
    assertNull(new PointWebMercator(0.517074375308642, 0.1).toPointCh());
  }

  @Test
  public void toPointChWorksForSwissCoordinates() {
    PointCh pointCh = new PointCh(2_500_000, 1_100_000);
    PointWebMercator webMercator = new PointWebMercator(0.517074375308642, 0.3555852531588708);
    // Tolerance of 2 meters
    assertEquals(pointCh.e(), webMercator.toPointCh().e(), 2);
    assertEquals(pointCh.n(), webMercator.toPointCh().n(), 2);
  }
}
