package fr.enssat.lanniontech.api.verticles;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.exceptions.AuthenticationException;
import fr.enssat.lanniontech.api.services.AuthenticationService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.verticles.utilities.HttpResponseBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
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
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AuthenticationVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationVerticle.class);

    private static final String INPUT_JSON_EMAIL = "email";
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
        router.route(HttpMethod.POST, "/login").blockingHandler(this::processLogin);

        router.route(HttpMethod.POST, "/api/logout").handler(this::processLogout);

        router.route(HttpMethod.GET, "/api/me").blockingHandler(this::processUserDetails);
    }

    // ========
    // BUSINESS
    // ========

    private void processLogout(RoutingContext routingContext) {
        try {
            // We need to set the expiration date of the authentication cookie
            DateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String headerValue = "vertx-web.session=" + routingContext.session().id() + "; path=/; expires=" + formatter.format(new Date());
            routingContext.response().putHeader(HttpHeaders.SET_COOKIE, headerValue);
            routingContext.session().destroy();
            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processLogin(RoutingContext routingContext) {
        // The content type is not set by default for this request
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        try {
            JsonObject requestBody = routingContext.getBodyAsJson();
            if (requestBody == null || StringUtils.isBlank(requestBody.getString(INPUT_JSON_EMAIL)) || StringUtils.isBlank(requestBody.getString(INPUT_JSON_PASSWORD))) {
                throw new BadRequestException(); // FIXME: HTTP 500 error if one of the fields are not set
            }

            String userName = requestBody.getString(INPUT_JSON_EMAIL);
            String password = requestBody.getString(INPUT_JSON_PASSWORD);
            User user = authenticationService.login(userName, password); // Insure the user credentials are valid

            // We can't use "routingContext.user()" since we don't use any Vert.x auth provider
            routingContext.session().put(Constants.SESSION_CURRENT_USER, user);
            //TODO: Create and set a 'Simulator' instnce in the session when the user connect?

            HttpResponseBuilder.buildNoContentResponse(routingContext);
        } catch (BadRequestException | ClassCastException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Email and password must be strings, not null, empty or blank.");
        } catch (DecodeException e) {
            HttpResponseBuilder.buildBadRequestResponse(routingContext, "Invalid JSON format.");
        } catch (AuthenticationException e) {
            HttpResponseBuilder.buildForbiddenResponse(routingContext, "Bad credentials, check your login and/or password.");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }

    private void processUserDetails(RoutingContext routingContext) {
        try {
            User currentUser = routingContext.session().get(Constants.SESSION_CURRENT_USER);
            HttpResponseBuilder.buildOkResponse(routingContext, currentUser);
        } catch (Exception e) {
            HttpResponseBuilder.buildUnexpectedErrorResponse(routingContext, e);
        }
    }
}
