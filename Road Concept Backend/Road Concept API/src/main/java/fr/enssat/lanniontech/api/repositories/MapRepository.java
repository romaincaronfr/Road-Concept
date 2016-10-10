package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.MapInfo;
import fr.enssat.lanniontech.api.entities.User;
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

    private static final String INSERT = "INSERT INTO map_info(id_user, name, image_url, description) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String SELECT_ALL = "SELECT id, name, image_url, description FROM map_info WHERE id_user = ?";

    // ======
    // CREATE
    // ======

    public MapInfo create(User user, String name, String imageURL, String description) throws DatabaseOperationException {
        try (PreparedStatement statement = SQLDatabaseConnector.getConnection().prepareStatement(INSERT)) {
            statement.setInt(1, user.getId());
            statement.setString(2, name);
            statement.setString(3, imageURL);
            statement.setString(4, description);

            try (ResultSet result = statement.executeQuery()) {
                result.next(); // Has exactly one row
                MapInfo map = new MapInfo();
                map.setId(result.getInt("id"));
                map.setName(name);
                map.setImageURL(imageURL);
                map.setDescription(description);
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

    public List<MapInfo> getAll(User user) throws DatabaseOperationException {
        try (PreparedStatement statement = SQLDatabaseConnector.getConnection().prepareStatement(SELECT_ALL)) {
            statement.setInt(1, user.getId());

            try (ResultSet result = statement.executeQuery()) {
                List<MapInfo> maps = new ArrayList<>();

                while (result.next()) {
                    MapInfo map = new MapInfo();
                    map.setId(result.getInt("id"));
                    map.setName(result.getString("name"));
                    map.setImageURL(result.getString("image_url"));
                    map.setDescription(result.getString("description"));

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

