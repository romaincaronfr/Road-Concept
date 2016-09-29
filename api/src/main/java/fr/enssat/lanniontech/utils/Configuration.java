package fr.enssat.lanniontech.utils;

public class Configuration {

    public final static int serverPort = 8080;

    //MongoDB Config params
    public static final String dbName = "RoadConcept";
    public static final String mongoUrl = "mongodb://localhost:27017";

    private Configuration ()
    {}

    private static class SingletonHolder
    {
        private final static Configuration instance = new Configuration();
    }

    public static Configuration getInstance()
    {
        return SingletonHolder.instance;
    }

}
