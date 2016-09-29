package fr.enssat.lanniontech.verticles;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.impl.AuthHandlerImpl;

import java.util.Set;

/**
 * Created by maelig on 28/09/2016.
 */
public class CustomAuthHandler extends AuthHandlerImpl {

    public CustomAuthHandler(AuthProvider authProvider) {
        super(authProvider);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CustomAuthHandler.handle()");
        if (routingContext.user() == null) {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CustomAuthHandler.handle() USER NOT LOGGED IN");

            JsonObject loginInfos = new JsonObject();
            String login = routingContext.getBodyAsJson().getString("login");
            String password = routingContext.getBodyAsJson().getString("password");
            loginInfos.put("login", login);
            loginInfos.put("password", password);
            authProvider.authenticate(loginInfos, res -> {
                if (res.succeeded()) {
                    Session session = routingContext.session();
                    io.vertx.ext.auth.User user = res.result();
                    session.put("user", user);
                    routingContext.response().setStatusCode(204).end("user Login success");
                }
            });
        } else {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CustomAuthHandler.handle() USER ALREADY LOGGED IN");
        }
    }
}
