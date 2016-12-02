package fr.enssat.lanniontech.api.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Simple verticle to serve static API documentation.
 */
public class APIDocVerticle extends AbstractVerticle {

    private static final String DOC_PATH = "/doc*";

    private Router router;

    public APIDocVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {
        StaticHandler staticHandler = StaticHandler.create();
        staticHandler.setCachingEnabled(false); // TODO: Remove in production mode
        staticHandler.setAllowRootFileSystemAccess(true);
        router.route(DOC_PATH).handler(staticHandler);
        // An "application/json" content type have been set by default. We must re-set a "text/html" one.
        router.route(DOC_PATH).handler(routingContext -> routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8"));
    }

}
