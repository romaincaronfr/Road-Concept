package fr.enssat.lanniontech.api.repositories;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.exceptions.DatabaseOperationException;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.JSONHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class MapFeatureRepository extends MapRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapFeatureRepository.class);

    private static final String MONGODB_MAP_COLLECTION_PREFIX = "map_";

    public Feature create(int mapID, Feature feature) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            Document document = Document.parse(JSONHelper.toJSON(feature));
            collection.insertOne(document);
        }
        return feature;
    }

    private String computeCollectionName(int mapID) {
        return MONGODB_MAP_COLLECTION_PREFIX + mapID;
    }

    public void createAll(int mapID, FeatureCollection features) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));


            for (Feature added : features.getFeatures()) {
                LOGGER.debug("Going to insert = " + added.getOpenStreetMapID());
            }

            List<Document> documents = new ArrayList<>();
            for (Feature feature : features) {
                if (feature.getGeometry() instanceof LineString) {
                    LineString lineString = (LineString) feature.getGeometry();
                    if (!lineString.getCoordinates().isEmpty()) {
                        documents.add(Document.parse(JSONHelper.toJSON(feature)));
                    }
                } else {
                    documents.add(Document.parse(JSONHelper.toJSON(feature)));
                }
            }
            collection.insertMany(documents);
        }
    }

    public Feature getFromUUID(int mapID, UUID featureUUID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            try {
                BasicDBObject query = new BasicDBObject();
                query.put("properties.id", featureUUID.toString());
                FindIterable<Document> queryResult = collection.find(query);

                Document item = queryResult.iterator().next(); //FIXME: Ressource
                Feature result = JSONHelper.fromJSON(item.toJson(), Feature.class);
                String uuid = (String) result.getProperties().get("id");
                result.setUuid(UUID.fromString(uuid));
                return result;
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                throw new DatabaseOperationException("Error while reading JSON from NoSQL database", e);
            }
        }
    }

    public long delete(int mapID, UUID featureUUID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            try {
                BasicDBObject query = new BasicDBObject();
                query.put("properties.id", featureUUID.toString());
                DeleteResult deleteResult = collection.deleteOne(query);
                return deleteResult.getDeletedCount();
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
                throw new DatabaseOperationException("Error occured @@@ TODO", e);
            }
        }
    }

    public FeatureCollection getAll(int mapID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            try {
                FeatureCollection features = new FeatureCollection();
                for (Document item : collection.find()) {
                    Feature result = JSONHelper.fromJSON(item.toJson(), Feature.class);
                    result.setUuid(UUID.fromString((String) result.getProperties().get("id")));

                    features.getFeatures().add(result);
                }
                return features;
            } catch (Exception e) {
                LOGGER.debug(ExceptionUtils.getStackTrace(e));
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

    public Feature getFromOSMID(int mapID, String openStreetMapID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(computeCollectionName(mapID));

            try {
                BasicDBObject query = new BasicDBObject();
                query.put("id", openStreetMapID);
                FindIterable<Document> queryResult = collection.find(query);
                Document item = queryResult.iterator().next(); //FIXME Resource

                Feature result = JSONHelper.fromJSON(item.toJson(), Feature.class);
                String uuid = (String) result.getProperties().get("id");
                result.setUuid(UUID.fromString(uuid));
                return result;
            } catch (NoSuchElementException e) {
                return null;
            } catch (Exception e) {
                throw new DatabaseOperationException("Error while reading JSON from NoSQL database", e);
            }
        }
    }
}
