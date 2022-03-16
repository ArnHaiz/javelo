package ch.epfl.javelo.data;


import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class GraphSectorsTest {
    @Test
    public void sectorsInAreaReturnsCorrectNumberOfSectors() {
        ByteBuffer bbt = ByteBuffer.wrap(new byte[16383 * 6]);
        for (int i = 0; i < 16383; i++) {
            bbt.putInt(i);
            bbt.putShort((short) 1);
        }
        GraphSectors graph = new GraphSectors(bbt);
        assertEquals(1, graph.sectorsInArea(new PointCh(2600000, 1200000), 100).size());
        assertEquals(6, graph.sectorsInArea(new PointCh(2600000, 1200000), 1000).size());
        assertEquals(1, graph.sectorsInArea(new PointCh(2600400, 1200400), 100).size());
        assertEquals(12, graph.sectorsInArea(new PointCh(2601000, 1201000), 2000).size());
        assertEquals(16, graph.sectorsInArea(new PointCh(2602000, 1201000), 3000).size());
    }

    @Test
    public void sectorInAreaReturnsCorrectSpecificSectors() {
        ByteBuffer bbt = ByteBuffer.wrap(new byte[16383 * 6]);
        for (int i = 0; i < 16383; i++) {
            bbt.putInt(i);
            bbt.putShort((short) 16);
        }
        GraphSectors graph = new GraphSectors(bbt);
        var sectorList=  graph.sectorsInArea(new PointCh(
                GraphSectors.SWISS_MIN_EAST + 1.5 * GraphSectors.SECTOR_WIDTH,
                GraphSectors.SWISS_MIN_NORTH + 1.5 * GraphSectors.SECTOR_HEIGHT), 1500);
        boolean check = false;
        Set<Integer> sept = new TreeSet<>();
        for (int i = 0; i < sectorList.size(); i++) {
            sept.add(sectorList.get(i).startNodeId());
        }
        Set<Integer> septDeux = new TreeSet<>(Set.of(0, 1, 2, 128, 129, 130, 256, 257, 258));
        assertEquals(septDeux, sept);
    }

    @Test
    public void sectorsInAreaOnNegativeDistance() {
        ByteBuffer bbt = ByteBuffer.wrap(new byte[12]);
        GraphSectors graph = new GraphSectors(bbt);
        assertThrows(IllegalArgumentException.class, () -> graph.sectorsInArea(new PointCh(2400000, 1200000), -5));
    }
}
