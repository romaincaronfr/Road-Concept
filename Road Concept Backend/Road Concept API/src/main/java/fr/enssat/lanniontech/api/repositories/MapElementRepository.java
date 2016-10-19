package fr.enssat.lanniontech.api.repositories;

public class MapElementRepository extends MapInfoRepository {

    private static final String MONGODB_MAP_COLLECTION_PREFIX = "map_";

    private String computeCollectionName(int mapID) {
        return MONGODB_MAP_COLLECTION_PREFIX + mapID;
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
    //            Document document = Document.parse(JSONSerializer.toJSON(user));
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
