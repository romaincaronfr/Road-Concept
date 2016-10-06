package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.InvalidParameterException;
import fr.enssat.lanniontech.api.exceptions.NotImplementedException;
import fr.enssat.lanniontech.api.exceptions.PrivilegeLevelException;
import fr.enssat.lanniontech.api.exceptions.database.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.services.UserService;
import fr.enssat.lanniontech.api.utilities.Constants;
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
import java.util.List;

public class UserVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserVerticle.class);

    private Router router;
    private UserService userService = new UserService();

    public UserVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {
        router.route(HttpMethod.GET, "/api/users").blockingHandler(this::processGetAllUsers);
        router.route(HttpMethod.POST, "/api/users").blockingHandler(this::processCreateUser);
        router.route(HttpMethod.GET, "/api/users/:userMail").blockingHandler(this::processGetUser);
        router.route(HttpMethod.PUT, "/api/users/:userMail").blockingHandler(this::processUpdateUser);
        router.route(HttpMethod.DELETE, "/api/users/:userMail").blockingHandler(this::processDeleteUser);
    }

    // ========
    // BUSINESS
    // ========

    private void processDeleteUser(RoutingContext routingContext) {
        try {
            checkAdminLevel(routingContext);

            String email = routingContext.request().getParam("userMail");
            User user = new User();
            user.setEmail(email);
            userService.delete(user);
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processCreateUser(RoutingContext routingContext) {
        try {
            checkAdminLevel(routingContext);

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
            HttpResponseBuilder.buildCreatedResponse(routingContext, user);
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (ClassCastException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Bad format for parameters. Check the API documentation.");
        } catch (InvalidParameterException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, e.getMessage());
        } catch (EntityAlreadyExistsException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "An user already exists for this email");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processUpdateUser(RoutingContext routingContext) {
        try {
            checkAdminLevel(routingContext);
            throw new NotImplementedException();
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetAllUsers(RoutingContext routingContext) {
        try {
            checkAdminLevel(routingContext);
            List<User> users = userService.getAll();
            HttpResponseBuilder.buildOkResponse(routingContext, users);
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processGetUser(RoutingContext routingContext) {
        try {
            checkAdminLevel(routingContext);
            String email = routingContext.request().getParam("userMail");
            User user = userService.get(email);
            HttpResponseBuilder.buildOkResponse(routingContext, user);
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (Exception e) { //TODO: 404
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    // =========
    // UTILITIES
    // =========

    private void checkAdminLevel(RoutingContext routingContext) {
        User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
        if (currentUser.getType() == null || currentUser.getType() != UserType.ADMINISTRATOR) {
            throw new PrivilegeLevelException();
        }
    }

}
