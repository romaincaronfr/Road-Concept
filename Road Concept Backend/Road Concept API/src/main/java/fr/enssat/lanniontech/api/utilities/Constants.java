package fr.enssat.lanniontech.api.utilities;

public final class Constants {

    public static final int HTTP_SERVER_PORT = 8080;
    public static final String SESSION_CURRENT_USER = "me";

    // =======
    // MONGODB
    // =======

    public static final String MONGODB_DATABASE_NAME = "RoadConcept";
    //public static final String MONGODB_SERVER_URL = "mongodb";
    public static final String MONGODB_SERVER_URL = "localhost";
    public static final int MONGODB_SERVER_PORT = 27017;

    // ==========
    // POSTGRESQL
    // ==========

    public static final String POSTGRESQL_DATABASE_NAME = "roadconcept";
    public static final String POSTGRESQL_USER_NAME = "roadconcept";
    public static final String POSTGRESQL_USER_PASSWORD = "roadconcept";
    //public static final String POSTGRESQL_SERVER_HOST = "postgresql";
    public static final String POSTGRESQL_SERVER_HOST = "localhost";

    public static final int POSTGRESQL_MAX_CONNECTIONS = 100;

    // ERROR CODES
    // -----------
    public static final String POSTGRESQL_FOREIGN_KEY_VIOLATION = "23503";
    public static final String POSTGRESQL_UNIQUE_VIOLATION = "23505";

    // ====================
    // SINGLETON MANAGEMENT
    // ====================

    private Constants() {
        // Prevent instantiation
    }

}
