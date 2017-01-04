package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.Entity;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.FeatureType;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.entities.simulation.SimulationCongestionResult;
import fr.enssat.lanniontech.api.entities.simulation.SimulationVehicleResult;
import fr.enssat.lanniontech.api.entities.simulation.SimulationVehicleStatistics;
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

public class SimulationResultRepository extends SimulationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationResultRepository.class);

    private static final String INSERT_ROAD_METRIC = "INSERT INTO simulation_congestion(feature_uuid, congestion_percentage, simulation_uuid, timestamp_s) VALUES (?, ?, ?, ?)";
    private static final String INSERT_VEHICLE_POSITION = "INSERT INTO simulation_vehicle(simulation_uuid, vehicle_id, timestamp_s, longitude, latitude, angle, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
   private static final String INSERT_VEHICLE_STATISTICS = "INSERT INTO simulation_vehicle_statistics(simulation_uuid, vehicle_id, delay_congestion, average_speed) VALUES (?,?,?,?)";
    private static final String SELECT_CONGESTION_AT = "SELECT  feature_uuid, congestion_percentage FROM simulation_congestion WHERE simulation_uuid = ? AND timestamp_s = ?";
    private static final String SELECT_VEHICLES_AT = "SELECT vehicle_id, longitude, latitude, angle, type FROM simulation_vehicle WHERE simulation_uuid = ? and timestamp_s = ?";
    private static final String SELECT_ITINERARY_FOR = "SELECT longitude, latitude, angle, timestamp_s, type FROM simulation_vehicle WHERE simulation_uuid = ? AND vehicle_id = ?";
    private static final String SELECT_VEHICLE_STATISTICS = "SELECT average_speed, delay_congestion FROM simulation_vehicle_statistics WHERE simulation_uuid = ? AND vehicle_id = ?";
    private static final String DELETE_ROAD_METRICS = "DELETE FROM simulation_congestion WHERE simulation_uuid = ?";
    private static final String DELETE_VEHCILES_POSITIONS = "DELETE FROM simulation_vehicle WHERE simulation_uuid = ?";
    private static final String DELETE_VEHICLE_STATISTICS = "DELETE FROM simulation_vehicle_statistics WHERE simulation_uuid = ?";

    // ==========
    // CONGESTION
    // ==========

    public void addRoadMetric(UUID simulationUUID, UUID featureUUID, int congestion, int timestamp) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_ROAD_METRIC)) {

                statement.setString(1, featureUUID.toString());
                statement.setInt(2, congestion);
                statement.setString(3, simulationUUID.toString());
                statement.setInt(4, timestamp);
                try {
                    statement.execute();
                } finally {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }

    public List<SimulationCongestionResult> getCongestionAt(UUID simulationUUID, int timestamp) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_CONGESTION_AT)) {
                statement.setString(1, simulationUUID.toString());
                statement.setInt(2, timestamp);

                try (ResultSet result = statement.executeQuery()) {
                    List<SimulationCongestionResult> congestions = new ArrayList<>();
                    while (result.next()) {
                        SimulationCongestionResult congestion = new SimulationCongestionResult();
                        congestion.setCongestionPercentage(result.getInt("congestion_percentage"));
                        congestion.setFeatureUUID(UUID.fromString(result.getString("feature_uuid")));

                        congestions.add(congestion);
                    }
                    return congestions;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, SimulationCongestionResult.class);
        }
    }

    // ========
    // VEHICLES
    // ========

    public void addVehicleInfo(UUID simulationUUID, int vehicleID, int timestamp, Coordinates coordinate, double angle, FeatureType type) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_VEHICLE_POSITION)) {
                statement.setString(1, simulationUUID.toString());
                statement.setInt(2, vehicleID);
                statement.setInt(3, timestamp);
                statement.setDouble(4, coordinate.getLongitude());
                statement.setDouble(5, coordinate.getLatitude());
                statement.setDouble(6, angle);
                statement.setInt(7, type.getJsonID());
                try {
                    statement.execute();
                } finally {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, SimulationVehicleResult.class);
        }
    }

    public List<SimulationVehicleResult> getItineraryFor(UUID simulationUUID, int vehicleID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ITINERARY_FOR)) {
                statement.setString(1, simulationUUID.toString());
                statement.setInt(2, vehicleID);

                try (ResultSet result = statement.executeQuery()) {
                    List<SimulationVehicleResult> itinerary = new ArrayList<>();
                    while (result.next()) {
                        SimulationVehicleResult step = new SimulationVehicleResult();
                        step.setAngle(result.getDouble("angle"));
                        step.setTimestamp(result.getInt("timestamp_s"));
                        step.setType(FeatureType.forValue(result.getInt("type")));
                        step.setVehicleID(vehicleID);
                        double longitude = result.getDouble("longitude");
                        double latitude = result.getDouble("latitude");
                        step.setCoordinates(new Coordinates(longitude, latitude));

                        itinerary.add(step);
                    }
                    return itinerary;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, SimulationVehicleResult.class);
        }
    }

    public List<SimulationVehicleResult> getVehiclesAt(UUID simulationUUID, int timestamp) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_VEHICLES_AT)) {
                statement.setString(1, simulationUUID.toString());
                statement.setInt(2, timestamp);

                try (ResultSet result = statement.executeQuery()) {
                    List<SimulationVehicleResult> itinerary = new ArrayList<>();
                    while (result.next()) {
                        SimulationVehicleResult step = new SimulationVehicleResult();
                        step.setAngle(result.getDouble("angle"));
                        step.setTimestamp(timestamp);
                        step.setType(FeatureType.forValue(result.getInt("type")));
                        step.setVehicleID(result.getInt("vehicle_id"));
                        double longitude = result.getDouble("longitude");
                        double latitude = result.getDouble("latitude");
                        step.setCoordinates(new Coordinates(longitude, latitude));

                        itinerary.add(step);
                    }
                    return itinerary;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, SimulationVehicleResult.class);
        }
    }

    // ==========
    // STATISTICS
    // ==========

    public SimulationVehicleStatistics getStatistics(UUID simulationUUID, int vehicleID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_VEHICLE_STATISTICS)) {
                statement.setString(1, simulationUUID.toString());
                statement.setInt(2, vehicleID);

                try (ResultSet result = statement.executeQuery()) {
                    result.next(); // Has exactly one row
                    SimulationVehicleStatistics statistics = new SimulationVehicleStatistics();
                    statistics.setVehicleID(vehicleID);
                    statistics.setAverageSpeed(result.getInt("average_speed"));
                    statistics.setDelayDueToCongestionS(result.getInt("delay_congestion"));

                    return statistics;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, SimulationVehicleResult.class);
        }
    }

    public SimulationVehicleStatistics addVehicleStatistics(UUID simulationUUID, int vehicleID, int averageSpeed, int delayDueToCongestionS) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_VEHICLE_STATISTICS)) {
                statement.setString(1, simulationUUID.toString());
                statement.setInt(2, vehicleID);
                statement.setInt(3, delayDueToCongestionS);
                statement.setInt(3, averageSpeed);

                try {
                    statement.execute();
                    SimulationVehicleStatistics statistics = new SimulationVehicleStatistics();
                    statistics.setVehicleID(vehicleID);
                    statistics.setAverageSpeed(averageSpeed);
                    statistics.setDelayDueToCongestionS(delayDueToCongestionS);
                    return statistics;
                } finally {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, SimulationVehicleResult.class);
        }
    }


    // ======
    // COMMON
    // ======

    /**
     * Delete all the results associated to the given simulation
     */
    public void delete(UUID simulationUUID) {
        delete(simulationUUID, DELETE_ROAD_METRICS, SimulationCongestionResult.class);
        delete(simulationUUID, DELETE_VEHCILES_POSITIONS, SimulationVehicleResult.class);
        delete(simulationUUID, DELETE_VEHICLE_STATISTICS, SimulationVehicleStatistics.class);
    }

    private void delete(UUID simulationUUID, String query, Class<? extends Entity> clazz) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, simulationUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, clazz);
        }
    }
}
