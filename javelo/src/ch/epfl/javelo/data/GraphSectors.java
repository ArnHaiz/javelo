package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
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
    private static final byte OFFSET_FIRST_NODE = 0b00000000;
    /**
     * index of the relative position containing the number of nodes in the sector.
     */
    private static final byte OFFSET_NUMBER_NODES = OFFSET_FIRST_NODE + 0b00000001;
    /**
     * number of bytes necessary to represent a sector.
     */
    private static final int OFFSET_SECTOR_INTS = OFFSET_NUMBER_NODES;
    /**
     * width of a sector.
     */
    public static final int SECTOR_WIDTH = 2730;
    /**
     * height of a sector.
     */
    public static final int SECTOR_HEIGHT = 1730;
    /**
     * minimum east coordinate that is in Switzerland in swiss coordinates.
     */
    public static final int SWISS_MIN_EAST = 2485000;
    /**
     * minimum north coordinate that is in switzerland in swiss coordinates.
     */
    public static final int SWISS_MIN_NORTH = 1075000;
    /**
     * maximum east coordinate that is in Switzerland in swiss coordinates.
     */
    public static final int SWISS_MAX_EAST = 2834000;
    /**
     * maximum north coordinate that is in Switzerland in swiss coordinates.
     */
    public static final int SWISS_MAX_NORTH = 1296000;

    /**
     * finds all the sectors in a square of side double <code>distance</code> around the point <code>center</code>
     *
     * @param center
     * @param distance
     * @return an ArrayList of sectors that are in the square centered at
     * <code>point</code> and of side equals to double <code>distance</code>
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        Preconditions.checkArgument(distance > 0);
        ArrayList<Sector> sectorList = new ArrayList<>();

        int sectorId = (int) (Math.floor((Math.max(SWISS_MIN_EAST, center.e() - distance) - SWISS_MIN_EAST) / SECTOR_WIDTH)
                + Math.floor((Math.max(SWISS_MIN_NORTH, center.n() - distance) - SWISS_MIN_NORTH) / SECTOR_HEIGHT) * 128);

        PointCh inferiorLeft = new PointCh(
                Math.max(Math.floor((center.e() - distance - SWISS_MIN_EAST) / SECTOR_WIDTH) * SECTOR_WIDTH + SWISS_MIN_EAST, SWISS_MIN_EAST),
                Math.max(Math.floor((center.n() - distance - SWISS_MIN_NORTH) / SECTOR_HEIGHT) * SECTOR_HEIGHT + SWISS_MIN_NORTH, SWISS_MIN_NORTH));

        PointCh superiorRight = new PointCh(
                Math.min(Math.ceil((center.e() + distance - SWISS_MIN_EAST) / SECTOR_WIDTH) * SECTOR_WIDTH + SWISS_MIN_EAST, SWISS_MAX_EAST),
                Math.min(Math.ceil((center.n() + distance - SWISS_MIN_NORTH) / SECTOR_HEIGHT) * SECTOR_HEIGHT + SWISS_MIN_NORTH, SWISS_MAX_NORTH));

        int nbrNorthSectors = (int) ((superiorRight.n() - inferiorLeft.n()) / (double) SECTOR_HEIGHT);
        int nbrEastSectors = (int) ((superiorRight.e() - inferiorLeft.e()) / (double) SECTOR_WIDTH);

        for (int i = 0; i < nbrNorthSectors; i++)
            for (int j = 0; j < nbrEastSectors; j++) {
                int firstNodeId = buffer.getInt((sectorId + j + i * 128) * 6);
                int nodesCount = Short.toUnsignedInt(buffer.getShort((sectorId + j + i * 128) * 6 + Integer.BYTES));
                sectorList.add(new Sector(firstNodeId, firstNodeId + nodesCount));
            }

        return sectorList;
    }

    /**
     * record representing a sector
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}
