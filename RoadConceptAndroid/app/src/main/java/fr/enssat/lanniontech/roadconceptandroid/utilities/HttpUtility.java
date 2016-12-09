package fr.enssat.lanniontech.roadconceptandroid.utilities;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Romain on 09/12/2016.
 */

public class HttpUtility {

    public static String LoginGET (String url){
        URL myUrl = null;
        try {
            myUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
