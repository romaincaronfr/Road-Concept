package fr.enssat.lanniontech.repositories.connectors;

import com.mongodb.MongoClient;
import fr.enssat.lanniontech.utilities.Constants;
import org.postgresql.ds.PGPoolingDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

// TODO: Définir quand et comment une connection doit être fermée
public class SQLDatabaseConnector {

    private static final PGPoolingDataSource postgreDataSource = new PGPoolingDataSource();
    private static final SQLiteConnectionPoolDataSource sqliteDataSource = new SQLiteConnectionPoolDataSource();

    public static void configure(SGBD sgbd){
        if (sgbd == SGBD.POSTGRESQL) {
            postgreDataSource.setServerName(Constants.POSTGRESQL_SERVER_HOST);
            postgreDataSource.setPortNumber(Constants.POSTGRESQL_SERVER_PORT);
            postgreDataSource.setDatabaseName(Constants.POSTGRE_SQL_DATABASE_NAME);
            postgreDataSource.setMaxConnections(Constants.POSTGRESQL_MAX_CONNECTIONS);
        } else if (sgbd == SGBD.SQLITE) {
            // TODO: Set configuration for SQLite
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
