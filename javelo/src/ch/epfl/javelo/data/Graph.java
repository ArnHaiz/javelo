package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph {
    private GraphNodes nodes;
    private GraphSectors sectors;
    private GraphEdges edges;
    private List<AttributeSet> attributeSets;

    public static Graph loadFrom(Path basePath)throws IOException {
        basePath = Path.of("lausanne");
        Path nodesPath = basePath.resolve("nodes.bin");
        try {
            Path filePath = Path.of("lausanne/nodes_osmid.bin");
            LongBuffer osmIdBuffer;
            try (FileChannel channel = FileChannel.open(filePath)) {
                osmIdBuffer = channel
                        .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                        .asLongBuffer();
            }
        }catch (Exception e) {
            throw new IOException();
        }
    }
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }
    public int nodeCount() {
        return nodes.count();
    }
    public PointCh nodePoint(int nodeId) {
        PointCh nodePos = new PointCh(nodes.nodeE(nodeId), GraphNodes.nodeN(nodeId));
        return nodePos;
    }
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }
    public int nodeClosestTo(PointCh point, double searchDistance) {
        int nodeId;
        double distance = searchDistance*searchDistance;
        int counter = 0;
        List<Sector> targetSector = 
        return nodeId;
    }
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.AttributeSet(edgeId);
    }
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        float[] sample = edges.profileSamples(edgeId);
        if(sample.length==0) {
            return Double.NaN;
        }else{
            return Functions.sampled(sample, GraphEdges.elevationGain(edgeId));
        }
    }
}
