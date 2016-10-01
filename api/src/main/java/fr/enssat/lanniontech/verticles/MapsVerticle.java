package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.entities.MapInfo;
import fr.enssat.lanniontech.entities.User;
import fr.enssat.lanniontech.exceptions.UnconsistentException;
import fr.enssat.lanniontech.jsonparser.entities.Map;
import fr.enssat.lanniontech.services.MapService;
import fr.enssat.lanniontech.utilities.Constants;
import fr.enssat.lanniontech.verticles.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MapsVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapsVerticle.class);

    private MapService mapService = new MapService();

    private Router router;

    public MapsVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.GET, "/api/maps").handler(routingContext -> {
            processGetAllMaps(routingContext);
        });

        router.route(HttpMethod.GET, "/api/maps/:mapID").handler(routingContext -> {
            processGetOneMap(routingContext);
        });
    }

    private void processGetOneMap(RoutingContext routingContext) {
        try {
            User currentUser = (User) routingContext.session().get(Constants.SESSION_CURRENT_USER);
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID")); // may throw

            Map map = mapService.getMap(currentUser, mapID); // may throw
            HttpResponseBuilder.buildOkResponse(routingContext, map);
        } catch (NumberFormatException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Incorrect map ID.");
        } catch (UnconsistentException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "The authenticated user and the given map ID are not consistent.");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetAllMaps(RoutingContext routingContext) {
        try {
            User currentUser = (User) routingContext.session().get(Constants.SESSION_CURRENT_USER);
            List<MapInfo> maps = mapService.getAllMapsInfo(currentUser);
            HttpResponseBuilder.buildOkResponse(routingContext, maps);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }
}
