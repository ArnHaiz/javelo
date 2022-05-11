package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public final class RouteBean {
    public ObservableList<Waypoint> waypoints;
    private ObjectProperty<Route> route;
    private DoubleProperty  highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfile;
    public RouteBean(RouteComputer routeComputer) {

    }
    public ReadOnlyObjectProperty<Route> getRoute() {
        return route;
    }
    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return elevationProfile;
    }
    private void setRoute() {

    }
    private void setElevationProfile() {
        if(route == null) {
            elevationProfile = null;
        }else {
            elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), 5));
        }
    }
}
