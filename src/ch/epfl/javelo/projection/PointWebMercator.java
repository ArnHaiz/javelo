package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;
/**
 * @author Hervé Sérandour (328233)
 *
 * record representing a point in the system WebMercator.
 * @param x : x coordinate of the point.
 * @param y : y coordinate of the point.
 */
public record PointWebMercator(double x, double y) {
    /**
     * construct a PointWebMercator
     * @throws IllegalArgumentException if the coordinates given aren't between 0 and 1
     */
    public PointWebMercator {
        Preconditions.checkArgument(((x>=0)&&(x<=1)&&(y>=0)&&(y<=1)));
    }

    /**
     * return the point that have the coordinates given at a certain zoom level
     * @param zoomLevel : zoom level of the point
     * @param x : x coordinate of the point
     * @param y : y coordinate of the point
     * @return the point that have the coordinates given at a certain zoom level
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        PointWebMercator p = new PointWebMercator(Math.scalb(x,-(zoomLevel+8)),Math.scalb(y,-(zoomLevel+8)));
        return p;
    }

    /**
     * return the WebMercator point corresponding of the swiss coordinates given
     * @param pointCh : point in the swiss coordinates
     * @return the WebMercator point corresponding of the swiss coordinates given
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());
        PointWebMercator p = new PointWebMercator(x,y);
        return p;
    }

    /**
     * return the coordinate at a given zoom level
     * @param zoomLevel : zoom level
     * @return the coordinate at a given zoom level
     */
    public double xAtZoomLevel(int zoomLevel) {
        double xAZ = Math.scalb(x ,8+zoomLevel);
        return xAZ;
    }

    /**
     * return the coordinate at a given zoom level
     * @param zoomLevel : zoom level
     * @return the coordinate at a given zoom level
     */
    public double yAtZoomLevel(int zoomLevel) {
        double yAZ = Math.scalb(y ,8+zoomLevel);
        return yAZ;
    }
    /**
     * return the longitude in radiant
     * @return the longitude in radiant
     */
    public double lon() {
        double lon = WebMercator.lon(x);
        return lon;
    }
    /**
     * return the latitude in radiant
     * @return the latitude in radiant
     */
    public double lat() {
        double lat = WebMercator.lat(y);
        return lat;
    }

    /**
     * return the coordinate of the point in the swiss coordinates
     * @return the coordinate of the point in the swiss coordinates if the point is in the swiss bounds otherwise null
     */
    public PointCh toPointCh () {
        double e = Ch1903.e(lon(),lat());
        double n = Ch1903.n(lon(),lat());
        if(SwissBounds.containsEN(e,n)){
            PointCh p = new PointCh(e,n);
            return p;
        }else{
            return null;
        }
    }
}
