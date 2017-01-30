package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.entities.simulation.SimulationZone;
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

public class SimulationZonesRepository extends SimulationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationZonesRepository.class);

    private static final String INSERT = "INSERT INTO simulation_zone(simulation_uuid, living_feature, working_feature, departure_living_s, departure_working_s, car_percentage, vehicle_count) VALUES (?,?,?,?,?,?,?)";
    private static final String SELECT_FROM_SIMULATION = "SELECT simulation_uuid, living_feature, working_feature, departure_living_s, departure_working_s, car_percentage, vehicle_count FROM simulation_zone WHERE simulation_uuid = ?";

    // CREATE
    // ------

    public SimulationZone create(Simulation simulation, SimulationZone zone) { //NOSONAR: Parameters count
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                statement.setString(1, simulation.getUuid().toString());
                statement.setString(2, zone.getLivingFeatureUUID().toString());
                statement.setString(3, zone.getWorkingFeatureUUID().toString());
                statement.setInt(4, zone.getDepartureLivingS());
                statement.setInt(5, zone.getDepartureWorkingS());
                statement.setInt(6, zone.getCarPercentage());
                statement.setInt(7, zone.getVehicleCount());

                try {
                    statement.execute();
                    return zone;
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

    public List<SimulationZone> getAll(UUID simulationUUID) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_SIMULATION)) {
                statement.setString(1, simulationUUID.toString());

                try (ResultSet result = statement.executeQuery()) {

                    List<SimulationZone> zones = new ArrayList<>();
                    while (result.next()) {
                        SimulationZone zone = new SimulationZone();
                        zone.setCarPercentage(result.getInt("car_percentage"));
                        zone.setDepartureLivingS(result.getInt("departure_living_s"));
                        zone.setDepartureWorkingS(result.getInt("departure_working_s"));
                        zone.setLivingFeatureUUID(UUID.fromString(result.getString("living_feature")));
                        zone.setWorkingFeatureUUID(UUID.fromString(result.getString("working_feature")));
                        zone.setVehicleCount(result.getInt("vehicle_count"));

                        zones.add(zone);
                    }
                    return zones;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, Simulation.class);
        }
    }
}
