import fr.enssat.lanniontech.core.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import org.junit.Assert;
import org.junit.Test;

public class TestIntersection {

    @Test
    public void addRoads(){
        RoadManager RM = new RoadManager();
        Position I = new Position(0,0);
        Position A = new Position(1,0);
        Position B = new Position(-1,0);
        Position C = new Position(0,1);
        Position D = new Position(0,-1);
        Position E = new Position(1,1);

        Road R1 = RM.addRoad(A,I,0);
        Road R2 = RM.addRoad(B,I,1);
        Road R3 = RM.addRoad(C,I,2);
        Road R4 = RM.addRoad(D,I,3);
        Road R5 = RM.addRoad(D,I,4);

        Intersection Inter = new Intersection(I);


        Assert.assertTrue(Inter.addRoadSection(R1.get(0)));
        Assert.assertTrue(Inter.addRoadSection(R2.get(0)));
        Assert.assertTrue(Inter.addRoadSection(R3.get(0)));
        Assert.assertTrue(Inter.addRoadSection(R4.get(0)));
        Assert.assertTrue(Inter.addRoadSection(R5.get(0)));
    }
}
