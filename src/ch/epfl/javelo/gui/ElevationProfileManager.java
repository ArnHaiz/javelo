package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public final class ElevationProfileManager {
    private final BorderPane borderPane;
    private final Pane pane;
    private final Polygon polygon;
    private final Path path;
    private final VBox vBox;
    private final Line line;

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
        fillBorderPane();


    }

    public BorderPane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return null;
    } //TODO write this method

    private void fillBorderPane() {
        AddPane();
        AddVBox();

        borderPane.getChildren().add(new TextFlow(new Text("can place text")));
    }

    private void AddPane() {
        bindRectangleProperty();
        AddPath();
        AddGroup();
        AddPolygon();
        AddLine();


        borderPane.setCenter(pane);
        BorderPane.setMargin(pane, new Insets(10, 10, 20, 40));
    }

    private void AddVBox() {
        Text text = new Text();
        vBox.getChildren().add(text);
        borderPane.setBottom(vBox);
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
        redrawPolygon();
        pane.getChildren().add(polygon);
    }

    private void AddLine() {
        pane.getChildren().add(line);
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

    private void bindRectangleProperty() {
        rectangleProperty.bind(Bindings.createObjectBinding(this::updateRectangle, pane.widthProperty(), pane.heightProperty()));
    }

    private Rectangle2D updateRectangle() {
        System.out.println("rectangle is being updated");
        return new Rectangle2D(
                0,
                0,
                Math2.clamp(0, pane.getWidth() - 30, borderPane.getWidth()),
                Math2.clamp(0, pane.getHeight() - 50, borderPane.getHeight()));
    }

    private void redrawPolygon() {
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
}
