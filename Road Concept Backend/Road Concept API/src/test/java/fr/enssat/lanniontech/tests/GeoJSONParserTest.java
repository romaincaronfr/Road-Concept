package fr.enssat.lanniontech.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.GeoJsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class GeoJSONParserTest {

    @Test
    public void testSuccessMap() {
        try {
            // Read a file from the project resource folder
            //     File json = new File("src/test/resources/geosjon-example.json");
            //     Assert.assertNotNull(json);


            InputStream json = getClass().getResourceAsStream("/geojson-example.json");


            GeoJsonObject object = new ObjectMapper().readValue(json, GeoJsonObject.class);
            System.out.println(object.toString());


        } catch (Exception e) {
            // Should not happen
            Assert.fail();
        }
    }
}
