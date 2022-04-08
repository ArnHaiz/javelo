package ch.epfl.javelo.routing;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * interface representing a cost function.
 */
public interface CostFunction {

    /**
     * returns the factor by which the edge of id <code>edgeId</code>
     * starting at node <code>nodeId</code> will be multiplied by ;
     * the factor must be superior or equal to 1.
     * <code>edgeId</code> must be one coming out of <code>nodeId</code>.
     *
     * @param nodeId the first node of the wanted edge
     * @param edgeId the to-multiply edge
     * @return the multiplying factor of edge <code>edgeId</code> that starts at node <code>nodeId</code>.
     */
    double costFactor(int nodeId, int edgeId);
}
