package fr.enssat.lanniontech.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.services.MapService;
import fr.enssat.lanniontech.api.services.SimulatorService;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class MapToCoreTest {

    @Test
    public void sendMapToCore() {
        try {
            // Load data
            InputStream source = getClass().getResourceAsStream("/marseille-saint-antoine.json");
            FeatureCollection features = new ObjectMapper().readValue(source, FeatureCollection.class);

            // From OSM adaptation
            new MapService().fromOSMAdaptation(features);

            // Send to core
            new SimulatorService().sendFeatures(features);

        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            Assert.fail();
        }
    }

    private int getNumberOfLineString(FeatureCollection features) {
        int count = 0;
        for (Feature feature : features) {
            if (feature.getGeometry() instanceof LineString) {
                count++;
            }
        }
        return count;
    }

}
