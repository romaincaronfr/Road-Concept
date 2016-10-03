package fr.enssat.lanniontech.api.exceptions.database;

import java.sql.SQLException;

public class SQLUnexpectedException extends DatabaseOperationException {

    public SQLUnexpectedException(SQLException e) {
        super("An unexpected error occured",e);
    }
}