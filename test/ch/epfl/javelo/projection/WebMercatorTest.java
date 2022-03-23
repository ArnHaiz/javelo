package ch.epfl.javelo.projection;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {
  private final static double DELTA = 1e-11;
  private final static double NAPOLEON_TREE_X = 0.518275214444;
  private final static double NAPOLEON_TREE_Y = 0.353664894749;
  private final static double NAPOLEON_TREE_LON = Math.toRadians(6.5790772);
  private final static double NAPOLEON_TREE_LAT = Math.toRadians(46.5218976);

  @Test
  public void xWorksWithExample() {
    assertEquals(NAPOLEON_TREE_X, WebMercator.x(NAPOLEON_TREE_LON), DELTA);
  }

  @Test
  public void xWorksWithLongitudePi() {
    assertEquals(1, WebMercator.x(Math.PI), DELTA);
  }

  @Test
  public void xWorksWithLongitudePiOver2() {
    assertEquals(0.75, WebMercator.x(Math.PI * 0.5), DELTA);
  }

  @Test
  public void xWorksWithLongitudeMinusPi() {
    assertEquals(0, WebMercator.x(-Math.PI), DELTA);
  }

  @Test
  public void xWorksWithLongitude0() {
    assertEquals(0.5, WebMercator.x(0), DELTA);
  }

  @Test
  public void yWorksWithExample() {
    assertEquals(NAPOLEON_TREE_Y, WebMercator.y(NAPOLEON_TREE_LAT), DELTA);
  }

  @Test
  public void yWorksWithLatitude0() {
    assertEquals(0.5, WebMercator.y(0), DELTA);
  }

  @Test
  public void yWorksWithLatitudePi() {
    assertEquals(0.5, WebMercator.y(Math.PI), DELTA);
  }

  @Test
  public void lonWorksWithExample() {
    assertEquals(NAPOLEON_TREE_LON, WebMercator.lon(NAPOLEON_TREE_X), DELTA);
  }

  @Test
  public void lonWorksWithX0() {
    assertEquals(-Math.PI, WebMercator.lon(0), DELTA);
  }

  @Test
  public void lonWorksWithX1() {
    assertEquals(Math.PI, WebMercator.lon(1), DELTA);
  }

  @Test
  public void latWorksWithExample() {
    assertEquals(NAPOLEON_TREE_LAT, WebMercator.lat(NAPOLEON_TREE_Y), DELTA);
  }

  @Test
  public void latWorksWithY0() {
    assertEquals(Math.atan(Math.sinh(Math.PI)), WebMercator.lat(0), DELTA);
  }

  @Test
  public void latWorksWithX1() {
    assertEquals(Math.atan(Math.sinh(-Math.PI)), WebMercator.lat(1), DELTA);
  }
}
