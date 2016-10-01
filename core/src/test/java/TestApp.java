import fr.enssat.lanniontech.Simulator;
import org.junit.Assert;
import org.junit.Test;

public class TestApp {

    @Test
    public void test() {
        Assert.assertTrue(Simulator.foo() == 4);
    }
}
