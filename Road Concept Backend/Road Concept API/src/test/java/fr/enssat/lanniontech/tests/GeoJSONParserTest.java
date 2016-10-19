package fr.enssat.lanniontech.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.Map;
import fr.enssat.lanniontech.api.geojson.Feature;
import fr.enssat.lanniontech.api.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.geojson.GeoJsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class GeoJSONParserTest {

    @Test
    public void testSuccessOSM() {
        try {
            InputStream source = getClass().getResourceAsStream("/from-osm-lannion-center.json");

            FeatureCollection object = new ObjectMapper().readValue(source, FeatureCollection.class);
            Feature feature = object.getFeatures().get(0);


            Object tags = feature.getProperties().get("tags");
            System.out.println("tags class -> " + tags.getClass());

            FeatureCollection features = (FeatureCollection) object;
        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSuccessSmallMap() {
        try {
            InputStream source = getClass().getResourceAsStream("/geojson-small-map.json");

            FeatureCollection features = new ObjectMapper().readValue(source, FeatureCollection.class);

        //    System.out.println(features);

        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testAntoine() {
        try {
            InputStream source = getClass().getResourceAsStream("/antoine-example.json");

            Map map = new Map();
            FeatureCollection features = new ObjectMapper().readValue(source, FeatureCollection.class);
            map.setFeatures(features);

       //     for (Feature feature : features) {
       //         System.out.println(feature.getType());
       //     }

        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSuccessSingleFeature() {
        try {
            InputStream source = getClass().getResourceAsStream("/geojson-single-feature.json");

            GeoJsonObject object = new ObjectMapper().readValue(source, GeoJsonObject.class);
            Assert.assertTrue(object instanceof Feature);

            Feature feature = (Feature) object;
            System.out.println(feature);

        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            Assert.fail();
        }
    }
}
