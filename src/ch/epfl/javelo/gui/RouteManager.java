package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {
    RouteBean routeBean;
    ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    Consumer<String> errorConsumer;

    Pane pane = new Pane();
    Polyline polyline;
    Circle circle;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters, Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;
        this.errorConsumer = errorConsumer;

        polyline = new Polyline();
        List<PointCh> points = routeBean.getRoute().getValue().points();
        for (int i = 0; i < points.size(); i += 2) {
            polyline.getPoints().add(i, points.get(i).e());
            polyline.getPoints().add(i + 1, points.get(i).n());
        }
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
                //TODO redraw polyline if necessary
            }
            updateHighlightedPosition();
        }));

        routeBean.getRoute().addListener((observable, oldValue, newValue) -> {
            updateHighlightedPosition();
        });

        /*routeBean.getHighlightedPosition().addListener((observable, oldValue, newValue) -> {
            updateHighlightedPosition();
        });*/

        pane.setOnMouseClicked((clickEvent -> {
            if (clickEvent.isStillSincePress() &&
                    routeBean.getRoute() != null &&
                    compare2dCh(
                            circle.localToParent(clickEvent.getX(), clickEvent.getY()),
                            routeBean.getRoute().getValue().pointAt(routeBean.getHighlightedPosition()))) {
                Waypoint newWaypoint = new Waypoint(
                        routeBean.getRoute().getValue().pointAt(routeBean.getHighlightedPosition()),
                        routeBean.getRoute().getValue().nodeClosestTo(routeBean.getHighlightedPosition()));
                if (!routeBean.waypoints.contains(newWaypoint)) {
                    routeBean.waypoints.add(newWaypoint);
                } else {
                    errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
                }
            }
        }));
    }

    public Pane pane() {
        return pane;
    }


    private void updateHighlightedPosition() {
        double highlightedPosition = routeBean.getHighlightedPosition();
        PointCh highlightedPoint = routeBean.getRoute().getValue().pointAt(highlightedPosition);

        if (routeBean.getRoute().get() != null && highlightedPosition < routeBean.getRoute().get().length() && highlightedPosition >= 0) {
            circle.setVisible(true);
            circle.setCenterX(highlightedPoint.e());
            circle.setCenterY(highlightedPoint.n());

        } else if (routeBean.getRoute().get() == null) {
            circle.setVisible(false);
        }
    }

    private boolean compare2dCh(Point2D point2D, PointCh pointCh) {
        return point2D.getX() == mapViewParameters.get().topLeftX() - pointCh.e() &&
                point2D.getY() == mapViewParameters.get().topLeftY() - pointCh.n();
    }
}
