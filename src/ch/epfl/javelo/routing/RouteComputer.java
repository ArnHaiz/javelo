package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

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
        while(inExploration.size()>0) {
            int node = 0;
            for (Integer integer : inExploration) {
                double length = Double.POSITIVE_INFINITY;
                if(distance[integer]<length) {
                    node = integer;
                }
            }
            inExploration.remove(node);
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
            while (node >= startNodeId) {
                int fromNode = predecessor[node-startNodeId];
                int toNode = node;
                int index = obtainEdgeIndex(fromNode, toNode);
                double length = graph.edgeLength(graph.nodeOutEdgeId(fromNode, index));
                DoubleUnaryOperator profile = graph.edgeProfile(graph.nodeOutEdgeId(fromNode, index));
                edgePath.add(new Edge(fromNode, toNode, graph.nodePoint(fromNode), graph.nodePoint(toNode), length, profile));
                node = node-predecessor[node-startNodeId];
            }
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
