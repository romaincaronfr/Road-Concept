package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.repositories.SimulationParametersRepository;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.List;

public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    @Override
    public void start() {
        Router router = Router.router(vertx);

        DatabaseConnector.setUp(); // Throws if the server can't connect/set up the databases connections
        cleanUpData();

        configureGlobalHandlers(router);

        vertx.deployVerticle(new DocumentationVerticle(router));
        vertx.deployVerticle(new AuthenticationVerticle(router));
        vertx.deployVerticle(new MapsVerticle(router));
        vertx.deployVerticle(new SimulatorVerticle(router));
        vertx.deployVerticle(new UserVerticle(router));
        vertx.deployVerticle("js-verticles/osm-to-geojson-verticle.js");

        vertx.createHttpServer().requestHandler(router::accept).listen(Constants.HTTP_SERVER_PORT);
        LOGGER.warn("Road Concept API successfully started !");
    }

    private void configureGlobalHandlers(Router router) {
        CorsHandler corsHandler = CorsHandler.create("*");
        configureCORS(corsHandler);
        router.route().handler(corsHandler); // Allows cross domain origin request

        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx))); // All request *MUST* terminate on the same server

        // Require authentication for all path starting "/api"
        router.route("/api/*").handler(routingContext -> {
            // Set the default Content Type for all the responses
            routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

            if (routingContext.session() == null || routingContext.session().get(Constants.SESSION_CURRENT_USER) == null) {
                HttpResponseBuilder.buildUnauthorizedResponse(routingContext);
            } else {
                checkActivesSimulations(routingContext); //TODO: Do it here ? Really ?
                routingContext.next(); // process the next handler, if any
            }
        });

        // TODO: Add Throwable and then remove the catch (Exception) in each verticle method
        router.route().failureHandler(HttpResponseBuilder::buildUnexpectedErrorResponse);
    }

    private void configureCORS(CorsHandler corsHandler) {
        corsHandler.allowedMethod(HttpMethod.GET);
        corsHandler.allowedMethod(HttpMethod.OPTIONS);
        corsHandler.allowedMethod(HttpMethod.POST);
        corsHandler.allowedMethod(HttpMethod.PUT);
        corsHandler.allowedMethod(HttpMethod.DELETE);
        corsHandler.allowedHeader("Authorization");
        corsHandler.allowedHeader("Content-Type");
        corsHandler.allowedHeader("Set-Cookie");
        corsHandler.allowedHeader("Access-Control-Allow-Origin");
        corsHandler.allowedHeader("Access-Control-Allow-Headers");
        corsHandler.allowCredentials(true);
    }

    //TODO: Pas idéal d'instancier un repository dans les verticles
    private void cleanUpData() {
        try {
            SimulationParametersRepository simulationParametersRepository = new SimulationParametersRepository();
            simulationParametersRepository.deleteUnfinished();
        } catch (Exception e) {
            LOGGER.error("Cannot clean up data [ " + e.getClass() + " ]");
        }
    }

    private void checkActivesSimulations(RoutingContext routingContext) {
        List<Simulation> sessionStoredSimulations = routingContext.session().get("actives_simulations");
        sessionStoredSimulations.removeIf(Simulation::isFinish);
    }
}
