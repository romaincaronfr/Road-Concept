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
    public GeoJSONException(final String message) {
        super(message);
    }

    /**
     * Constructs a new GeoJSONException with specified detail message
     * and nested Throwable.
     */
    public GeoJSONException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new GeoJSONException with specified nested Throwable
     * and default message.
     */
    public GeoJSONException(final Throwable cause) {
        super(cause);
    }
}
