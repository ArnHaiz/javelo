package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    private final int PRIMARY_WIDTH = 800;
    private final int PRIMARY_HEIGHT = 600;
    private final double MAX_STEP_LENGTH = 5;
    private boolean splitPaneHasBeenUpdated = false;

    private Graph graph;
    private TileManager tileManager;

    private RouteBean routeBean;
    private ErrorManager errorManager;
    private AnnotatedMapManager annotatedMapManager;
    private ElevationProfileManager elevationProfileManager;

    private BorderPane borderPane;
    private StackPane stackPane;
    private SplitPane splitPane;

    private MenuBar menuBar;
    private MenuItem menuItem;

    private void updateProfileManager() {
        elevationProfileManager = new ElevationProfileManager(
                routeBean.elevationProfileProperty(),
                routeBean.highlightedPositionProperty());

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        graph = Graph.loadFrom(Path.of("javelo-data"));
        tileManager = new TileManager(Path.of("osm-cache"), "https://tile.openstreetmap.org");

        routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        errorManager = new ErrorManager();
        annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, e -> errorManager.display(e));

        borderPane = new BorderPane();
        stackPane = new StackPane();
        splitPane = new SplitPane();

        menuBar = new MenuBar();
        menuItem = new MenuItem();

        splitPane.getItems().add(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        stackPane.getChildren().add(splitPane);
        stackPane.getChildren().add(errorManager.pane());

        borderPane.setCenter(stackPane);
        borderPane.setTop(menuBar);

        primaryStage.setScene(new Scene(borderPane, PRIMARY_WIDTH, PRIMARY_HEIGHT));
        primaryStage.show();


        routeBean.routeProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                updateProfileManager();
                splitPane.getItems().add(elevationProfileManager.pane());
                SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);

            } else if (oldValue != null && newValue != null) {
                updateProfileManager();
                splitPane.getItems().remove(splitPane.getItems().size() - 1);
                splitPane.getItems().add(elevationProfileManager.pane());

            } else {
                splitPane.getItems().remove(splitPane.getItems().size() - 1);

            }
        });

    }

    private static final class ErrorConsumer implements Consumer<String> {
        @Override
        public void accept(String s) {
            System.out.println(s);
        }
    }


}
