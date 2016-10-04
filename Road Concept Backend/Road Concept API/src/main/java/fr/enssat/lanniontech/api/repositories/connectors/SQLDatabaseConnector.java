package fr.enssat.lanniontech.api.repositories.connectors;

import com.mongodb.MongoClient;
import fr.enssat.lanniontech.api.exceptions.database.SQLUnexpectedException;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.ProjetEnvironment;
import fr.enssat.lanniontech.api.utilities.SQLScriptRunner;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

// TODO: Définir quand et comment une connection doit être fermée
public class SQLDatabaseConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLDatabaseConnector.class);

    private static final PGPoolingDataSource dataSource = new PGPoolingDataSource();

    // ==============
    // DB CONNECTIONS
    // ==============

    public synchronized static MongoClient getMongoDBClient() {
        return new MongoClient(Constants.MONGODB_SERVER_URL, Constants.MONGODB_SERVER_PORT);
    }

    public synchronized static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // ==============================
    // CONFIGURATION & INITIALIZATION
    // ==============================

    public static void setUp() throws SQLUnexpectedException {
        configure();
        if (Constants.ENVIRONMENT == ProjetEnvironment.DEVELOPPMENT) {
            initializeSchema();
        }
    }

    private static void configure() {
        dataSource.setServerName(Constants.POSTGRESQL_SERVER_HOST);
        dataSource.setPortNumber(Constants.POSTGRESQL_SERVER_PORT);
        dataSource.setDatabaseName(Constants.POSTGRESQL_DATABASE_NAME);
        dataSource.setMaxConnections(Constants.POSTGRESQL_MAX_CONNECTIONS);
    }

    private static void initializeSchema() {
        /**
         * The development script do not contains any procedure or trigger, since it cause trouble to process the file on server startup.
         */
        String file = "roadConceptDB_dev.sql";
        try (Connection connection = getConnection()) {
            SQLScriptRunner runner = new SQLScriptRunner(connection, false);
            InputStream script = SQLDatabaseConnector.class.getClassLoader().getResourceAsStream("SQL/" + file);
            runner.runScript(new BufferedReader(new InputStreamReader(script)));
            connection.close();
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new SQLUnexpectedException(e);
        }
    }
}
