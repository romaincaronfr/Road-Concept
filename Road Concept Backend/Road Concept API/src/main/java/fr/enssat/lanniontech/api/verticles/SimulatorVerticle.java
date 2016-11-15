package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.services.SimulatorService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.List;
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
        router.route(HttpMethod.GET, "/api/simulate").blockingHandler(this::processFakeSimulation); //TODO: Remove this

        router.route(HttpMethod.POST, "/api/maps/:mapID/simulations").blockingHandler(this::processCreateSimulation);
        router.route(HttpMethod.GET, "/api/maps/:mapID/simulations").blockingHandler(this::processGetAllSimulationsForMap);
        router.route(HttpMethod.GET, "/api/users/:userID/simulations").blockingHandler(this::processGetAllSimulationsForUser);
        router.route(HttpMethod.GET, "/api/simulations/:simulationUUID").blockingHandler(this::processGetSimulation);
        router.route(HttpMethod.DELETE, "/api/simulations/:simulationUUID").blockingHandler(this::processDeleteSimulation);
//        router.route(HttpMethod.GET, "/api/users/:userID/simulations/:simulationUUID/results").blockingHandler(this::processGetResultAt);
//        router.route(HttpMethod.GET, "/api/simulation/:simulationUUID/vehicles/:vehicleID").blockingHandler(this::processGetVehiclePositionHistory);
    }

    //TODO: Handle exceptions
    private void processGetAllSimulationsForUser(RoutingContext routingContext) {
        try {
            int userID = Integer.valueOf(routingContext.request().getParam("userID"));
            List<Simulation> simulations = simulatorService.getAll(userID);
            HttpResponseBuilder.buildOkResponse(routingContext, simulations);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions
    private void processGetSimulation(RoutingContext routingContext) {
        try {
            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
            Simulation simulation = simulatorService.get(simulationUUID);
            HttpResponseBuilder.buildOkResponse(routingContext, simulation);
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions
    private void processDeleteSimulation(RoutingContext routingContext) {
        try {
            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
            simulatorService.delete(simulationUUID);
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions
    private void processGetAllSimulationsForMap(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID"));
            List<Simulation> simulations = simulatorService.getAll(currentUser, mapID);
            HttpResponseBuilder.buildOkResponse(routingContext, simulations);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions
//    private void processGetVehiclePositionHistory(RoutingContext routingContext) {
//        try {
//            int vehicleID = Integer.valueOf(routingContext.request().getParam("vehicleID"));
//            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
//
//            FeatureCollection positionsHistory = simulatorService.getVehiculePositionsHistory(simulationUUID, vehicleID);
//            HttpResponseBuilder.buildOkResponse(routingContext, positionsHistory);
//        } catch (Exception e) {
//            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
//        }
//    }

    @Deprecated
    private void processFakeSimulation(RoutingContext routingContext) {
        try {
            FeatureCollection simulationResult = simulatorService.getFakeSimulationResult();
            HttpResponseBuilder.buildOkResponse(routingContext, simulationResult);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions
    private void processCreateSimulation(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID"));

            JsonObject body = routingContext.getBodyAsJson();
            if (body == null) {
                throw new BadRequestException();
            }

            String name = body.getString("name");
            int durationS = body.getInteger("duration_s");

            Simulation simulation = simulatorService.create(currentUser, name, mapID, durationS);
            HttpResponseBuilder.buildOkResponse(routingContext, simulation);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions
//    private void processGetResultAt(RoutingContext routingContext) {
//        try {
//            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
//            FeatureCollection features = simulatorService.getResult(simulationUUID, 0); //TODO: timestamp à récupérer de la requête
//            HttpResponseBuilder.buildOkResponse(routingContext, features);
//        } catch (Exception e) {
//            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
//        }
//    }

}
