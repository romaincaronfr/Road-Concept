package fr.enssat.lanniontech.injector.osm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.enssat.lanniontech.injector.osm.entities.OSMFile;
import fr.enssat.lanniontech.injector.osm.entities.exceptions.OSMFileParsingException;

import java.io.File;
import java.io.IOException;

/**
 * Created by maelig on 20/09/2016.
 */
public class OSMParser {

    /**
     * Jackson 2 XML Object Mapper
     */
    private static ObjectMapper MAPPER = new XmlMapper();

    /**
     * Unmarshall an OSM XML file
     *
     * @param xml Java file object containing the source XML to process
     * @return a complete OSMFile entity
     * @throws OSMFileParsingException
     */
    public static OSMFile parseFile(File xml) throws OSMFileParsingException {
        try {
            return MAPPER.readValue(xml, OSMFile.class);
        } catch (IOException e) {
            throw new OSMFileParsingException(xml.getName(), e);
        }
    }
}
