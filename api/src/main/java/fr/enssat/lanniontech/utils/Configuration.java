package fr.enssat.lanniontech.utils;

/**
 * Created by Romain on 22/09/2016.
 */
public class Configuration {

    public final static int serverPort = 8080;

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