package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class GraphNodesTest {

    @Test
    public void countReturnsCorrectAmount() {
        IntBuffer bt = IntBuffer.wrap(new int[]{});
        IntBuffer bt2 = IntBuffer.wrap(new int[] {1, 2, 3, 4, 5, 6});
        GraphNodes graph = new GraphNodes(bt);
        GraphNodes graph2 = new GraphNodes(bt2);
        assertEquals(0, graph.count());
        assertEquals(2, graph2.count());
    }

    @Test
    public void nodeEReturnsCorrectValue() {
        IntBuffer bt = IntBuffer.wrap(new int[] {1, 2, 3, 4, 5, 6});
        GraphNodes graph = new GraphNodes(bt);
        assertEquals(1, graph.nodeE(0));
        assertEquals(4, graph.nodeE(1));
    }

    @Test
    public void nodeNReturnsCorrectValue() {
        IntBuffer bt = IntBuffer.wrap(new int[] {1, 2, 3, 4, 5, 6});
        GraphNodes graph = new GraphNodes(bt);
        assertEquals(2, graph.nodeN(0));
        assertEquals(5, graph.nodeN(1));
    }

    @Test
    public void outDegreeReturnsCorrectValue() {
        IntBuffer bt = IntBuffer.wrap(new int[] {1, 2,268435456});
        GraphNodes graph = new GraphNodes(bt);
        assertEquals(1, graph.outDegree(0));
    }

    @Test
    public void outDegreeOnZeroEdge() {
        IntBuffer bt = IntBuffer.wrap(new int[] {1, 2, 268435455});
        GraphNodes graph = new GraphNodes(bt);
        assertEquals(0, graph.outDegree(0));
    }

    @Test
    public void edgeIdReturnsCorrectValue() {
        IntBuffer bt = IntBuffer.wrap((new int[] {1, 2, 268435457}));
        GraphNodes graph = new GraphNodes(bt);
        assertEquals(1, graph.edgeId(0, 0));
    }
}
