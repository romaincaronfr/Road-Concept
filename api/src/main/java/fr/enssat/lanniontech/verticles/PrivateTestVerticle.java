package fr.enssat.lanniontech.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;


public class PrivateTestVerticle extends AbstractVerticle {

    private Router router;

    public PrivateTestVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        router.route(HttpMethod.GET, "/api/test").handler(routingContext -> {
            String userName = routingContext.user().principal().getString("username");
            routingContext.response().end("USERNAME = " + userName);
        });

    }
}
