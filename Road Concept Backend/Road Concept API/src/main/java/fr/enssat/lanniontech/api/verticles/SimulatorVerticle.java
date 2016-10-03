package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.services.SimulatorService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.verticles.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatorVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorVerticle.class);

    private SimulatorService simulatorService = new SimulatorService();

    private Router router;

    public SimulatorVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.GET, "/api/simulate").handler(routingContext -> {
            processSimulation(routingContext);
        });
    }

    private void processSimulation(RoutingContext routingContext) {
        try {
            User currentUser = (User) routingContext.session().get(Constants.SESSION_CURRENT_USER);
            boolean result = simulatorService.simulate(); // TODO: Add parameters
            HttpResponseBuilder.buildOkResponse(routingContext, result);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

}
