import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public class StageTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "https://tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, "https://tile.openstreetmap.org");

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);

        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        routeBean.setHighlightedPosition(1000);

        ObservableList<Waypoint> waypoints = routeBean.waypoints;
        Consumer<String> errorConsumer = new ErrorConsumer();

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        waypoints,
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager
                        , mapViewParametersP
                        , waypointsManager);

        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersP);

        StackPane mainPane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setScene(new Scene(mainPane, 600, 300));
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) {
            System.out.println(s);
        }
    }
}
