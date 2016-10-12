package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.MapInfo;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.exceptions.UnconsistentException;
import fr.enssat.lanniontech.api.exceptions.database.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.services.MapService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.verticles.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
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

        router.route(HttpMethod.GET, "/api/maps").blockingHandler(this::processGetAllMaps);
        router.route(HttpMethod.POST, "/api/maps").blockingHandler(this::processCreateMap);
        router.route(HttpMethod.GET, "/api/maps/:mapID").blockingHandler(this::processGetOneMap);
        router.route(HttpMethod.DELETE, "/api/maps/:mapID").blockingHandler(this::processDeleteMap);
    }

    private void processGetOneMap(RoutingContext routingContext) {
        try {
            User currentUser = (User) routingContext.session().get(Constants.SESSION_CURRENT_USER);
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID")); // may throw

            FeatureCollection map = mapService.getMap(currentUser, mapID); // may throw
            HttpResponseBuilder.buildOkResponse(routingContext, map.getFeatures());
        } catch (NumberFormatException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Incorrect map ID.");
        } catch (UnconsistentException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "The authenticated user and the given map ID are not consistent.");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processCreateMap(RoutingContext routingContext) {
        try {
            JsonObject body = routingContext.getBodyAsJson();
            if (body == null) {
                throw new BadRequestException();
            }
            User currentUser = (User) routingContext.session().get(Constants.SESSION_CURRENT_USER);
            String name = body.getString("name");
            String imageURL = body.getString("image_url");
            String description = body.getString("description");

            MapInfo mapInfo = mapService.create(currentUser, name, imageURL, description);
            HttpResponseBuilder.buildOkResponse(routingContext, mapInfo);
        } catch (EntityAlreadyExistsException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "An user already exists for this email");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetAllMaps(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            List<MapInfo> maps = mapService.getAllMapsInfo(currentUser);
            HttpResponseBuilder.buildOkResponse(routingContext, maps);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processDeleteMap(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            // TODO: Check ID consistent

            Integer mapID = Integer.valueOf(routingContext.request().getParam("mapID"));
            mapService.delete(mapID);
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }
}
