package fr.enssat.lanniontech.verticles;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

public class CustomAuthProvider implements AuthProvider {

    @Override
    public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CUSTOM AUTH PROVIDER AUTHENTICATE");

        System.out.println("@@@ json object => " + jsonObject.toString());
        String username = jsonObject.getString("username");
        if (username.equals("foo")) {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ SUCCESS SUCCESS");

            handler.handle(Future.succeededFuture(new RoadConceptUser(username)));
        } else {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ FAILURE FAILURE");

            handler.handle(Future.failedFuture("Invalid username/password"));
        }
    }
}
