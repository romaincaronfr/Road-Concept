package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.map.Map;
import fr.enssat.lanniontech.api.entities.map.MapInfo;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.exceptions.InconsistentException;
import fr.enssat.lanniontech.api.exceptions.JSONProcessingException;
import fr.enssat.lanniontech.api.services.MapService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.HttpResponseBuilder;
import fr.enssat.lanniontech.api.utilities.JSONHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

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

        router.route(HttpMethod.POST, "/api/maps/:mapID/features").blockingHandler(this::processCreateFeature);
        router.route(HttpMethod.PUT, "/api/maps/:mapID/features/:featureUUID").blockingHandler(this::processUpdateFeature);
        router.route(HttpMethod.GET, "/api/maps/:mapID/features/:featureUUID").blockingHandler(this::processGetOneFeature);
        router.route(HttpMethod.DELETE, "/api/maps/:mapID/features/:featureUUID").blockingHandler(this::processDeleteFeature);

        router.route(HttpMethod.POST, "/api/maps/:mapID/import").blockingHandler(this::processImportFromOSM);
    }

    private void processCreateFeature(RoutingContext routingContext) {
        try {
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID")); // may throw
            Feature feature = JSONHelper.fromJSON(routingContext.getBodyAsString(), Feature.class);

            Feature created = mapService.addFeature(mapID, feature);
            HttpResponseBuilder.buildCreatedResponse(routingContext, created);
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext,e);
        } catch (InconsistentException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext,"User and Map are not consistent");
        } catch (JSONProcessingException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext,"Invalid GeoJSON");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processDeleteFeature(RoutingContext routingContext) {
        try {
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID")); // may throw
            UUID featureUUID = UUID.fromString(routingContext.request().getParam("featureUUID"));

            mapService.deleteFeature(mapID, featureUUID);
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (IllegalArgumentException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid UUID");
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (InconsistentException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "Entities are not consistent");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processUpdateFeature(RoutingContext routingContext) {
        try {
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID")); // may throw
            UUID featureUUID = UUID.fromString(routingContext.request().getParam("featureUUID"));
            Feature feature = JSONHelper.fromJSON(routingContext.getBodyAsString(), Feature.class);
            feature.setUuid(featureUUID); //FIXME: It's just an ugly temporary fix...

            Feature updated = mapService.updateFeature(mapID, feature);
            HttpResponseBuilder.buildOkResponse(routingContext, updated);
        } catch (IllegalArgumentException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid UUID");
        } catch (JSONProcessingException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext,"Invalid GeoJSON");
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (InconsistentException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "Entities are not consistent");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetOneFeature(RoutingContext routingContext) {
        try {
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID")); // may throw
            UUID featureUUID = UUID.fromString(routingContext.request().getParam("featureUUID"));

            Feature feature = mapService.getFeature(mapID, featureUUID);
            HttpResponseBuilder.buildOkResponse(routingContext, feature);
        } catch (IllegalArgumentException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid UUID");
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (InconsistentException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "Entities are not consistent");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetOneMap(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            int mapID = Integer.valueOf(routingContext.request().getParam("mapID")); // may throw

            Map map = mapService.getMap(currentUser, mapID); // may throw
            HttpResponseBuilder.buildOkResponse(routingContext, map);
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (NumberFormatException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Incorrect map ID.");
        } catch (InconsistentException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "The authenticated user and the given map ID are not consistent.");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processCreateMap(RoutingContext routingContext) {
        try {
            JsonObject body = routingContext.getBodyAsJson();
            if (body == null) {
                throw new BadRequestException();
            }
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            String name = body.getString("name");
            String imageURL = body.getString("image_url");
            String description = body.getString("description");

            MapInfo mapInfo = mapService.create(currentUser, name, false, imageURL, description);
            HttpResponseBuilder.buildOkResponse(routingContext, mapInfo);
        } catch (BadRequestException | DecodeException | ClassCastException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid JSON format");
        } catch (Exception e) { //TODO
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetAllMaps(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            List<MapInfo> maps = mapService.getAllMapsInfo(currentUser);
            HttpResponseBuilder.buildOkResponse(routingContext, maps);
        } catch (Exception e) { //TODO
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processDeleteMap(RoutingContext routingContext) {
        try {
            Integer mapID = Integer.valueOf(routingContext.request().getParam("mapID"));
            mapService.delete(mapID);
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (Exception e) { //TODO
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processImportFromOSM(RoutingContext routingContext) {
        try {
            Integer mapID = Integer.valueOf(routingContext.request().getParam("mapID"));

            Set<FileUpload> fileUploadSet = routingContext.fileUploads();
            Iterator<FileUpload> fileUploadIterator = fileUploadSet.iterator();
            FileUpload fileUpload = fileUploadIterator.next(); // We expect only one file

            Buffer uploadedFile = vertx.fileSystem().readFileBlocking(fileUpload.uploadedFileName());

            int importedCount = mapService.importFromOSM(mapID, uploadedFile.toString());

            vertx.fileSystem().deleteBlocking(fileUpload.uploadedFileName());

            HttpResponseBuilder.buildOkResponse(routingContext, importedCount);
        } catch (NoSuchElementException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Nothing to process");
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }
}
