package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.entities.MapInfo;
import fr.enssat.lanniontech.entities.User;
import fr.enssat.lanniontech.jsonparser.entities.Map;
import fr.enssat.lanniontech.services.MapsService;
import fr.enssat.lanniontech.utils.Constants;
import fr.enssat.lanniontech.utils.JSONSerializer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.List;

public class MapsVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapsVerticle.class);

    private MapsService mapsService = new MapsService();

    private Router router;

    public MapsVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.GET, "/api/maps").handler(routingContext -> {
            try {
                int currentUserID = ((User) routingContext.session().get(Constants.SESSION_CURRENT_USER)).getId();
                List<MapInfo> data = mapsService.getAllMapsInfo(currentUserID);
                routingContext.response()
                        .setStatusCode(HttpStatus.SC_ACCEPTED)
                        .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .end(JSONSerializer.toJSON(JSONSerializer.toJSON(data)));
            } catch (Exception e) {
                routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).end(); //TODO: Add a "unexpected exception to return a rest exception
            }
        });

        router.route(HttpMethod.GET, "/api/maps/:mapID").handler(routingContext -> {
            try {
                int currentUserID = ((User) routingContext.session().get(Constants.SESSION_CURRENT_USER)).getId();
                int mapID = Integer.valueOf(routingContext.request().getParam("mapID"));
                // TODO: Check mapID incorrect (400)
                // TODO: Check mapID pour 404
                Map map = mapsService.getMap(currentUserID, mapID);
                routingContext.response()
                        .setStatusCode(HttpStatus.SC_ACCEPTED)
                        .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .end(JSONSerializer.toJSON(JSONSerializer.toJSON(map)));
            } catch  (Exception e) {
                e.printStackTrace();
                routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).end(); //TODO: Add a "unexpected exception to return a rest exception
            }
        });

    }
}
