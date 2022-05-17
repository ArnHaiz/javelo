package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * class managing the display of a route and the highlighted position
 */
public final class RouteManager {
    RouteBean routeBean;
    ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    Consumer<String> errorConsumer;

    Pane pane = new Pane();
    Polyline polyline;
    Circle circle;

    /**
     * public constructor of the <code>RouteManager</code> class
     *
     * @param routeBean bean representing the route to display
     * @param mapViewParameters the parameters of the current display
     * @param errorConsumer the consumer for specific problems
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters, Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;
        this.errorConsumer = errorConsumer;

        polyline = new Polyline();
        List<PointCh> points = routeBean.routeProperty().getValue().points();
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

        routeBean.routeProperty().addListener((observable, oldValue, newValue) -> updateHighlightedPosition());

        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> updateHighlightedPosition());

        pane.setOnMouseClicked((clickEvent -> {
            if (clickEvent.isStillSincePress() &&
                    routeBean.routeProperty() != null &&
                    compare2dCh(
                            circle.localToParent(clickEvent.getX(), clickEvent.getY()),
                            routeBean.routeProperty().getValue().pointAt(routeBean.getHighlightedPosition()))) {
                Waypoint newWaypoint = new Waypoint(
                        routeBean.routeProperty().getValue().pointAt(routeBean.getHighlightedPosition()),
                        routeBean.routeProperty().getValue().nodeClosestTo(routeBean.getHighlightedPosition()));
                if (!routeBean.waypoints.contains(newWaypoint)) {
                    routeBean.waypoints.add(newWaypoint);
                } else {
                    errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
                }
            }
        }));
    }

    /**
     * returns the pane on which the route and highlighted position are drawn
     *
     * @return the pane with the route and the highlighted position
     */
    public Pane pane() {
        return pane;
    }


    private void updateHighlightedPosition() {
        double highlightedPosition = routeBean.getHighlightedPosition();
        PointCh highlightedPoint = routeBean.routeProperty().getValue().pointAt(highlightedPosition);

        if (routeBean.routeProperty().get() != null && highlightedPosition < routeBean.routeProperty().get().length() && highlightedPosition >= 0) {
            circle.setVisible(true);
            circle.setCenterX(highlightedPoint.e());
            circle.setCenterY(highlightedPoint.n());

        } else if (routeBean.routeProperty().get() == null) {
            circle.setVisible(false);
        }
    }

    private boolean compare2dCh(Point2D point2D, PointCh pointCh) {
        return point2D.getX() == mapViewParameters.get().topLeftX() - pointCh.e() &&
                point2D.getY() == mapViewParameters.get().topLeftY() - pointCh.n();
    }
}
