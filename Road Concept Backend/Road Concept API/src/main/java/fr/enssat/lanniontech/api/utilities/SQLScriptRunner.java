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
     * @param connection : Connection to database.
     * @param autoCommit : True - it will commit automatically, false - you have to commit manualy.
     */
    public SQLScriptRunner(final Connection connection, final boolean autoCommit) {
        this.connection = connection;
        this.autoCommit = autoCommit;
    }

    /**
     * @param reader - file with your script
     * @throws SQLException - throws SQLException on error
     */
    public void runScript(final Reader reader) throws SQLException {
        final boolean originalAutoCommit = this.connection.getAutoCommit();
        try {
            if (originalAutoCommit != this.autoCommit) {
                this.connection.setAutoCommit(autoCommit);
            }
            this.runScript(this.connection, reader);
        } finally {
            this.connection.setAutoCommit(originalAutoCommit);
            this.connection.close();
        }
    }

    private void runScript(final Connection connection, final Reader reader) {
        for (String script : formatString(reader)) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(script);
                statement.execute();

                // If auto commit is enabled, then commit
                if (autoCommit) {
                    connection.commit();
                }
            } catch (SQLException e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        LOGGER.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }
    }

    /**
     * Parses file into commands delimeted by ';'
     *
     * @param reader
     * @return string[] - commands from file
     */
    private String[] formatString(final Reader reader) {
        String result = "";
        String line;
        final LineNumberReader lineReader = new LineNumberReader(reader);

        try {
            while ((line = lineReader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("//") || line.startsWith("#")) {
                    //DO NOTHING - It is a commented line
                } else {
                    result += line;
                }
            }
        } catch (IOException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        return result.replaceAll("(?<!" + DEFAULT_SCRIPT_DELIMETER + ")(\\r?\\n)+", "").split(DEFAULT_SCRIPT_DELIMETER);
    }
}
