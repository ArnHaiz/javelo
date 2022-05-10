package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.List;
import java.util.function.Consumer;


public class WaypointsManager {
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> property;
    private final List<Waypoint> waypointList;
    private final Consumer<String> errorManager;
    private final Pane pane;
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property,
                            List<Waypoint> waypointList, Consumer<String> errorManager) {
        this.graph = graph;
        this.property = property;
        this.waypointList = waypointList;
        this.errorManager = errorManager;
        this.pane = new Pane();
        property.addListener((p, oldMapViewParams, newMapViewParams) -> {
            for(int i = 0; i<pane.getChildren().size(); i++) {
                Waypoint waypoint = waypointList.get(i);
                PointCh pointCh = waypoint.pointCh();
                PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);
                Group group = (Group) pane.getChildren().get(i);
                group.setLayoutX(newMapViewParams.viewX(pointWebMercator));
                group.setLayoutY(newMapViewParams.viewY(pointWebMercator));
            }
        });
        SVGUsher();
    }

    public Pane pane() {
        pane.setPickOnBounds(false);
        return pane;
    }
    public void addWaypoint(double coordinateX, double coordinateY) {
        PointCh pointCh = property.get().pointAt(coordinateX, coordinateY).toPointCh();
        int nodeClose = graph.nodeClosestTo(pointCh, 500);
        if(nodeClose!=-1) {
            waypointList.add(new Waypoint(pointCh, nodeClose));
            pane.getChildren().clear();
            SVGUsher();
        }else {
            errorManager.accept("Aucune route à proximité !");
        }
    }
    private Group SVGCreator(Waypoint waypoint) {
        SVGPath inside = new SVGPath();
        SVGPath outside = new SVGPath();
        inside.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        outside.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        inside.getStyleClass().add("pin_inside");
        outside.getStyleClass().add("pin_outside");
        Group group = new Group(inside, outside);
        group.getStyleClass().add("pin");
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(waypoint.pointCh());
        group.setLayoutX(property.get().viewX(pointWebMercator));
        group.setLayoutY(property.get().viewY(pointWebMercator));
        group.setOnMouseClicked((e)-> {
            if(e.isBackButtonDown()) {
                if(!(waypointList.isEmpty())) {
                group.getStyleClass().clear();
                pane.getChildren().remove(waypointList.indexOf(waypoint));
                }
            }
        });
        group.setOnMouseDragged((e)-> {
            PointWebMercator point = property.get().pointAt(e.getSceneX(), e.getSceneY());
            group.setLayoutX(property.get().viewX(point));
            group.setLayoutX(property.get().viewY(point));
        });
        group.setOnMouseReleased((e)-> {
            PointWebMercator point = property.get().pointAt(e.getSceneX(), e.getSceneY());
            PointCh newPointCh = point.toPointCh();
            int nodeClose = graph.nodeClosestTo(newPointCh, 500);
            if(nodeClose!=-1) {
                group.setLayoutX(property.get().viewX(point));
                group.setLayoutX(property.get().viewY(point));
                int index = waypointList.indexOf(waypoint);
                waypointList.remove(waypoint);
                waypointList.add(index, new Waypoint(newPointCh, nodeClose));
                pane.getChildren().clear();
                SVGUsher();
            }else {
                group.setLayoutX(property.get().viewX(pointWebMercator));
                group.setLayoutX(property.get().viewY(pointWebMercator));
                errorManager.accept("Aucune route à proximité !");
            }
        });
        return group;
    }
    private void SVGUsher() {
        for(int i = 0; i<waypointList.size(); i++) {
            Group group = SVGCreator(waypointList.get(i));
            if(i==0) {
                group.getStyleClass().add("first");
            } else if(i==waypointList.size()-1){
                group.getStyleClass().add("last");
            } else {
                group.getStyleClass().add("middle");
            }
            pane.getChildren().add(group);
        }
        pane.setPickOnBounds(false);
    }
}
