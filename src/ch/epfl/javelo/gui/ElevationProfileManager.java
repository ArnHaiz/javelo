package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public final class ElevationProfileManager {
    private final BorderPane borderPane;

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final ReadOnlyDoubleProperty highlightedPosition;

    private final ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>();

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty highlightedPosition) throws NonInvertibleTransformException {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;

        borderPane = new BorderPane();
        fillBorderPane();
    }

    public BorderPane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return null;
    } //TODO write this method

    private void fillBorderPane() throws NonInvertibleTransformException {
        borderPane.getStylesheets().add("elevation_profile.css");

        AddPane();
        AddVBox();

        borderPane.getChildren().add(new Text("can place text"));
    }

    private void AddPane() throws NonInvertibleTransformException {
        Pane pane = new Pane();
        setRectangle(pane);
        AddPath(pane);
        AddGroup(pane);
        AddPolygon(pane);
        AddLine(pane);

        borderPane.setCenter(pane);
    }

    private void AddVBox() {
        VBox vBox = new VBox();
        vBox.setId("profile_data");
        AddTextVBox(vBox);
        borderPane.setBottom(vBox);
    }

    private void AddTextVBox(VBox vBox) {
        Text text = new Text();
        vBox.getChildren().add(text);
    }

    private void AddPath(Pane pane) {
        Path path = new Path();
        path.setId("grid");
        pane.getChildren().add(path);
    }

    private void AddGroup(Pane pane) {
        Group group = new Group(); //FIXME need to add the stylesheets of all the elements going in the group
        pane.getChildren().add(group);
    }

    private void AddTextGroup(Group group, String string) {
        Text text = new Text();
        text.getStyleClass().add("grid_label");
        text.getStyleClass().add(string);
        group.getChildren().add(text);
    }

    private void AddPolygon(Pane pane) throws NonInvertibleTransformException {
        Polygon polygon = new Polygon();
        polygon.setId("profile");

        polygon.getPoints().add(rectangle.get().getMinX());
        polygon.getPoints().add(rectangle.get().getMinY());

        for (int i = 40; i < rectangle.get().getWidth() + 40; i++) {
            Transform toWorld = screenToWorld(i, 10 + rectangle.get().getHeight());
            double height = elevationProfile.get().elevationAt(toWorld.getMyy());
            Transform toScreen = worldToScreen(toWorld.getMxx(), height);
            polygon.getPoints().add(i, toScreen.getMyy());
        }

        polygon.getPoints().add(rectangle.get().getMaxX());
        polygon.getPoints().add(rectangle.get().getMaxY());

        polygon.setLayoutX(rectangle.get().getMinX());
        polygon.setLayoutY(rectangle.get().getMinY());

        pane.getChildren().add(polygon);
    }

    private void AddLine(Pane pane) {
        Line line = new Line();
        pane.getChildren().add(line);
    }

    private Transform screenToWorld(double xScreen, double yScreen) {
        Affine affine = new Affine();
        affine.setMxx(xScreen);
        affine.setMyy(yScreen);
        affine.prependTranslation(-40, +20);
        affine.prependScale(elevationProfile.get().length() / (rectangle.get().getWidth() - 50), (elevationProfile.get().maxElevation() - elevationProfile.get().minElevation() / (rectangle.get().getHeight() - 30)));
        affine.prependTranslation(0, elevationProfile.get().minElevation());
        return affine;
    }

    private Transform worldToScreen(double xWorld, double yWorld) throws NonInvertibleTransformException {
        return screenToWorld(xWorld, yWorld).createInverse();
    }

    private void setRectangle(Pane pane) {
        Insets insets = new Insets(10, 10, 20, 40);
        /*ObjectBinding<Rectangle> rectBind = Bindings.createObjectBinding(() -> {
            return new Rectangle(pane.getWidth() - 50, pane.getHeight() - 30);
        }, pane.getWidth(), pane.getHeight());*/
        //Plus faim
    }
}
