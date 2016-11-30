import fr.enssat.lanniontech.core.Tools;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by 4r3 on 28/11/16.
 */
public class TestTools {
    @Test
    public void testOrientedAngle() {
        Assert.assertEquals(0, Tools.getOrientedAngle(0, 1, 0, 1), 0.000000000001);

        Assert.assertEquals(Math.PI, Tools.getOrientedAngle(0, 1, 0, -1), 0.000000000001);
        Assert.assertEquals(-Math.PI / 2, Tools.getOrientedAngle(0, 1, 1, 0), 0.000000000001);
        Assert.assertEquals(Math.PI / 2, Tools.getOrientedAngle(0, 1, -1, 0), 0.000000000001);
    }
}
