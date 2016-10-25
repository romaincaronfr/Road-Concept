import fr.enssat.lanniontech.core.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import org.junit.Assert;
import org.junit.Test;


public class TestRoadManager {

    @Test
    public void addRoadSectionToRoadSectionA() {
        RoadManager RM = new RoadManager();
        Position A = new Position(0, 0);
        Position B = new Position(1, 1);
        Position C = new Position(2, 2);

        RoadSection RS1 = RM.addRoadSection(A, B);
        RoadSection RS2 = RM.addRoadSection(B, C);

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

        RoadSection RS1 = RM.addRoadSection(A, B);
        RoadSection RS2 = RM.addRoadSection(C, A);

        Assert.assertTrue(RS2.getLaneAB().getNextLane() == RS1.getLaneAB());
        Assert.assertTrue(RS1.getLaneBA().getNextLane() == RS2.getLaneBA());
        Assert.assertNull(RS2.getLaneBA().getNextLane());
        Assert.assertNull(RS1.getLaneAB().getNextLane());
    }

    @Test
    public void connectRoad2Road() {

    }

    @Test
    public void addRoads() {
        RoadManager RM = new RoadManager();
        Position I = new Position(0, 0);
        Position A = new Position(1, 0);
        Position B = new Position(-1, 0);
        Position C = new Position(0, 1);
        Position D = new Position(0, -1);
        Position E = new Position(1, 1);

        Road R1 = RM.addRoadSectionToRoad(A, I, 0);
        Road R2 = RM.addRoadSectionToRoad(B, I, 1);

        Intersection Inter = RM.getIntersection(I);

        Assert.assertEquals(2,Inter.getRoadSectionsSize());
        Assert.assertEquals(Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1),
                Inter.getTrajectoriesSize());

        Road R3 = RM.addRoadSectionToRoad(C, I, 2);
        Assert.assertEquals(3,Inter.getRoadSectionsSize());
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Road R4 = RM.addRoadSectionToRoad(D, I, 3);
        Assert.assertEquals(4,Inter.getRoadSectionsSize());
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Road R5 = RM.addRoadSectionToRoad(E, I, 4);
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

        Road R1 = RM.addRoadSectionToRoad(A, I1, 0);
        Road R2 = RM.addRoadSectionToRoad(B, I1, 1);

        Intersection Inter1 = RM.getIntersection(I1);

        Assert.assertNull(R1.get(0).getLaneBA().getInsertTrajectory().getNext());
        Assert.assertNull(R2.get(0).getLaneBA().getInsertTrajectory().getNext());
        Assert.assertNotNull(R1.get(0).getLaneAB().getInsertTrajectory().getNext());
        Assert.assertNotNull(R2.get(0).getLaneAB().getInsertTrajectory().getNext());


        Assert.assertEquals(2,Inter1.getRoadSectionsSize());
        Assert.assertEquals(Inter1.getRoadSectionsSize() * (Inter1.getRoadSectionsSize() - 1),
                Inter1.getTrajectoriesSize());

    }
}
