package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.entities.User;
import fr.enssat.lanniontech.services.AuthenticationService;
import fr.enssat.lanniontech.utils.JSONSerializer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;


public class AuthenticationVerticle extends AbstractVerticle {

    private AuthenticationService authenticationService = new AuthenticationService();

    private Router router;

    public AuthenticationVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.POST, "/login").blockingHandler(routingContext -> {

            String userName = routingContext.getBodyAsJson().getString("login");
            String password = routingContext.getBodyAsJson().getString("password");
            try {
                User user = authenticationService.login(userName, password); // Insure the user credentials are valid
                routingContext.session().put("me", user);
                routingContext.response().setStatusCode(HttpStatus.SC_ACCEPTED).end(JSONSerializer.toJSON(user));
            } catch (AuthenticationException e) {
                routingContext.response().setStatusCode(HttpStatus.SC_FORBIDDEN).end();
            }
        });

        router.route(HttpMethod.POST, "/logout").handler(routingContext -> {
            routingContext.session().destroy();
            routingContext.response().setStatusCode(HttpStatus.SC_NO_CONTENT).end();
        });
    }
}
