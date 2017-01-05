package fr.enssat.lanniontech.api.repositories.connectors;

import fr.enssat.lanniontech.api.utilities.Constants;

import java.sql.Connection;
import java.sql.SQLException;

//TODO: Execute simulation results using batch
public class SQLContext {

    private Connection connection;
    private int uncommitedRequestCount;

    public SQLContext(Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(false);
        this.uncommitedRequestCount = 0;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Indicates that the current connection has been used to do a new request
     */
    public void addRequest() throws SQLException {
        uncommitedRequestCount++;
        if (uncommitedRequestCount >= Constants.QUERY_PER_SQL_COMMIT) {
commit();
        }
    }

    public void commit() throws SQLException {
        connection.commit();
        uncommitedRequestCount = 0;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getUncommitedRequestCount() {
        return uncommitedRequestCount;
    }

    public void setUncommitedRequestCount(int uncommitedRequestCount) {
        this.uncommitedRequestCount = uncommitedRequestCount;
    }
}
