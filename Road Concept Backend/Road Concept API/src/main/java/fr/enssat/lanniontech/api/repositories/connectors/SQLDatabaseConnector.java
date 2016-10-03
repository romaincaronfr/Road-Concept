package fr.enssat.lanniontech.api.repositories.connectors;

import com.mongodb.MongoClient;
import fr.enssat.lanniontech.api.utilities.Constants;
import org.postgresql.ds.PGPoolingDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

// TODO: Définir quand et comment une connection doit être fermée
public class SQLDatabaseConnector {

    private static final PGPoolingDataSource postgreDataSource = new PGPoolingDataSource();
    private static final SQLiteConnectionPoolDataSource sqliteDataSource = new SQLiteConnectionPoolDataSource();

    public static final String SQLITE_URL_PREFIX = "jdbc:sqlite:";
    public static final String SQLITE_DB_FILE_EXTENSION = ".db";


    public static void configure(SGBD sgbd){
        if (sgbd == SGBD.POSTGRESQL) {
            postgreDataSource.setServerName(Constants.POSTGRESQL_SERVER_HOST);
            postgreDataSource.setPortNumber(Constants.POSTGRESQL_SERVER_PORT);
            postgreDataSource.setDatabaseName(Constants.POSTGRESQL_DATABASE_NAME);
            postgreDataSource.setMaxConnections(Constants.POSTGRESQL_MAX_CONNECTIONS);
        } else if (sgbd == SGBD.SQLITE) {
            // TODO: Set configuration for SQLite
            sqliteDataSource.setDatabaseName(Constants.SQLITE_DATABASE_NAME);
            String path = "../../../../BDD/";

          //  sqliteDataSource.setUrl(SQLITE_URL_PREFIX + "Users/maelig/Desktop/Projet GL/Road-Concept/BDD/" + Constants.SQLITE_DATABASE_NAME + SQLITE_DB_FILE_EXTENSION);
          //  sqliteDataSource.setUrl(SQLITE_URL_PREFIX + path + Constants.SQLITE_DATABASE_NAME + SQLITE_DB_FILE_EXTENSION);
            sqliteDataSource.setUrl(SQLITE_URL_PREFIX + Constants.SQLITE_DATABASE_NAME + SQLITE_DB_FILE_EXTENSION);

        }
    }

    public static MongoClient getMongoDBClient() {
        return new MongoClient(Constants.MONGODB_SERVER_URL, Constants.MONGODB_SERVER_PORT);
    }

    public static Connection getConnection() throws SQLException{
        Connection connection = null;
        if (Constants.ACTIVE_SGBD == SGBD.POSTGRESQL) {
            connection = postgreDataSource.getConnection();
        } else if (Constants.ACTIVE_SGBD == SGBD.SQLITE) {
            connection = sqliteDataSource.getConnection();
        }
        return connection;
    }
}
