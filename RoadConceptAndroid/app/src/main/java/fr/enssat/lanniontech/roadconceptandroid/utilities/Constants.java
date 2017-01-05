package fr.enssat.lanniontech.roadconceptandroid.Utilities;

/**
 * Created by Romain on 08/12/2016.
 */

public class Constants {

    // ====================
    // BASE URL API
    // ====================

    private static final String BASE_URL = "http://192.168.1.101";
    private static final String BASE_PORT = "8080";
    public static final String SERVER_URL = BASE_URL+":"+BASE_PORT+"/";

    // ====================
    // SINGLETON MANAGEMENT
    // ====================

    private Constants() {
        // Prevent instantiation
    }
}
