package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;

        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());

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

        /*rectangleProperty.bind(Bindings.createObjectBinding({new Rectangle2D(
                        0,
                        0,
                        Math2.clamp(0, pane.getWidth() - 30, borderPane.getWidth()),
                        Math2.clamp(0, pane.getHeight() - 50, borderPane.getHeight()))},
                pane.widthProperty(),
                pane.heightProperty()));*/

        pane.widthProperty().addListener(((observable, oldValue, newValue) ->
                rectangleProperty.set(new Rectangle2D(40, 10, Math.max(pane.getWidth() - 50, 0), Math.max(pane.getHeight() - 50, 0)))));

        pane.heightProperty().addListener(((observable, oldValue, newValue) ->
                rectangleProperty.set(new Rectangle2D(40, 10, Math.max(pane.getWidth() - 50, 0), Math.max(pane.getHeight() - 50, 0)))));
    }

    public BorderPane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return null;
    } //TODO write this method

    private void fillPane() {
        AddPath();
        AddGroup();
        AddPolygon();
        AddLine();
    }

    private void fillVBox() {
        Text text = new Text("this is the VBox");
        vBox.getChildren().add(text);
    }

    private void AddPath() {
        pane.getChildren().add(path);
    }

    private void AddGroup() {
        Group group = new Group(); //FIXME need to add the stylesheets of all the elements going in the group
        pane.getChildren().add(group);
    }

    private void AddTextGroup(Group group, String string) {
        Text text = new Text();
        text.getStyleClass().add("grid_label");
        text.getStyleClass().add(string);
        group.getChildren().add(text);
    }

    private void AddPolygon() {
        pane.getChildren().add(polygon);
        redrawPolygon();

    }

    private void AddLine() {
        pane.getChildren().add(line);
        redrawLine();
    }

    private Transform screenToWorld(double xScreen, double yScreen) {
        Affine affine = new Affine();

        affine.setMxx(xScreen);
        affine.setMyy(yScreen);
        affine.prependTranslation(-40, +20);
        affine.prependScale(elevationProfile.get().length() / (rectangleProperty.get().getWidth() - 50),
                (elevationProfile.get().maxElevation() - elevationProfile.get().minElevation() /
                        (rectangleProperty.get().getHeight() - 30)));
        affine.prependTranslation(0, elevationProfile.get().minElevation());

        return affine;
    }

    private Transform worldToScreen(double xWorld, double yWorld) {
        try {
            return screenToWorld(xWorld, yWorld).createInverse();
        } catch (NonInvertibleTransformException e) {
            return null;
        }
    }

    private void redrawPolygon() {
        System.out.println("updating polygon");
        polygon.getPoints().clear();
        if (rectangleProperty.get() != null) {
            polygon.getPoints().add(rectangleProperty.get().getMinX());
            polygon.getPoints().add(rectangleProperty.get().getMinY() + rectangleProperty.get().getHeight());

            for (int i = 40; i < rectangleProperty.get().getWidth() + 40; i++) {
                Transform toWorld = screenToWorld(i, 10 + rectangleProperty.get().getHeight());
                double height = elevationProfile.get().elevationAt(toWorld.getMyy());
                Transform toScreen = worldToScreen(toWorld.getMxx(), height);
                polygon.getPoints().add(i, toScreen.getMyy());
            }

            polygon.getPoints().add(rectangleProperty.get().getMaxX());
            polygon.getPoints().add(rectangleProperty.get().getMaxY() - rectangleProperty.get().getHeight());

            polygon.setLayoutX(0);
            polygon.setLayoutY(rectangleProperty.get().getHeight());
        }
    }

    private void redrawLine() {

    }
}
