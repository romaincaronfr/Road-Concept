package fr.enssat.lanniontech.verticles;

import com.fasterxml.jackson.databind.util.JSONPObject;
import fr.enssat.lanniontech.utils.Configuration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    @Override
    public void start() {
        Router router = Router.router(vertx);

        configureHandlers(router);

        vertx.deployVerticle(new APIDocVerticle(router));
        vertx.deployVerticle(new AuthenticationVerticle(router));
        vertx.deployVerticle(new PrivateTestVerticle(router));

        vertx.createHttpServer().requestHandler(router::accept).listen(Configuration.serverPort);
    }

    private void configureHandlers(Router router) {
        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        CustomAuthProvider authProvider = new CustomAuthProvider();
        router.route().handler(UserSessionHandler.create(authProvider));

        AuthHandler basicAuthHandler = BasicAuthHandler.create(authProvider);
        // All requests to paths starting with '/api/' will be protected
        router.route("/api/*").handler(basicAuthHandler);
      //  router.route("/api/*").handler(new CustomAuthHandler(authProvider));
    }
}
