package fr.enssat.lanniontech.roadconceptandroid.utilities;

/**
 * Created by Romain on 08/12/2016.
 */

public class Constants {

    // ====================
    // BASE URL API
    // ====================

    private static final String BASE_URL = "http://roadconcept.4r3.fr";
    private static final String BASE_PORT = "8081";
    public static final String SERVER_URL = BASE_URL+BASE_PORT;

    // ====================
    // SINGLETON MANAGEMENT
    // ====================

    private Constants() {
        // Prevent instantiation
    }
}
