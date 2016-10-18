package trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import org.junit.Assert;
import org.junit.Test;

public class testSimpleTrajectory {
    @Test
    public void testGPSNormal(){
        Position A = new Position(42,0);
        Position B = new Position(42,1);

        PosFunction pF = new PosFunction(A,B);

        SimpleTrajectory sT = new SimpleTrajectory(pF,0,2000,0);

        Assert.assertEquals(sT.getLength(),2000,0.0001);

        Assert.assertEquals(pF.get(0),sT.getGPS(0));
        Assert.assertNotEquals(pF.get(2000),sT.getGPS(0));
        Assert.assertEquals(pF.get(500),sT.getGPS(500));
    }

    @Test
    public void testGPSInverted(){
        Position A = new Position(42,0);
        Position B = new Position(42,1);

        PosFunction pF = new PosFunction(A,B);

        SimpleTrajectory sT = new SimpleTrajectory(pF,2000,0,0);

        Assert.assertEquals(sT.getLength(),2000,0.0001);

        Assert.assertEquals(pF.get(2000),sT.getGPS(0));
        Assert.assertNotEquals(pF.get(0),sT.getGPS(0));
        Assert.assertEquals(pF.get(1999),sT.getGPS(1));
    }

    @Test
    public void testGPSWset(){
        Position A = new Position(42,0);
        Position B = new Position(42,1);

        PosFunction pF = new PosFunction(A,B);

        SimpleTrajectory sT = new SimpleTrajectory(pF,0,2000,10);

        Assert.assertEquals(sT.getLength(),2000,0.0001);

        Assert.assertEquals(pF.get(0,10),sT.getGPS(0));
        Assert.assertNotEquals(pF.get(0),sT.getGPS(0));
        Assert.assertEquals(pF.get(2000,10),sT.getGPS(2000));
    }

    @Test
    public void testGPSInvertedWset(){
        Position A = new Position(42,0);
        Position B = new Position(42,1);

        PosFunction pF = new PosFunction(A,B);

        SimpleTrajectory sT = new SimpleTrajectory(pF,2000,0,10);

        Assert.assertEquals(sT.getLength(),2000,0.0001);

        Assert.assertEquals(pF.get(0,-10),sT.getGPS(2000));
        Assert.assertNotEquals(pF.get(0),sT.getGPS(0));
        Assert.assertEquals(pF.get(2000,-10),sT.getGPS(0));
    }
}
