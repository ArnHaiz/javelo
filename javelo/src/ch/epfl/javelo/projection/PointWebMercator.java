package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double x, double y) {
    public PointWebMercator {
        Preconditions.checkArgument(((x>=0)&&(x<=1)&&(y>=0)&&(y<=1)));
    };
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        PointWebMercator p = new PointWebMercator(Math.scalb(x,zoomLevel+8),Math.scalb(y,zoomLevel+8));
        return p;
    };
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());
        PointWebMercator p = new PointWebMercator(x,y);
        return p;
    };
    public double xAtZoomLevel(int zoomLevel) {
        double xAZ = Math.scalb(x ,zoomLevel);
        return xAZ;
    };
    public double yAtZoomLevel(int zoomLevel) {
        double yAZ = Math.scalb(y ,zoomLevel);
        return yAZ;
    };
    public double lon() {
        double lon = WebMercator.lon(x);
        return lon;
    };
    public double lat() {
        double lat = WebMercator.lat(y);
        return lat;
    };
    public PointCh toPointCh () {
        double e = Ch1903.e(lon(),lat());
        double n = Ch1903.n(lon(),lat());
        if(SwissBounds.containsEN(e,n)){
            PointCh p = new PointCh(e,n);
            return p;
        }else{
            return null;
        }
    };
}
