package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.services.SimulatorService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

public class SimulatorVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorVerticle.class);

    private SimulatorService simulatorService = new SimulatorService();

    private Router router;

    public SimulatorVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {
        router.route(HttpMethod.GET, "/api/simulate").handler(this::processFakeSimulation);
        router.route(HttpMethod.POST, "/api/maps/:mapID/simulation").handler(this::processSimulation);
        router.route(HttpMethod.GET, "/api/maps/:mapID/simulation/:simulationUUID/results").handler(this::processGetSimulationResult);
    }

    private void processFakeSimulation(RoutingContext routingContext) {
        try {
            FeatureCollection simulationResult = simulatorService.getFakeSimulationResult();
            HttpResponseBuilder.buildOkResponse(routingContext, simulationResult);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processSimulation(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            int id = Integer.valueOf(routingContext.request().getParam("mapID"));

            Simulation simulation = new Simulation();
            simulation.setMapID(id);
            simulation.setUser(currentUser);

            boolean started = simulatorService.simulate(simulation);
            if (started) { //TODO: Ouverture WebSocket ici pour renvoyer la progression au FrontEnd
                HttpResponseBuilder.buildOkResponse(routingContext,simulation);
            } else {
                throw new BadRequestException();
            }
        } catch (BadRequestException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext,"Simulation not started");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetSimulationResult(RoutingContext routingContext) {
        try {
            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
            FeatureCollection features = simulatorService.getResult(simulationUUID);
            HttpResponseBuilder.buildOkResponse(routingContext, features);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

}
