package fr.enssat.lanniontech.api.repositories;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.exceptions.DatabaseOperationException;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.JSONUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimulationRepository extends AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationRepository.class);

    public void duplicateFeatures(Simulation simulation) {
        //   cloneCollection(MapFeatureRepository.computeCollectionName(simulation.getMapID()), computeDuplicatedCollectionName(simulation.getUuid()));

        MapFeatureRepository mapFeatureRepository = new MapFeatureRepository();
        FeatureCollection features = mapFeatureRepository.getAll(simulation.getMapID());
        createAll(simulation.getUuid(), features);
    }

    //TODO: Refactor to avoid code duplication
    public void createAll(UUID simulationUUID, FeatureCollection features) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            MongoCollection<Document> collection = db.getCollection(computeDuplicatedCollectionName(simulationUUID));

            List<Document> documents = new ArrayList<>();
            for (Feature feature : features) {
                if (feature.getGeometry() instanceof LineString) {
                    LineString lineString = (LineString) feature.getGeometry();
                    if (!lineString.getCoordinates().isEmpty()) {
                        documents.add(Document.parse(JSONUtils.toJSON(feature)));
                    }
                } else {
                    documents.add(Document.parse(JSONUtils.toJSON(feature)));
                }
            }
            collection.insertMany(documents);
        }
    }


    public FeatureCollection getFeatures(UUID simulationUUID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(computeDuplicatedCollectionName(simulationUUID));

            // TODO: Refactor to avoid code duplication
            try {
                FeatureCollection features = new FeatureCollection();
                for (Document item : collection.find()) {
                    Feature result = JSONUtils.fromJSON(item.toJson(), Feature.class);
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

    public void deleteAssociatedFeatures(UUID simulationUUID) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            try {
                MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
                MongoCollection<Document> collection = db.getCollection(computeDuplicatedCollectionName(simulationUUID));
                collection.drop();
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
                throw new DatabaseOperationException("Error occured @@@ TODO", e);
            }
        }
    }

    // =========
    // UTILITIES
    // =========

    private String computeDuplicatedCollectionName(UUID uuid) {
        return "simulation_" + uuid;
    }

    /**
     * Clone a collection.
     *
     * @param fromCollectionName
     *         - The name of collection to be cloned
     * @param toCollectionName
     *         - The name of the cloned collection
     */
    private void cloneCollection(String fromCollectionName, String toCollectionName) throws MongoException {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            MongoCollection toCol = db.getCollection(toCollectionName);

            List<Document> ops = new ArrayList<>();
            ops.add(new Document("$out", toCollectionName));
            MongoCollection sourceCollection = db.getCollection(fromCollectionName);
            sourceCollection.aggregate(ops);
        }
    }
}
