package fr.enssat.lanniontech.api.repositories.connectors;

import com.mongodb.MongoClient;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.database.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.exceptions.database.SQLUnexpectedException;
import fr.enssat.lanniontech.api.services.UserService;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.ProjetEnvironment;
import fr.enssat.lanniontech.api.utilities.SQLScriptRunner;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public final class SQLDatabaseConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLDatabaseConnector.class);

    private static BasicDataSource DATA_SOURCE = new BasicDataSource(); //FIXME: Handle close or just the server do it on stop ?

    // ==============
    // DB CONNECTIONS
    // ==============

    public static MongoClient getMongoDBClient() {
        return new MongoClient(Constants.MONGODB_SERVER_URL, Constants.MONGODB_SERVER_PORT);
    }

    public static void setUp() throws SQLUnexpectedException {
        configure();
        if (Constants.ENVIRONMENT == ProjetEnvironment.DEVELOPPMENT) {
            try (Connection initConnection = getConnection()) {
                initializeDeveloppmentSchema(initConnection);
                new UserService().create("admin@enssat.fr", "admin", "Admin", "Admin", UserType.ADMINISTRATOR);
            } catch (EntityAlreadyExistsException ignored) {
                // The default admin user already exists, just ignore the exception
                LOGGER.debug("Default admin user already exists ! Nothing created.");
            } catch (SQLException e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
                throw new SQLUnexpectedException(e);
            }
        }
    }

    // ==============================
    // CONFIGURATION & INITIALIZATION
    // ==============================

    private static void configure() {
        DATA_SOURCE.setUsername(Constants.POSTGRESQL_USER_NAME);
        DATA_SOURCE.setPassword(Constants.POSTGRESQL_USER_PASSWORD);
        DATA_SOURCE.setUrl("jdbc:postgresql://" + Constants.POSTGRESQL_SERVER_HOST + "/" + Constants.POSTGRESQL_DATABASE_NAME);
        DATA_SOURCE.setMaxTotal(Constants.POSTGRESQL_MAX_CONNECTIONS);
    }

    /**
     * WARNING: This connection *NEEDS* TO BE CLOSED AFTER USAGE !
     */
    public static Connection getConnection() throws SQLException { // DATA_SOURCE is thread safe
        return DATA_SOURCE.getConnection();
    }

    /**
     * The development script do not contains any procedure or trigger, since it cause trouble to process the file on server startup.
     */
    private static void initializeDeveloppmentSchema(Connection connection) {
        try {
            String file = "roadConceptDB_dev.sql";
            SQLScriptRunner runner = new SQLScriptRunner(connection, false);
            try (InputStream script = SQLDatabaseConnector.class.getClassLoader().getResourceAsStream("SQL/" + file)) {
                runner.runScript(new BufferedReader(new InputStreamReader(script)));
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new SQLUnexpectedException(e);
        }
    }
}
