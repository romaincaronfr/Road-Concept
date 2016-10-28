package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.map.Map;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.Map;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.positioning.Position;

import java.io.IOException;
import java.io.InputStream;

public class SimulatorService extends AbstractService {

    public boolean simulate(Map map) {
        FeatureCollection features = map.getFeatures();

        //        Simulator simulator = new Simulator();
        //        for (Feature feature : features) {
        //            if (feature.getGeometry() instanceof LineString) {
        //                LineString road = (LineString) feature.getGeometry();
        //                for (Coordinates coordinates : road.getCoordinates()) {
        //                    Position A = new Position(coordinates.getLongitude(),coordinates.getLatitude());
        //                    Position B = new Position();
        //                    simulator.roadManager.addRoadSectionToRoad(A, B, feature.getUuid());
        //                    // FIXME: Voir avec Antoine si il créé une méthode pour ajouter point à point ou faut il diviser en road sections ?
        //                }
        //
        //
        //            }
        //            //TODO: Prévoir les ronds points et les feux rouges
        //        }

        return true;
    }
    private Simulator simulator = new Simulator();

    public FeatureCollection getFakeSimulationResult(FeatureCollection features) {
        sendFeatures(features);
        //TODO: sendVehicles
        simulator.launchSimulation(3600,0.1);

        simulator.waitForEnd(); // FIXME: ne peu pas être fait ici
        simulator.simulationHistory.getVehiclesIn();

        return null;
    }







    public void sendFeatures(FeatureCollection features) {
        for (Feature feature : features) {
            if (feature.getGeometry() instanceof LineString) {
                LineString road = (LineString) feature.getGeometry();
                Coordinates last = road.getCoordinates().get(0);

                for (int i = 1; i < road.getCoordinates().size(); i++) {
                    Coordinates coordinates = road.getCoordinates().get(i);
                    Position A = simulator.positionManager.addPosition(last.getLongitude(), last.getLongitude());
                    Position B = simulator.positionManager.addPosition(coordinates.getLongitude(), coordinates.getLongitude());
                    simulator.roadManager.addRoadSectionToRoad(A, B, feature.getUuid());
                    last = coordinates;
                }
            }
            //TODO: Prévoir les ronds points et les feux rouges
        }
    }

    public FeatureCollection getFakeSimulationResult() throws IOException {
        InputStream source = getClass().getResourceAsStream("/from-osm-lannion-center.json");

        FeatureCollection features = new ObjectMapper().readValue(source, FeatureCollection.class);
         new MapService().fromOSMAdaptation(features);



        return features;
    }


    public static void main(String[] args) throws IOException {
        SimulatorService service = new SimulatorService();
        service.getFakeSimulationResult();

    }
}
