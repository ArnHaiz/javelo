package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * record representing all the sectors in switzerland
 */
public record GraphSectors(ByteBuffer buffer) {
    /**
     * index of the relative position containing the id of the first node of the sector.
     */
    private static final int OFFSET_FIRST_NODE = 0;
    /**
     * index of the relative position containing the number of nodes in the sector.
     */
    private static final int OFFSET_NUMBER_NODES = OFFSET_FIRST_NODE + 4;
    /**
     * number of bytes necessary to represent a sector.
     */
    private static final int OFFSET_SECTOR_INTS = OFFSET_NUMBER_NODES + 2;
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
     * number of sectors in a row or a column.
     */
    public static final int SUBDIVISIONS_PER_SIDE = 128;
    /**
     * width of a sector.
     */
    public static final double SECTOR_WIDTH = SwissBounds.WIDTH / SUBDIVISIONS_PER_SIDE;
    /**
     * height of a sector.
     */
    public static final double SECTOR_HEIGHT = SwissBounds.HEIGHT / SUBDIVISIONS_PER_SIDE;

    /**
     * finds all the sectors in a square of side double <code>distance</code> around the point <code>center</code>.
     *
     * @param center   the center point of search
     * @param distance the distance from the center in which to search for sectors
     * @return an ArrayList of sectors that are in the square centered at
     * <code>point</code> and of side equals to double <code>distance</code>.
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        Preconditions.checkArgument(distance > 0);

        ArrayList<Sector> sectorList = new ArrayList<>();

        PointCh min = new PointCh(
                Math.max(Math.floor((center.e() - distance - SWISS_MIN_EAST) / SECTOR_WIDTH) * SECTOR_WIDTH + SWISS_MIN_EAST
                        , SWISS_MIN_EAST)
                , Math.max(Math.floor((center.n() - distance - SWISS_MIN_NORTH) / SECTOR_HEIGHT) * SECTOR_HEIGHT + SWISS_MIN_NORTH
                        , SWISS_MIN_NORTH));

        PointCh max = new PointCh(
                Math.min(Math.ceil((center.e() + distance - SWISS_MIN_EAST) / SECTOR_WIDTH)
                                * SECTOR_WIDTH + SWISS_MIN_EAST
                        , SWISS_MAX_EAST),
                Math.min(Math.ceil((center.n() + distance - SWISS_MIN_NORTH) / SECTOR_HEIGHT)
                                * SECTOR_HEIGHT + SWISS_MIN_NORTH
                        , SWISS_MAX_NORTH));

        int firstSectorId = (int) ((min.e() - SWISS_MIN_EAST) / SECTOR_WIDTH +
                SUBDIVISIONS_PER_SIDE * (min.n() - SWISS_MIN_NORTH) / SECTOR_HEIGHT);
        int nbrNorthSectors = (int) Math.round((max.n() - min.n()) / SECTOR_HEIGHT);
        int nbrEastSectors = (int) Math.round((max.e() - min.e()) / SECTOR_WIDTH);

        for (int i = 0; i < nbrNorthSectors; i++) {
            for (int j = firstSectorId; j < firstSectorId + nbrEastSectors; j++) {

                int firstNodeId = Bits.extractUnsigned(
                        buffer.getInt((j + i * SUBDIVISIONS_PER_SIDE) * OFFSET_SECTOR_INTS), 0, 31);
                int endNodeId = firstNodeId +
                        Short.toUnsignedInt(buffer.getShort(
                                (j + i * SUBDIVISIONS_PER_SIDE) * OFFSET_SECTOR_INTS + Integer.BYTES));

                sectorList.add(new Sector(firstNodeId, endNodeId));
            }
        }

        return sectorList;
    }

    /**
     * record representing a sector
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}
