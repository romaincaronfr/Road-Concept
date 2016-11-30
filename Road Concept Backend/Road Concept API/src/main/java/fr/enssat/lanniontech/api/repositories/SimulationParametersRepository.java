package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.exceptions.DatabaseOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector.getConnection;

public class SimulationParametersRepository extends SimulationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParametersRepository.class);

    private static final String INSERT = "INSERT INTO simulation(uuid, id_user, id_map, name, sampling, finish, creation_date, living_feature, working_feature, departure_living_s, departure_working_s, car_percentage, vehicle_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_FROM_UUID = "SELECT id_user, id_map, name, sampling, finish, creation_date, living_feature, working_feature, departure_living_s, departure_working_s, car_percentage, vehicle_count FROM simulation WHERE uuid = ?";
    private static final String SELECT_ALL = "SELECT uuid, id_map, name, sampling, finish, creation_date, living_feature, working_feature, departure_living_s, departure_working_s, car_percentage, vehicle_count FROM simulation WHERE id_user = ?";
    private static final String SELECT_FROM_MAP = "SELECT uuid, name, sampling, finish, creation_date, living_feature, working_feature, departure_living_s, departure_working_s, car_percentage, vehicle_count FROM simulation WHERE id_map = ? AND id_user = ?";
    private static final String UPDATE_FINISH = "UPDATE simulation SET finish = ? WHERE uuid = ? AND id_user = ?";

    // CREATE
    // ------

    public Simulation create(int creatorID, String name, int mapID, int samplingRate, int departureLivingS, int departureWorkingS, UUID livingFeatureUUID, UUID workingFeatureUUID, int carPercentage, int vehicleCount) throws DatabaseOperationException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                Simulation simulation = new Simulation();

                statement.setString(1, simulation.getUuid().toString());
                statement.setInt(2, creatorID);
                statement.setInt(3, mapID);
                statement.setString(4, name);
                statement.setInt(5, samplingRate);
                statement.setBoolean(6, false);
                statement.setString(7, simulation.getCreationDate());
                statement.setString(8, livingFeatureUUID.toString());
                statement.setString(9, workingFeatureUUID.toString());
                statement.setInt(10, departureLivingS);
                statement.setInt(11, departureWorkingS);
                statement.setInt(12, carPercentage);
                statement.setInt(13, vehicleCount);

                try {
                    statement.execute();
                    simulation.setSamplingRate(samplingRate);
                    simulation.setMapID(mapID);
                    simulation.setName(name);
                    simulation.setCreatorID(creatorID);
                    simulation.setFinish(false);
                    simulation.setDepartureLivingS(departureLivingS);
                    simulation.setDepartureWorkingS(departureWorkingS);
                    simulation.setCarPercentage(carPercentage);
                    simulation.setLivingFeatureUUID(livingFeatureUUID);
                    simulation.setWorkingFeatureUUID(workingFeatureUUID);
                    simulation.setVehicleCount(vehicleCount);
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

    public List<Simulation> getAll(int userID) throws DatabaseOperationException {
        try (Connection connection = getConnection()) {
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
                        simulation.setDepartureLivingS(result.getInt("departure_living_s"));
                        simulation.setDepartureWorkingS(result.getInt("departure_working_s"));
                        simulation.setCarPercentage(result.getInt("car_percentage"));
                        simulation.setLivingFeatureUUID(UUID.fromString(result.getString("living_feature")));
                        simulation.setWorkingFeatureUUID(UUID.fromString(result.getString("working_feature")));
                        simulation.setVehicleCount(result.getInt("vehicle_count"));

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
        try (Connection connection = getConnection()) {
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
                        simulation.setDepartureLivingS(result.getInt("departure_living_s"));
                        simulation.setDepartureWorkingS(result.getInt("departure_working_s"));
                        simulation.setCarPercentage(result.getInt("car_percentage"));
                        simulation.setLivingFeatureUUID(UUID.fromString(result.getString("living_feature")));
                        simulation.setWorkingFeatureUUID(UUID.fromString(result.getString("working_feature")));
                        simulation.setVehicleCount(result.getInt("vehicle_count"));

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
        try (Connection connection = getConnection()) {
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
                        simulation.setDepartureLivingS(result.getInt("departure_living_s"));
                        simulation.setDepartureWorkingS(result.getInt("departure_working_s"));
                        simulation.setCarPercentage(result.getInt("car_percentage"));
                        simulation.setLivingFeatureUUID(UUID.fromString(result.getString("living_feature")));
                        simulation.setWorkingFeatureUUID(UUID.fromString(result.getString("working_feature")));
                        simulation.setVehicleCount(result.getInt("vehicle_count"));

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

    public void updateFinish(Simulation simulation, boolean newValue) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_FINISH)) {
                statement.setBoolean(1, newValue);
                statement.setString(2, simulation.getUuid().toString());
                statement.setInt(3, simulation.getCreatorID());
                statement.executeUpdate();
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
