package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Class (record) representing a point in the Web Mercator coordinate system.
 * 
 * @author Arnaud Haizmann (329072)
 * @author Florian Kolly (328313)
 */
public record PointWebMercator(double x, double y) {
  /**
   * Constructor for a point in Web Mercator coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @throws IllegalArgumentException if the x or y coordinates are not in the
   *                                  range from 0 to 1 (both included).
   */
  public PointWebMercator {
    Preconditions.checkArgument(inRange(0, x, 1) && inRange(0, y, 1));
  }

  /**
   * Returns the point with coordinates x and y for the specified zoom level.
   * 
   * @param zoomLevel the zoom level for which the points must be recalculated
   * @param x         the x coordinate to recalculate
   * @param y         the y coordinate to recalculate
   * @return the point at the specified zoom level.
   */
  public static PointWebMercator of(int zoomLevel, double x, double y) {
    return new PointWebMercator(Math.scalb(x, -zoomLevel - 8), Math.scalb(y, -zoomLevel - 8));
  }

  /**
   * Returns the Web Mercator point corresponding to the given swiss coordinates
   * point.
   * 
   * @param pointCh the point in swiss coordinate to convert
   * @return the converted point in Web Mercator format of the given point in
   *         swiss coordinates.
   */
  public static PointWebMercator ofPointCh(PointCh pointCh) {
    return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
  }

  /**
   * Returns the x coordinate at the specified zoom level.
   * 
   * @param zoomLevel the zoom level (usually between 0 and 20)
   * @return the x coordinate at the given zoom level.
   */
  public double xAtZoomLevel(int zoomLevel) {
    return Math.scalb(x, zoomLevel + 8);
  }

  /**
   * Returns the y coordinate at the specified zoom level.
   * 
   * @param zoomLevel the zoom level (usually between 0 and 20)
   * @return the x coordinate at the given zoom level.
   */
  public double yAtZoomLevel(int zoomLevel) {
    return Math.scalb(y, zoomLevel + 8);
  }

  /**
   * Returns the longitude of the x coordinate in WGS 84.
   * 
   * @return the longitude (in radians) in WGS 84.
   */
  public double lon() {
    return WebMercator.lon(x);
  }

  /**
   * Returns the latitude of the y coordinate in WGS 84.
   * 
   * @return the latitude (in radians) in WGS 84.
   */
  public double lat() {
    return WebMercator.lat(y);
  }

  /**
   * Returns the equivalent swiss coordinates point.
   * 
   * @return the swiss coordinates point or null if the point is not inside the
   *         boundaries of Switzerland.
   */
  public PointCh toPointCh() {
    double east = Ch1903.e(lon(), lat());
    double north = Ch1903.n(lon(), lat());
    return SwissBounds.containsEN(east, north) ? new PointCh(east, north) : null;
  }

  /**
   * Checks if a given value is between the min and max limits (both included).
   * 
   * @param min   the (included) minimum value
   * @param value the value to be checked
   * @param max   the (included) maximum value
   * @return true if the value is comprised between min and max, false
   *         otherwise.
   */
  private static boolean inRange(double min, double value, double max) {
    return value >= min && value <= max;
  }
}
