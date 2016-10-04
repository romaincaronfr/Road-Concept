package fr.enssat.lanniontech.tests;

import fr.enssat.lanniontech.api.jsonparser.MapJSONParser;
import fr.enssat.lanniontech.api.jsonparser.entities.Map;
import fr.enssat.lanniontech.api.jsonparser.entities.MapElementType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class MapJSONParserTest {

    @Test
    public void testSuccessMap() {
        try {
            // Read a file from the project resource folder
            File json = new File("src/test/resources/map_all.json");
            Assert.assertNotNull(json);

            Map result = MapJSONParser.unmarshall(json);
            Assert.assertNotNull(result);

            Assert.assertEquals(123, result.getId());
            Assert.assertEquals(3, result.getElements().size());

            Assert.assertEquals(123, result.getElements().get(0).getMapID());
            Assert.assertEquals(12, result.getElements().get(0).getId());

            Assert.assertEquals(2, result.getElements().get(0).getProperties().getType().getJsonID());
            Assert.assertEquals(MapElementType.DOUBLE_ROAD, result.getElements().get(0).getProperties().getType());
            Assert.assertEquals(false, result.getElements().get(0).getProperties().isBridge());
            Assert.assertEquals("Rue avec un nom de président dont personne ne se rappellera dans 100 ans", result.getElements().get(0).getProperties().getName());
            Assert.assertEquals(110, result.getElements().get(0).getProperties().getMaxSpeed().intValue());

            Assert.assertEquals(4, result.getElements().get(0).getGeometry().getCoordinates().size());
            Assert.assertEquals(4.54559326171875, result.getElements().get(0).getGeometry().getCoordinates().get(0).getLatitude(), 0.0001);
            Assert.assertEquals(45.754109791149865, result.getElements().get(0).getGeometry().getCoordinates().get(0).getLongitude(), 0.0001);
            Assert.assertEquals(5.625, result.getElements().get(0).getGeometry().getCoordinates().get(1).getLatitude(), 0.0001);
            Assert.assertEquals(45.71576867700507, result.getElements().get(0).getGeometry().getCoordinates().get(1).getLongitude(), 0.0001);
            Assert.assertEquals(5.614013671875, result.getElements().get(0).getGeometry().getCoordinates().get(2).getLatitude(), 0.0001);
            Assert.assertEquals(45.50057194157223, result.getElements().get(0).getGeometry().getCoordinates().get(2).getLongitude(), 0.0001);
            Assert.assertEquals(4.80926513671875, result.getElements().get(0).getGeometry().getCoordinates().get(3).getLatitude(), 0.0001);
            Assert.assertEquals(45.37144349133922, result.getElements().get(0).getGeometry().getCoordinates().get(3).getLongitude(), 0.0001);

            Assert.assertEquals(123, result.getElements().get(1).getMapID());
            Assert.assertEquals(14, result.getElements().get(1).getId());

            Assert.assertEquals(5, result.getElements().get(1).getProperties().getType().getJsonID());
            Assert.assertEquals(MapElementType.RED_LIGHT, result.getElements().get(1).getProperties().getType());
            Assert.assertEquals(false, result.getElements().get(1).getProperties().isBridge());
            Assert.assertEquals("Feu rouge pour faire chier", result.getElements().get(1).getProperties().getName());
            Assert.assertEquals(40, result.getElements().get(1).getProperties().getRedLightTime().intValue());
            Assert.assertEquals(1, result.getElements().get(1).getGeometry().getCoordinates().size());
            Assert.assertEquals(4.54559326171875, result.getElements().get(1).getGeometry().getCoordinates().get(0).getLatitude(), 0.0001);
            Assert.assertEquals(45.754109791149865, result.getElements().get(1).getGeometry().getCoordinates().get(0).getLongitude(), 0.0001);

            Assert.assertEquals(123, result.getElements().get(2).getMapID());
            Assert.assertEquals(13, result.getElements().get(2).getId());
            Assert.assertEquals(4, result.getElements().get(2).getProperties().getType().getJsonID());
            Assert.assertEquals(MapElementType.ROUNDABOUT, result.getElements().get(2).getProperties().getType());
            Assert.assertEquals(false, result.getElements().get(2).getProperties().isBridge());
            Assert.assertEquals("Magnifique rond poind de la mort", result.getElements().get(2).getProperties().getName());
            Assert.assertEquals(40, result.getElements().get(2).getProperties().getMaxSpeed().intValue());
            Assert.assertEquals(2, result.getElements().get(2).getProperties().getRoundaboutLanes().intValue());

            Assert.assertEquals(14, result.getElements().get(2).getGeometry().getCoordinates().size());
            Assert.assertEquals(6.305146515369415, result.getElements().get(2).getGeometry().getCoordinates().get(0).getLatitude(), 0.0001);
            Assert.assertEquals(45.89396215331486, result.getElements().get(2).getGeometry().getCoordinates().get(0).getLongitude(), 0.0001);

        } catch (Exception e) {
            // Should not happen
            Assert.fail();
        }
    }

    @Test
    public void testSuccessRedLight() {
        try {
            File json = new File("src/test/resources/map_RedLight.json");
            Assert.assertNotNull(json);
            Map result = MapJSONParser.unmarshall(json);
            Assert.assertNotNull(result);

            Assert.assertEquals(5, result.getElements().get(0).getProperties().getType().getJsonID());
            Assert.assertEquals(MapElementType.RED_LIGHT, result.getElements().get(0).getProperties().getType());
            Assert.assertEquals(false, result.getElements().get(0).getProperties().isBridge());
            Assert.assertEquals("Feu rouge pour faire chier", result.getElements().get(0).getProperties().getName());
            Assert.assertEquals(40, result.getElements().get(0).getProperties().getRedLightTime().intValue());
            Assert.assertEquals(1, result.getElements().get(0).getGeometry().getCoordinates().size());
            Assert.assertEquals(4.54559326171875, result.getElements().get(0).getGeometry().getCoordinates().get(0).getLatitude(), 0.0001);
            Assert.assertEquals(45.754109791149865, result.getElements().get(0).getGeometry().getCoordinates().get(0).getLongitude(), 0.0001);
        } catch (Exception e) {
            // Should not happen
            Assert.fail();
        }
    }

    @Test
    public void testSuccessRoad() {
        try {
            File json = new File("src/test/resources/map_road.json");
            Assert.assertNotNull(json);
            Map result = MapJSONParser.unmarshall(json);
            Assert.assertNotNull(result);

            Assert.assertEquals(2, result.getElements().get(0).getProperties().getType().getJsonID());
            Assert.assertEquals(MapElementType.DOUBLE_ROAD, result.getElements().get(0).getProperties().getType());
            Assert.assertEquals(false, result.getElements().get(0).getProperties().isBridge());
            Assert.assertEquals("Rue avec un nom de président dont personne ne se rappellera dans 100 ans", result.getElements().get(0).getProperties().getName());
            Assert.assertEquals(110, result.getElements().get(0).getProperties().getMaxSpeed().intValue());
        } catch (Exception e) {
            // Should not happen
            Assert.fail();
        }
    }

    @Test
    public void testSuccessRoundabout() {
        try {
            File json = new File("src/test/resources/map_roundabout.json");
            Assert.assertNotNull(json);
            Map result = MapJSONParser.unmarshall(json);
            Assert.assertNotNull(result);

            Assert.assertEquals(123, result.getElements().get(0).getMapID());
            Assert.assertEquals(13, result.getElements().get(0).getId());
            Assert.assertEquals(4, result.getElements().get(0).getProperties().getType().getJsonID());
            Assert.assertEquals(MapElementType.ROUNDABOUT, result.getElements().get(0).getProperties().getType());
            Assert.assertEquals(false, result.getElements().get(0).getProperties().isBridge());
            Assert.assertEquals("Magnifique rond poind de la mort", result.getElements().get(0).getProperties().getName());
            Assert.assertEquals(40, result.getElements().get(0).getProperties().getMaxSpeed().intValue());
            Assert.assertEquals(2, result.getElements().get(0).getProperties().getRoundaboutLanes().intValue());
        } catch (Exception e) {
            // Should not happen
            Assert.fail();
        }
    }
}