package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public final class ElevationProfileManager {
    private BorderPane borderPane;
    private Pane pane;
    private Polygon polygon;
    private Path path;
    private VBox vBox;
    private Line line;

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final ReadOnlyDoubleProperty highlightedPosition;

    private final ObjectProperty<Rectangle2D> rectangleProperty;
    private Transform screenToWorld;
    private Transform worldToScreen;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;

        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
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
    }

    public BorderPane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return null;
    } //TODO write this method

    private void fillPane() {
        screenToWorld = screenToWorld();
        worldToScreen = worldToScreen();

        addPath();
        addGroup();
        addPolygon();
        addLine();
    }

    private void fillVBox() {
        double length = elevationProfile.get().length()*1e-3;
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

    private void addPath() {
        pane.getChildren().add(path);
    }

    private void addGroup() {
        Group group = new Group(); //FIXME need to add the stylesheets of all the elements going in the group
        pane.getChildren().add(group);
    }

    private void addTextGroup(Group group, String string) {
        Text text = new Text();
        text.getStyleClass().add("grid_label");
        text.getStyleClass().add(string);
        group.getChildren().add(text);
    }

    private void addPolygon() {
        pane.getChildren().add(polygon);
    }

    private void addLine() {
        pane.getChildren().add(line);
        updateLine();
    }

    private Transform screenToWorld() {
        try {
            return worldToScreen().createInverse();
        } catch (NonInvertibleTransformException e) {
            return new Affine();
        }
    }

    private Transform worldToScreen() {
        Affine affine = new Affine();

        affine.prependScale(
                rectangleProperty.get().getWidth() / (elevationProfile.get().length()),
                -rectangleProperty.get().getHeight() / elevationProfile.get().maxElevation());
        affine.prependTranslation(rectangleProperty.get().getMinX(), rectangleProperty.get().getMaxY());
        return affine;

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

        updatePolygon();
        updateLine();
        updateGroup();
        updatePath();
    }

    private void updatePolygon() {
        polygon.getPoints().clear();
        Rectangle2D rect = rectangleProperty.get();

        polygon.getPoints().add(rect.getMinX());
        polygon.getPoints().add(rect.getMaxY());

        for (int i = 0; i < rect.getWidth(); ++i) {
            double position = ((double) i) / rect.getWidth() * elevationProfile.get().length();

            Point2D transformed = worldToScreen.transform(position, elevationProfile.get().elevationAt(position));
            polygon.getPoints().add(i + rect.getMinX());
            polygon.getPoints().add(transformed.getY());

        }

        polygon.getPoints().add(rect.getMaxX());
        polygon.getPoints().add(rect.getMaxY());
        polygon.setFill(Color.RED);
    }

    private void updateLine() {

    } //TODO complete the method

    private void updateGroup() {
    } //TODO complete the method

    private void updatePath() {
    } //TODO complete the method
}
