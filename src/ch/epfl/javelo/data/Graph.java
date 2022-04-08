package ch.epfl.javelo.data;


import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * class representing a graph
 */
public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * return the graph given by the data obtained
     *
     * @param basePath : directory of the data
     * @return the graph given by the data obtained
     * @throws IOException if there is an error in the load of the data
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        Path nodesPath = basePath.resolve("nodes.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path attributeSetsPath = basePath.resolve("attributes.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");
        Path profile_idsPath = basePath.resolve("profile_ids.bin");
        IntBuffer channelNodesBuffer;
        ByteBuffer channelSectorsBuffer;
        ByteBuffer channelEdgesBuffer;
        ShortBuffer channelElevationsBuffer;
        IntBuffer channelProfile_idsBuffer;
        LongBuffer channelAttributeSetsBuffer;

        try (FileChannel channelNodes = FileChannel.open(nodesPath);
             FileChannel channelSectors = FileChannel.open(sectorsPath);
             FileChannel channelEdges = FileChannel.open(edgesPath);
             FileChannel channelAttributes = FileChannel.open(attributeSetsPath);
             FileChannel channelElevations = FileChannel.open(elevationsPath);
             FileChannel channelProfile_ids = FileChannel.open(profile_idsPath)) {

            channelNodesBuffer = channelNodes.map(FileChannel.MapMode.READ_ONLY, 0, channelNodes.size()).asIntBuffer();
            channelSectorsBuffer = channelSectors.map(FileChannel.MapMode.READ_ONLY, 0, channelSectors.size());
            channelEdgesBuffer = channelEdges.map(FileChannel.MapMode.READ_ONLY, 0, channelEdges.size());
            channelElevationsBuffer = channelElevations.map(FileChannel.MapMode.READ_ONLY, 0, channelElevations.size()).asShortBuffer();
            channelProfile_idsBuffer = channelProfile_ids.map(FileChannel.MapMode.READ_ONLY, 0, channelProfile_ids.size()).asIntBuffer();
            channelAttributeSetsBuffer = channelAttributes.map(FileChannel.MapMode.READ_ONLY, 0, channelAttributes.size()).asLongBuffer();
        }
        List<AttributeSet> attributeSets = new ArrayList<>();
        for (int i = 0; i < channelAttributeSetsBuffer.capacity(); i++) {
            attributeSets.add(new AttributeSet(channelAttributeSetsBuffer.get(i)));
        }

        GraphNodes nodes = new GraphNodes(channelNodesBuffer);
        GraphSectors sectors = new GraphSectors(channelSectorsBuffer);
        GraphEdges edges = new GraphEdges(channelEdgesBuffer, channelProfile_idsBuffer, channelElevationsBuffer);
        return new Graph(nodes, sectors, edges, attributeSets);
    }

    /**
     * construct the graph with the given parameters
     *
     * @param nodes         : data of the graph's nodes
     * @param sectors       : data of the graph's sectors
     * @param edges         : data of the graph's edges
     * @param attributeSets : data of the attributes attached on the nodes and edges
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = new ArrayList<>(attributeSets);
    }

    /**
     * return the total number of nodes of the graph
     *
     * @return the total number of nodes of the graph
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * return the position of the node of given id
     *
     * @param nodeId : id of the node
     * @return the position of the node of given id
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * return the number of edges going out of a given node
     *
     * @param nodeId : id of the node
     * @return the number of edges going out of a given node
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * return the id i-th edge going out of the given node
     *
     * @param nodeId    : id of the node
     * @param edgeIndex : index of the edge
     * @return the id i-th edge going out of the given node
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * return the closet node to a given position or -1 if there isn't any node in the max distance given
     *
     * @param point          : coordinate of the point
     * @param searchDistance : max distance of search
     * @return the closet node to a given position or -1 if there isn't any node in the max distance given
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        int nodeId = 0;
        double distance = searchDistance * searchDistance;
        int counter = 0;
        List<GraphSectors.Sector> targetSector = sectors.sectorsInArea(point, searchDistance);
        for (int i = 0; i < targetSector.size(); i++) {
            GraphSectors.Sector workSector = targetSector.get(i);
            for (int j = workSector.startNodeId(); j < workSector.endNodeId(); j++) {
                PointCh target = nodePoint(j);
                if (target.squaredDistanceTo(point) <= distance) {
                    nodeId = j;
                    counter++;
                    distance = target.squaredDistanceTo(point);
                }
            }
        }
        if (counter == 0) {
            nodeId = -1;
        }
        return nodeId;
    }

    /**
     * return the destination's node of a given edge
     *
     * @param edgeId : id of the edge
     * @return the destination's node of a given edge
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * return true if the edge is inverted
     *
     * @param edgeId : id of the edge
     * @return true if the edge is inverted
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * return all the OSM attributes attached to a given edge
     *
     * @param edgeId : id of the edge
     * @return all the OSM attributes attached to a given edge
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * return the length of a given edge
     *
     * @param edgeId : id of the edge
     * @return the length of a given edge
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * return the total positive height difference
     *
     * @param edgeId : id of the edge
     * @return the total positive height difference
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * return the profile in length of a given edge, under the form of a function,
     * if the edge has no profile return Double.Nan
     *
     * @param edgeId : id of the edge
     * @return the profile in length of a given edge, under the form of a function,
     * if the edge has no profile return Double.Nan
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        float[] sample = edges.profileSamples(edgeId);
        if (!edges.hasProfile(edgeId)) {
            return Functions.constant(Double.NaN);
        } else {
            return Functions.sampled(sample, edges.length(edgeId));
        }
    }
}