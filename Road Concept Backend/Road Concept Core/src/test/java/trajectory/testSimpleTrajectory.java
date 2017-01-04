package trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class testSimpleTrajectory {
    @Test
    public void testGPSNormal() {
        Position A = new Position(42, 0);
        Position B = new Position(42, 1);

        PosFunction pF = new PosFunction(A, B);

        SimpleTrajectory sT = new SimpleTrajectory(pF, 0, 2000, 0, UUID.randomUUID(),null,50);

        Assert.assertFalse(sT.isInverted());

        Assert.assertEquals(2000, sT.getLength(), 0.0001);

        Assert.assertEquals(pF.get(0), sT.getGPS(0));
        Assert.assertNotEquals(pF.get(2000), sT.getGPS(0));
        Assert.assertEquals(pF.get(500), sT.getGPS(500));
    }

    @Test
    public void testGPSInverted() {
        Position A = new Position(42, 0);
        Position B = new Position(42, 1);

        PosFunction pF = new PosFunction(A, B);

        SimpleTrajectory sT = new SimpleTrajectory(pF, 2000, 0, 0, UUID.randomUUID(),null,50);

        Assert.assertTrue(sT.isInverted());

        Assert.assertEquals(2000, sT.getLength(), 0.0001);

        Assert.assertEquals(pF.get(2000), sT.getGPS(0));
        Assert.assertNotEquals(pF.get(0), sT.getGPS(0));
        Assert.assertEquals(pF.get(1999), sT.getGPS(1));
    }

    @Test
    public void testGPSWset() {
        Position A = new Position(42, 0);
        Position B = new Position(42, 1);

        PosFunction pF = new PosFunction(A, B);

        SimpleTrajectory sT = new SimpleTrajectory(pF, 0, 2000, 10, UUID.randomUUID(),null,50);

        Assert.assertEquals(2000, sT.getLength(), 0.0001);

        Assert.assertEquals(pF.get(0, 10), sT.getGPS(0));
        Assert.assertNotEquals(pF.get(0), sT.getGPS(0));
        Assert.assertEquals(pF.get(2000, 10), sT.getGPS(2000));
    }

    @Test
    public void testGPSInvertedWset() {
        Position A = new Position(42, 0);
        Position B = new Position(42, 1);

        PosFunction pF = new PosFunction(A, B);

        SimpleTrajectory sT = new SimpleTrajectory(pF, 2000, 0, 10, UUID.randomUUID(),null,50);

        Assert.assertEquals(2000, sT.getLength(), 0.0001);

        Assert.assertEquals(pF.get(0, -10), sT.getGPS(2000));
        Assert.assertNotEquals(pF.get(0), sT.getGPS(0));
        Assert.assertEquals(pF.get(2000, -10), sT.getGPS(0));
    }

/*    @Test
    public void testLengthFromSide() {
        Position A = new Position(42, 0);
        Position B = new Position(42, 1);

        RoadSection Rs = new RoadSection(A, B);

        Side S = new Side(0, null, Rs.getLaneAB());

        Assert.assertEquals(Rs.getLength(), S.getDistanceToNextCar(AI.getFreeDistance()), 0.01);

        S.move(100);

        Assert.assertEquals(Rs.getLength() - 100, S.getDistanceToNextCar(AI.getFreeDistance()), 0.01);
    }

    @Test
    public void testLengthSide2Side() {
        Position A = new Position(42, 0);
        Position B = new Position(42, 1);

        RoadSection Rs = new RoadSection(A, B);

        Side S1 = new Side(0, null, Rs.getLaneAB());
        Side S2 = new Side(100, null, Rs.getLaneAB());

        Assert.assertFalse(Rs.getLaneAB().getInsertTrajectory().isInverted());
        Assert.assertTrue(Rs.getLaneBA().getInsertTrajectory().isInverted());

        Assert.assertEquals(S2.getPos(), S1.getDistanceToNextCar(AI.getFreeDistance()), 0.01);

        S1.move(10);

        Assert.assertEquals(S2.getPos() - 10, S1.getDistanceToNextCar(AI.getFreeDistance()), 0.01);
    }*/

}
