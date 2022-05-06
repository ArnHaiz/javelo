package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

public final class BaseMapManager {
    TileManager tileManager;
    ObjectProperty<MapViewParameters> mapViewParameters;
    WaypointsManager waypointsManager;
    Canvas canvas;
    Pane pane;

    private final int PIXELS_PER_TILE_SIDE = 256;
    private boolean redrawNeeded = false;

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

        canvas.setOnScroll((scrollEvent -> {
            int newZoom = Math2.clamp(8,
                    (int) Math.round(mapViewParameters.get().zoomLevel() + scrollEvent.getDeltaY()),
                    19);
            double newX = mapViewParameters.get().topLeftX()
                    + scrollEvent.getDeltaY() * PIXELS_PER_TILE_SIDE;
            double newY = mapViewParameters.get().topLeftY()
                    + scrollEvent.getDeltaY() * PIXELS_PER_TILE_SIDE;

            mapViewParameters.set(new MapViewParameters(newZoom, newX, newY));
            redrawOnNextPulse();
        }));

        canvas.setOnMouseClicked(clickEvent -> {
            if (!clickEvent.isDragDetect()) {
                waypointsManager.addWaypoint(clickEvent.getX(), clickEvent.getY());
                redrawOnNextPulse();
            }

            System.out.println("4");
        });

        ObjectProperty<Point2D> pos = new SimpleObjectProperty<>();
        canvas.setOnMousePressed(e -> pos.set(new Point2D(e.getX(), e.getY())));
        canvas.setOnMouseDragged(dragEvent -> {
            if (!dragEvent.isStillSincePress()) {
                double newX = mapViewParameters.get().topLeftX() - dragEvent.getX() - pos.get().getX();
                double newY = mapViewParameters.get().topLeftY() - dragEvent.getY() - pos.get().getY();

                mapViewParameters.set(mapViewParameters.get().withMinXY(newX, newY));
                redrawOnNextPulse();
            }
        });
    }

    public Pane pane() {
        return pane;
    }

    private void drawMap() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        for (int i = 0; i < Math.ceil(canvas.getHeight() / PIXELS_PER_TILE_SIDE); i++) {
            for (int j = 0; j < Math.ceil(canvas.getWidth() / PIXELS_PER_TILE_SIDE); j++) {
                try {
                    Image tileImage = tileManager.imageForTileAt
                            (new TileManager.TileId(
                                    mapViewParameters.get().zoomLevel(),
                                            (int) mapViewParameters.get().topLeftX() / PIXELS_PER_TILE_SIDE + j,
                                            (int) mapViewParameters.get().topLeftY() / PIXELS_PER_TILE_SIDE + i));
                    graphicsContext.drawImage(tileImage,
                            j * PIXELS_PER_TILE_SIDE,
                            i * PIXELS_PER_TILE_SIDE ); //FIXME
                } catch (IOException e) {
                    System.out.println("1");
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
