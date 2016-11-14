package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.map.MapInfo;
import fr.enssat.lanniontech.api.exceptions.DatabaseOperationException;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapInfoRepository extends MapRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapInfoRepository.class);

    private static final String INSERT = "INSERT INTO map_info(id_user, name, image_url, description) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String SELECT_ALL = "SELECT id, name, image_url, description FROM map_info WHERE id_user = ?";
    private static final String SELECT_FROM_ID = "SELECT id_user, name, image_url, description FROM map_info WHERE id = ?";

    // ===============
    // MAPS INFO - SQL
    // ===============

    // CREATE
    // ------

    public MapInfo create(User user, String name, String imageURL, String description) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
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
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, MapInfo.class);
        }
    }

    // UPDATE
    // ------
    // Maps info are not supposed to be updated yet...

    // GET
    // ---

    public List<MapInfo> getAll(User user) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
                statement.setInt(1, user.getId());

                try (ResultSet result = statement.executeQuery()) {
                    List<MapInfo> maps = new ArrayList<>();

                    while (result.next()) {
                        MapInfo map = new MapInfo();
                        map.setId(result.getInt("id"));
                        map.setUserID(user.getId());
                        map.setName(result.getString("name"));
                        map.setImageURL(result.getString("image_url"));
                        map.setDescription(result.getString("description"));

                        maps.add(map);
                    }
                    return maps;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, MapInfo.class);
        }
    }

    public MapInfo get(int mapID) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_ID)) {
                statement.setInt(1, mapID);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        MapInfo map = new MapInfo();
                        map.setId(mapID);
                        map.setUserID(Integer.valueOf(result.getString("id_user")));
                        map.setName(result.getString("name"));
                        map.setImageURL(result.getString("image_url"));
                        map.setDescription(result.getString("description"));
                        return map;
                    }
                    return null; // No row
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, MapInfo.class);
        }
    }

    // DELETE
    // ------

    public int delete(int mapID) throws DatabaseOperationException {
        MapInfo map = new MapInfo();
        map.setId(mapID);
        return delete("map_info", map);
    }
}

