package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.awt.*;
import java.util.function.Consumer;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * class managing the display of a route and the highlighted position
 */
public final class RouteManager {
    RouteBean routeBean;
    ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;

    Pane pane = new Pane();
    Polyline polyline;
    Circle circle;

    /**
     * public constructor of the <code>RouteManager</code> class
     *
     * @param routeBean         bean representing the route to display
     * @param mapViewParameters the parameters of the current display
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;

        polyline = new Polyline();
        polyline.setId("route");

        circle = new Circle(5);
        circle.setId("highlight");

        pane.getChildren().add(polyline);
        pane.getChildren().add(circle);
        pane.setPickOnBounds(false);

        mapViewParameters.addListener(((observable, oldValue, newValue) -> updateRoute()));

        routeBean.routeProperty().addListener((observable, oldValue, newValue) -> updateRoute());

        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> updateRoute());

        circle.setOnMouseClicked(event -> {
            double position = routeBean.highlightedPositionProperty().get();
            int index = routeBean.indexOfNonEmptySegmentAt(position);
            routeBean.waypoints.add(index, new Waypoint(
                    routeBean.routeProperty().get().pointAt(position),
                    routeBean.routeProperty().get().nodeClosestTo(position)));
        });
    }

    /**
     * returns the pane on which the route and highlighted position are drawn
     *
     * @return the pane with the route and the highlighted position
     */
    public Pane pane() {
        return pane;
    }


    private void updateRoute() {
        double highlightedPosition = routeBean.highlightedPositionProperty().get();

        if (routeBean.routeProperty().get() != null &&
                highlightedPosition < routeBean.routeProperty().get().length() &&
                highlightedPosition >= 0) {


            PointCh highlightedPoint = routeBean.routeProperty().get().pointAt(highlightedPosition);
            PointWebMercator pwm = PointWebMercator.ofPointCh(highlightedPoint);
            circle.setVisible(true);
            circle.setCenterX(mapViewParameters.get().viewX(pwm));
            circle.setCenterY(mapViewParameters.get().viewY(pwm));

            polyline.getPoints().clear();
            for (PointCh point : routeBean.routeProperty().get().points()) {
                pwm = PointWebMercator.ofPointCh(point);
                polyline.getPoints().add(mapViewParameters.get().viewX(pwm));
                polyline.getPoints().add(mapViewParameters.get().viewY(pwm));
            }

            polyline.setVisible(true);


        } else if (routeBean.routeProperty().get() == null) {
            circle.setVisible(false);
            polyline.getPoints().clear();
            polyline.setVisible(false);
        }
    }
}
