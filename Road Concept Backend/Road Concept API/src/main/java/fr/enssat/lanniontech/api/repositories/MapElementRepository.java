package fr.enssat.lanniontech.api.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.enssat.lanniontech.api.exceptions.DatabaseOperationException;
import fr.enssat.lanniontech.api.geojson.Feature;
import fr.enssat.lanniontech.api.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.JSONHelper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MapElementRepository extends MapInfoRepository {

    private static final String MONGODB_MAP_COLLECTION_PREFIX = "map_";

    public void addFeature(int mapID, Feature feature) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            Document document = Document.parse(JSONHelper.toJSON(feature));
            collection.insertOne(document);
        }
    }

    private String computeCollectionName(int mapID) {
        return MONGODB_MAP_COLLECTION_PREFIX + mapID;
    }

    public void addFeatures(int mapID, FeatureCollection features) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            List<Document> documents = new ArrayList<>();
            for (Feature feature : features) {
                documents.add(Document.parse(JSONHelper.toJSON(feature)));
            }
            collection.insertMany(documents);
        }
    }

    public Feature getFeature(int featureID) {
        return null; //TODO
    }

    public void removeFeature(int featureID) {
        //TODO
    }

    public FeatureCollection getAllFeatures(int mapID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            try {
                FeatureCollection features = new FeatureCollection();
                ObjectMapper mapper = new ObjectMapper();
                for (Document item : collection.find()) {
                    features.add(mapper.readValue(item.toJson(), Feature.class));
                }
                return features;
            } catch (Exception e) {
                e.printStackTrace();
                throw new DatabaseOperationException("Error while reading JSON from NoSQL database", e);
            }
        }


        //    public void testConnection(User user) {
        //        insert(user, "test_connection");
        //        getMap("test_connection");
        //    }
        //
        //    public void insert(User user, String collectionName) {
        //        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
        //            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
        //
        //            MongoCollection<Document> collection = db.getCollection(collectionName);
        //
        //            Document document = Document.parse(JSONHelper.toJSON(user));
        //            collection.insertOne(document); // insertMany(List) also available :)
        //
        //        }
        //    }
        //
        //    public void getMap(String collectionName) {
        //
        //        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
        //            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
        //
        //            User map = null;
        //            MongoCollection<Document> collection = db.getCollection(collectionName);
        //
        //            System.out.println("Total items in the collection -> " + collection.count());
        //
        //            // Display all the collection
        //            for (Document item : collection.find()) {
        //                System.out.println(item);
        //            }
        //
        //        }
        //    }
    }

    public void deleteAll(Integer mapID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            db.getCollection(computeCollectionName(mapID)).drop();
        }
    }
}
