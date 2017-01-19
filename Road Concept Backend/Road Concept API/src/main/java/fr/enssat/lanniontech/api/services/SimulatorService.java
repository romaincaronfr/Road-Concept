package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.FeatureType;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.entities.geojson.Point;
import fr.enssat.lanniontech.api.entities.map.Map;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.entities.simulation.SimulationCongestionResult;
import fr.enssat.lanniontech.api.entities.simulation.SimulationVehicleResult;
import fr.enssat.lanniontech.api.entities.simulation.SimulationVehicleStatistics;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.exceptions.EntityStillInUseException;
import fr.enssat.lanniontech.api.exceptions.InvalidParameterException;
import fr.enssat.lanniontech.api.exceptions.ProgressUnavailableException;
import fr.enssat.lanniontech.api.exceptions.RoadConceptUnexpectedException;
import fr.enssat.lanniontech.api.repositories.MapInfoRepository;
import fr.enssat.lanniontech.api.repositories.SimulationParametersRepository;
import fr.enssat.lanniontech.api.repositories.SimulationRepository;
import fr.enssat.lanniontech.api.repositories.SimulationResultRepository;
import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.managers.HistoryManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;
import fr.enssat.lanniontech.core.roadElements.RoadMetrics;
import fr.enssat.lanniontech.core.vehicleElements.VehicleStats;
import fr.enssat.lanniontech.core.vehicleElements.VehicleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class SimulatorService extends AbstractService implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorService.class);

    private SimulationRepository simulationGlobalRepository = new SimulationRepository();
    private SimulationParametersRepository simulationParametersRepository = new SimulationParametersRepository();
    private SimulationResultRepository simulationResultRepository = new SimulationResultRepository();
    private MapInfoRepository mapRepository = new MapInfoRepository();

    private MapService mapService = new MapService();

    // ======
    // CREATE
    // ======

    public Simulation create(User user, String name, int mapID, int samplingRate, int departureLivingS, int departureWorkingS, UUID livingFeatureUUID, UUID workingFeatureUUID, int carPercentage, int vehicleCount) {
        try {
            Simulation simulation = simulationParametersRepository.create(user.getId(), name, mapID, samplingRate, departureLivingS, departureWorkingS, livingFeatureUUID, workingFeatureUUID, carPercentage, vehicleCount);
            simulation.setSimulator(new Simulator(simulation.getUuid()));

            simulationGlobalRepository.duplicateFeatures(simulation);

            simulation.getSimulator().addObserver(this); // Being notified when the simulation is finish
            simulation.getSimulator().getHistoryManager().addObserver(this); // Being notified at each result step

            start(simulation);

            return simulation;
        } catch (EntityStillInUseException e) { //FIXME: The repository should not throw an EntityStillInUseException if the map does not exist
            throw new EntityNotExistingException(Map.class);
        }
    }

    private boolean start(Simulation simulation) {
        Map map = mapService.getMap(simulation.getCreatorID(), simulation.getMapID());
        sendFeatures(simulation, map.getFeatures());

        simulation.getSimulator().getVehicleManager().createTrafficGenerator(simulation.getDepartureLivingS(),
                simulation.getDepartureWorkingS(), simulation.getVehicleCount(),
                simulation.getCarPercentage(),simulation.getLivingFeatureUUID(),simulation.getWorkingFeatureUUID());

        return simulation.getSimulator().launchSimulation(86400, 0.01, 100 * simulation.getSamplingRate()); // 86400 is the count of seconds in one day
    }

    private void sendFeatures(Simulation simulation, FeatureCollection features) {
        for (Feature feature : features) {
            if (feature.getGeometry() instanceof LineString) {
                LineString geometry = (LineString) feature.getGeometry();

                if (feature.isRoad()) {
                    sendRoads(simulation, feature, geometry);
                } else if (feature.isRoundabout()) {
                    sendRoundabout(simulation, feature, geometry);
                }
            }
        }
        simulation.getSimulator().getRoadManager().closeRoads();
        if (simulation.getSimulator().getRoadManager().checkIntegrity() != 0) {
            throw new RoadConceptUnexpectedException("Simulator check integrity failed");
        }
    }

    private void sendRoundabout(Simulation simulation, Feature feature, LineString road) {
        List<Position> roundaboutPositions = new ArrayList<>();
        for (Coordinates C : road.getCoordinates()) {
            roundaboutPositions.add(simulation.getSimulator().getPositionManager().addPosition(C.getLongitude(), C.getLatitude()));
        }
        simulation.getSimulator().getRoadManager().addRoundAbout(roundaboutPositions, feature.getUuid());
    }

    private void sendRoads(Simulation simulation, Feature feature, LineString road) {
        Coordinates last = road.getCoordinates().get(0);

        for (int i = 1; i < road.getCoordinates().size(); i++) { // avoid the first feature
            Coordinates coordinates = road.getCoordinates().get(i);
            Position A = simulation.getSimulator().getPositionManager().addPosition(last.getLongitude(), last.getLatitude());
            Position B = simulation.getSimulator().getPositionManager().addPosition(coordinates.getLongitude(), coordinates.getLatitude());

            int maxSpeed = (int) feature.getProperties().get("maxspeed");
            boolean oneWay = computeOneWayFromProperty((String) feature.getProperties().get("oneway"));

            if (isReverseDrawed((String) feature.getProperties().get("oneway"))) {
                Collections.reverse(road.getCoordinates());
            }

            simulation.getSimulator().getRoadManager().addRoadSectionToRoad(A, B, feature.getUuid(), maxSpeed, oneWay);

            last = coordinates;
        }
    }

    // =========
    // EXECUTION
    // =========

    public int getExecutionProgress(UUID simulationUUID, List<Simulation> activesSimulations) {
        Simulation simulation = null;
        for (Simulation active : activesSimulations) {
            if (active.getUuid().equals(simulationUUID)) {
                simulation = active;
                break;
            }
        }
        if (simulation == null || simulation.getSimulator() == null) {
            throw new ProgressUnavailableException();
        }
        return (int) (Math.round(simulation.getSimulator().getProgress() * 100));
    }

    @Override
    public void update(Observable observable, Object argument) {
        if (argument != null && argument instanceof UUID) {
            UUID simulationUUID = (UUID) argument;

            if (observable instanceof HistoryManager) {
                HistoryManager historyManager = (HistoryManager) observable;
                saveVehiclesPosition(simulationUUID, historyManager);
                saveRoadMetrics(simulationUUID, historyManager);
                historyManager.removeSamples();
            } else if (observable instanceof Simulator) {
                Simulation simulation = get(simulationUUID);
                simulationParametersRepository.finish(simulation);
                saveVehiclesStatistics(simulation, (Simulator) observable); // Needed cause 'simulator' is null in the given simulation
                simulation.setFinish(true);
                simulation.setSimulator(null); // garbage collector
            }
        }
    }

    private void saveRoadMetrics(UUID simulationUUID, HistoryManager historyManager) {
        List<RoadMetrics> roadMetrics = historyManager.getRoadMetricsSample();
        List<SimulationCongestionResult> congestions = new ArrayList<>();
        for (RoadMetrics metric : roadMetrics) {
            if (metric.getCongestion() != 0) {
                SimulationCongestionResult congestion = new SimulationCongestionResult(metric.getRoadId(), metric.getCongestion(), metric.getTimestamp());
                congestions.add(congestion);
            }
        }
        simulationResultRepository.addRoadMetric(simulationUUID, congestions);
    }

    private void saveVehiclesPosition(UUID simulationUUID, HistoryManager historyManager) {
        List<SpaceTimePosition> positions = historyManager.getPositionSample();
        List<SimulationVehicleResult> vehicles = new ArrayList<>();
        for (SpaceTimePosition vehicle : positions) {
            FeatureType type = computeVehicleTypeFromCore(vehicle);

            SimulationVehicleResult vehicleData = new SimulationVehicleResult(vehicle.getId(), vehicle.getTime(), new Coordinates(vehicle.getLongitude(), vehicle.getLatitude()), vehicle.getAngle(), type);
            vehicles.add(vehicleData);
        }
        simulationResultRepository.addVehicleInfo(simulationUUID, vehicles);
    }

    private void saveVehiclesStatistics(Simulation simulation, Simulator simulator) {
        List<SimulationVehicleStatistics> statistics = new ArrayList<>();
        for (VehicleStats stat : simulator.getVehicleManager().getStatistics()) {
            statistics.add(new SimulationVehicleStatistics(stat.getId(), stat.getAverageSpeed(), stat.getElapsedTime(), stat.getDistanceDone()));
        }
        simulationResultRepository.addVehicleStatistics(simulation.getUuid(), statistics);
    }

    // =======
    // RESULTS
    // =======

    public SimulationVehicleStatistics getVehicleStatistics(UUID simulationUUID, int vehicleID) {
        return simulationResultRepository.getStatistics(simulationUUID, vehicleID);
    }

    public FeatureCollection getResultAt(UUID simulationUUID, int timestamp) {
        Simulation simulation = get(simulationUUID);
        checkTimestampValue(timestamp, simulation);

        FeatureCollection features = simulationGlobalRepository.getFeatures(simulationUUID);
        fillCongestions(simulationUUID, timestamp, features);
        fillVehicles(simulationUUID, timestamp, features);
        return features;
    }

    public List<SimulationCongestionResult> getMinimalCongestions(UUID simulationUUID, int timestamp) {
        Simulation simulation = get(simulationUUID);
        checkTimestampValue(timestamp, simulation);
        return simulationResultRepository.getCongestionAt(simulationUUID, timestamp);
    }

    private void fillVehicles(UUID simulationUUID, int timestamp, FeatureCollection features) {
        List<SimulationVehicleResult> vehicles = simulationResultRepository.getVehiclesAt(simulationUUID, timestamp);
        for (SimulationVehicleResult vehicle : vehicles) {
            Feature feature = new Feature(); // Vehicle ID is set from the simulator. We don't use the generated one.
            feature.setGeometry(new Point(vehicle.getCoordinates()));
            feature.getProperties().put("type", vehicle.getType());
            feature.getProperties().put("id", vehicle.getVehicleID());
            feature.getProperties().put("angle", vehicle.getAngle());
            features.getFeatures().add(feature);
        }
    }

    private void fillCongestions(UUID simulationUUID, int timestamp, FeatureCollection features) {
        List<SimulationCongestionResult> congestions = simulationResultRepository.getCongestionAt(simulationUUID, timestamp);
        for (Feature feature : features) {
            for (SimulationCongestionResult congestion : congestions) {
                if (feature.getUuid().equals(congestion.getFeatureUUID())) {
                    feature.getProperties().put("congestion", congestion.getCongestionPercentage());
                }
            }
        }
        addMissingCongestions(features);
    }

    private void addMissingCongestions(FeatureCollection features) {
        // Congestion is stored as a sparse matrix, so we need to set default congestion congestion value
        for (Feature feature : features) {
            if (!feature.getProperties().containsKey("congestion")) {
                feature.getProperties().put("congestion", 0);
            }
        }
    }

    public FeatureCollection getVehiculePositionsHistory(UUID simulationUUID, int vehicleID) {
        FeatureCollection features = simulationGlobalRepository.getFeatures(simulationUUID);
        List<SimulationVehicleResult> history = simulationResultRepository.getItineraryFor(simulationUUID, vehicleID);
        for (int i = 0; i < history.size(); i++) {
            SimulationVehicleResult position = history.get(i);
            Feature feature = new Feature();
            feature.getProperties().put("id", i);
            feature.getProperties().put("type", position.getType());
            feature.getProperties().put("vehicle_id", position.getVehicleID());
            feature.getProperties().put("angle", position.getAngle());
            feature.getProperties().put("timestamp", position.getTimestamp());
            feature.setGeometry(new Point(new Coordinates(position.getCoordinates().getLongitude(), position.getCoordinates().getLatitude())));

            features.getFeatures().add(feature);
        }
        return features;
    }

    // ===
    // GET
    // ===

    public Simulation get(UUID simulationUUID) {
        Simulation simulation = simulationParametersRepository.getFromUUID(simulationUUID);
        if (simulation == null) {
            throw new EntityNotExistingException(Simulation.class);
        }
        return simulation;
    }

    public List<Simulation> getAll(User user, int mapID) {
        List<Simulation> simulations = simulationParametersRepository.getAllFromMap(user, mapID);
        getMapInfos(simulations);
        return simulations;
    }

    public List<Simulation> getAll(int userID) {
        List<Simulation> simulations = simulationParametersRepository.getAll(userID);
        getMapInfos(simulations);
        return simulations;
    }

    private void getMapInfos(List<Simulation> simulations) {
        for(Simulation simulation : simulations) {
            simulation.setMapInfo(mapRepository.get(simulation.getMapID()));
        }
    }

    // ======
    // DELETE
    // ======

    public void delete(UUID simulationUUID) {
        Simulation simulation = new Simulation();
        simulation.setUuid(simulationUUID);

        // Delete simulation parameters
        simulationParametersRepository.delete(simulation);
        // Delete duplicated features
        simulationGlobalRepository.deleteAssociatedFeatures(simulationUUID);
    }

    // =========
    // UTILITIES
    // =========

    private boolean computeOneWayFromProperty(String property) {
        return !(property == null || "no".equals(property));
    }

    private boolean isReverseDrawed(String property) {
        return !(property == null || !"-1".equals(property));
    }

    private void checkTimestampValue(int timestamp, Simulation simulation) {
        if (timestamp < 0 || timestamp >= 86400) {
            throw new InvalidParameterException("Invalid timestamp (min value = 0 | max value = 86399)");
        } else if (timestamp % simulation.getSamplingRate() != 0) {
            throw new InvalidParameterException("Timestamp must be consistent with the simulation sampling rate.");
        }
    }

    private FeatureType computeVehicleTypeFromCore(SpaceTimePosition vehicle) {
        FeatureType type;
        if (vehicle.getType() == VehicleType.CAR) {
            type = FeatureType.CAR;
        } else {
            type = FeatureType.TRUCK;
        }
        return type;
    }
}
