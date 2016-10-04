package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.NotImplementedException;
import fr.enssat.lanniontech.api.services.UserService;
import fr.enssat.lanniontech.api.verticles.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;

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
        router.route(HttpMethod.GET, "/api/users/:userMail").handler(this::processGetUser); //TODO: UUID plutôt que mail ? A voir avec Romain
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
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processCreateUser(RoutingContext routingContext) {
        try {
            JsonObject body = routingContext.getBodyAsJson();
            if (body == null) {
                throw new BadRequestException();
            }

            String email = body.getString("email");
            String password = body.getString("password");
            String lastName = body.getString("lastName");
            String firstName = body.getString("firstName");
            if (StringUtils.isBlank(email) || StringUtils.isBlank(password) || StringUtils.isBlank(lastName) || StringUtils.isBlank(firstName)) {
                throw new BadRequestException();
            }

            UserType type = UserType.forValue(body.getInteger("type"));

            User user = userService.create(email, password, lastName, firstName, type);
            HttpResponseBuilder.buildOkResponse(routingContext, user);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processUpdateUser(RoutingContext routingContext) {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetAllUsers(RoutingContext routingContext) {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetUser(RoutingContext routingContext) {
        try {
            String email = routingContext.request().getParam("userMail");

            User user = userService.get(email);
            HttpResponseBuilder.buildOkResponse(routingContext, user);
        } catch (Exception e) { //TODO: 404, 403 si pas soit même ou que pas admin
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

}
