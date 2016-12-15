package fr.enssat.lanniontech.roadconceptandroid.utilities;

import android.os.AsyncTask;

<<<<<<< Updated upstream
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
=======
import fr.enssat.lanniontech.roadconceptandroid.Entities.Entity;
>>>>>>> Stashed changes

/**
 * Created by Romain on 09/12/2016.
 */

<<<<<<< Updated upstream
public class AsyncHTTPGet extends AsyncTask<String, Void, String> {

    private CallBackHTTPGet callBack;

    public AsyncHTTPGet (CallBackHTTPGet callBackHTTPGet){
        this.callBack = callBackHTTPGet;
=======
public class AsyncHTTPGet extends AsyncTask<String, Void, Entity> {

    private CallBackHTTP callBack;

    public AsyncHTTPGet (CallBackHTTP callBackHTTP){
        this.callBack = callBackHTTP;
>>>>>>> Stashed changes
    }


    @Override
<<<<<<< Updated upstream
    protected String doInBackground(String... stringList) {

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callBack.onResult();
=======
    protected Entity doInBackground(String... stringList) {
        String url = stringList[0];
        String email = stringList[1];
        String pwd = stringList[2];
        return HttpUtility.LoginPOST(url,email,pwd);
    }

    @Override
    protected void onPostExecute(Entity s) {
        super.onPostExecute(s);
        callBack.onResult(s);
>>>>>>> Stashed changes
    }
}
