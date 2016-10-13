package fr.enssat.lanniontech.api.repositories;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.JSONSerializer;
import org.bson.Document;

public class TestMongoDBRepository {

    public void testConnection(User user) {
        insert(user, "test_connection");
        getMap("test_connection");
    }

    public void insert(User user, String collectionName) {
        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            MongoCollection<Document> collection = db.getCollection(collectionName);

            Document document = Document.parse(JSONSerializer.toJSON(user));
            collection.insertOne(document); // insertMany(List) also available :)

        }
    }

    public void getMap(String collectionName) {

        try (MongoClient client = DatabaseConnector.getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);

            User map = null;
            MongoCollection<Document> collection = db.getCollection(collectionName);

            System.out.println("Total items in the collection -> " + collection.count());

            // Display all the collection
            for (Document item : collection.find()) {
                System.out.println(item);
            }

        }
    }
}
