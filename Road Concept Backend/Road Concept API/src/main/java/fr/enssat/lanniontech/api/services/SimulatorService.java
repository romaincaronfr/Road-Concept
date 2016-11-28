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
import fr.enssat.lanniontech.api.exceptions.InvalidParameterException;
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
        Simulation simulation = simulationParametersRepository.create(user.getId(), name, mapID, samplingRate, departureLivingS, departureWorkingS, livingFeatureUUID, workingFeatureUUID, carPercentage, vehicleCount);
        simulation.setSimulator(new Simulator(simulation.getUuid()));

        simulationGlobalRepository.duplicateFeatures(simulation);

        // Observe the simulator to get results in real time
        simulation.getSimulator().historyManager.addObserver(this);

        start(simulation);

        //TODO: Lancer un nouveau Runnable qui vas enregistrer au fur et à mesure les résultats
        //TODO: Mettre simulation.simulator à null une fois la simulation terminée (conso mémoire)
        return simulation;
    }

    @Override
    public void update(Observable observable, Object argument) {
        UUID simulationUUID = UUID.fromString(argument.toString());
        HistoryManager historyManager = (HistoryManager) observable;

        List<SpaceTimePosition> positions = historyManager.getPositionSample();
        for (SpaceTimePosition vehicle : positions) {
            FeatureType type;
            if (vehicle.getType() == VehicleType.CAR) {
                type = FeatureType.CAR;
            } else {
                type = FeatureType.TRUCK;
            }
            simulationResultRepository.addVehicleInfo(simulationUUID, vehicle.getId(), vehicle.getTime(), new Coordinates(vehicle.getLon(), vehicle.getLat()), vehicle.getAngle(), type);
        }

        List<RoadMetrics> roadMetrics = historyManager.getRoadMetricsSample();
        for (RoadMetrics metric : roadMetrics) {
            if (metric.getCongestion() != 0) {
                simulationResultRepository.addRoadMetric(simulationUUID, metric.getRoadId(), metric.getCongestion(), metric.getTimestamp());
            }
        }
        historyManager.removeSample();
    }

    private boolean start(Simulation simulation) throws EntityNotExistingException {
        Map map = mapService.getMap(simulation.getCreatorID(), simulation.getMapID());
        sendFeatures(simulation, map.getFeatures());

        simulation.getSimulator().vehicleManager.addToSpawnArea(simulation.getSimulator().roadManager.getRoad(simulation.getLivingFeatureUUID()));
        int count = 0;
        for (int i = 0; i < simulation.getVehicleCount(); i++) {
            if (simulation.getSimulator().vehicleManager.addVehicle()) {
                count++;
            }
        }
        LOGGER.debug("VEHCILE COUNT = " + count);
        // TODO: Définir point d'arrivée

        return simulation.getSimulator().launchSimulation(86400, 0.1, 10 * simulation.getSamplingRate()); // 86400 is the count of seconds in one day
    }

    private void sendFeatures(Simulation simulation, FeatureCollection features) {
        for (Feature feature : features) {
            if (feature.isRoad()) { // TODO: A voir quand Antoine acceptera de recevoir des ronds points
                LineString road = (LineString) feature.getGeometry();
                Coordinates last = road.getCoordinates().get(0);
                for (int i = 1; i < road.getCoordinates().size(); i++) { // avoid the first feature
                    Coordinates coordinates = road.getCoordinates().get(i);
                    Position A = simulation.getSimulator().positionManager.addPosition(last.getLatitude(), last.getLongitude());
                    Position B = simulation.getSimulator().positionManager.addPosition(coordinates.getLatitude(), coordinates.getLongitude());
                    simulation.getSimulator().roadManager.addRoadSectionToRoad(A, B, feature.getUuid());
                    last = coordinates;
                }
            }
        }
        simulation.getSimulator().roadManager.closeRoads();
        if (simulation.getSimulator().roadManager.checkIntegrity() != 0) {
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
        if (timestamp % simulation.getSamplingRate() != 0) {
            throw new InvalidParameterException("Invalid timestamp");
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

        for (Feature feature : features) {
            if (feature.getProperties().containsKey("congestion")) {
                feature.getProperties().put("congestion",0);
            }
        }

        List<SimulationVehicleResult> vehicles = simulationResultRepository.getVehiclesAt(simulationUUID, timestamp);
        for (SimulationVehicleResult vehicle : vehicles) {
            Feature feature = new Feature(); // ID du véhicule rémonté du simulateur, on n'utilise pas l'UUID généré.
            feature.setGeometry(new Point(vehicle.getCoordinates()));
            feature.getProperties().put("type", vehicle.getType());
            feature.getProperties().put("id", vehicle.getVehicleID());
            feature.getProperties().put("angle", vehicle.getAngle());
            features.getFeatures().add(feature);
        }
        return features;
    }

    public FeatureCollection getVehiculePositionsHistory(UUID simulationUUID, int vehicleID) {
        FeatureCollection result = new FeatureCollection();
        for (SimulationVehicleResult position : simulationResultRepository.getItineraryFor(simulationUUID, vehicleID)) {
            Feature feature = new Feature();
            feature.getProperties().put("type", position.getType());
            feature.getProperties().put("id", position.getVehicleID());
            feature.getProperties().put("angle", position.getAngle());
            feature.getProperties().put("timestamp", position.getTimestamp());
            feature.setGeometry(new Point(new Coordinates(position.getCoordinates().getLongitude(), position.getCoordinates().getLatitude())));

            result.getFeatures().add(feature);
        }
        return result;
    }

    public void delete(UUID simulationUUID) {
        Simulation simulation = new Simulation();
        simulation.setUuid(simulationUUID);

        // Delete simulation parameters
        simulationParametersRepository.delete(simulation);

        // Delete duplicated features
        simulationGlobalRepository.deleteAssociatedFeatures(simulationUUID);

        // Delete results
        simulationResultRepository.delete(simulationUUID);
    }
}
