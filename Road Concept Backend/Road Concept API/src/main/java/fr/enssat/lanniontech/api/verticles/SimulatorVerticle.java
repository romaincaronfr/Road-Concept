package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.entities.simulation.SimulationVehicleStatistics;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.exceptions.InvalidParameterException;
import fr.enssat.lanniontech.api.exceptions.ProgressUnavailableException;
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
import java.util.Iterator;
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
        router.route(HttpMethod.POST, "/api/maps/:mapID/simulations").blockingHandler(this::processCreateSimulation);
        router.route(HttpMethod.GET, "/api/maps/:mapID/simulations").blockingHandler(this::processGetAllSimulationsForMap);
        router.route(HttpMethod.GET, "/api/users/:userID/simulations").blockingHandler(this::processGetAllSimulationsForUser);
        router.route(HttpMethod.GET, "/api/simulations/:simulationUUID").blockingHandler(this::processGetSimulation);
        router.route(HttpMethod.GET, "/api/simulations/:simulationUUID/progress").blockingHandler(this::processGetSimulationProgress);
        router.route(HttpMethod.DELETE, "/api/simulations/:simulationUUID").blockingHandler(this::processDeleteSimulation);
        router.route(HttpMethod.DELETE, "/api/maps/:mapID/simulations/:simulationUUID").blockingHandler(this::processDeleteSimulation);
        router.route(HttpMethod.GET, "/api/simulations/:simulationUUID/results").blockingHandler(this::processGetResultAt);
        router.route(HttpMethod.GET, "/api/simulations/:simulationUUID/vehicles/:vehicleID").blockingHandler(this::processGetVehiclePositionHistory);
        router.route(HttpMethod.GET, "/api/simulations/:simulationUUID/vehicles/:vehicleID/statistics").blockingHandler(this::processGetVehicleStatistics);
    }

    //TODO: Handle exceptions + doc Swagger
    private void processGetVehicleStatistics(RoutingContext routingContext) {
        try {
            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
            int vehicleID = Integer.parseInt(routingContext.request().getParam("vehicleID"));

            SimulationVehicleStatistics statistics = simulatorService.getVehicleStatistics(simulationUUID, vehicleID);

            HttpResponseBuilder.buildOkResponse(routingContext, statistics);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions + doc Swagger
    private void processGetSimulationProgress(RoutingContext routingContext) {
        try {
            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
            List<Simulation> activesSimulations = routingContext.session().get("actives_simulations");
            int progress = simulatorService.getExecutionProgress(simulationUUID, activesSimulations);
            HttpResponseBuilder.buildOkResponse(routingContext, progress);
        } catch (ProgressUnavailableException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "The given simulation is not currently available :( (not in the session)");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
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

            // Remove the given simulation from the session scope, if present
            List<Simulation> activesSimulations = routingContext.session().get("actives_simulations");
            for (Iterator<Simulation> iterator = activesSimulations.iterator(); iterator.hasNext(); ) {
                Simulation simulation = iterator.next();
                if (simulation.getUuid().equals(simulationUUID)) {
                    iterator.remove();
                }
            }

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

    //TODO: Handle exceptions + doc Swagger
    private void processGetVehiclePositionHistory(RoutingContext routingContext) {
        try {
            int vehicleID = Integer.valueOf(routingContext.request().getParam("vehicleID"));
            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));

            FeatureCollection positionsHistory = simulatorService.getVehiculePositionsHistory(simulationUUID, vehicleID);
            HttpResponseBuilder.buildOkResponse(routingContext, positionsHistory);
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
            int samplingRate = body.getInteger("sampling_rate");
            int departureLivingS = body.getInteger("departure_living_s");
            int departureWorkingS = body.getInteger("departure_working_s");
            UUID livingFeatureUUID = UUID.fromString(body.getString("living_feature"));
            UUID workingFeatureUUID = UUID.fromString(body.getString("working_feature"));
            int carPercentage = body.getInteger("car_percentage");
            int vehicleCount = body.getInteger("vehicle_count");

            Simulation simulation = simulatorService.create(currentUser, name, mapID, samplingRate, departureLivingS, departureWorkingS, livingFeatureUUID, workingFeatureUUID, carPercentage, vehicleCount);

            List<Simulation> activesSimulations = routingContext.session().get("actives_simulations");
            activesSimulations.add(simulation);

            HttpResponseBuilder.buildOkResponse(routingContext, simulation);
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    //TODO: Handle exceptions + doc Swagger
    private void processGetResultAt(RoutingContext routingContext) {
        try {
            UUID simulationUUID = UUID.fromString(routingContext.request().getParam("simulationUUID"));
            int timestamp = Integer.valueOf(routingContext.request().getParam("timestamp"));
            FeatureCollection features = simulatorService.getResultAt(simulationUUID, timestamp);
            HttpResponseBuilder.buildOkResponse(routingContext, features);
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (InvalidParameterException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "The timestamp value is not coherent with the sampling rate.");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

}
