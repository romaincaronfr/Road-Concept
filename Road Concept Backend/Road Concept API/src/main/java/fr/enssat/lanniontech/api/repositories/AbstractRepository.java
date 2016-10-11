package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.Entity;
import fr.enssat.lanniontech.api.entities.SQLStoredEntity;
import fr.enssat.lanniontech.api.exceptions.database.DatabaseOperationException;
import fr.enssat.lanniontech.api.exceptions.database.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.exceptions.database.SQLUnexpectedException;
import fr.enssat.lanniontech.api.repositories.connectors.SQLDatabaseConnector;
import fr.enssat.lanniontech.api.utilities.Constants;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

    // ===================
    // SQL - DELETE ENTITY
    // ===================
    private static final Pattern SQL_EXCEPTION_PATTERN = Pattern.compile("\\[([a-z_]+)\\]");

    // =====================
    // SQL - BASIC EXCEPTION
    // =====================

    protected final int delete(String tableName, SQLStoredEntity entity) throws DatabaseOperationException {
        try (Connection connection = SQLDatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM \"" + tableName + "\" WHERE " + entity.getIdentifierName() + " = ?")) {
                statement.setObject(1, entity.getIdentifierValue());
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, entity.getClass());
        }
    }

    static DatabaseOperationException processBasicSQLException(SQLException e, Class<? extends Entity> clazz) {
        switch (e.getSQLState()) {
            case Constants.POSTGRESQL_CHECK_VIOLATION:
                return new DatabaseOperationException(extractErrorFromMessage(e), e); //TODO: Create new exception (see nabuTalk)
            case Constants.POSTGRESQL_FOREIGN_KEY_VIOLATION:
                return new DatabaseOperationException("Entity '" + clazz + "' is still in use.", e); //TODO: Create new exception (see nabuTalk)
            case Constants.POSTGRESQL_UNIQUE_VIOLATION:
                return new EntityAlreadyExistsException("Entity '" + clazz + "' already exists.", e);//TODO: Rename? (see nabuTalk)
            default:
                break;
        }
        LOGGER.error(ExceptionUtils.getStackTrace(e));
        return new SQLUnexpectedException(e);
    }

    private static String extractErrorFromMessage(SQLException e) {
        Matcher matcher = SQL_EXCEPTION_PATTERN.matcher(e.getMessage());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
