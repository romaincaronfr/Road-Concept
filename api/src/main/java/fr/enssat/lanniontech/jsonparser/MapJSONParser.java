package fr.enssat.lanniontech.jsonparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.exceptions.JSONProcessingException;
import fr.enssat.lanniontech.jsonparser.entities.MapJSON;

import java.io.File;
import java.io.IOException;

/**
 * JSON Parser for JSON object representing a map. The MAPJson objet is used to represent a map between the API and the appliation front end.
 */
public class MapJSONParser {

    /**
     * Jackson 2 Object Mapper
     */
    private static ObjectMapper MAPPER = new ObjectMapper();

    public static MapJSON unmarshall(File json) throws JSONProcessingException {
        try {
            return MAPPER.readValue(json, MapJSON.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new JSONProcessingException("file " + json.getName(), e);
        }
    }

    public static MapJSON unmarshall(String json) throws JSONProcessingException {
        try {
            return MAPPER.readValue(json, MapJSON.class);
        } catch (IOException e) {
            throw new JSONProcessingException("string input", e);
        }
    }
}