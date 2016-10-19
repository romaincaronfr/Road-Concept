package fr.enssat.lanniontech.api.exceptions;

public class GeoJSONException extends JSONProcessingException {

    /**
     * Constructs a new GeoJSONException with default message.
     */
    public GeoJSONException() {
    }

    /**
     * Constructs a new GeoJSONException with specified detail message.
     */
    public GeoJSONException(String message) {
        super(message);
    }

    /**
     * Constructs a new GeoJSONException with specified detail message
     * and nested Throwable.
     */
    public GeoJSONException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new GeoJSONException with specified nested Throwable
     * and default message.
     */
    public GeoJSONException(Throwable cause) {
        super(cause);
    }
}
