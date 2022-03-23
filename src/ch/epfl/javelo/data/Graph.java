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

public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    public static Graph loadFrom(Path basePath)throws IOException {
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
            FileChannel channelProfile_ids = FileChannel.open(profile_idsPath)){

            channelNodesBuffer = channelNodes.map(FileChannel.MapMode.READ_ONLY, 0, channelNodes.size()).asIntBuffer();
            channelSectorsBuffer = channelSectors.map(FileChannel.MapMode.READ_ONLY, 0, channelSectors.size());
            channelEdgesBuffer = channelEdges.map(FileChannel.MapMode.READ_ONLY, 0, channelEdges.size());
            channelElevationsBuffer = channelElevations.map(FileChannel.MapMode.READ_ONLY, 0, channelElevations.size()).asShortBuffer();
            channelProfile_idsBuffer = channelProfile_ids.map(FileChannel.MapMode.READ_ONLY, 0, channelProfile_ids.size()).asIntBuffer();
            channelAttributeSetsBuffer = channelAttributes.map(FileChannel.MapMode.READ_ONLY, 0, channelAttributes.size()).asLongBuffer();
        }
        List<AttributeSet> attributeSets = new ArrayList<>();
        for(int i = 0; i< channelAttributeSetsBuffer.capacity(); i++) {
            attributeSets.add(new AttributeSet(channelAttributeSetsBuffer.get(i)));
        }

        GraphNodes nodes = new GraphNodes(channelNodesBuffer);
        GraphSectors sectors = new GraphSectors(channelSectorsBuffer);
        GraphEdges edges = new GraphEdges(channelEdgesBuffer, channelProfile_idsBuffer, channelElevationsBuffer);
        return new Graph(nodes, sectors, edges, attributeSets);
    }
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = new ArrayList<>(attributeSets);
    }
    public int nodeCount() {
        return nodes.count();
    }
    public PointCh nodePoint(int nodeId) {
        PointCh nodePos = new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
        return nodePos;
    }
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }
    public int nodeClosestTo(PointCh point, double searchDistance) {
        int nodeId = 0;
        double distance = searchDistance*searchDistance;
        int counter = 0;
        List<GraphSectors.Sector> targetSector = sectors.sectorsInArea(point, searchDistance);
        for(int i = 0; i<targetSector.size(); i++) {
            GraphSectors.Sector workSector = targetSector.get(i);
            for(int j = workSector.startNodeId(); j<workSector.endNodeId(); j++) {
                PointCh target = nodePoint(j);
                if(target.squaredDistanceTo(point)<=distance) {
                    nodeId = j;
                    counter ++;
                    distance = target.squaredDistanceTo(point);
                }
            }
        }
        if(counter==0) {
            nodeId = -1;
        }
        return nodeId;
    }
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
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
            return Functions.constant(Double.NaN);
        }else{
            return Functions.sampled(sample, edges.elevationGain(edgeId));
        }
    }
}
