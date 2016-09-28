package fr.enssat.lanniontech.jsonparser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.exceptions.MapJSONProcessingException;
import fr.enssat.lanniontech.jsonparser.entities.MapJSON;

import java.io.File;
import java.io.IOException;

public class MapJSONParser {

    /**
     * Jackson 2 Object Mapper
     */
    private static ObjectMapper MAPPER = new ObjectMapper();

    public static MapJSON unmarshall(File json) throws MapJSONProcessingException {
        try {
            return MAPPER.readValue(json, MapJSON.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MapJSONProcessingException("file " + json.getName(), e);
        }
    }

    public static MapJSON unmarshall(String json) throws MapJSONProcessingException {
        try {
            return MAPPER.readValue(json, MapJSON.class);
        } catch (IOException e) {
            throw new MapJSONProcessingException("string input", e);
        }
    }

    public static String marshall(MapJSON map) {
        try {
            MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new MapJSONProcessingException("map ID " + Integer.toString(map.getId()), e);
        }
    }
}