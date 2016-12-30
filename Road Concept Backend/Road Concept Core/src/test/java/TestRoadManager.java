import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roads.OneWayRoad;
import fr.enssat.lanniontech.core.roadElements.roads.Road;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TestRoadManager {

    @Test
    public void addRoadSectionToRoadSectionA() {
        RoadManager RM = new RoadManager();
        Position A = new Position(0, 0);
        Position B = new Position(1, 1);
        Position C = new Position(2, 2);
        Road R = new OneWayRoad(UUID.randomUUID(), 50);

        DualWayRoadSection RS1 = (DualWayRoadSection) RM.addDualWayRoadSection(A, B, R);
        DualWayRoadSection RS2 = (DualWayRoadSection) RM.addDualWayRoadSection(B, C, R);

        Assert.assertTrue(RS1.getLaneAB().getNextLane() == RS2.getLaneAB());
        Assert.assertTrue(RS2.getLaneBA().getNextLane() == RS1.getLaneBA());
        Assert.assertNull(RS1.getLaneBA().getNextLane());
        Assert.assertNull(RS2.getLaneAB().getNextLane());
    }

    @Test
    public void addRoadSectionToRoadSectionB() {
        RoadManager RM = new RoadManager();
        Position A = new Position(0, 0);
        Position B = new Position(1, 1);
        Position C = new Position(2, 2);
        Road R = new OneWayRoad(UUID.randomUUID(), 50);

        DualWayRoadSection RS1 = (DualWayRoadSection) RM.addDualWayRoadSection(A, B, R);
        DualWayRoadSection RS2 = (DualWayRoadSection) RM.addDualWayRoadSection(C, A, R);

        Assert.assertTrue(RS2.getLaneAB().getNextLane() == RS1.getLaneAB());
        Assert.assertTrue(RS1.getLaneBA().getNextLane() == RS2.getLaneBA());
        Assert.assertNull(RS2.getLaneBA().getNextLane());
        Assert.assertNull(RS1.getLaneAB().getNextLane());
    }

    @Test
    public void addRoundAbout() {
        RoadManager RM = new RoadManager();
        Position P1 = new Position(1,0);
        Position P2 = new Position(0,1);
        Position P3 = new Position(-1,0);
        Position P4 = new Position(0,-1);
        Position P0 = new Position(2,0);

        List<Position> list = new ArrayList<>();
        list.add(P1);
        list.add(P2);
        list.add(P3);
        list.add(P4);
        list.add(P1);

        UUID id1 = UUID.fromString("0-0-0-0-1");
        UUID id2 = UUID.fromString("0-0-0-0-2");

        RM.addRoundAbout(list,id1);

        RM.addRoadSectionToRoad(P0,P1,id2,50,false);

        RM.closeRoads();

        int integrity = RM.checkIntegrity();

        Assert.assertEquals(0, integrity);
    }

/*  @Test
    public void addRoads() {
        RoadManager RM = new RoadManager();
        Position I = new Position(0, 0);
        Position A = new Position(1, 0);
        Position B = new Position(-1, 0);
        Position C = new Position(0, 1);
        Position D = new Position(0, -1);
        Position E = new Position(1, 1);

        Road R1 = RM.addRoadSectionToRoad(A, I, UUID.randomUUID());
        Road R2 = RM.addRoadSectionToRoad(B, I, UUID.randomUUID());

        Intersection Inter = RM.getIntersection(I);

        Assert.assertEquals(2,Inter.getRoadSectionsSize());
        Assert.assertEquals(Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1),
                Inter.getTrajectoriesSize());

        Road R3 = RM.addRoadSectionToRoad(C, I, UUID.randomUUID());
        Assert.assertEquals(3,Inter.getRoadSectionsSize());
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Road R4 = RM.addRoadSectionToRoad(D, I, UUID.randomUUID());
        Assert.assertEquals(4,Inter.getRoadSectionsSize());
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Road R5 = RM.addRoadSectionToRoad(E, I, UUID.randomUUID());
        Assert.assertEquals(5,Inter.getRoadSectionsSize());
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

    }

    @Test
    public void testJoint(){
        RoadManager RM = new RoadManager();
        Position I1 = new Position(0, -1);
        Position A = new Position(1, 0);
        Position B = new Position(-1, 0);

        Road R1 = RM.addRoadSectionToRoad(A, I1, UUID.randomUUID());
        Road R2 = RM.addRoadSectionToRoad(B, I1, UUID.randomUUID());

        Intersection Inter1 = RM.getIntersection(I1);

        Assert.assertNull(R1.get(0).getLaneBA().getInsertTrajectory().getNext());
        Assert.assertNull(R2.get(0).getLaneBA().getInsertTrajectory().getNext());
        Assert.assertNotNull(R1.get(0).getLaneAB().getInsertTrajectory().getNext());
        Assert.assertNotNull(R2.get(0).getLaneAB().getInsertTrajectory().getNext());

        Assert.assertEquals(2,Inter1.getRoadSectionsSize());
        Assert.assertEquals(Inter1.getRoadSectionsSize() * (Inter1.getRoadSectionsSize() - 1),
                Inter1.getTrajectoriesSize());

    }*/
}
