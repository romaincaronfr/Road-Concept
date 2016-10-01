package fr.enssat.lanniontech.repositories.connectors;

import com.mongodb.MongoClient;
import fr.enssat.lanniontech.utilities.Constants;

public class MongoDBConnector {

    public static MongoClient getConnection() {
        return new MongoClient(Constants.MONGODB_SERVER_URL, Constants.MONGODB_SERVER_PORT);
    }
}
