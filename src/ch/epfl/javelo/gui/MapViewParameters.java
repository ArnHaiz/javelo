package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * record class representing a portion of the map.
 */
public record MapViewParameters(int zoomLevel, double topLeftX, double topLeftY) {
    public Point2D topLeft() {
        return new Point2D(topLeftX, topLeftY);
    }

    public MapViewParameters withMinXY(double topLeftX, double topLeftY) {
        return new MapViewParameters(zoomLevel, topLeftX, topLeftY);
    }

    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel, x, y);
    }

    public double viewX(PointWebMercator p) {
        return Math.scalb(p.x(), zoomLevel + 8);
    }

    public double viewY(PointWebMercator p) {
        return Math.scalb(p.y(), zoomLevel + 8);
    }
}


