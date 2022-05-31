package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public class AnnotatedMapManager {
    private final int INITIAL_ZOOM_LEVEL = 12;
    private final int INITIAL_TOP_LEFT_X = 543200;
    private final int INITIAL_TOP_LEFT_Y = 370650;

    private WaypointsManager waypointsManager;
    private BaseMapManager baseMapManager;
    private RouteManager routeManager;

    private StackPane stackPane;

    private final ObjectProperty<MapViewParameters> mapViewParameters = new SimpleObjectProperty<>();
    private final ObjectProperty<Point2D> pointUnderMouse = new SimpleObjectProperty<>(new Point2D(0, 0));
    private final DoubleProperty mousePositionOnRouteProperty = new SimpleDoubleProperty();

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorManager) {
        mapViewParameters.set(new MapViewParameters(INITIAL_ZOOM_LEVEL, INITIAL_TOP_LEFT_X, INITIAL_TOP_LEFT_Y));
        waypointsManager = new WaypointsManager(graph, mapViewParameters, routeBean.waypoints, errorManager);
        baseMapManager = new BaseMapManager(tileManager, mapViewParameters, waypointsManager);
        routeManager = new RouteManager(routeBean, mapViewParameters);

        stackPane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        stackPane.getStylesheets().add("map.css");

        stackPane.setOnMouseMoved(e -> pointUnderMouse.set(new Point2D(stackPane.getLayoutX(), stackPane.getLayoutY())));
        stackPane.setOnMouseExited(e -> pointUnderMouse.set(new Point2D(Double.NaN, Double.NaN)));

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(this::computePositionOnRoute, pointUnderMouse));
    }

    public Pane pane() {
        return stackPane;
    }

    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    private double computePositionOnRoute() {
        PointCh pointCh = mapViewParameters.get().pointAt(pointUnderMouse.get().getX(), pointUnderMouse.get().getY()).toPointCh();

        if ((routeManager.routeBean.routeProperty().get() == null) ||
                (Double.isNaN(pointUnderMouse.get().getX())) ||
                (Double.isNaN(pointUnderMouse.get().getY()))) {

            return Double.NaN;

        } else if (pointCh.distanceTo(routeManager.routeBean.routeProperty().get().pointClosestTo(pointCh).point()) <= 15) {
            return routeManager.routeBean.routeProperty().get().pointClosestTo(pointCh).position();

        } else {
            return Double.NaN;

        }
    }
}
