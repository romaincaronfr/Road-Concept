package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.Map;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;

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

}
