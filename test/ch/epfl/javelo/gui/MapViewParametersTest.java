package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.SwissBounds;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapViewParametersTest {

    @Test
    public void topLeftWorks() {
        Point2D pointTL = new Point2D(SwissBounds.MIN_E, SwissBounds.MIN_N);
        assertEquals(pointTL, new MapViewParameters(0, SwissBounds.MIN_E, SwissBounds.MIN_N).topLeft());
    }

    @Test
    public void withMinXYWorks() {

    }
}
