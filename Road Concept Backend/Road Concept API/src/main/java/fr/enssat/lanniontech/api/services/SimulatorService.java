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
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.exceptions.EntityStillInUseException;
import fr.enssat.lanniontech.api.exceptions.InvalidParameterException;
import fr.enssat.lanniontech.api.exceptions.ProgressUnavailableException;
import fr.enssat.lanniontech.api.exceptions.RoadConceptUnexpectedException;
import fr.enssat.lanniontech.api.repositories.SimulationParametersRepository;
import fr.enssat.lanniontech.api.repositories.SimulationRepository;
import fr.enssat.lanniontech.api.repositories.SimulationResultRepository;
import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.managers.HistoryManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;
import fr.enssat.lanniontech.core.roadElements.RoadMetrics;
import fr.enssat.lanniontech.core.vehicleElements.VehicleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class SimulatorService extends AbstractService implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorService.class);

    private SimulationRepository simulationGlobalRepository = new SimulationRepository();
    private SimulationParametersRepository simulationParametersRepository = new SimulationParametersRepository();
    private SimulationResultRepository simulationResultRepository = new SimulationResultRepository();

    private MapService mapService = new MapService();

    public Simulation create(User user, String name, int mapID, int samplingRate, int departureLivingS, int departureWorkingS, UUID livingFeatureUUID, UUID workingFeatureUUID, int carPercentage, int vehicleCount) {
        //FIXME: The repository should not throw an EntityStillInUseException if the map does not exist
        try {
            Simulation simulation = simulationParametersRepository.create(user.getId(), name, mapID, samplingRate, departureLivingS, departureWorkingS, livingFeatureUUID, workingFeatureUUID, carPercentage, vehicleCount);
            simulation.setSimulator(new Simulator(simulation.getUuid()));

            simulationGlobalRepository.duplicateFeatures(simulation);

            simulation.getSimulator().addObserver(this); // Being notified when the simulation is finish
            simulation.getSimulator().getHistoryManager().addObserver(this); // Being notified at each result step

            start(simulation);

            return simulation;
        } catch (EntityStillInUseException e) {
            throw new EntityNotExistingException(Map.class);
        }
    }

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

                List<SpaceTimePosition> positions = historyManager.getPositionSample();
                for (SpaceTimePosition vehicle : positions) {
                    FeatureType type;
                    if (vehicle.getType() == VehicleType.CAR) {
                        type = FeatureType.CAR;
                    } else {
                        type = FeatureType.TRUCK;
                    }
                    simulationResultRepository.addVehicleInfo(simulationUUID, vehicle.getId(), vehicle.getTime(), new Coordinates(vehicle.getLongitude(), vehicle.getLatitude()), vehicle.getAngle(), type);
                }

                List<RoadMetrics> roadMetrics = historyManager.getRoadMetricsSample();
                for (RoadMetrics metric : roadMetrics) {
                    if (metric.getCongestion() != 0) {
                        //FIXME: Quick and ugly fix. Voir avec Antoine et retirer le /10
                        simulationResultRepository.addRoadMetric(simulationUUID, metric.getRoadId(), metric.getCongestion() / 10 , metric.getTimestamp());
                    }
                }
                historyManager.removeSamples();
            } else if (observable instanceof Simulator) {
                Simulation simulation = get(simulationUUID);
                simulationParametersRepository.updateFinish(simulation, true);
                simulation.setFinish(true);
                simulation.setSimulator(null); // garbage collector
            }
        }
    }

    private boolean start(Simulation simulation) throws EntityNotExistingException {
        Map map = mapService.getMap(simulation.getCreatorID(), simulation.getMapID());
        sendFeatures(simulation, map.getFeatures());

        simulation.getSimulator().getVehicleManager().addToSpawnArea(simulation.getSimulator().getRoadManager().getRoad(simulation.getLivingFeatureUUID()));
        int count = 0; //TODO: REMOVE
        for (int i = 0; i < simulation.getVehicleCount(); i++) {
            if (simulation.getSimulator().getVehicleManager().addVehicle()) {
                count++;
            }
        }
        LOGGER.debug("VEHICLE COUNT = " + count);
        // TODO: Définir point d'arrivée + heure de départ lieu habitation + heure de départ lieu de travail

        return simulation.getSimulator().launchSimulation(86400, 0.1, 10 * simulation.getSamplingRate()); // 86400 is the count of seconds in one day
    }

    private void sendFeatures(Simulation simulation, FeatureCollection features) {
        for (Feature feature : features) {
            if (feature.isRoad()) { // TODO: A voir quand Antoine acceptera de recevoir des ronds points et feux rouges
                LineString road = (LineString) feature.getGeometry();
                Coordinates last = road.getCoordinates().get(0);
                for (int i = 1; i < road.getCoordinates().size(); i++) { // avoid the first feature
                    Coordinates coordinates = road.getCoordinates().get(i);
                    Position A = simulation.getSimulator().getPositionManager().addPosition(last.getLongitude(), last.getLatitude());
                    Position B = simulation.getSimulator().getPositionManager().addPosition(coordinates.getLongitude(), coordinates.getLatitude());
                    simulation.getSimulator().getRoadManager().addRoadSectionToRoad(A, B, feature.getUuid());
                    last = coordinates;
                }
            }
        }
        simulation.getSimulator().getRoadManager().closeRoads();
        if (simulation.getSimulator().getRoadManager().checkIntegrity() != 0) {
            throw new RoadConceptUnexpectedException();
        }
    }

    public Simulation get(UUID simulationUUID) {
        Simulation simulation = simulationParametersRepository.getFromUUID(simulationUUID);
        if (simulation == null) {
            throw new EntityNotExistingException(Simulation.class);
        }
        return simulation;
    }

    public List<Simulation> getAll(User user, int mapID) {
        return simulationParametersRepository.getAllFromMap(user, mapID);
    }

    public List<Simulation> getAll(int userID) {
        return simulationParametersRepository.getAll(userID);
    }

    public FeatureCollection getResultAt(UUID simulationUUID, int timestamp) {
        Simulation simulation = get(simulationUUID);

        if (timestamp < 0 || timestamp >= 86400) {
            throw new InvalidParameterException("Invalid timestamp (min value = 0 | max value = 86399)");
        } else if (timestamp % simulation.getSamplingRate() != 0) {
            throw new InvalidParameterException("Timestamp must be consistent with the simulation sampling rate.");
        }

        FeatureCollection features = simulationGlobalRepository.getFeatures(simulationUUID);

        List<SimulationCongestionResult> congestions = simulationResultRepository.getCongestionAt(simulationUUID, timestamp);
        for (Feature feature : features) {
            for (SimulationCongestionResult congestion : congestions) {
                if (feature.getUuid().equals(congestion.getFeatureUUID())) {
                    feature.getProperties().put("congestion", congestion.getCongestionPercentage());
                }
            }
        }

        // Congestion is stored as a sparse matrix, so we need to set default congestion congestion value
        for (Feature feature : features) {
            if (!feature.getProperties().containsKey("congestion")) {
                feature.getProperties().put("congestion", 0);
            }
        }

        List<SimulationVehicleResult> vehicles = simulationResultRepository.getVehiclesAt(simulationUUID, timestamp);
        for (SimulationVehicleResult vehicle : vehicles) {
            Feature feature = new Feature(); // Vehicle ID is set from the simulator. We don't use the generated one.
            feature.setGeometry(new Point(vehicle.getCoordinates()));
            feature.getProperties().put("type", vehicle.getType());
            feature.getProperties().put("id", vehicle.getVehicleID());
            feature.getProperties().put("angle", vehicle.getAngle());
            features.getFeatures().add(feature);
        }
        return features;
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

    public void delete(UUID simulationUUID) {
        Simulation simulation = new Simulation();
        simulation.setUuid(simulationUUID);

        // Delete simulation parameters
        simulationParametersRepository.delete(simulation);
        // Delete duplicated features
        simulationGlobalRepository.deleteAssociatedFeatures(simulationUUID);
    }
}
