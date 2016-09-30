package fr.enssat.lanniontech.utils;

public class Constants {

    public static final int HTTP_SERVER_PORT = 8080;
    public static final String SESSION_CURRENT_USER = "me";

    public static final String MONGODB_DATABASE_NAME = "RoadConcept";
    public static final String MONGODB_SERVER_URL = "mongodb://localhost:27017";

    private Constants() {
    }

    private static class SingletonHolder {
        private final static Constants instance = new Constants();
    }

    public static Constants getInstance() {
        return SingletonHolder.instance;
    }

}
