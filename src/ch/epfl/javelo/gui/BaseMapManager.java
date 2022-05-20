package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * class handling most of the display of the map and it's associated interactive features.
 */
public final class BaseMapManager {
    TileManager tileManager;
    ObjectProperty<MapViewParameters> mapViewParameters;
    WaypointsManager waypointsManager;
    Canvas canvas;
    Pane pane;

    private final int PIXELS_PER_TILE_SIDE = 256;
    private boolean redrawNeeded = false;

    /**
     * public constructor of the <code>BaseMapManager</code> class.
     *
     * @param tileManager       handler of the tiles of the map to be drawn
     * @param mapViewParameters the basic parameters deciding what part of the map to draw
     * @param waypointsManager  the handler of the waypoints deciding the route
     */
    public BaseMapManager(TileManager tileManager, ObjectProperty<MapViewParameters> mapViewParameters, WaypointsManager waypointsManager) {
        this.tileManager = tileManager;
        this.mapViewParameters = mapViewParameters;
        this.waypointsManager = waypointsManager;

        canvas = new Canvas();
        pane = new Pane();
        pane.setPrefSize(400, 600);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        pane.getChildren().add(canvas);


        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        canvas.setOnScroll((scrollEvent -> {
            if (scrollEvent.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(scrollEvent.getDeltaY());

            int newZoom = Math2.clamp(8,
                    Math.round(mapViewParameters.get().zoomLevel() + zoomDelta),
                    19);

            Point2D pointUnderMouse = mapViewParameters.get().topLeft().add(scrollEvent.getX(), scrollEvent.getY());
            double newX = -scrollEvent.getX() + Math.scalb(pointUnderMouse.getX(), newZoom - mapViewParameters.get().zoomLevel());
            double newY = -scrollEvent.getY() + Math.scalb(pointUnderMouse.getY(), newZoom - mapViewParameters.get().zoomLevel());

            mapViewParameters.set(new MapViewParameters(newZoom, newX, newY));
        }));

        canvas.setOnMouseClicked(clickEvent -> {
            if (clickEvent.isStillSincePress()) {

                waypointsManager.addWaypoint(
                        mapViewParameters.get().topLeftX() + clickEvent.getX(),
                        mapViewParameters.get().topLeftY() + clickEvent.getY());
            }


        });

        ObjectProperty<Point2D> oldPos = new SimpleObjectProperty<>(new Point2D(0, 0));
        canvas.setOnMousePressed(event -> {
            oldPos.set(new Point2D(event.getX(), event.getY()));
        });

        canvas.setOnMouseDragged(dragEvent -> {
            ObjectProperty<Point2D> newPos = new SimpleObjectProperty<>();
            newPos.set(new Point2D(dragEvent.getX(), dragEvent.getY()));

            double newX = mapViewParameters.get().topLeftX() - (newPos.get().getX() - oldPos.get().getX());
            double newY = mapViewParameters.get().topLeftY() - (newPos.get().getY() - oldPos.get().getY());


            mapViewParameters.set(mapViewParameters.get().withMinXY(newX, newY));

            oldPos.set(newPos.get());
        });

        mapViewParameters.addListener(event -> redrawOnNextPulse());

        redrawOnNextPulse();
    }

    /**
     * returns the pane on which the map is drawn.
     *
     * @return the pane with the drawn map
     */
    public Pane pane() {
        return pane;
    }

    private void drawMap() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int topLeftXIndex = (int) (mapViewParameters.get().topLeftX() - mapViewParameters.get().topLeftX() % PIXELS_PER_TILE_SIDE)
                / PIXELS_PER_TILE_SIDE;
        int topLeftYIndex = (int) (mapViewParameters.get().topLeftY() - mapViewParameters.get().topLeftY() % PIXELS_PER_TILE_SIDE)
                / PIXELS_PER_TILE_SIDE;

        int bottomRightXIndex = (int) (mapViewParameters.get().topLeftX() + canvas.getWidth() +
                (PIXELS_PER_TILE_SIDE - (mapViewParameters.get().topLeftX() + canvas.getWidth()) % PIXELS_PER_TILE_SIDE))
                / PIXELS_PER_TILE_SIDE;
        int bottomRightYIndex = (int) (mapViewParameters.get().topLeftY() + canvas.getHeight() +
                (PIXELS_PER_TILE_SIDE - (mapViewParameters.get().topLeftY() + canvas.getHeight()) % PIXELS_PER_TILE_SIDE))
                / PIXELS_PER_TILE_SIDE;

        for (int i = topLeftYIndex; i <= bottomRightYIndex; ++i) {
            for (int j = topLeftXIndex; j <= bottomRightXIndex; ++j) {
                try {
                    Image tileImage = tileManager.imageForTileAt
                            (new TileManager.TileId(mapViewParameters.get().zoomLevel(), j, i));

                    graphicsContext.drawImage(tileImage,
                            j * PIXELS_PER_TILE_SIDE - mapViewParameters.get().topLeftX(),
                            i * PIXELS_PER_TILE_SIDE - mapViewParameters.get().topLeftY()); //FIXME

                } catch (IOException e) {
                    System.out.println("draw failed");
                    break;
                }
            }
        }
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        drawMap();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
