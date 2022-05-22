package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class RouteBean {
    public final ObservableList<Waypoint> waypoints;

    private final ObjectProperty<Route> routeProperty;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final RouteComputer routeComputer;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> samplesRoute = new LinkedHashMap<>(10);

    public RouteBean(RouteComputer routeComputer) {
        routeProperty = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        elevationProfile = new SimpleObjectProperty<>();
        this.routeComputer = routeComputer;

        waypoints = FXCollections.observableArrayList();
        waypoints.addListener((Observable o)-> updateRoute());
        waypoints.addListener((Observable o)-> updateElevationProfile());
    }

    public ReadOnlyObjectProperty<Route> routeProperty() {
        return routeProperty;
    }
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    public void setHighlightedPosition(double position) {
        highlightedPosition.set(position);
    }
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    private void updateRoute() {
        if(waypoints.size()<2) {
            routeProperty.set(null);
        }else {
            List<Route> segments = new ArrayList<>();

            for (int i = 0; i < waypoints.size()-1; i++) {
                Route way;
                Pair<Integer, Integer> nodes = new Pair<>(
                        waypoints.get(i).nodeClosestToId(),
                        waypoints.get(i+1).nodeClosestToId());

                if(samplesRoute.containsKey(nodes)) {
                    way = samplesRoute.get(nodes);

                }else {
                    way = routeComputer.bestRouteBetween(nodes.getKey(), nodes.getValue());
                    samplesRoute.put(nodes, way);

                }

                if(way == null) {
                    routeProperty.set(null);
                    return;

                }else {
                    segments.add(way);
                }
            }

            routeProperty.set(new MultiRoute(segments));
        }
    }

    private void updateElevationProfile() {
        if(routeProperty.get() == null) {
            elevationProfile.set(null);

        }else {
            elevationProfile.set(ElevationProfileComputer.elevationProfile(routeProperty.get(), 5));
        }
    }
}
