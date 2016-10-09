package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.MapInfo;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.database.DatabaseOperationException;
import fr.enssat.lanniontech.api.repositories.connectors.SQLDatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapRepository extends AbstractRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapRepository.class);

    private static final String INSERT = "INSERT INTO map_info(id_user, name, image_url, description) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String SELECT_ALL = "SELECT name, image_url, description FROM map_info";

    // ======
    // CREATE
    // ======

    public MapInfo create(String email, String lastName, String firstName, String password, UserType type) throws DatabaseOperationException {
        try (PreparedStatement statement = SQLDatabaseConnector.getConnection().prepareStatement(INSERT)) {
            // TODO: statement.set...

            try (ResultSet result = statement.executeQuery()) {
                result.next(); // Has exactly one row
                MapInfo map = new MapInfo();
                //TODO
                return map;
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, MapInfo.class);
        }
    }

    // ======
    // UPDATE
    // ======

    // ===
    // GET
    // ===

    public List<MapInfo> getAll() throws DatabaseOperationException {
        try (PreparedStatement statement = SQLDatabaseConnector.getConnection().prepareStatement(SELECT_ALL)) {

            try (ResultSet result = statement.executeQuery()) {
                List<MapInfo> maps = new ArrayList<>();

                while (result.next()) {
                    MapInfo map = new MapInfo();
                    // TODO

                    maps.add(map);
                }
                return maps;
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, MapInfo.class);
        }
    }

    // ======
    // DELETE
    // ======

    public int delete(MapInfo map) throws DatabaseOperationException {
        return delete("map_info", map);
    }
}

