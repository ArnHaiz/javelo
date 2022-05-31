package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    private final int PRIMARY_WIDTH = 800;
    private final int PRIMARY_HEIGHT = 600;
    private final double MAX_STEP_LENGTH = 5;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"), "tile.openstreetmap.org");
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        ErrorManager errorManager = new ErrorManager();
        BorderPane borderPane = new BorderPane();
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, new ErrorConsumer());
        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(
                new SimpleObjectProperty<>(
                        ElevationProfileComputer.elevationProfile(
                                routeBean.routeProperty().get(),
                                MAX_STEP_LENGTH)),
                annotatedMapManager.mousePositionOnRouteProperty());
        Pane stackPane = new StackPane();
        SplitPane splitPane = new SplitPane();
        MenuBar menuBar = new MenuBar();

        splitPane.getItems().add(annotatedMapManager.pane());
        routeBean.routeProperty().addListener((observable, oldValue, newValue) -> {if((oldValue==null)&&(newValue!=null)) {
            splitPane.getItems().add(elevationProfileManager.pane());
        }});
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
        stackPane.getChildren().add(splitPane);
        stackPane.getChildren().add(errorManager.pane());
        borderPane.setCenter(stackPane);
        borderPane.setTop(menuBar);
        primaryStage.setScene(new Scene(borderPane, PRIMARY_WIDTH, PRIMARY_HEIGHT));
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
