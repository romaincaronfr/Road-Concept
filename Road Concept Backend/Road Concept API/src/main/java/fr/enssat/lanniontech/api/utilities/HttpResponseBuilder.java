package fr.enssat.lanniontech.api.utilities;

import fr.enssat.lanniontech.api.entities.RestException;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseBuilder.class);

    public static void buildUnexpectedErrorResponse(RoutingContext routingContext, Throwable cause) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        error.setCause("Unexpected error due to [" + cause.getClass() + "]");

        routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        routingContext.response().end(JSONUtils.toJSON(error));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ExceptionUtils.getStackTrace(cause));
        }
    }

    public static void buildUnexpectedErrorResponse(RoutingContext routingContext) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        error.setCause("Unexpected error occured");

        routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        routingContext.response().end(JSONUtils.toJSON(error));
    }

    public static void buildForbiddenResponse(RoutingContext routingContext, String cause) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_FORBIDDEN);
        error.setCause(cause);

        routingContext.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
        routingContext.response().end(JSONUtils.toJSON(error));
    }

    public static void buildCreatedResponse(RoutingContext routingContext, Object dataCreated) {
        routingContext.response().setStatusCode(HttpStatus.SC_CREATED);
        routingContext.response().end(JSONUtils.toJSON(dataCreated));
    }

    public static void buildBadRequestResponse(RoutingContext routingContext, String cause) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_BAD_REQUEST);
        error.setCause(cause);

        routingContext.response().setStatusCode(HttpStatus.SC_BAD_REQUEST);
        routingContext.response().end(JSONUtils.toJSON(error));
    }

    public static void buildOkResponse(RoutingContext routingContext, Object data) {
        routingContext.response().setStatusCode(HttpStatus.SC_OK);
        routingContext.response().end(JSONUtils.toJSON(data));
    }

    public static void buildNoContentResponse(RoutingContext routingContext) {
        routingContext.response().setStatusCode(HttpStatus.SC_NO_CONTENT);
        routingContext.response().end();
    }

    public static void buildUnauthorizedResponse(RoutingContext routingContext) {
        routingContext.response().setStatusCode(HttpStatus.SC_UNAUTHORIZED);
        routingContext.response().end();
    }

    public static void buildNotFoundException(RoutingContext routingContext, EntityNotExistingException e) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_NOT_FOUND);
        error.setCause(e.getEntityClass() + " not found");
        routingContext.response().end(JSONUtils.toJSON(error));
    }
}
