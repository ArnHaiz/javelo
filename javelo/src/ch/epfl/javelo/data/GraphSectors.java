package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * record representing all the sectors in switzerland
 */
public record GraphSectors(ByteBuffer buffer) {
    /**
     * index of the relative position containing the id of the first node of the sector.
     */
    private static final byte OFFSET_FIRST_NODE = 0;
    /**
     * index of the relative position containing the number of nodes in the sector.
     */
    private static final byte OFFSET_NUMBER_NODES = OFFSET_FIRST_NODE + 1;
    /**
     * number of bytes necessary to represent a sector.
     */
    private static final int OFFSET_SECTOR_INTS = OFFSET_NUMBER_NODES + 1;
    /**
     * width of a sector.
     */
    public static final int SECTOR_WIDTH = 2730;
    /**
     * height of a sector.
     */
    public static final int SECTOR_HEIGHT = 1730;

    /**
     * finds all the sectors in a square of side double <code>distance</code> around the point <code>center</code>
     *
     * @param center
     * @param distance
     * @return an ArrayList of sectors that are in the square centered at
     * <code>point</code> and of side equals to double <code>distance</code>
     */
    public List<Sector> sectionInArea(PointCh center, double distance) {
        ArrayList<Sector> sectorList = new ArrayList<Sector>();
        PointCh temp = new PointCh(center.e() - distance, center.n() - distance);
        int sectorId = (int) (Math.floor(temp.e() / SECTOR_WIDTH) + Math.floor(temp.n() / SECTOR_HEIGHT) * 128);
        for (int i = 0; i < Math2.ceilDiv(((int)Math.ceil(distance)), SECTOR_HEIGHT); i++) {
            for (int j = 0; j < Math2.ceilDiv(((int)Math.ceil(distance)), SECTOR_WIDTH); j++) {
                int firstNodeId = buffer.getInt((sectorId + j) * 6);
                short nodesCount = buffer.getShort((sectorId + j) * 6 + Integer.BYTES);
                sectorList.add(new Sector(firstNodeId, firstNodeId + nodesCount));
            }
            sectorId += 128;
        }
        return sectorList;
    }

    /**
     * record representing a sector
     */
    record Sector(int startNodeId, int endNodeId) {
        /**
         *
         */

    }
}
