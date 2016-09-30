package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.entities.MapInfo;
import fr.enssat.lanniontech.entities.User;
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

public class GeneralMapsVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralMapsVerticle.class);

    private MapsService mapsService = new MapsService();

    private Router router;

    public GeneralMapsVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.GET, "/api/maps").blockingHandler(routingContext -> {
            try {
                int currentUserID = ((User) routingContext.session().get(Constants.SESSION_CURRENT_USER)).getId();
                List<MapInfo> data = mapsService.getAllMapsInfo(currentUserID);
                routingContext.response()
                        .setStatusCode(HttpStatus.SC_ACCEPTED)
                        .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .end(JSONSerializer.toJSON(JSONSerializer.toJSON(data)));
            } catch (Exception e) {
                e.printStackTrace();
                routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).end(); //TODO: Add a "unexpected exception to return a rest exception
            }
        });

    }
}
