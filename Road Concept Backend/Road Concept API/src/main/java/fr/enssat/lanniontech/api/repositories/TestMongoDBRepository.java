package fr.enssat.lanniontech.api.repositories;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.repositories.connectors.SQLDatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.JSONSerializer;
import org.bson.Document;

public class TestMongoDBRepository {

        public static void main(String[] args) {
            //  MongoDatabase db = SQLDatabaseConnector.getMongoDBClient().getDatabase(Constants.MONGODB_DATABASE_NAME);
            //  MongoCollection<Document> collection = db.getCollection("test");
            //  collection.drop();

            //  TestMongoDBRepository repository = new TestMongoDBRepository();

            // Map map = new MapService().getMap(null, 1); // Fake data
            //  map.setId(new Random().nextInt());
            //  repository.insertMap(map, "test"); // "test" collection *MUST* already exist

            //   Map fromMongo = repository.getMap(-1513300328, "test");
            //    System.out.println("fromMongo id => " + fromMongo.getId());

        }

        public void testConnection(User user){
            insertMap(user, "test_connection");
            getMap("test_connection");
        }

        public void insertMap(User user, String collectionName) {
            try (MongoClient mongo = SQLDatabaseConnector.getMongoDBClient()) { // try-with-resource block, don't need to call mongo.close()
                MongoDatabase db = mongo.getDatabase(Constants.MONGODB_DATABASE_NAME);
                MongoCollection<Document> collection = db.getCollection(collectionName);

                Document document = Document.parse(JSONSerializer.toJSON(user));
                collection.insertOne(document); // insertMany(List) also available :)
            } catch (Exception e) {
                e.printStackTrace(); // :( TODO: Handle all possible errors (read the doc...)
            }
        }

        public void getMap(String collectionName) {
            User map = null;
            try (MongoClient mongo = SQLDatabaseConnector.getMongoDBClient()) {
                MongoDatabase db = mongo.getDatabase(Constants.MONGODB_DATABASE_NAME);
                MongoCollection<Document> collection = db.getCollection(collectionName);

                System.out.println("Total items in the collection -> " + collection.count());

                // Display all the collection
                for (Document item : collection.find()) {
                    System.out.println(item);
                }

            }
        }
}
