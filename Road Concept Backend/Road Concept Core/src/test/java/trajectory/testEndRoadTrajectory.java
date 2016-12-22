package trajectory;


import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roads.DualWayRoad;
import fr.enssat.lanniontech.core.roadElements.roads.Road;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;
import fr.enssat.lanniontech.core.trajectory.EndRoadTrajectory;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.TrajectoryJunction;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class testEndRoadTrajectory {

    @Test
    public void test1EdgeClosing() {
        Position A = new Position(0, 0);
        Position B = new Position(0, 1);

        DualWayRoadSection RS = new DualWayRoadSection(A, B, new DualWayRoad(UUID.randomUUID(), 50));

        Assert.assertNull(RS.getLaneAB().getInsertTrajectory().getNext());
        Assert.assertNull(RS.getLaneBA().getInsertTrajectory().getNext());

        SimpleTrajectory AB = RS.getLaneAB().getInsertTrajectory();
        SimpleTrajectory BA = RS.getLaneBA().getInsertTrajectory();

        EndRoadTrajectory BB = new EndRoadTrajectory(AB, BA, UUID.randomUUID());

        TrajectoryJunction junction = new TrajectoryJunction(AB, BB, AB.getStop(), 0);
        AB.addDestination(junction);
        BB.setSource(junction);

        junction = new TrajectoryJunction(BB, BA, BB.getLength(), BA.getStart());
        BA.addSource(junction);
        BB.setDestination(junction);

        Assert.assertEquals(BB, AB.getNext().getDestination());
        Assert.assertEquals(BA, BB.getNext().getDestination());

        Assert.assertNull(BA.getNext());
    }
}
