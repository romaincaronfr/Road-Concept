package fr.enssat.lanniontech.api.utilities;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLScriptRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLScriptRunner.class);

    private static final String DEFAULT_SCRIPT_DELIMETER = ";";

    private final boolean autoCommit;
    private final Connection connection;

    /**
     * @param connection
     *         : Connection to database.
     * @param autoCommit
     *         : True - it will commit automatically, false - you have to commit manualy.
     */
    public SQLScriptRunner(Connection connection, boolean autoCommit) {
        this.connection = connection;
        this.autoCommit = autoCommit;
    }

    /**
     * @param reader
     *         - file with your script
     *
     * @throws SQLException
     *         - throws SQLException on error
     */
    public void runScript(Reader reader) throws SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            if (originalAutoCommit != autoCommit) {
                connection.setAutoCommit(autoCommit);
            }
            runScript(connection, reader);
        } finally {
            connection.setAutoCommit(originalAutoCommit);
            connection.close();
        }
    }

    private void runScript(Connection connection, Reader reader) {
        for (String script : formatString(reader)) {
            try (PreparedStatement statement = connection.prepareStatement(script)) {
                statement.execute();

                // If auto commit is enabled, then commit
                if (autoCommit) {
                    connection.commit();
                }
            } catch (SQLException e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * Parses file into commands delimeted by ';'
     *
     * @param reader
     *         content
     *
     * @return string[] - commands from file
     */
    private static String[] formatString(Reader reader) {
        StringBuilder result = new StringBuilder();
        String line;
        try (LineNumberReader lineReader = new LineNumberReader(reader)) {
            while ((line = lineReader.readLine()) != null) {
                if (!line.startsWith("--") && !line.startsWith("//") && !line.startsWith("#")) { // DO NOTHING if it is a commented line
                    result.append(line);
                }
            }
        } catch (IOException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
        return result.toString().replaceAll("(?<!" + DEFAULT_SCRIPT_DELIMETER + ")(\\r?\\n)+", "").split(DEFAULT_SCRIPT_DELIMETER);
    }
}
