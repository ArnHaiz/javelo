package ch.epfl.javelo.data;

import java.nio.IntBuffer;

/**
 * record representing all the nods in Switzerland.
 */
public record GraphNodes(IntBuffer buffer) {
    /**
     * index of the relative position containing the east coordinate of the node.
     */
    private static final int OFFSET_E = 0;
    /**
     * index of the relative position containing the north coordinate of the node.
     */
    private static final int OFFSET_N = OFFSET_E + 1;
    /**
     * index of the relative position containing the number of edges coming out of the node.
     */
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    /**
     * number of integers necessary to represent a node.
     */
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    /**
     * returns the total number of nodes in the graph
     */
    public int count() {return buffer.capacity()/NODE_INTS;}
    /**
     * returns the east coordinate of the given node
     */
    public double nodeE(int nodeId) {return buffer.get(nodeId * NODE_INTS +OFFSET_E);}
    /**
     * returns the north coordinate of the given node
     */
    public double nodeN(int nodeId) {return buffer.get(nodeId * NODE_INTS + OFFSET_N);}
    /**
     * returns the number of edges coming out of the given node
     */
    public int outDegree(int nodeId) {return buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES) >> 28;}
    /**
     * returns the id of the <code>edgeIndex</code> edge coming out of the given node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return ((buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES) << 4) >> 4) + edgeIndex;
    }
}
