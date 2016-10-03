package fr.enssat.lanniontech.api.utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.exceptions.JSONProcessingException;

public class JSONSerializer {

    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJSON(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JSONProcessingException("Error while converted objet " + object.getClass() + " to JSON string", e);
        }
    }
}
