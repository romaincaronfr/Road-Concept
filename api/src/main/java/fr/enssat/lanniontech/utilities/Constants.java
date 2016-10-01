package fr.enssat.lanniontech.utilities;

public class Constants {

    public static final int HTTP_SERVER_PORT = 8080;
    public static final String SESSION_CURRENT_USER = "me";

    public static final String MONGODB_DATABASE_NAME = "RoadConcept";
    public static final String MONGODB_SERVER_URL = "localhost";
    public static final int MONGODB_SERVER_PORT = 27017;

    // ====================
    // SINGLETON MANAGEMENT
    // ====================

    private Constants() {
        // Prevent instantiation
    }

    private static class InstanceHolder {
        private final static Constants INSTANCE = new Constants();
    }

    public static Constants getInstance() {
        return InstanceHolder.INSTANCE;
    }

}
