import fr.enssat.lanniontech.core.managers.HistoryManager;
import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;
import org.junit.Assert;
import org.junit.Test;

public class TestSimulationHistory {
    @Test
    public void getPositionFromId(){
        HistoryManager SH = new HistoryManager();

        SpaceTimePosition STP1 = new SpaceTimePosition(0,0,0,0,0);

        SH.AddPosition(STP1);

        Assert.assertEquals(1,SH.getVehiclePosition(0).size());
        Assert.assertTrue(STP1==SH.getVehiclePosition(0).get(0));
    }
}
