package fr.enssat.lanniontech.api.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.exceptions.DatabaseOperationException;
import fr.enssat.lanniontech.api.exceptions.NotImplementedException;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.JSONHelper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MapFeatureRepository extends MapRepository {

    private static final String MONGODB_MAP_COLLECTION_PREFIX = "map_";

    public void create(int mapID, Feature feature) {
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

    public void createAll(int mapID, FeatureCollection features) {
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

    public Feature get(int featureID) {
        throw new NotImplementedException(); //TODO
    }

    public void delete(int featureID) {
        throw new NotImplementedException(); //TODO
    }

    public FeatureCollection getAll(int mapID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            try {
                FeatureCollection features = new FeatureCollection();
                ObjectMapper mapper = new ObjectMapper(); //TODO: Move to JSONHelper ?
                for (Document item : collection.find()) {
                    features.add(mapper.readValue(item.toJson(), Feature.class));
                }
                return features;
            } catch (Exception e) {
                throw new DatabaseOperationException("Error while reading JSON from NoSQL database", e);
            }
        }
    }

    public void deleteAll(Integer mapID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            db.getCollection(computeCollectionName(mapID)).drop();
        }
    }
}
