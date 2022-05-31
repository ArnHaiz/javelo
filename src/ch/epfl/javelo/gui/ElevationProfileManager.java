package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import com.sun.javafx.scene.control.LabeledText;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.awt.*;

public final class ElevationProfileManager {
    private final BorderPane borderPane;
    private final Pane pane;
    private final Polygon polygon;
    private final Path path;
    private final VBox vBox;
    private final Line line;
    private final Group group;

    private final DoubleProperty mouseXPositionProperty;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final ReadOnlyDoubleProperty highlightedPosition;
    private final ObjectProperty<Point2D> pointUnderMouse = new SimpleObjectProperty<>(new Point2D(0, 0));

    private final ObjectProperty<Rectangle2D> rectangleProperty;
    javafx.geometry.Insets insets;
    private Transform screenToWorld;
    private Transform worldToScreen;

    private final int[] POS_STEPS =
            {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private final int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};
    private double horizontalStep = POS_STEPS[POS_STEPS.length - 1];
    private double verticalStep = ELE_STEPS[ELE_STEPS.length - 1];
    private final int MIN_VERTICAL_SPACING = 50;
    private final int MIN_HORIZONTAL_SPACING = 25;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;

        mouseXPositionProperty = new SimpleDoubleProperty(Double.NaN);
        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        insets = new Insets(10, 10, 20, 40);
        screenToWorld = screenToWorld();
        worldToScreen = worldToScreen();

        polygon = new Polygon();
        polygon.setId("profile");

        path = new Path();
        path.setId("grid");

        vBox = new VBox();
        vBox.setId("profile_data");

        line = new Line();

        group = new Group();

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

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            if (screenToWorld.transform(mouseXPositionProperty.get(), 0).getX() < 0 ||
                    screenToWorld.transform(mouseXPositionProperty.get(), 0).getX() > elevationProfile.get().length() ||
                    screenToWorld.transform(0, pointUnderMouse.get().getY()).getY() > elevationProfile.get().maxElevation() ||
                    screenToWorld.transform(0, pointUnderMouse.get().getY()).getY() < elevationProfile.get().minElevation()) {
                mouseXPositionProperty.set(Double.NaN);
                return Double.NaN;
            }
            return mouseXPositionProperty.get();
        }, pointUnderMouse));
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
        pane.getChildren().add(group);
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

        affine.prependTranslation(-insets.getLeft(), -insets.getTop());
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
        rectangleProperty.set(new Rectangle2D(
                40,
                10,
                Math.max(0, pane.getWidth() - 50),
                Math.max(0, pane.getHeight() - 30)));

        screenToWorld = screenToWorld();
        worldToScreen = worldToScreen();

        for (int i : POS_STEPS) {
            if (worldToScreen.deltaTransform(i, 0).getX() >= MIN_HORIZONTAL_SPACING) {
                horizontalStep = i;
                break;
            }
        }

        for (int i : ELE_STEPS) {
            if (-worldToScreen.deltaTransform(0, i).getY() >= MIN_VERTICAL_SPACING) {
                verticalStep = i;
                break;
            }
        }

        updatePolygon();
        updatePath();
        updateGroup();
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

    private void updateGroup() {
        //FIXME styleClass
        group.getChildren().clear();
        for (int i = 0; i <= elevationProfile.get().length(); i += horizontalStep) {
            Text text = new Text(String.valueOf(i / 1000));
            text.setFont(Font.font("Avenir", 10));
            text.textOriginProperty().set(VPos.TOP);
            text.setLayoutX(worldToScreen.transform(i, 0).getX() - text.prefWidth(0) / 2);
            text.setLayoutY(rectangleProperty.get().getMaxY());
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("horizontal");
            group.getChildren().add(text);
        }

        for (double i = elevationProfile.get().minElevation() + horizontalStep; i <= elevationProfile.get().maxElevation(); i += horizontalStep) {
            Text text = new Text(String.valueOf((int) elevationProfile.get().elevationAt(i)));
            text.setFont(Font.font("Avenir", 10));
            text.textOriginProperty().set(VPos.CENTER);
            text.setLayoutX(rectangleProperty.get().getMinX() - text.prefWidth(0));
            text.setLayoutY(worldToScreen.transform(0, i).getY());
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("vertical");
            group.getChildren().add(text);
        }
    }

    private void updatePath() {
        path.getElements().clear();
        //FIXME does not display the right number of lines
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
