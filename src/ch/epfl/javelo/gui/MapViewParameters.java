package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * record class containing paameters on which portion of the map to display.
 */
public record MapViewParameters(int zoomLevel, double topLeftX, double topLeftY) {

    /**
     * returns the top left point of the visible map.
     *
     * @return the top left point
     */
    public Point2D topLeft() {
        return new Point2D(topLeftX, topLeftY);
    }

    /**
     * returns a new instance of <code>MapViewParameters</code> with shifted position of the top left corner.
     *
     * @param topLeftX the new x coordinate of the top left corner
     * @param topLeftY the new y coordinate of the top left corner
     * @return a new instance with shifted top left corner
     */
    public MapViewParameters withMinXY(double topLeftX, double topLeftY) {
        return new MapViewParameters(zoomLevel, topLeftX, topLeftY);
    }

    /**
     * returns a <code>PointWebMercator</code> point of the given coordinates.
     *
     * @param x the x coordinate to be transformed
     * @param y the y coordinate to be transformed
     * @return a PointWebMercator point of the given coordinates
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel, x, y);
    }

    /**
     * returns the x coordinate of the given <code>PointWebMercator</code> point in terms of the top left corner.
     *
     * @param p the PointWebMercator point of which to take the coordinate
     * @return the x coordinate of the given PointWebMercator point
     */
    public double viewX(PointWebMercator p) {
        return p.xAtZoomLevel(zoomLevel)-topLeftX;
    }

    /**
     * returns the y coordinate of the given <code>PointWebMercator</code> point in terms of the top left corner.
     *
     * @param p the PointWebMercator point of which to take the coordinate
     * @return the y coordinate of the given PointWebMercator point
     */
    public double viewY(PointWebMercator p) {
        return p.yAtZoomLevel(zoomLevel)-topLeftY;
    }
}


