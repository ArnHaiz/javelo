package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import com.sun.javafx.geom.transform.Affine2D;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
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

import java.time.temporal.Temporal;

public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final ReadOnlyDoubleProperty highlightedPosition;
    private final Pane pane;
    private final ObjectProperty<Rectangle2D> rectangle = new ObjectProperty<Rectangle2D>() {
        @Override
        public void addListener(InvalidationListener listener) {

        }

        @Override
        public void removeListener(InvalidationListener listener) {

        }

        @Override
        public void addListener(ChangeListener<? super Rectangle2D> listener) {

        }

        @Override
        public void removeListener(ChangeListener<? super Rectangle2D> listener) {

        }

        @Override
        public Rectangle2D get() {
            return null;
        }

        @Override
        public void set(Rectangle2D value) {

        }

        @Override
        public Object getBean() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void bind(ObservableValue<? extends Rectangle2D> observable) {

        }

        @Override
        public void unbind() {

        }

        @Override
        public boolean isBound() {
            return false;
        }
    };
    private final ObjectProperty<Transform> screenToWorld = new ObjectProperty<Transform>() {
        @Override
        public void bind(ObservableValue<? extends Transform> observable) {

        }

        @Override
        public void unbind() {

        }

        @Override
        public boolean isBound() {
            return false;
        }

        @Override
        public Object getBean() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Transform get() {
            return null;
        }

        @Override
        public void addListener(ChangeListener<? super Transform> listener) {

        }

        @Override
        public void removeListener(ChangeListener<? super Transform> listener) {

        }

        @Override
        public void addListener(InvalidationListener listener) {

        }

        @Override
        public void removeListener(InvalidationListener listener) {

        }

        @Override
        public void set(Transform value) {

        }
    };
    private final ObjectProperty<Transform> worldToScreen = new ObjectProperty<Transform>() {
        @Override
        public void bind(ObservableValue<? extends Transform> observable) {

        }

        @Override
        public void unbind() {

        }

        @Override
        public boolean isBound() {
            return false;
        }

        @Override
        public Object getBean() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Transform get() {
            return null;
        }

        @Override
        public void addListener(ChangeListener<? super Transform> listener) {

        }

        @Override
        public void removeListener(ChangeListener<? super Transform> listener) {

        }

        @Override
        public void addListener(InvalidationListener listener) {

        }

        @Override
        public void removeListener(InvalidationListener listener) {

        }

        @Override
        public void set(Transform value) {

        }
    };

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty highlightedPosition) throws NonInvertibleTransformException {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;
        pane = new Pane();
        setRectangle(this.rectangle);
        CreateHierarchy(pane);
    }

    public Pane pane() {return pane;}

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return null;
    }

    private void CreateHierarchy(Pane pane) throws NonInvertibleTransformException {
        AddBorderPane(pane);
    }

    private void AddBorderPane(Pane pane) throws NonInvertibleTransformException {
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");
        AddPane(borderPane);
        AddVBox(borderPane);
        pane.getChildren().add(borderPane);
    }

    private void AddPane(BorderPane borderPane) throws NonInvertibleTransformException {
        Pane pane = new Pane();
        AddPath(pane);
        AddGroup(pane);
        AddPolygon(pane);
        AddLine(pane);
        new Insets(10, 10, 20, 40);
        borderPane.setCenter(pane);
    }

    private void AddVBox(BorderPane borderPane) {
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
        Group group = new Group();
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
        for(int i = 40; i<=pane.getWidth()-10; i++) {
            Transform toWorld = screenToWorld(i, 10);
            double height = elevationProfile.get().elevationAt(toWorld.getMyy());
            Transform toScreen = worldToScreen(toWorld.getMxx(), height);
            polygon.getPoints().add(i,toScreen.getMyy());
        }
        polygon.getPoints().add((int) (pane.getWidth()-10), pane.getHeight()-20);
        polygon.getPoints().add(40, pane.getHeight()-20);
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
        affine.prependTranslation(-40 , +20);
        affine.prependScale(elevationProfile.get().length()/(rectangle.get().getWidth()-50), (elevationProfile.get().maxElevation()-elevationProfile.get().minElevation()/(rectangle.get().getHeight()-30)));
        affine.prependTranslation(0, elevationProfile.get().minElevation());
        return affine;
    }

    private Transform worldToScreen(double xWorld, double yWorld) throws NonInvertibleTransformException {
        return screenToWorld(xWorld, yWorld).createInverse();
    }

    private void setRectangle(ObjectProperty<Rectangle2D> rectangle) {
        rectangle.set(new Rectangle2D(40,10, pane.getWidth(), pane.getHeight()));
    }
}
