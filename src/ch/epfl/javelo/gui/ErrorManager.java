package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.function.Consumer;

public final class ErrorManager {

    private final VBox vBox;
    private final Text text = new Text();
    private final SequentialTransition animation = new SequentialTransition();

    private final double APPARITION_TIME = 200;
    private final double WAITING_TIME = 2000;
    private final double DISAPPEARING_TIME = 500;

    public ErrorManager() {
        vBox = new VBox();
        vBox.getStylesheets().add("error.css");
        vBox.getChildren().add(text);
        vBox.setMouseTransparent(true);
    }

    public Pane pane() {return vBox;}

    public void display(String string) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        text.setText(string);
        animation.stop();
        createAnimation(vBox);
        animation.play();
    }

    private void createAnimation(VBox vBox) {
        FadeTransition apparition = new FadeTransition(new Duration(APPARITION_TIME), vBox);
        PauseTransition pause = new PauseTransition(new Duration(WAITING_TIME));
        FadeTransition disappearance = new FadeTransition(new Duration(DISAPPEARING_TIME), vBox);

        apparition.setToValue(0.8);
        disappearance.setToValue(0);

        animation.getChildren().add(apparition);
        animation.getChildren().add(pause);
        animation.getChildren().add(disappearance);
    }
}
