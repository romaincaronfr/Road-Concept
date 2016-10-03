package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.exceptions.AuthenticationException;
import fr.enssat.lanniontech.api.services.AuthenticationService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.verticles.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;

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
        router.route(HttpMethod.POST, "/login").handler(routingContext -> {
            processLogin(routingContext);
        });

        router.route(HttpMethod.POST, "/api/logout").handler(routingContext -> {
            processLogout(routingContext);
        });
    }

    // ========
    // BUSINESS
    // ========

    private void processLogout(RoutingContext routingContext) {
        try {
            routingContext.session().destroy();
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processLogin(RoutingContext routingContext) {
        try {
            JsonObject requestBody = routingContext.getBodyAsJson();
            if (requestBody == null || StringUtils.isBlank(requestBody.getString("username")) || StringUtils.isBlank(requestBody.getString("password"))) {
                throw new BadRequestException();
            }
            String userName = requestBody.getString(INPUT_JSON_USERNAME);
            String password = requestBody.getString(INPUT_JSON_PASSWORD);
            User user = authenticationService.login(userName, password); // Insure the user credentials are valid

            // We can't use "routingContext.user()" since we don't use any Vert.x auth provider
            routingContext.session().put(Constants.SESSION_CURRENT_USER, user);
            HttpResponseBuilder.buildOkResponse(routingContext, user);
        } catch (BadRequestException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Username and password can't be null, empty or blank.");
        } catch (AuthenticationException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "Bad credentials, check your login and/or password.");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }
}
