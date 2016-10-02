import fr.enssat.lanniontech.RoadManager;
import fr.enssat.lanniontech.positioning.Position;
import fr.enssat.lanniontech.roadElements.RoadSection;
import org.junit.Assert;
import org.junit.Test;


public class TestRoadManager {

    @Test
    public void addRoadSectionToRoadSectionA(){
        RoadManager RM = new RoadManager();
        Position A = new Position(0,0);
        Position B = new Position(1,1);
        Position C = new Position(2,2);

        RoadSection RS1 = RM.addRoadSection(A,B);
        RoadSection RS2 = RM.addRoadSection(B,C);

        Assert.assertTrue(RS1.getLaneA().getNextLane()==RS2.getLaneA());
        Assert.assertTrue(RS2.getLaneB().getNextLane()==RS1.getLaneB());
        Assert.assertNull(RS1.getLaneB().getNextLane());
        Assert.assertNull(RS2.getLaneA().getNextLane());
    }

    @Test
    public void addRoadSectionToRoadSectionB(){
        RoadManager RM = new RoadManager();
        Position A = new Position(0,0);
        Position B = new Position(1,1);
        Position C = new Position(2,2);

        RoadSection RS1 = RM.addRoadSection(A,B);
        RoadSection RS2 = RM.addRoadSection(C,A);

        Assert.assertTrue(RS2.getLaneA().getNextLane()==RS1.getLaneA());
        Assert.assertTrue(RS1.getLaneB().getNextLane()==RS2.getLaneB());
        Assert.assertNull(RS2.getLaneB().getNextLane());
        Assert.assertNull(RS1.getLaneA().getNextLane());
    }
}
