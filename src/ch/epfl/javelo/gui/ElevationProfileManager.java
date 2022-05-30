package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.awt.*;

public final class ElevationProfileManager {
    private BorderPane borderPane;
    private Pane pane;
    private Polygon polygon;
    private Path path;
    private VBox vBox;
    private Line line;

    private final DoubleProperty mouseXPositionProperty;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final ReadOnlyDoubleProperty highlightedPosition;
    private ObjectProperty<Point2D> pointUnderMouse = new SimpleObjectProperty<>(new Point2D(0, 0));

    private final ObjectProperty<Rectangle2D> rectangleProperty;
    Insets insets;
    private Transform screenToWorld;
    private Transform worldToScreen;

    private final int MIN_VERTICAL_SPACING = 50;
    private final int MIN_HORIZONTAL_SPACING = 25;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;

        mouseXPositionProperty = new SimpleDoubleProperty(Double.NaN);
        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        insets = new Insets(10, 40, 20, 10);
        screenToWorld = screenToWorld();
        worldToScreen = worldToScreen();

        polygon = new Polygon();
        polygon.setId("profile");

        path = new Path();
        path.setId("grid");

        vBox = new VBox();
        vBox.setId("profile_data");

        line = new Line();

        pane = new Pane();

        borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);

        fillPane();
        fillVBox();

        pane.widthProperty().addListener(((observable, oldValue, newValue) -> updatePane()));

        pane.heightProperty().addListener(((observable, oldValue, newValue) -> updatePane()));

        pane.setOnMouseMoved(event -> {
            pointUnderMouse.set(new Point2D(event.getSceneX(), event.getSceneY()));
            mouseXPositionProperty.set(pointUnderMouse.get().getX());
        });

        pane.setOnMouseExited(event -> mouseXPositionProperty.set(Double.NaN));

        line.layoutXProperty().bind(mousePositionOnProfileProperty());
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> rectangleProperty.get().getMinY(), rectangleProperty));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> rectangleProperty.get().getMaxY(), rectangleProperty));
        line.visibleProperty().bind(mousePositionOnProfileProperty().greaterThanOrEqualTo(0));
    }

    public BorderPane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mouseXPositionProperty;
    }

    private void fillPane() {
        screenToWorld = screenToWorld();
        worldToScreen = worldToScreen();

        pane.getChildren().add(path);
        pane.getChildren().add(polygon);
        pane.getChildren().add(line);
        //TODO add groups with their texts ans tags
    }

    private void fillVBox() {
        double length = elevationProfile.get().length() * 1e-3;
        double ascent = elevationProfile.get().totalAscent();
        double descent = elevationProfile.get().totalDescent();
        double minElevation = elevationProfile.get().minElevation();
        double maxElevation = elevationProfile.get().maxElevation();
        Text text = new Text(String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                length,
                ascent,
                descent,
                minElevation,
                maxElevation));
        vBox.getChildren().add(text);
    }

    private Transform screenToWorld() {
        Affine affine = new Affine();

        affine.prependTranslation(-insets.left, -insets.top);
        affine.prependScale(elevationProfile.get().length() /
                        rectangleProperty.get().getWidth(),
                (elevationProfile.get().minElevation() - elevationProfile.get().maxElevation()) /
                        rectangleProperty.get().getHeight());
        affine.prependTranslation(0, elevationProfile.get().maxElevation());

        return affine;
    }

    private Transform worldToScreen() {
        try {
            return screenToWorld.createInverse();
        } catch (NonInvertibleTransformException e) {
            return null;
        }
    }

    private void updatePane() {
        System.out.println("updating pane");
        rectangleProperty.set(new Rectangle2D(
                40,
                10,
                Math.max(0, pane.getWidth() - 50),
                Math.max(0, pane.getHeight() - 30)));

        screenToWorld = screenToWorld();
        worldToScreen = worldToScreen();

        updateLine();
        updateGroup();
        //updatePolygon();
        updatePath();
    }

    private void updatePolygon() {
        polygon.getPoints().clear();
        Rectangle2D rect = rectangleProperty.get();
        ElevationProfile profile = elevationProfile.get();

        polygon.getPoints().add(rect.getMinX());
        polygon.getPoints().add(rect.getMaxY());

        for (int i = 0; i < rect.getWidth(); ++i) {
            double position = ((double) i) / rect.getWidth() * profile.length();

            Point2D transformed = worldToScreen.transform(
                    position,
                    profile.elevationAt(position));
            polygon.getPoints().add(i + rect.getMinX());
            polygon.getPoints().add(transformed.getY());

        }

        polygon.getPoints().add(rect.getMaxX());
        polygon.getPoints().add(rect.getMaxY());
        polygon.setFill(Color.RED);

    }

    private void updateLine() {
        //TODO complete the method
    }

    private void updateGroup() {
        //TODO complete the method
    }

    private void updatePath() {
        path.getElements().clear();
        //FIXME does not display the right number of lines
        int[] POS_STEPS =
                {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
        int[] ELE_STEPS =
                {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

        double horizontalStep = POS_STEPS[POS_STEPS.length - 1];
        for (int i : POS_STEPS) {
            if (worldToScreen.deltaTransform(0, i).getY() >= MIN_HORIZONTAL_SPACING) {
                horizontalStep = i;
                break;
            }
        }
        System.out.println(horizontalStep);

        double verticalStep = ELE_STEPS[ELE_STEPS.length - 1];
        for (int i : ELE_STEPS) {
            if (worldToScreen.deltaTransform(i,0).getX() >= MIN_VERTICAL_SPACING) {
                verticalStep = i;
                break;
            }
        }
        System.out.println(verticalStep);

        for (int i = 0; i <= elevationProfile.get().length(); i += horizontalStep) {
            Point2D startPoint = worldToScreen.transform(i, elevationProfile.get().minElevation());
            Point2D endPoint = worldToScreen.transform(i, elevationProfile.get().maxElevation());

            path.getElements().add(new MoveTo(startPoint.getX(), startPoint.getY()));
            path.getElements().add(new LineTo(endPoint.getX(), endPoint.getY()));
        }

        for (double i = elevationProfile.get().minElevation(); i <= elevationProfile.get().maxElevation(); i += verticalStep) {
            Point2D startPoint = worldToScreen.transform(0, i);
            Point2D endPoint = worldToScreen.transform(elevationProfile.get().length(), i);

            path.getElements().add(new MoveTo(startPoint.getX(), startPoint.getY()));
            path.getElements().add(new LineTo(endPoint.getX(), endPoint.getY()));
        }
    }
}
