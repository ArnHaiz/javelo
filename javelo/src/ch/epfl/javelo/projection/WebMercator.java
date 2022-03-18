package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * @author Arnaud Haizmann (329072)
 *
 * Helper class for converting coordinates between WGS 84 and the Web Mercator
 * coordinates.
 */
public final class WebMercator {
  /**
   * Constructor of the non-instantiable WebMercator class.
   */
  private WebMercator() {
  }

  /**
   * Converts the given WGS 84 longitude to the corresponding x coordinate in the
   * Web Mercator projection.
   * 
   * @param lon the WGS 84 longitude (in radians)
   * @return the x coordinate of the Web Mercator projection.
   */
  public static double x(double lon) {
    return Math.fma(lon, 1 / (2 * Math.PI), 0.5);
  }

  /**
   * Converts the given WGS 84 latitude to the corresponding y coordinate in the
   * Web Mercator projection.
   * 
   * @param lat the WGS 84 latitude (in radians)
   * @return the y coordinate of the Web Mercator projection.
   */
  public static double y(double lat) {
    return -Math.fma(Math2.asinh(Math.tan(lat)), 1 / (2 * Math.PI), -0.5);
  }

  /**
   * Converts the given Web Mercator x coordinate to the WGS 84 longitude (in
   * radians).
   * 
   * @param x the Web Mercator x coordinate
   * @return the longitude (in radians) in the WGS 84 projection.
   */
  public static double lon(double x) {
    return Math.fma(2 * Math.PI, x, -Math.PI);
  }

  /**
   * Converts the given Web Mercator y coordinate to the WGS 84 latitude (in
   * radians).
   * 
   * @param y the Web Mercator y coordinate
   * @return the latitude (in radians) in the WGS 84 projection.
   */
  public static double lat(double y) {
    return Math.atan(Math.sinh(-Math.fma(2 * Math.PI, y, -Math.PI)));
  }
}