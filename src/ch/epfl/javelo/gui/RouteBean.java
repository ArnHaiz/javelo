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

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * class representing the property of the waypoint and the itinirary corresponding
 */
public final class RouteBean {

    /**
     * constant representing the list of waypoint
     */
    public final ObservableList<Waypoint> waypoints;

    private final ObjectProperty<Route> routeProperty;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final RouteComputer routeComputer;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> samplesRoute = new LinkedHashMap<>(10);

    /**
     * construct a rout bean
     *
     * @param routeComputer : constructor of itinirary
     */
    public RouteBean(RouteComputer routeComputer) {
        routeProperty = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        elevationProfile = new SimpleObjectProperty<>();
        this.routeComputer = routeComputer;

        waypoints = FXCollections.observableArrayList();
        waypoints.addListener((Observable o) -> updateRoute());
        routeProperty.addListener((Observable o) -> updateElevationProfile());
    }

    /**
     * return the itinirary of the waypoints
     *
     * @return the itinirary of the waypoints
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return routeProperty;
    }

    /**
     * return the elevation profile attached to the itinirary
     *
     * @return the elevation profile attached to the itinirary
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * change the position to highlight on the itinirary by the new position
     *
     * @param position : position on the itinirary in meter
     */
    public void setHighlightedPosition(double position) {
        highlightedPosition.set(position);
    }

    /**
     * return the position highlighted on the itinirary
     *
     * @return the position highlighted on the itinirary
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeClosestToId();
            int n2 = waypoints.get(i + 1).nodeClosestToId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    private Route route() {
        return routeProperty.get();
    }

    private void updateRoute() {
        if (waypoints.size() < 2) {
            routeProperty.set(null);
        } else {
            List<Route> segments = new ArrayList<>();

            for (int i = 0; i < waypoints.size() - 1; ++i) {
                Route way;
                Pair<Integer, Integer> nodes = new Pair<>(
                        waypoints.get(i).nodeClosestToId(),
                        waypoints.get(i + 1).nodeClosestToId());

                if (samplesRoute.containsKey(nodes)) {
                    way = samplesRoute.get(nodes);

                } else {
                    way = routeComputer.bestRouteBetween(nodes.getKey(), nodes.getValue());
                    samplesRoute.put(nodes, way);

                }

                if (way == null) {
                    routeProperty.set(null);
                    return;

                } else {
                    segments.add(way);
                }
            }

            if (segments.size() == 0) {
                System.out.println("route is empty");
                routeProperty.set(null);
            } else {
                routeProperty.set(new MultiRoute(segments));
            }
        }
    }

    private void updateElevationProfile() {
        if (routeProperty.get() == null) {
            elevationProfile.set(null);

        } else {
            elevationProfile.set(ElevationProfileComputer.elevationProfile(routeProperty.get(), 5));
        }
    }
}
