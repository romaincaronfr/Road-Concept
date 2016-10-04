import fr.enssat.lanniontech.core.RoadManager;
import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by 4r3 on 04/10/16.
 */
public class TestPosFunction {
    @Test
    public void testParallelRoads1(){
        RoadManager RM = new RoadManager();
        Position A = new Position(0,0);
        Position B = new Position(0,1);
        Position C = new Position(0,2);

        RoadSection RS1 = RM.addRoadSection(A,B);
        RoadSection RS2 = RM.addRoadSection(B,C);

        PosFunction Pf1 = RS1.getFunction();
        PosFunction Pf2 = RS2.getFunction();

        Assert.assertFalse(Pf1.cross(Pf2));
        Assert.assertFalse(Pf2.cross(Pf1));

        Assert.assertTrue(Pf1.det(Pf2)==0);
    }

    @Test
    public void testParallelRoads2(){
        RoadManager RM = new RoadManager();
        Position A = new Position(0,0);
        Position B = new Position(0,1);
        Position C = new Position(0,-1);

        RoadSection RS1 = RM.addRoadSection(A,B);
        RoadSection RS2 = RM.addRoadSection(B,C);

        PosFunction Pf1 = RS1.getFunction();
        PosFunction Pf2 = RS2.getFunction();

        Assert.assertFalse(Pf1.cross(Pf2));
        Assert.assertFalse(Pf2.cross(Pf1));

        Assert.assertTrue(Pf1.det(Pf2)==0);
    }

    @Test
    public void testNotParallelRoads1(){
        RoadManager RM = new RoadManager();
        Position A = new Position(0,0);
        Position B = new Position(0,1);
        Position C = new Position(1,1);

        RoadSection RS1 = RM.addRoadSection(A,B);
        RoadSection RS2 = RM.addRoadSection(B,C);

        PosFunction Pf1 = RS1.getFunction();
        PosFunction Pf2 = RS2.getFunction();

        Assert.assertTrue(Pf1.cross(Pf2));
        Assert.assertTrue(Pf2.cross(Pf1));

        Assert.assertTrue(Pf1.det(Pf2)>0);
    }

    @Test
    public void testNotParallelRoads2(){
        RoadManager RM = new RoadManager();
        Position A = new Position(0,0);
        Position B = new Position(0,1);
        Position C = new Position(-1,1);

        RoadSection RS1 = RM.addRoadSection(A,B);
        RoadSection RS2 = RM.addRoadSection(B,C);

        PosFunction Pf1 = RS1.getFunction();
        PosFunction Pf2 = RS2.getFunction();

        Assert.assertTrue(Pf1.cross(Pf2));
        Assert.assertTrue(Pf2.cross(Pf1));

        Assert.assertTrue(Pf1.det(Pf2)<0);
    }
}
