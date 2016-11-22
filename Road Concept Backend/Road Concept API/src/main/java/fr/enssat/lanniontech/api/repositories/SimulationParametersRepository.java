package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
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
import java.util.UUID;

public class SimulationParametersRepository extends SimulationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParametersRepository.class);

    private static final String INSERT = "INSERT INTO simulation(uuid, id_user, name, id_map, duration_s, finish, creation_date) VALUES (?, ?, ?, ?,?,?,?)";
    private static final String SELECT_FROM_UUID = "SELECT id_user, name, id_map, creation_date, finish, duration_s FROM simulation WHERE uuid = ?";
    private static final String SELECT_ALL = "SELECT uuid, name, id_map, creation_date, finish, duration_s FROM simulation WHERE id_user = ?";
    private static final String SELECT_FROM_MAP = "SELECT uuid, name, duration_s, finish, creation_date FROM simulation WHERE id_map = ? AND id_user = ?";

    // CREATE
    // ------

    public Simulation create(int creatorID, String name, int mapID, int duration) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                Simulation simulation = new Simulation();

                statement.setString(1, simulation.getUuid().toString());
                statement.setInt(2, creatorID);
                statement.setString(3, name);
                statement.setInt(4, mapID);
                statement.setInt(5, duration);
                statement.setBoolean(6, false);
                statement.setString(7, simulation.getCreationDate());

                try {
                    statement.execute();
                    simulation.setDurationS(duration);
                    simulation.setMapID(mapID);
                    simulation.setName(name);
                    simulation.setCreatorID(creatorID);
                    simulation.setFinish(false);
                    return simulation;
                } finally {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    // ======
    // UPDATE
    // ======

    public void updateName(Simulation simulation, String newValue) throws DatabaseOperationException {
        updateStringField("simulation", "name", simulation, newValue);
    }

    // ===
    // GET
    // ===

    public List<Simulation> getAll(int userID) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
                statement.setInt(1, userID);

                try (ResultSet result = statement.executeQuery()) {
                    List<Simulation> simulations = new ArrayList<>();

                    while (result.next()) {
                        Simulation simulation = new Simulation();
                        simulation.setUuid(UUID.fromString(result.getString("uuid")));
                        simulation.setCreatorID(userID);
                        simulation.setName(result.getString("name"));
                        simulation.setMapID(result.getInt("id_map"));
                        simulation.setCreationDate(result.getString("creation_date"));
                        simulation.setFinish(result.getBoolean("finish"));
                        simulation.setDurationS(result.getInt("duration_s"));

                        simulations.add(simulation);
                    }
                    return simulations;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    public Simulation getFromUUID(UUID uuid) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_UUID)) {
                statement.setString(1, uuid.toString());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        Simulation simulation = new Simulation();
                        simulation.setUuid(uuid);
                        simulation.setCreatorID(result.getInt("id_user"));
                        simulation.setName(result.getString("name"));
                        simulation.setMapID(result.getInt("id_map"));
                        simulation.setCreationDate(result.getString("creation_date"));
                        simulation.setFinish(result.getBoolean("finish"));
                        simulation.setDurationS(result.getInt("duration_s"));

                        return simulation;
                    }
                    return null; // no row
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    public List<Simulation> getAllFromMap(User user, int mapID) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_MAP)) {
                statement.setInt(1, mapID);
                statement.setInt(2, user.getId());
                try (ResultSet result = statement.executeQuery()) {
                    List<Simulation> simulations = new ArrayList<>();

                    while (result.next()) {
                        Simulation simulation = new Simulation();
                        simulation.setUuid(UUID.fromString(result.getString("uuid")));
                        simulation.setCreatorID(user.getId());
                        simulation.setName(result.getString("name"));
                        simulation.setMapID(mapID);
                        simulation.setCreationDate(result.getString("creation_date"));
                        simulation.setFinish(result.getBoolean("finish"));
                        simulation.setDurationS(result.getInt("duration_s"));

                        simulations.add(simulation);
                    }
                    return simulations;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    // ======
    // DELETE
    // ======

    public int delete(Simulation simulation) throws DatabaseOperationException {
        return delete("simulation", simulation);
    }
}
