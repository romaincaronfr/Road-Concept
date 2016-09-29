package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.entities.RestException;
import fr.enssat.lanniontech.entities.User;
import fr.enssat.lanniontech.exceptions.AuthenticationException;
import fr.enssat.lanniontech.services.AuthenticationService;
import fr.enssat.lanniontech.utils.Constants;
import fr.enssat.lanniontech.utils.JSONSerializer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

public class AuthenticationVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationVerticle.class);

    private static final String INPUT_JSON_USERNAME = "username";
    private static final String INPUT_JSON_PASSWORD = "password";

    private AuthenticationService authenticationService = new AuthenticationService();

    private Router router;

    public AuthenticationVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        /**
         * The login route *DO NOT* contains "/api" in its path since it *MUST* be accessible when the user is not logged in.
         */
        router.route(HttpMethod.POST, "/login").blockingHandler(routingContext -> {

            JsonObject body = routingContext.getBodyAsJson();
            if (body == null || StringUtils.isBlank(body.getString("username")) || StringUtils.isBlank(body.getString("password"))) {
                String message = "Username and password can't be null, empty or blank.";
                routingContext.response()
                        .setStatusCode(HttpStatus.SC_BAD_REQUEST)
                        .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .end(JSONSerializer.toJSON(new RestException(HttpStatus.SC_BAD_REQUEST, message)));
            } else {
                String userName = body.getString(INPUT_JSON_USERNAME);
                String password = body.getString(INPUT_JSON_PASSWORD);
                try {
                    User user = authenticationService.login(userName, password); // Insure the user credentials are valid
                    routingContext.session().put(Constants.SESSION_CURRENT_USER, user);
                    routingContext.response().setStatusCode(HttpStatus.SC_ACCEPTED).end();
                } catch (AuthenticationException e) {
                    routingContext.response().setStatusCode(HttpStatus.SC_FORBIDDEN).end();
                }
            }
        });

        router.route(HttpMethod.POST, "/api/logout").handler(routingContext -> {
            routingContext.session().destroy();
            routingContext.response().setStatusCode(HttpStatus.SC_NO_CONTENT).end();
        });
    }
}
