package fr.enssat.lanniontech.roadconceptandroid.utilities;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Romain on 09/12/2016.
 */

public class AsyncHTTPGet extends AsyncTask<String, Void, String> {

    private CallBackHTTPGet callBack;

    public AsyncHTTPGet (CallBackHTTPGet callBackHTTPGet){
        this.callBack = callBackHTTPGet;
    }


    @Override
    protected String doInBackground(String... stringList) {

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callBack.onResult();
    }
}
