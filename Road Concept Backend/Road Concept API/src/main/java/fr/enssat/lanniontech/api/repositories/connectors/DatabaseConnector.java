package fr.enssat.lanniontech.api.repositories.connectors;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.exceptions.SQLUnexpectedException;
import fr.enssat.lanniontech.api.services.UserService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.SQLScriptRunner;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnector.class);

    private static final BasicDataSource DATA_SOURCE = new BasicDataSource(); // Vert.x will destroy it on stop

    private DatabaseConnector(){
        // Prevent instantiation
    }

    // ==============
    // DB CONNECTIONS
    // ==============

    /**
     * WARNING: This connection *NEEDS* TO BE CLOSED AFTER USAGE !
     */
    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    public static MongoClient getMongoDBClient() {
        return new MongoClient(Constants.MONGODB_SERVER_URL, Constants.MONGODB_SERVER_PORT);
    }

    // ==============================
    // CONFIGURATION & INITIALIZATION
    // ==============================

    public static void setUp() {
        setUpSQL();
        setUpNoSQL();
    }

    private static void setUpSQL() {
        configurePostgreSQL();
        try (Connection initConnection = getConnection()) {
            initializeDeveloppmentSchema(initConnection);
            new UserService().create("admin@enssat.fr", "admin", "Admin", "Admin", UserType.ADMINISTRATOR);
            LOGGER.info("Default administrator account has been created.");
        } catch (EntityAlreadyExistsException ignored) { // NOSONAR: The default admin user already exists, just ignore the exception
            LOGGER.info("Default admin user already exists ! Nothing created.");
        } catch (SQLException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new SQLUnexpectedException(e);
        }
    }

    private static void configurePostgreSQL() {
        DATA_SOURCE.setUsername(Constants.POSTGRESQL_USER_NAME);
        DATA_SOURCE.setPassword(Constants.POSTGRESQL_USER_PASSWORD);
        DATA_SOURCE.setUrl("jdbc:postgresql://" + Constants.POSTGRESQL_SERVER_HOST + "/" + Constants.POSTGRESQL_DATABASE_NAME);
        DATA_SOURCE.setMaxTotal(Constants.POSTGRESQL_MAX_CONNECTIONS);
    }

    private static void setUpNoSQL() { // Check the connection is ok
        try (MongoClient client = getMongoDBClient()) {
            MongoDatabase db = client.getDatabase(Constants.MONGODB_DATABASE_NAME);
            db.createCollection("fake");
            MongoCollection<Document> fakeCollection = db.getCollection("fake");
            fakeCollection.drop();
        }
    }

    /**
     * The development script do not contains any procedure or trigger, since it cause trouble to process the file on server startup.
     */
    private static void initializeDeveloppmentSchema(Connection connection) {
        try {
            SQLScriptRunner runner = new SQLScriptRunner(connection, false);
            try (InputStream script = DatabaseConnector.class.getClassLoader().getResourceAsStream("SQL/roadConceptDB.sql")) {
                runner.runScript(new BufferedReader(new InputStreamReader(script)));
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new SQLUnexpectedException(e);
        }
    }

}
