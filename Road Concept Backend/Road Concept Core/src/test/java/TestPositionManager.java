import fr.enssat.lanniontech.PositionManager;
import fr.enssat.lanniontech.positioning.Position;
import org.junit.Assert;
import org.junit.Test;

public class TestPositionManager {

    @Test
    public void addPos(){
        PositionManager PM = new PositionManager();
        Position P1 = new Position(42.25555555,56.5);
        Assert.assertTrue(PM.addPosition(P1)==P1 && PM.getSize()==1);
    }

    @Test
    public void add2Pos(){
        PositionManager PM = new PositionManager();
        Position P1 = new Position(42.25555555,56.5);
        Position P2 = new Position(46.25,56.5);

        PM.addPosition(P1);
        Assert.assertTrue(PM.addPosition(P2)==P2 && PM.getSize()==2);

    }

    @Test
    public void addDuplicatePos(){
        PositionManager PM = new PositionManager();
        Position P1 = new Position(42.25555555,56.5);
        Position P2 = new Position(46.25,56.5);
        Position P3 = new Position(42.25555555,56.5);

        PM.addPosition(P1);
        PM.addPosition(P2);
        Assert.assertTrue(PM.addPosition(P3)==P1 && PM.getSize()==2);
    }

    @Test
    public void addDuplicatePos2(){
        PositionManager PM = new PositionManager();
        Position P1 = new Position(42.25555555,56.5);
        Position P2 = new Position(46.25,56.5);

        PM.addPosition(P1);
        PM.addPosition(P2);
        Assert.assertTrue(PM.addPosition(46.25,56.5)==P2 && PM.getSize()==2);
    }

    @Test
    public void addManual(){
        PositionManager PM = new PositionManager();
        Position P1 = PM.addPosition(46.25,56.5);
        Assert.assertTrue(PM.addPosition(46.25,56.45)!=P1 && PM.getSize()==2);
    }

}
