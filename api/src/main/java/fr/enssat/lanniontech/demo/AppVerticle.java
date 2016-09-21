package fr.enssat.lanniontech.demo;

/**
 * Created by maelig on 20/09/2016.
 * http://vertx.io/blog/my-first-vert-x-3-application/
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class AppVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        vertx
                .createHttpServer()
                .requestHandler(r -> {
                    r.response().end("<h1>Road Concept - API</h1>");
                })
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }
}