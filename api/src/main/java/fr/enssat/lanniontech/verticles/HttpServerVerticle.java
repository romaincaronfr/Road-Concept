package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.utils.Configuration;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.apache.http.HttpStatus;
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

        router.route("/api/*").handler(routingContext -> {
            if (routingContext.session() == null || routingContext.session().get("me") == null) {
                routingContext.response().setStatusCode(HttpStatus.SC_FORBIDDEN).end();
            }
        });
    }
}
