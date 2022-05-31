package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Route;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.naming.Binding;
import java.util.function.Consumer;

public class AnnotatedMapManager {
    private final int INITIAL_ZOOM_LEVEL = 12;
    private final int INITIAL_TOP_LEFT_X = 543200;
    private final int INITIAL_TOP_LEFT_Y = 543200;
    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;
    private final ObjectProperty<MapViewParameters> mapViewParameters = new SimpleObjectProperty<>();
    private final Pane pane = new StackPane();
    private final ObjectProperty<Point2D> pointUnderMouse = new SimpleObjectProperty<>(new Point2D(0, 0));
    private final DoubleProperty mousePositionOnRouteProperty = new SimpleDoubleProperty();

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorManager) {
        mapViewParameters.set(new MapViewParameters(INITIAL_ZOOM_LEVEL,INITIAL_TOP_LEFT_X,INITIAL_TOP_LEFT_Y));
        waypointsManager = new WaypointsManager(graph, mapViewParameters, routeBean.waypoints, errorManager);
        baseMapManager = new BaseMapManager(tileManager, mapViewParameters, waypointsManager);
        routeManager = new RouteManager(routeBean, mapViewParameters, errorManager);
        //TODO enlever errorManager

        pane.getChildren().add(baseMapManager.pane());
        pane.getChildren().add(routeManager.pane());
        pane.getChildren().add(waypointsManager.pane());
        pane.getStylesheets().add("map.css");

        pane.setOnMouseMoved(e-> pointUnderMouse.set(new Point2D(pane().getLayoutX(), pane.getLayoutY())));
        pane.setOnMouseExited(e-> pointUnderMouse.set(new Point2D(Double.NaN, Double.NaN)));
        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> computePositionOnRoute(mapViewParameters.get(), routeBean.routeProperty().get(), pointUnderMouse)));
    }

    public Pane pane() {
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    private double computePositionOnRoute(MapViewParameters mapViewParameters, Route route, ObjectProperty<Point2D> point) {
        PointCh pointCh = mapViewParameters.pointAt(point.get().getX(), point.get().getY()).toPointCh();
        if((route == null)||(Double.isNaN(point.get().getX()))||(Double.isNaN(point.get().getY()))) {
            mousePositionOnRouteProperty.set(Double.NaN);
            return Double.NaN;
        }else if(pointCh.distanceTo(route.pointClosestTo(pointCh).point())<=15){
            mousePositionOnRouteProperty.set(route.pointClosestTo(pointCh).position());
            return route.pointClosestTo(pointCh).position();
        }else {
            mousePositionOnRouteProperty.set(Double.NaN);
            return Double.NaN;
        }
    }
}
