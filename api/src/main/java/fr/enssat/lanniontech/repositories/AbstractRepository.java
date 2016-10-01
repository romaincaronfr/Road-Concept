package fr.enssat.lanniontech.repositories;

import fr.enssat.lanniontech.entities.Entity;
import fr.enssat.lanniontech.exceptions.database.DatabaseOperationException;
import fr.enssat.lanniontech.exceptions.database.SQLUnexpectedException;
import fr.enssat.lanniontech.utilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

    // =====================
    // SQL - BASIC EXCEPTION
    // =====================

    private static final Pattern SQL_EXCEPTION_PATTERN = Pattern.compile("\\[([a-z_]+)\\]");

    protected static DatabaseOperationException processBasicSQLException(SQLException e, Class<? extends Entity> clazz)
    {
        switch ( e.getSQLState() ) { // TODO: Tester qu'on est compatible PostgreSQL et SQLite sur les error codes
            case Constants.SQLITE_CHECK_VIOLATION:
            case Constants.POSTGRESQL_CHECK_VIOLATION :
                return new DatabaseOperationException(extractErrorFromMessage(e),e);
            case Constants.SQLITE_FOREIGN_KEY_VIOLATION:
            case Constants.POSTGRESQL_FOREIGN_KEY_VIOLATION :
                return new DatabaseOperationException("Entity '" + clazz + "' is still in use.", e);
            case Constants.SQLITE_UNIQUE_VIOLATION:
            case Constants.POSTGRESQL_UNIQUE_VIOLATION :
                return new DatabaseOperationException("Entity '" + clazz + "' already exists.", e);
            default :
                break;
        }
        return new SQLUnexpectedException(e);
    }

    private static String extractErrorFromMessage(SQLException e)
    {
        Matcher matcher = SQL_EXCEPTION_PATTERN.matcher(e.getMessage());
        if ( matcher.find() ) {
            return matcher.group(1);
        }
        return null;
    }
}
