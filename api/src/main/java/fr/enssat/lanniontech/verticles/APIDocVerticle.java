package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.demo.TestLogs;
import fr.enssat.lanniontech.utils.Configuration;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIDocVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(APIDocVerticle.class);

    private Router router;

    public APIDocVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        LOGGER.debug("Entrée dans start de APIDocVerticle");

        StaticHandler staticHandler = StaticHandler.create();
        staticHandler.setCachingEnabled(false); //TODO remove in production mode
        staticHandler.setAllowRootFileSystemAccess(true);
        router.route("/doc/*").handler(staticHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(Configuration.serverPort);

    }

}
