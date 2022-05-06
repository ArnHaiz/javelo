package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.DoubleUnaryOperator;

import static java.util.Collections.reverse;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * class representing an itinerary planner
 */
public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;
    private final float NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;

    /**
     * construct an itinerary planner
     *
     * @param graph        : graph where the itinerary will be planned
     * @param costfunction : function that help to find an itinerary
     */
    public RouteComputer(Graph graph, CostFunction costfunction) {
        this.graph = graph;
        this.costFunction = costfunction;
    }

    /**
     * return the best itinerary on the graph or 0 if there is no itinerary
     *
     * @param startNodeId : id of the starting node of the itinerary
     * @param endNodeId   : id of the ending node of the itinerary
     * @return the best itinerary on the graph or 0 if there is no itinerary
     * @throws IllegalArgumentException if the start node and the end node are the same
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        /**
         * @author prof
         *
         * recorde representing a node with weight given by its distance
         * @param nodeId : id of the node
         * @param distance : weight distance attached to the node
         */
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }
        Preconditions.checkArgument(startNodeId != endNodeId);
        double[] distance = new double[graph.nodeCount()];
        Arrays.fill(distance, Double.POSITIVE_INFINITY);
        int[] predecessor = new int[graph.nodeCount()];
        distance[startNodeId] = 0;
        PriorityQueue<WeightedNode> inExploration = new PriorityQueue<>();
        inExploration.add(new WeightedNode(startNodeId, 0));
        while (!inExploration.isEmpty()) {
            int node = inExploration.remove().nodeId;
            if (distance[node] != NEGATIVE_INFINITY) {
                if (node == endNodeId) {
                    break;
                }
                for (int i = 0; i < graph.nodeOutDegree(node); i++) {
                    int newNode = graph.edgeTargetNodeId(graph.nodeOutEdgeId(node, i));
                    double d = distance[node]
                            + graph.edgeLength(graph.nodeOutEdgeId(node, i))
                            * costFunction.costFactor(node, graph.nodeOutEdgeId(node, i));
                    if (d < distance[newNode]) {
                        distance[newNode] = d;
                        predecessor[newNode] = node;
                        inExploration.add(new WeightedNode(newNode, (float) (distance[newNode]
                                + graph.nodePoint(newNode).distanceTo(graph.nodePoint(endNodeId)))));
                    }
                }
                distance[node] = NEGATIVE_INFINITY;
            }
        }
        if (predecessor[endNodeId] == 0) {
            return null;
        } else {
            List<Edge> edgePath = new ArrayList<>();
            int node = endNodeId;
            while (!(node == startNodeId)) {
                int fromNode = predecessor[node];
                int toNode = node;
                int index = obtainEdgeIndex(fromNode, toNode);
                edgePath.add(Edge.of(graph, graph.nodeOutEdgeId(fromNode,index), fromNode, toNode));
                node = predecessor[node];
            }
            reverse(edgePath);
            return new SingleRoute(edgePath);
        }
    }

    private int obtainEdgeIndex(int startNodeId, int endNodeId) {
        int index = 0;
        for (int i = 0; i < graph.nodeOutDegree(startNodeId); i++) {
            if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(startNodeId, i)) == endNodeId) {
                index = i;
            }
        }
        return index;
    }
}
