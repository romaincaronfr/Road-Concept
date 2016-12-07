import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
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

        Road R1 = RM.addRoadSectionToRoad(A, I, uuid0, 50, false);
        Road R2 = RM.addRoadSectionToRoad(B, I, uuid1, 50, false);
        Road R3 = RM.addRoadSectionToRoad(C, I, uuid2, 50, false);
        Road R4 = RM.addRoadSectionToRoad(D, I, uuid3, 50, false);
        Road R5 = RM.addRoadSectionToRoad(E, I, uuid4, 50, false);


    }

}
