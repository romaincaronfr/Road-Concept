package fr.enssat.lanniontech.verticles;

import fr.enssat.lanniontech.utils.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;


/**
 * Created by Romain on 28/09/2016.
 */
public class MongoVerticle extends AbstractVerticle {

    private MongoClient mongoClient;


    @Override
    public void start() throws Exception {
        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", Constants.mongoUrl)
                .put("db_name", Constants.dbName);

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);


        //Only to test
        JsonObject document = new JsonObject().put("title", "The Hobbit");
        mongoClient.insert("map123", document, res -> {

            if (res.succeeded()) {

                String id = res.result();
                System.out.println("Inserted book with id " + id);

            } else {
                res.cause().printStackTrace();
            }

        });
        //End test
    }
}
