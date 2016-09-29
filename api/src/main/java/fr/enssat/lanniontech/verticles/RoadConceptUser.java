package fr.enssat.lanniontech.verticles;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

/**
 * Created by maelig on 28/09/2016.
 */
public class RoadConceptUser implements User {

    private String userName;

    public RoadConceptUser(String userName) {
        this.userName = userName;
    }

    @Override
    public User isAuthorised(String s, Handler<AsyncResult<Boolean>> handler) {
        return null;
    }

    @Override
    public User clearCache() {
        return null;
    }

    @Override
    public JsonObject principal() {
        return new JsonObject().put("username",userName);
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {

    }
}
