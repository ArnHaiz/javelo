package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * @param pointCh : pointCh representing the coordinate of the waypoint
 * @param nodeClosestToId : id of the closest node of the waypoint
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * record class representing a waypoint.
 */
public record Waypoint(PointCh pointCh, int nodeClosestToId) {

}
