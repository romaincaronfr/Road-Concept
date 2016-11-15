package fr.enssat.lanniontech.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.FeatureType;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.entities.geojson.Point;
import fr.enssat.lanniontech.api.entities.map.Map;
import fr.enssat.lanniontech.api.entities.simulation.RoadCongestionLevel;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.exceptions.RoadConceptUnexpectedException;
import fr.enssat.lanniontech.api.repositories.SimulationParametersRepository;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SimulatorService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorService.class);

    private SimulationParametersRepository simulationParametersRepository = new SimulationParametersRepository();

    private MapService mapService = new MapService();

    public Simulation create(User user, String name, int mapID, int durationS) {
        Simulation simulation = new Simulation();
        simulation.setCreatorID(user.getId());
        simulation.setMapID(mapID);
        simulation.setName(name);
        simulation.setDurationS(durationS);

        simulationParametersRepository.create(user.getId(), name, mapID, durationS);

        start(simulation);
        //TODO: Mettre simulation.simulator à null une fois la simulation terminée et les résultats sdtockés en base (conso mémoire)
        return simulation;
    }

    public Simulation get(UUID simulationUUID) {
        Simulation simulation = simulationParametersRepository.getFromUUID(simulationUUID);
        if (simulation == null) {
            throw new EntityNotExistingException(Simulation.class);
        }
        return simulation;
    }

    private boolean start(Simulation simulation) throws EntityNotExistingException {
        Map map = mapService.getMap(simulation.getCreatorID(), simulation.getMapID());
        List<Road> roads = sendFeatures(simulation, map.getFeatures());
        simulation.getSimulator().vehicleManager.addToSpawnArea(roads.get(0)); //TODO: Set as simulation parameter
        simulation.getSimulator().vehicleManager.addVehicle();
        return simulation.getSimulator().launchSimulation(simulation.getDurationS(), 0.1, 10);
    }

    public List<Simulation> getAll(User user, int mapID) {
        return simulationParametersRepository.getAllFromMap(user, mapID);
    }

    public List<Simulation> getAll(int userID) {
        return simulationParametersRepository.getAll(userID);
    }

    public boolean delete(UUID simulationUUID) {
        Simulation simulation = new Simulation();
        simulation.setUuid(simulationUUID);
        int count = simulationParametersRepository.delete(simulation);
        return count == 1; // // If false, something goes wrong (0 or more than 1 rows deleted)
    }

    //    public FeatureCollection getResult(UUID simulationUUID, int timestamp) throws SimulationImcompleteException {
    //        Simulation simulation = get(simulationUUID);
    //        if (simulation.isFinish()) {
    //            Map map = mapService.getMap(simulation.getCreatorID(), simulation.getMapID());
    //            getResultAt(map.getFeatures(), timestamp);
    //            return map.getFeatures();
    //        }
    //        throw new SimulationImcompleteException();
    //    }

    //    private void getResultAt(FeatureCollection features, int timestamp) {
    //        for (Feature feature : features) {
    //            Double status = simulation.getSimulator().historyManager.getRoadStatus(feature.getUuid(), timestamp);
    //            feature.getProperties().put("congestion", status.intValue());
    //        }
    //
    //        List<SpaceTimePosition> vehicles = simulation.getSimulator().historyManager.getAllVehicleAt(timestamp);
    //        LOGGER.debug("Nombre de véhicules = " + vehicles.size());
    //        for (SpaceTimePosition vehicle : vehicles) {
    //            LOGGER.debug("Space Time Position = " + vehicle);
    //            Feature feature = new Feature(); // ID du véhicule rémonté du simulateur, on n'utilise pas l'UUID généré.
    //            feature.setGeometry(new Point(new Coordinates(vehicle.getLon(), vehicle.getLat())));
    //            feature.getProperties().put("type", FeatureType.VEHICLE);
    //            feature.getProperties().put("vehicle_id", vehicle.getId());
    //            feature.getProperties().put("angle", vehicle.getAngle());
    //            features.getFeatures().add(feature);
    //        }
    //    }

    //    public FeatureCollection getVehiculePositionsHistory(UUID simulationUUID, int vehicleID) {
    //        List<SpaceTimePosition> history = simulation.getSimulator().historyManager.getVehiclePosition(vehicleID);
    //        FeatureCollection result = new FeatureCollection();
    //
    //        for (SpaceTimePosition position : history) {
    //            Feature feature = new Feature();
    //            feature.getProperties().put("type", FeatureType.VEHICLE);
    //            feature.getProperties().put("vehicle_id", position.getId());
    //            feature.getProperties().put("angle", position.getAngle());
    //            feature.setGeometry(new Point(new Coordinates(position.getLon(), position.getLat())));
    //
    //            result.getFeatures().add(feature);
    //        }
    //        return result;
    //    }

    private List<Road> sendFeatures(Simulation simulation, FeatureCollection features) {
        List<Road> roads = new ArrayList<>();
        for (Feature feature : features) {
            if (feature.getGeometry() instanceof LineString) {
                LineString road = (LineString) feature.getGeometry();
                if (!feature.isRoad()) { // TODO: A voir quand Antoine acceptera de recevoir des ronds points
                    Coordinates last = road.getCoordinates().get(0);
                    for (int i = 1; i < road.getCoordinates().size(); i++) { // avoid the first feature
                        Coordinates coordinates = road.getCoordinates().get(i);
                        Position A = simulation.getSimulator().positionManager.addPosition(last.getLatitude(), last.getLongitude());
                        Position B = simulation.getSimulator().positionManager.addPosition(coordinates.getLatitude(), coordinates.getLongitude());
                        roads.add(simulation.getSimulator().roadManager.addRoadSectionToRoad(A, B, feature.getUuid()));
                        last = coordinates;
                    }
                }
            }
            //TODO: Prévoir les ronds points et les feux rouges. En attente d'Antoine
        }
        simulation.getSimulator().roadManager.closeRoads();
        if (simulation.getSimulator().roadManager.checkIntegrity() != 0) {
            throw new RoadConceptUnexpectedException();
        }
        return roads;
    }


    @Deprecated
    public FeatureCollection getFakeSimulationResult() throws IOException {
        InputStream source = getClass().getResourceAsStream("/from-osm-lannion-center.json");

        FeatureCollection features = new ObjectMapper().readValue(source, FeatureCollection.class);
        new MapService().fromOSMAdaptation(features);

        for (Feature feature : features.getFeatures()) {
            feature.getProperties().put("congestion", RoadCongestionLevel.random());
        }

        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            Feature feature = features.getFeatures().get(random.nextInt(features.getFeatures().size()));
            if (feature.getGeometry() instanceof LineString) {
                LineString lineString = (LineString) feature.getGeometry();

                Feature vehicle = new Feature();
                vehicle.setUuid(UUID.randomUUID());
                vehicle.getProperties().put("type", FeatureType.VEHICLE);
                vehicle.getProperties().put("angle", 7 * random.nextDouble());
                vehicle.getProperties().put("id", vehicle.getUuid());

                Point point = new Point();
                Coordinates coords = lineString.getCoordinates().get(random.nextInt(lineString.getCoordinates().size()));
                point.setCoordinates(coords);

                vehicle.setGeometry(point);
                features.getFeatures().add(vehicle);
            }
        }
        return features;
    }

}
