import fr.enssat.lanniontech.App;
import org.junit.Assert;
import org.junit.Test;

public class TestApp {

    @Test
    public void test() {
        Assert.assertTrue(App.foo() == 4);
    }
}
