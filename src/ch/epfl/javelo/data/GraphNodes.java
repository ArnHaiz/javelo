package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
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
     * function returning the total amount of nodes in the buffer.
     *
     * @return number of nodes in the buffer.
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * returns the east coordinate of the given node.
     *
     * @param nodeId the id of the wanted node
     * @return the east coordinate of the given node.
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * returns the north coordinate of the given node.
     *
     * @param nodeId the id of the wanted node
     * @return the north coordinate of the given node.
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * returns the number of edges coming out of the given node.
     *
     * @param nodeId the id of the wanted node
     * @return returns the number of edges coming out of the given node.
     */
    public int outDegree(int nodeId) {
        return buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES) >>> 28;
    }

    /**
     * returns the id of the edge <code>edgeIndex</code> coming out of the node <code>nodeId</code>.
     *
     * @param nodeId    the id of the wanted node
     * @param edgeIndex the index of the anted edge
     * @return the id of the edge <code>edgeIndex</code> coming out of the given node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(((buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES) << 4) >>> 4) + edgeIndex, 0, 31);
    }
}
