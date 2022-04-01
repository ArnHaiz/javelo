package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static java.util.Collections.reverse;

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;
    RouteComputer(Graph graph, CostFunction costfunction) {
        this.graph = graph;
        this.costFunction = costfunction;
    }
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(!(startNodeId==endNodeId));
        double[] distance = new double[graph.nodeCount()];
        Arrays.fill(distance, Double.POSITIVE_INFINITY);
        int[] predecessor = new int[graph.nodeCount()];
        Arrays.fill(predecessor, 0);
        distance[startNodeId] = 0;
        LinkedList<Integer> inExploration = new LinkedList<>();
        inExploration.add(startNodeId);
        while(!inExploration.isEmpty()) {
            int node = 0;
            double length = Double.POSITIVE_INFINITY;
            for (Integer integer : inExploration) {
                if(distance[integer]<length) {
                    node = integer;
                    length = distance[integer];
                }
            }
            inExploration.remove((Integer) node);
            if(node==endNodeId) {
                break;
            }
            for (int i = 0; i < graph.nodeOutDegree(node); i++) {
                int newNode = graph.edgeTargetNodeId(graph.nodeOutEdgeId(node, i));
                double d = distance[node] + graph.edgeLength(graph.nodeOutEdgeId(node, i))* costFunction.costFactor(node,graph.nodeOutEdgeId(node, i));
                if (d < distance[newNode]) {
                    distance[newNode] = d;
                    predecessor[newNode] = node;
                    inExploration.add(newNode);
                }
            }
        }
        if(predecessor[endNodeId]==0) {
            return null;
        }else {
            List<Edge> edgePath = new ArrayList<>();
            int node = endNodeId;
            while (!(node == startNodeId)) {
                int fromNode = predecessor[node];
                int toNode = node;
                int index = obtainEdgeIndex(fromNode, toNode);
                double length = graph.edgeLength(graph.nodeOutEdgeId(fromNode, index));
                DoubleUnaryOperator profile = graph.edgeProfile(graph.nodeOutEdgeId(fromNode, index));
                edgePath.add(new Edge(fromNode, toNode, graph.nodePoint(fromNode), graph.nodePoint(toNode), length, profile));
                node = predecessor[node];
            }
            reverse(edgePath);
            return new SingleRoute(edgePath);
        }
    }
    private int obtainEdgeIndex(int startNodeId, int endNodeId) {
        int index = 0;
        for(int i = 0; i < graph.nodeOutDegree(startNodeId); i++) {
            if(graph.edgeTargetNodeId(graph.nodeOutEdgeId(startNodeId, i))==endNodeId) {
                index = i;
            }
        }
        return index;
    }
}
