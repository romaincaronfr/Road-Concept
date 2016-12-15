package fr.enssat.lanniontech.roadconceptandroid.utilities;

<<<<<<< Updated upstream
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
=======
import android.location.Location;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Entity;
>>>>>>> Stashed changes

/**
 * Created by Romain on 09/12/2016.
 */

public class HttpUtility {

<<<<<<< Updated upstream
    public static String LoginGET (String url){
=======
    private static final String TAG = "HttpUtility";
    private static final String COOKIES_HEADER = "Set-Cookie";

    public static Entity LoginPOST (String url, String email, String pwd){
>>>>>>> Stashed changes
        URL myUrl = null;
        try {
            myUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
<<<<<<< Updated upstream
=======
            urlConnection.setRequestMethod("POST");
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            String test = "{\"email\": \""+email+"\",\"password\": \""+pwd+"\"}";
            writer.write(test);
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            int codeResponse = urlConnection.getResponseCode();
            if (codeResponse != 204){
                return null;
            }
            Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            String saveCookie = "test";
            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    Log.d(TAG,cookie);
                   saveCookie = cookie.split(";")[0];
                    break;
                }
            }
            Log.d(TAG,saveCookie);
            return new Login(saveCookie);
>>>>>>> Stashed changes

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
