package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * class representing all the waypoints on the map
 */
public class WaypointsManager {
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final ObservableList<Waypoint> waypointList;
    private final Consumer<String> errorManager;
    private final Pane pane;

    /**
     * construct a waypoint manager
     *
     * @param graph : graph attached at the map
     * @param mapViewParametersProperty : mapViewParameters attached at the map
     * @param waypointList : list of all the waypoints on the map
     * @param errorManager : errorManager attached at the waypoints
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersProperty,
                            ObservableList<Waypoint> waypointList, Consumer<String> errorManager) {
        this.graph = graph;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.waypointList = waypointList;
        this.errorManager = errorManager;
        this.pane = new Pane();

        pane.setPickOnBounds(false);

        this.mapViewParametersProperty.addListener((p, oldMapViewParams, newMapViewParams) -> recomputeSVGs());

        this.waypointList.addListener((ListChangeListener<? super Waypoint>) (change) -> {
            pane.getChildren().clear();
            recomputeSVGs();
        });

        recomputeSVGs();
    }

    /**
     * return the pain representing the waypoints
     *
     * @return the pain representing the waypoints
     */
    public Pane pane() {
        return pane;
    }

    /**
     * add a new waypoint at the waypoint list
     *
     * @param coordinateX : coordinate x of the new waypoint
     * @param coordinateY : coordinate y of the new waypoint
     */
    public void addWaypoint(double coordinateX, double coordinateY) {
        PointCh pointCh = mapViewParametersProperty.get().pointAt(coordinateX, coordinateY).toPointCh();
        int nodeClose = graph.nodeClosestTo(pointCh, 500);
        if (nodeClose != -1) {
            waypointList.add(new Waypoint(pointCh, nodeClose));
            pane.getChildren().clear();
            recomputeSVGs();
        } else {
            errorManager.accept("Aucune route à proximité !");
        }
    }

    private Group createSVG(Waypoint waypoint) {
        SVGPath inside = new SVGPath();
        SVGPath outside = new SVGPath();

        inside.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        outside.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        inside.getStyleClass().add("pin_inside");
        outside.getStyleClass().add("pin_outside");

        Group group = new Group(outside, inside);
        group.getStyleClass().add("pin");

        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(waypoint.pointCh());
        group.setLayoutX(mapViewParametersProperty.get().viewX(pointWebMercator));
        group.setLayoutY(mapViewParametersProperty.get().viewY(pointWebMercator));

        ObjectProperty<Point2D> oldPos = new SimpleObjectProperty<>();
        group.setOnMousePressed((event -> oldPos.set(new Point2D(event.getX(), event.getY()))));
        
        group.setOnMouseDragged((event) -> {
            ObjectProperty<Point2D> newPos = new SimpleObjectProperty<>(new Point2D(event.getX(), event.getY()));

            group.setLayoutX(group.getLayoutX() + (newPos.get().getX() - oldPos.get().getX()));
            group.setLayoutY(group.getLayoutY() + (newPos.get().getY() - oldPos.get().getY()));
        });

        group.setOnMouseClicked((event) -> {
            if (event.isStillSincePress()) {
                waypointList.remove(waypoint);

            } else {
                Point2D point2D = new Point2D(group.getLayoutX(), group.getLayoutY()).add(
                        mapViewParametersProperty.get().topLeftX(), mapViewParametersProperty.get().topLeftY());
                PointWebMercator point = mapViewParametersProperty.get().pointAt(point2D.getX(), point2D.getY());
                PointCh newPointCh = point.toPointCh();

                int nodeClose = graph.nodeClosestTo(newPointCh, 500);
                if (nodeClose != -1) {
                    int index = waypointList.indexOf(waypoint);
                    waypointList.set(index, new Waypoint(newPointCh, nodeClose));

                } else {
                    errorManager.accept("Aucune route à proximité !");
                }
            }

            pane.getChildren().clear();
            recomputeSVGs();

        });

        return group;
    }

    private void recomputeSVGs() {
        pane.getChildren().clear();

        for (int i = 0; i < waypointList.size(); ++i) {
            Group group = createSVG(waypointList.get(i));
            group.getStyleClass().add(i == 0 ? "first" : (i == waypointList.size() - 1 ? "last" : "middle"));

            pane.getChildren().add(group);
        }
    }
}
