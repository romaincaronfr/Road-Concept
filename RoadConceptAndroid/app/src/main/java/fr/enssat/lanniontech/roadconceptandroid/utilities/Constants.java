package fr.enssat.lanniontech.roadconceptandroid.Utilities;

/**
 * Created by Romain on 08/12/2016.
 */

public class Constants {

    // ====================
    // BASE URL API
    // ====================

    private static final String BASE_URL = "http://roadconcept.4r3.fr";
    private static final String BASE_PORT = "8081";
    public static final String SERVER_URL = BASE_URL+":"+BASE_PORT+"/";

    // ====================
    // SharedPreferences
    // ====================

    public static final String SHARE_PREF_NAME = "fr.enssat.lanniontech.roadconceptandroid_prefs";
    public static final String SHARE_USERNAME = "RoadConcept_Username";
    public static final String SHARE_PASSWORD = "RoadConcept_Password";
    public static final String SHARE_COOKIE = "RoadConcept_Cookie";
    public static final String SHARE_USER_NAME = "RoadConcept_User_Name";
    public static final String SHARE_USER_EMAIL = "RoadConcept_User_Email";

    // ====================
    // SINGLETON MANAGEMENT
    // ====================

    private Constants() {
        // Prevent instantiation
    }
}
