import fr.enssat.lanniontech.core.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class TestIntersection {

    @Test
    public void addRoads() {
        RoadManager RM = new RoadManager();

        Position I = new Position(0, 0);
        Position A = new Position(1, 0);
        Position B = new Position(-1, 0);
        Position C = new Position(0, 1);
        Position D = new Position(0, -1);
        Position E = new Position(1, 1);

        UUID uuid0 = UUID.randomUUID();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        UUID uuid4 = UUID.randomUUID();

        Road R1 = RM.addRoadSectionToRoad(A, I, uuid0);
        Road R2 = RM.addRoadSectionToRoad(B, I, uuid1);
        Road R3 = RM.addRoadSectionToRoad(C, I, uuid2);
        Road R4 = RM.addRoadSectionToRoad(D, I, uuid3);
        Road R5 = RM.addRoadSectionToRoad(E, I, uuid4);

        Intersection Inter = new Intersection(I);

        Assert.assertTrue(Inter.addRoadSection(R1.get(0)));
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Assert.assertTrue(Inter.addRoadSection(R2.get(0)));
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Assert.assertTrue(Inter.addRoadSection(R3.get(0)));
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Assert.assertTrue(Inter.addRoadSection(R4.get(0)));
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Assert.assertTrue(Inter.addRoadSection(R5.get(0)));
        Assert.assertEquals(Inter.getTrajectoriesSize(),
                Inter.getRoadSectionsSize() * (Inter.getRoadSectionsSize() - 1));

        Assert.assertTrue(Inter.removeRoadSection(uuid1));
        Assert.assertFalse(Inter.removeRoadSection(UUID.randomUUID()));
        Assert.assertEquals(Inter.getTrajectoriesSize(), 12);
    }

}
