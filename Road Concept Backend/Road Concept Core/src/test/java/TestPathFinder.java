import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.pathFinding.Path;
import fr.enssat.lanniontech.core.pathFinding.PathFinder;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class TestPathFinder {

    public static Simulator tearDown() {
        Simulator Sim = new Simulator();

        Position C = Sim.getPositionManager().addPosition(40, 0);
        Position D = Sim.getPositionManager().addPosition(40.1, 0);
        Position E = Sim.getPositionManager().addPosition(40.2, 0);
        Position B = Sim.getPositionManager().addPosition(40, 0.1);
        Position A = Sim.getPositionManager().addPosition(40.1, 0.1);
        Position F = Sim.getPositionManager().addPosition(40.2, 0.1);

        Position G0 = Sim.getPositionManager().addPosition(40.1, 0.2);
        Position G1 = Sim.getPositionManager().addPosition(40, 0.3);
        Position G2 = Sim.getPositionManager().addPosition(40.2, 0.3);

        UUID id1 = UUID.fromString("0-0-0-0-1");
        UUID id2 = UUID.fromString("0-0-0-0-2");
        UUID id3 = UUID.fromString("0-0-0-0-3");
        UUID id4 = UUID.fromString("0-0-0-0-4");
        UUID id5 = UUID.fromString("0-0-0-0-5");
        UUID id6 = UUID.fromString("0-0-0-0-6");

        Sim.getRoadManager().addRoadSectionToRoad(A, B, id1, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(B, C, id1, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(C, D, id1, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(D, A, id2, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(D, E, id3, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(E, F, id3, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(F, A, id3, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(A, G0, id4, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(G0, G1, id5, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(G0, G2, id6, 50, false);

        Sim.getRoadManager().closeRoads();

        int integrity = Sim.getRoadManager().checkIntegrity();

        Assert.assertEquals(0, integrity);
        return Sim;
    }

    @Test
    public void testRandomPathLength() {
        Simulator simulator = tearDown();

        PathFinder pathFinder = new PathFinder(simulator.getRoadManager());

        DualWayRoadSection roadSection = (DualWayRoadSection) simulator.getRoadManager().getRoad(UUID.fromString("0-0-0-0-6")).get(0);
        Trajectory startTrajectory = roadSection.getLaneAB().getInsertTrajectory();

        for (int i = 0; i < 10; i++) {
            Path testPath = pathFinder.getRandomPath(startTrajectory, 10 * i);

            Assert.assertEquals(10 * i + 1, testPath.getSize());
        }

    }

    @Test
    public void testAStarPath() {
        Simulator simulator = tearDown();

        PathFinder pathFinder = new PathFinder(simulator.getRoadManager());

        DualWayRoadSection roadSection = (DualWayRoadSection) simulator.getRoadManager().getRoad(UUID.fromString("0-0-0-0-6")).get(0);
        Trajectory startTrajectory = roadSection.getLaneAB().getInsertTrajectory();

        Path testPath = null;

        testPath = pathFinder.getPathTo(startTrajectory, UUID.fromString("0-0-0-0-5"), false);

        Assert.assertEquals(2, testPath.getSize());

        testPath = pathFinder.getPathTo(startTrajectory, UUID.fromString("0-0-0-0-4"), true);

        Assert.assertEquals(2, testPath.getSize());

        testPath = pathFinder.getPathTo(startTrajectory, UUID.fromString("0-0-0-0-1"), true);

        Assert.assertEquals(4, testPath.getSize());

    }
}
