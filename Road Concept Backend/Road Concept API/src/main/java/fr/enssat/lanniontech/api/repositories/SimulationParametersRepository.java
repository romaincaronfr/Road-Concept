package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
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

    private static final String INSERT = "INSERT INTO simulation(uuid, id_user, id_map, name, sampling, finish, creation_date, min_departure_living_s, random_traffic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_FROM_UUID = "SELECT id_user, id_map, name, sampling, finish, creation_date, min_departure_living_s, random_traffic FROM simulation WHERE uuid = ?";
    private static final String SELECT_ALL = "SELECT uuid, id_map, name, sampling, finish, creation_date, min_departure_living_s, random_traffic FROM simulation WHERE id_user = ?";
    private static final String SELECT_FROM_MAP = "SELECT uuid, name, sampling, finish, creation_date, min_departure_living_s, random_traffic FROM simulation WHERE id_map = ? AND id_user = ?";
    private static final String UPDATE_FINISH = "UPDATE simulation SET finish = true WHERE uuid = ? AND id_user = ?";
    private static final String DELETE_NOT_FINISH = "DELETE FROM simulation WHERE finish = false RETURNING uuid";

    // CREATE
    // ------

    public Simulation create(int creatorID, String name, int mapID, int samplingRate, int minDepartureLivingS, boolean randomTraffic) { //NOSONAR: Parameters count
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                Simulation simulation = new Simulation();

                statement.setString(1, simulation.getUuid().toString());
                statement.setInt(2, creatorID);
                statement.setInt(3, mapID);
                statement.setString(4, name);
                statement.setInt(5, samplingRate);
                statement.setBoolean(6, false);
                statement.setString(7, simulation.getCreationDate());
                statement.setInt(8, minDepartureLivingS);
                statement.setBoolean(9, randomTraffic);

                try {
                    statement.execute();

                    simulation.setSamplingRate(samplingRate);
                    simulation.setMapID(mapID);
                    simulation.setName(name);
                    simulation.setCreatorID(creatorID);
                    simulation.setFinish(false);
                    simulation.setDepartureLivingS(minDepartureLivingS);
                    simulation.setIncludeRandomTraffic(randomTraffic);
                    return simulation;
                } finally {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    // ===
    // GET
    // ===

    public List<Simulation> getAll(int userID) {
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
                        simulation.setSamplingRate(result.getInt("sampling"));
                        simulation.setDepartureLivingS(result.getInt("min_departure_living_s"));
                        simulation.setIncludeRandomTraffic(result.getBoolean("random_traffic"));

                        simulations.add(simulation);
                    }
                    return simulations;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    public Simulation getFromUUID(UUID uuid) {
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
                        simulation.setSamplingRate(result.getInt("sampling"));
                        simulation.setDepartureLivingS(result.getInt("min_departure_living_s"));
                        simulation.setIncludeRandomTraffic(result.getBoolean("random_traffic"));

                        return simulation;
                    }
                    return null; // no row
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    public List<Simulation> getAllFromMap(User user, int mapID) {
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
                        simulation.setSamplingRate(result.getInt("sampling"));
                        simulation.setDepartureLivingS(result.getInt("min_departure_living_s"));
                        simulation.setIncludeRandomTraffic(result.getBoolean("random_traffic"));

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
    // UPDATE
    // ======

    public void finish(Simulation simulation) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_FINISH)) {
                statement.setString(1, simulation.getUuid().toString());
                statement.setInt(2, simulation.getCreatorID());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    // ======
    // DELETE
    // ======

    public int delete(Simulation simulation) {
        return delete("simulation", simulation);
    }

    /**
     * Warning: only supposed to be called on server start-up
     */
    public void deleteUnfinished() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_NOT_FINISH)) {
                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        deleteAssociatedFeatures(uuid);
                        LOGGER.warn("Simulation " + uuid + " was not finish on server start-up and has been deleted.");
                    }
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }
}
