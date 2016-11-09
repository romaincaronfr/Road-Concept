package trajectory;


import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.trajectory.EndRoadTrajectory;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class testEndRoadTrajectory {

    @Test
    public void test1EdgeClosing(){
        Position A = new Position(0,0);
        Position B = new Position(0,1);

        RoadSection RS = new RoadSection(A,B);

        Assert.assertNull(RS.getLaneAB().getInsertTrajectory().getNext());
        Assert.assertNull(RS.getLaneBA().getInsertTrajectory().getNext());

        SimpleTrajectory AB = RS.getLaneAB().getInsertTrajectory();
        SimpleTrajectory BA = RS.getLaneBA().getInsertTrajectory();

        EndRoadTrajectory BB = new EndRoadTrajectory(AB,BA, UUID.randomUUID());

        Assert.assertEquals(BB,AB.getNext());
        Assert.assertEquals(BA,BB.getNext());

        Assert.assertNull(BA.getNext());

        EndRoadTrajectory AA = new EndRoadTrajectory(BA,AB,UUID.randomUUID());

        Assert.assertEquals(AA,BA.getNext());
        Assert.assertEquals(AB,AA.getNext());
    }
}
