package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.exceptions.NotImplementedException;
import fr.enssat.lanniontech.api.services.UserService;
import fr.enssat.lanniontech.api.verticles.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserVerticle.class);

    private Router router;
    private UserService userService = new UserService();

    public UserVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.GET, "/api/users").handler(this::processGetAllUsers);

        router.route(HttpMethod.POST, "/api/users").handler(this::processCreateUser);

        router.route(HttpMethod.GET, "/api/users/:userID").handler(this::processGetUser);

        router.route(HttpMethod.PUT, "/api/users/:userID").handler(this::processUpdateUser);

        router.route(HttpMethod.DELETE, "/api/users/:userID").handler(this::processDeleteUser);
    }

    // ========
    // BUSINESS
    // ========

    private void processDeleteUser(RoutingContext routingContext) {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processCreateUser(RoutingContext routingContext) {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processUpdateUser(RoutingContext routingContext) {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetAllUsers(RoutingContext routingContext) {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetUser(RoutingContext routingContext) {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

}
