package fr.enssat.lanniontech.utils;

public class Constants {

    public static final int HTTP_SERVER_PORT = 8080;
    public static final String SESSION_CURRENT_USER = "me";

    private Constants() {
    }

    private static class SingletonHolder {
        private final static Constants instance = new Constants();
    }

    public static Constants getInstance() {
        return SingletonHolder.instance;
    }

}
