package fr.enssat.lanniontech.tests;

import fr.enssat.lanniontech.api.verticles.AuthenticationVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import org.junit.After;
import org.junit.Before;

//@RunWith(VertxUnitRunner.class)
public class AuthenticationVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(AuthenticationVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

   // @Test
    public void testMyApplication(TestContext context) {

    }
}
