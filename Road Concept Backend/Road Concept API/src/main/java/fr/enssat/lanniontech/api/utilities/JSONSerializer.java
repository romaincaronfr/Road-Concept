package fr.enssat.lanniontech.api.utilities;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.exceptions.JSONProcessingException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONSerializer.class);

    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(Include.NON_NULL);
    }

    public static String toJSON(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new JSONProcessingException("Error while converted objet " + object.getClass() + " to JSON string", e);
        }
    }
}
