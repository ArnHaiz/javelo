package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * @param x : x coordinate of the point.
 * @param y : y coordinate of the point.
 * @author Hervé Sérandour (328233)
 * <p>
 * record representing a point in the system WebMercator.
 */
public record PointWebMercator(double x, double y) {
    private static final int BASE_ZOOM_LEVEL = 8;
    /**
     * construct a PointWebMercator
     *
     * @throws IllegalArgumentException if the coordinates given aren't between 0 and 1
     */
    public PointWebMercator {
        Preconditions.checkArgument(((x >= 0) && (x <= 1) && (y >= 0) && (y <= 1)));
    }

    /**
     * return the point that have the coordinates given at a certain zoom level
     *
     * @param zoomLevel : zoom level of the point
     * @param x         : x coordinate of the point
     * @param y         : y coordinate of the point
     * @return the point that have the coordinates given at a certain zoom level
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -(zoomLevel + BASE_ZOOM_LEVEL)), Math.scalb(y, -(zoomLevel + BASE_ZOOM_LEVEL)));
    }

    /**
     * return the WebMercator point corresponding of the swiss coordinates given
     *
     * @param pointCh : point in the swiss coordinates
     * @return the WebMercator point corresponding of the swiss coordinates given
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());
        return new PointWebMercator(x, y);
    }

    /**
     * return the coordinate at a given zoom level
     *
     * @param zoomLevel : zoom level
     * @return the coordinate at a given zoom level
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, BASE_ZOOM_LEVEL + zoomLevel);
    }

    /**
     * return the coordinate at a given zoom level
     *
     * @param zoomLevel : zoom level
     * @return the coordinate at a given zoom level
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, BASE_ZOOM_LEVEL + zoomLevel);
    }

    /**
     * return the longitude in radiant
     *
     * @return the longitude in radiant
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * return the latitude in radiant
     *
     * @return the latitude in radiant
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * return the coordinate of the point in the swiss coordinates
     *
     * @return the coordinate of the point in the swiss coordinates if the point is in the swiss bounds otherwise null
     */
    public PointCh toPointCh() {
        double lon = lon();
        double lat = lat();
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);
        return SwissBounds.containsEN(e, n) ?  new PointCh(e, n) : null;
    }
}
