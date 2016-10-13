package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.Entity;
import fr.enssat.lanniontech.api.entities.SQLStoredEntity;
import fr.enssat.lanniontech.api.exceptions.database.DatabaseOperationException;
import fr.enssat.lanniontech.api.exceptions.database.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.exceptions.database.EntityStillInUseException;
import fr.enssat.lanniontech.api.exceptions.database.SQLUnexpectedException;
import fr.enssat.lanniontech.api.utilities.Constants;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector.getConnection;

public abstract class AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

    // ===================
    // SQL - DELETE ENTITY
    // ===================

    protected final int delete(String tableName, SQLStoredEntity entity) throws DatabaseOperationException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM \"" + tableName + "\" WHERE " + entity.getIdentifierName() + " = ?")) {
                statement.setObject(1, entity.getIdentifierValue());
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, entity.getClass());
        }
    }

    // =====================
    // SQL - BASIC EXCEPTION
    // =====================

    protected static DatabaseOperationException processBasicSQLException(SQLException e, Class<? extends Entity> clazz) {
        switch (e.getSQLState()) {
            case Constants.POSTGRESQL_FOREIGN_KEY_VIOLATION:
                return new EntityStillInUseException("Entity '" + clazz + "' is still in use.", e);
            case Constants.POSTGRESQL_UNIQUE_VIOLATION:
                return new EntityAlreadyExistsException("Entity '" + clazz + "' already exists.", e);
            default:
                break;
        }
        LOGGER.error(ExceptionUtils.getStackTrace(e));
        return new SQLUnexpectedException(e);
    }
}
