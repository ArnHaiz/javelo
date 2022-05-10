package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;
import java.util.stream.Stream;

public final class RouteManager {
    RouteBean routeBean;
    ReadOnlyObjectProperty<MapViewParameters> mapViewParametersReadOnlyObjectProperty;
    Consumer<Error> errorConsumer;

    Pane pane = new Pane();
    Polyline polyline;
    Circle circle;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters, Consumer<Error> errorConsumer) {
        this.routeBean = routeBean;
        this.mapViewParametersReadOnlyObjectProperty = mapViewParameters;
        this.errorConsumer = errorConsumer;

        polyline = new Polyline(routeBean.route.points()); //FIXME
        polyline.setId("route");

        circle = new Circle(5);
        circle.setId("highlight");

        pane.getChildren().add(polyline);
        pane.getChildren().add(circle);
        pane.setPickOnBounds(false);

        mapViewParameters.addListener(((observable, oldValue, newValue) -> {
            if (oldValue.zoomLevel() == newValue.zoomLevel()) {
                polyline.setLayoutX(-newValue.topLeftX());
                polyline.setLayoutY(-newValue.topLeftY());
            } else {
                //TODO re draw the polyline.
            }
            updateHighlightedPosition();
        }));

        routeBean.route.addListener((observable, oldValue, newValue) -> {
            updateHighlightedPosition();
        });

        routeBean.highlightedPosition.addListener((observable, oldValue, newValue) -> {
            updateHighlightedPosition();
        });

        pane.setOnMouseClicked((clickEvent -> {
            if (clickEvent.isStillSincePress() &&
                    routeBean.route != null &&
                    circle.localToParent(clickEvent.getX(), clickEvent.getY()).equals(
                            routeBean.route.pointAt(routeBean.highlightedPosition()))) {
                Waypoint newWaypoint = new Waypoint(
                        new PointCh(routeBean.route.pointAt(routeBean.highlightedPosition)),
                        routeBean.route.nodeClosestTo(routeBean.highlightedPosition));
                if (!routeBean.waypoints.contains(newWaypoint)) { //TODO finish checking for a waypoint on the highlightedPosition.
                    routeBean.waypoints.get().add(newWaypoint); //TODO add a new waypoint.
                } else {
                    System.out.println("Un point de passage est déjà présent à cet endroit !");
                }
            }
        }));
    }

    public Pane pane() {
        return pane;
    }


    private void updateHighlightedPosition() {
        double highlightedPosition = routeBean.highlightedPosition();
        PointCh highlightedPoint = routeBean.route.pointat(highlightedPosition);
        if (routeBean.route.get() != null && highlightedPosition < routeBean.route.get().length() && highlightedPosition >= 0) {
            circle.setVisible(true);
            circle.setCenterX(highlightedPoint.e());
            circle.setCenterY(highlightedPoint.n());
        } else if (routeBean.route.get() == null) {
            circle.setVisible(false);
        }
    }
}
