package fr.enssat.lanniontech.tests.injector.osm;

import fr.enssat.lanniontech.injector.osm.OSMParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by maelig on 20/09/2016.
 */
public class OSMParserTest {

    @Test
    public void executeSuccess(){
        try {
            // Read a file from the project resource folder
            File osmFile = new File("src/test/resources/map.osm");
            Assert.assertNotNull(osmFile);

            OSMParser.parseFile(osmFile);

        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            Assert.fail();
        }
    }
}
