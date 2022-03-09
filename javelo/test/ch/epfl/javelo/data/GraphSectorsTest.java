package ch.epfl.javelo.data;


import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(2, graph.sectorsInArea(new PointCh(2600000, 1200000), 1000).size());
        assertEquals(1, graph.sectorsInArea(new PointCh(2600400, 1200400), 100).size());
        assertEquals(6, graph.sectorsInArea(new PointCh(2601000, 1201000), 2000).size());
        assertEquals(12, graph.sectorsInArea(new PointCh(2602000, 1201000), 3000).size());
    }

    @Test
    public void sectorInAreaReturnsCorrectSpecificSectors() {
        ByteBuffer bbt = ByteBuffer.wrap(new byte[16383 * 6]);
        for (int i = 0; i < 16383; i++) {
            bbt.putInt(i);
            bbt.putShort((short) 16);
        }
        GraphSectors graph = new GraphSectors(bbt);
        var sectorList=  graph.sectorsInArea(new PointCh(2600000, 1200000), 2000);
        for (int i = 0; i < 9; i++) {
            assertEquals(i, sectorList.get(i).startNodeId());
        }
    }
}
