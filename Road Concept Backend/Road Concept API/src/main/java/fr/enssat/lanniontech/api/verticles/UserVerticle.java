package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.exceptions.InvalidParameterException;
import fr.enssat.lanniontech.api.exceptions.PrivilegeLevelException;
import fr.enssat.lanniontech.api.services.UserService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
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
        router.route(HttpMethod.GET, "/api/users/:userID").blockingHandler(this::processGetUser);
        router.route(HttpMethod.PUT, "/api/users/:userID").blockingHandler(this::processUpdateUser);
        router.route(HttpMethod.DELETE, "/api/users/:userID").blockingHandler(this::processDeleteUser);
        router.route(HttpMethod.PUT, "/api/users/:userID").blockingHandler(this::processUpdateUser);
    }

    // ========
    // BUSINESS
    // ========

    private void processDeleteUser(RoutingContext routingContext) {
        try {
            checkAdminLevel(routingContext);

            int id = Integer.valueOf(routingContext.request().getParam("userID"));
            userService.delete(id);
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (ClassCastException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid ID");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }


    private void processGetUser(RoutingContext routingContext) {
        try {
            checkAdminLevel(routingContext);
            int id = Integer.valueOf(routingContext.request().getParam("userID"));
            User user = userService.get(id);
            HttpResponseBuilder.buildOkResponse(routingContext, user);
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (ClassCastException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid ID");
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (Exception e) {
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
        } catch (DecodeException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid JSON format");
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (ClassCastException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Bad format for parameters. Check the API documentation.");
        } catch (InvalidParameterException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, e.getMessage());
        } catch (EntityAlreadyExistsException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "An user already exists for this email");
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processUpdateUser(RoutingContext routingContext) {
        try {
            JsonObject body = routingContext.getBodyAsJson();
            if (body == null) {
                throw new BadRequestException();
            }

            int id = Integer.valueOf(routingContext.request().getParam("userID"));
            User logged = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            if (id != logged.getId()) {
                checkAdminLevel(routingContext);
            }

            User data = new User();
            data.setEmail(body.getString("email"));
            data.setFirstName(body.getString("firstName"));
            data.setLastName(body.getString("lastName"));

            User updated = userService.update(data);

            if (id == logged.getId()) {
                routingContext.session().put(Constants.SESSION_CURRENT_USER, updated);
            }
            HttpResponseBuilder.buildOkResponse(routingContext, updated);
        } catch (DecodeException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid JSON format");
        } catch (PrivilegeLevelException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "You must be an administrator to do this action.");
        } catch (EntityNotExistingException e) {
            HttpResponseBuilder.buildNotFoundException(routingContext, e);
        } catch (Exception e) {
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

    // =========
    // UTILITIES
    // =========

    private void checkAdminLevel(RoutingContext routingContext) { //TODO: Déplacer cette vérification dans la couche service ?
        User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
        if (currentUser.getType() == null || currentUser.getType() != UserType.ADMINISTRATOR) {
            throw new PrivilegeLevelException();
        }
    }
}
