import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roads.DualWayRoad;
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
        Road R = new DualWayRoad(UUID.randomUUID(), 50);

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

    @Test
    public void changeMiddleRoad(){
        RoadManager RM = new RoadManager();
        Position P1 = new Position(1,0);
        Position P2 = new Position(0,1);
        Position P3 = new Position(-1,0);
        Position P4 = new Position(0,-1);

        UUID id1 = UUID.fromString("0-0-0-0-1");
        UUID id2 = UUID.fromString("0-0-0-0-2");
        UUID id3 = UUID.fromString("0-0-0-0-3");

        RM.addRoadSectionToRoad(P1,P2,id1,50, false);
        RM.addRoadSectionToRoad(P2,P3,id2,50,true);
        RM.addRoadSectionToRoad(P3,P4,id3,50, false);

        RM.closeRoads();
        Assert.assertEquals(0,RM.checkIntegrity());
    }

}
