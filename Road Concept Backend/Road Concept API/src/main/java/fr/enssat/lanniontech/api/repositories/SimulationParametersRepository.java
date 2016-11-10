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

    private static final String INSERT = "INSERT INTO simulation(uuid, id_user, name, id_map) VALUES (?, ?, ?, ?)";
    private static final String SELECT_FROM_UUID = "SELECT id_user, name, id_map FROM simulation WHERE uuid = ?";
    private static final String SELECT_ALL= "SELECT uuid, name, id_map FROM simulation WHERE id_user = ?";
    private static final String SELECT_FROM_MAPID = "SELECT uuid, name, id_user FROM simulation WHERE id_map = ?";


    // ===============
    // SIMULATION INFO - SQL
    // ===============

    // CREATE
    // ------

    public Simulation create(int mapID, User user, String name, String uuid) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                statement.setString(1,uuid);
                statement.setInt(2,user.getId());
                statement.setString(3,name);

                try (ResultSet result = statement.executeQuery()) {
                    result.next(); // Has exactly one row
                    Simulation simu = new Simulation();
                    simu.setUuid(UUID.fromString(result.getString("uuid")));
                    simu.setUser(user);
                    simu.setName(name);
                    simu.setMapID(mapID);
                    return simu;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class); // ?
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

    public List<Simulation> getAll() throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
                try (ResultSet result = statement.executeQuery()) {
                    List<Simulation> simulations = new ArrayList<>();

                    while (result.next()) {
                        Simulation simulation = new Simulation();
                        simulation.setUuid(UUID.fromString(result.getString("uuid")));
                        User user = new User();
                        user.setId(result.getInt("id_user"));  //id_user
                        simulation.setName(result.getString("name")); //name
                        simulation.setMapID(result.getInt("id_map")); //id_map

                        simulations.add(simulation);
                    }
                    return simulations;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    public Simulation getFromuuid(int uuid) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_UUID)) {
                statement.setInt(1, uuid);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        Simulation simulation = new Simulation();
                        simulation.setUuid(UUID.fromString(result.getString("uuid")));   //uuid

                        User user = new User();
                        user.setId(result.getInt("user"));

                        simulation.setUser(user);                  //id_user
                        simulation.setName(result.getString("name"));                    //name
                        simulation.setMapID(result.getInt("id_map"));                    //id_map
                        return simulation;
                    }
                    return null; //no Row
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }
    public Simulation getFromId(int mapID) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_MAPID)) {
                statement.setInt(1, mapID);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        Simulation simulation = new Simulation();

                        simulation.setMapID(mapID);   //id_map
                        simulation.setUuid(UUID.fromString(result.getString("uuid"))); //uuid
                        User user = new User();
                        user.setId(result.getInt("user"));  //id_user
                        simulation.setName(result.getString("name"));   //name
                        return simulation;
                    }
                    return null; // No row
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
        return delete("uuid", simulation);
    }
}
