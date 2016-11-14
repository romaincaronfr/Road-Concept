package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.Entity;
import fr.enssat.lanniontech.api.entities.SQLEntity;
import fr.enssat.lanniontech.api.exceptions.DatabaseOperationException;
import fr.enssat.lanniontech.api.exceptions.EntityAlreadyExistsException;
import fr.enssat.lanniontech.api.exceptions.EntityStillInUseException;
import fr.enssat.lanniontech.api.exceptions.SQLUnexpectedException;
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
    // SQL - UPDATE ENTITY
    // ===================

    protected void updateStringField(String tableName, String columnName, SQLEntity entity, String newValue) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(computeUpdateSQLQuery(tableName, columnName, entity.getIdentifierName()))) {
                statement.setString(1, newValue);
                statement.setObject(2, entity.getIdentifierValue());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, entity.getClass());
        }
    }

    protected void updateIntField(String tableName, String columnName, SQLEntity entity, int newValue) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(computeUpdateSQLQuery(tableName, columnName, entity.getIdentifierName()))) {
                statement.setInt(1, newValue);
                statement.setObject(2, entity.getIdentifierValue());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, entity.getClass());
        }
    }

    private String computeUpdateSQLQuery(String tableName, String columnName, String identifierName) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ").append(columnName).append(" = ? WHERE ").append(identifierName).append(" = ?");
        return sql.toString();
    }

    // ===================
    // SQL - DELETE ENTITY
    // ===================

    protected final int delete(String tableName, SQLEntity entity) throws DatabaseOperationException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM \"" + tableName + "\" WHERE " + entity.getIdentifierName() + " = ?")) {
                statement.setObject(1, entity.getIdentifierValue().toString());
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
