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
    // SharedPreferences
    // ====================

    public static final String SHARE_PREF_NAME = "fr.enssat.lanniontech.roadconceptandroid_prefs";
    public static final String SHARE_USERNAME = "RoadConcept_Username";
    public static final String SHARE_PASSWORD = "RoadConcept_Password";
    public static final String SHARE_COOKIE = "RoadConcept_Cookie";

    // ====================
    // SINGLETON MANAGEMENT
    // ====================

    private Constants() {
        // Prevent instantiation
    }
}
