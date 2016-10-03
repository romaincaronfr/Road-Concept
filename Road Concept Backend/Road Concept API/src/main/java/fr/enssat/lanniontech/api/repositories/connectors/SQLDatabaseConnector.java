package fr.enssat.lanniontech.api.repositories.connectors;

import com.mongodb.MongoClient;
import fr.enssat.lanniontech.api.exceptions.database.SQLUnexpectedException;
import fr.enssat.lanniontech.api.utilities.Constants;
import fr.enssat.lanniontech.api.utilities.SQLScriptRunner;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

// TODO: Définir quand et comment une connection doit être fermée
public class SQLDatabaseConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLDatabaseConnector.class);

    private static final PGPoolingDataSource postgreDataSource = new PGPoolingDataSource();
    private static final SQLiteConnectionPoolDataSource sqliteDataSource = new SQLiteConnectionPoolDataSource();
    private static final SGBD currentSGBD = Constants.ACTIVE_SGBD;

    // ==============
    // DB CONNECTIONS
    // ==============

    public synchronized static MongoClient getMongoDBClient() {
        return new MongoClient(Constants.MONGODB_SERVER_URL, Constants.MONGODB_SERVER_PORT);
    }

    public synchronized static Connection getConnection() throws SQLException {
        if (Constants.ACTIVE_SGBD == SGBD.POSTGRESQL) {
            return postgreDataSource.getConnection();
        } else if (Constants.ACTIVE_SGBD == SGBD.SQLITE) {
            return sqliteDataSource.getConnection();
        }
        assert false : "The connection can't be null";
        return null;
    }

    // ==============================
    // CONFIGURATION & INITIALIZATION
    // ==============================

    public static void setUp() throws SQLUnexpectedException {
        configure();
        initializeSchema();
    }

    private static void configure() {
        if (currentSGBD == SGBD.POSTGRESQL) {
            postgreDataSource.setServerName(Constants.POSTGRESQL_SERVER_HOST);
            postgreDataSource.setPortNumber(Constants.POSTGRESQL_SERVER_PORT);
            postgreDataSource.setDatabaseName(Constants.POSTGRESQL_DATABASE_NAME);
            postgreDataSource.setMaxConnections(Constants.POSTGRESQL_MAX_CONNECTIONS);
        } else if (currentSGBD == SGBD.SQLITE) {
            sqliteDataSource.setDatabaseName(Constants.SQLITE_DATABASE_NAME);
            sqliteDataSource.setUrl(Constants.SQLITE_URL_PREFIX + Constants.SQLITE_DATABASE_NAME + Constants.SQLITE_DB_FILE_SUFFIX);
        }
    }

    private static void initializeSchema() {
        String file = getScriptFileName();
        try (Connection connection = getConnection()) {
            SQLScriptRunner runner = new SQLScriptRunner(connection, false);
            InputStream foo = SQLDatabaseConnector.class.getClassLoader().getResourceAsStream("SQL/" + file);
            runner.runScript(new BufferedReader(new InputStreamReader(foo)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLUnexpectedException(e);
        }
    }

    private static String getScriptFileName() {
        String file = null;
        if (currentSGBD == SGBD.POSTGRESQL) {
            file = "roadConceptDB_Postgre.sql";
        } else if (currentSGBD == SGBD.SQLITE) {
            file = "roadConceptDB_SQLite.sql";
        }
        assert file != null : "The file name can't be null";
        return file;
    }

}
