package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.utils.Configuration;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start() {
        LOGGER.debug("Entrée dans start de MainVerticle");

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        vertx.deployVerticle(new APIDocVerticle(router));
        vertx.deployVerticle(new AuthenticationVerticle(router));

        vertx.createHttpServer().requestHandler(router::accept).listen(Configuration.serverPort);
    }
}
