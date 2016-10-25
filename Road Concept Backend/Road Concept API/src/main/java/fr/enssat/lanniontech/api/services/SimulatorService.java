package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.Map;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.core.Simulator;

public class SimulatorService extends AbstractService {

    public boolean simulate(Map map) {
        FeatureCollection features = map.getFeatures();

        Simulator simulator = new Simulator();
        for (Feature feature : features) {
            //simulator.roadManager.addRoadSectionToRoad();
        }

        return true;
    }

}
