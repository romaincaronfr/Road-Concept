package fr.enssat.lanniontech.verticles;

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
        StaticHandler staticHandler = StaticHandler.create();
        staticHandler.setCachingEnabled(false); //TODO remove in production mode
        staticHandler.setAllowRootFileSystemAccess(true);
        router.route("/doc/*").handler(staticHandler);
    }

}
