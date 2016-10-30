package fr.enssat.lanniontech.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.FeatureType;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.entities.geojson.Point;
import fr.enssat.lanniontech.api.entities.simulation.RoadCongestionLevel;
import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SimulatorService extends AbstractService {

    private Simulator simulator = new Simulator();

    public FeatureCollection simulate(FeatureCollection features) {
        sendFeatures(features);
        //TODO: sendVehicles
        simulator.launchSimulation(3600, 0.1);
        //simulator.waitForEnd(); // FIXME: ne peu pas être fait ici

        return null;
    }

    public void getResult(FeatureCollection features) {
        long timestamp = 0; //TODO: Voir avec Antoine et Romain
        for (Feature feature : features) {
            double status = simulator.simulationHistory.getRoadStatus(feature.getUuid(), timestamp);
            RoadCongestionLevel congestion = null;
            if (status <= (1 / 3.0) * 100) {
                congestion = RoadCongestionLevel.LOW;
            } else if (status >= (2 / 3.0) * 100) {
                congestion = RoadCongestionLevel.HIGH;
            } else {
                congestion = RoadCongestionLevel.MEDIUM;
            }
            feature.getProperties().put("congestion", congestion);

        }
        List<SpaceTimePosition> vehicles = simulator.simulationHistory.getAllVehicleAt(timestamp);
        for (SpaceTimePosition vehicle : vehicles) {
            Feature feature = new Feature(); // Gestion problématique UUID d'une requête sur l'autre
            Point point = new Point(new Coordinates(vehicle.getLon(), vehicle.getLat()));
            feature.setGeometry(point);
            features.getFeatures().add(feature);
        }

        //TODO: à tester...
    }

    public void sendFeatures(FeatureCollection features) {
        for (Feature feature : features) {
            if (feature.getGeometry() instanceof LineString) {
                LineString road = (LineString) feature.getGeometry();
                Coordinates last = road.getCoordinates().get(0);

                for (int i = 1; i < road.getCoordinates().size(); i++) { // avoid the first feature
                    Coordinates coordinates = road.getCoordinates().get(i);
                    Position A = simulator.positionManager.addPosition(last.getLongitude(), last.getLongitude());
                    Position B = simulator.positionManager.addPosition(coordinates.getLongitude(), coordinates.getLongitude());
                    simulator.roadManager.addRoadSectionToRoad(A, B, feature.getUuid());
                    last = coordinates;
                }
            }
            //TODO: Prévoir les ronds points et les feux rouges
        }
        simulator.roadManager.closeRoads();
    }

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
