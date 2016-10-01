package fr.enssat.lanniontech.verticles.utilities;

import fr.enssat.lanniontech.entities.RestException;
import fr.enssat.lanniontech.utilities.JSONSerializer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;

import javax.ws.rs.core.MediaType;

public class HttpResponseBuilder {

    public static void buildUnexpectedErrorResponse(RoutingContext routingContext, Throwable cause) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        error.setCause("Unexpected error due to {" + cause.getClass() + "}");

        routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        routingContext.response().end(JSONSerializer.toJSON(error));
    }

    public static void buildForbiddenResponse(RoutingContext routingContext, String cause) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_FORBIDDEN);
        error.setCause(cause);

        routingContext.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        routingContext.response().end(JSONSerializer.toJSON(error));
    }

    public static void buildBadRequestResponse(RoutingContext routingContext, String cause) {
        RestException error = new RestException();
        error.setCode(HttpStatus.SC_BAD_REQUEST);
        error.setCause(cause);

        routingContext.response().setStatusCode(HttpStatus.SC_BAD_REQUEST);
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        routingContext.response().end(JSONSerializer.toJSON(error));
    }

    public static void buildOkResponse(RoutingContext routingContext, Object data) {
        routingContext.response().setStatusCode(HttpStatus.SC_ACCEPTED);
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        routingContext.response().end(JSONSerializer.toJSON(data));
    }

    public static void buildNoContentResponse(RoutingContext routingContext) {
        routingContext.response().setStatusCode(HttpStatus.SC_NO_CONTENT);
        routingContext.response().end();
    }

    public static void buildUnauthorizedResponse(RoutingContext routingContext) {
        routingContext.response().setStatusCode(HttpStatus.SC_UNAUTHORIZED);
        routingContext.response().end();
    }
}
