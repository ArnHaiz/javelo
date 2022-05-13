package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public final class RouteBean {
    public ObservableList<Waypoint> waypoints;
    private ObjectProperty<Route> route;
    private DoubleProperty highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfile;
    private final RouteComputer routeComputer;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> samplesRoute = new LinkedHashMap<>(10);
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        assert false;
        waypoints.addListener((Observable o)->setRoute());
        waypoints.addListener((Observable o)->setElevationProfile());
    }
    public ReadOnlyObjectProperty<Route> getRoute() {
        return route;
    }
    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return elevationProfile;
    }
    private void setRoute() {
        if(waypoints.size()<2) {
            route = null;
        }else {
            List<Route> segments = new ArrayList<>();
            for (int i = 0; i < waypoints.size()-1; i++) {
                Route way;
                Pair<Integer, Integer> nodes = new Pair<>(waypoints.get(i).nodeClosestToId(), waypoints.get(i+1).nodeClosestToId());
                if(samplesRoute.containsKey(nodes)) {
                    way = samplesRoute.get(nodes);
                }else {
                    way = routeComputer.bestRouteBetween(nodes.getKey(), nodes.getValue());
                    samplesRoute.put(nodes, way);
                }
                if(way == null) {
                    route = null;
                    return;
                }else {
                    segments.add(way);
                }
            }
            route.set(new MultiRoute(segments));
        }
    }
    private void setElevationProfile() {
        if(route == null) {
            elevationProfile = null;
        }else {
            elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), 5));
        }
    }
    public double getHighlightedPosition() {
        return highlightedPosition.getValue();
    }
    public void setHighlightedPosition(double position) {
        highlightedPosition = Objects.requireNonNullElseGet(new SimpleDoubleProperty(position), () -> new SimpleDoubleProperty(Double.NaN));
    }
}
