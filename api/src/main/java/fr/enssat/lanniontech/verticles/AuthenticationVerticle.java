package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.services.AuthenticationService;
import fr.enssat.lanniontech.utils.Configuration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Created by maelig on 27/09/2016.
 */
public class AuthenticationVerticle extends AbstractVerticle {

    private AuthenticationService authenticationService = new AuthenticationService();

    private Router router;

    public AuthenticationVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.POST, "/login").blockingHandler(routingContext -> {

            // Do something that might take some time synchronously (db access)
            System.out.println("@@@ routingContext -> " + routingContext);
            System.out.println("@@@ routingContext.getBodyAsJson -> " + routingContext.getBodyAsJson());
            System.out.println("@@@ routingContext.getBodyAsString -> " + routingContext.getBodyAsString());


            String login = routingContext.getBodyAsJson().getString("login");
            String password = routingContext.getBodyAsJson().getString("password");

            boolean result = authenticationService.login(login, password);

            // Now end the request
            routingContext.response().end("User " + login + " authenticated ? " + result);
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(Configuration.serverPort);


    }
}
