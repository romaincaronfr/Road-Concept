package fr.enssat.lanniontech.roadconceptandroid.utilities;

import android.os.AsyncTask;


import fr.enssat.lanniontech.roadconceptandroid.Entities.Entity;

/**
 * Created by Romain on 09/12/2016.
 */

public class AsyncHTTPGet extends AsyncTask<String, Void, Entity> {

    private CallBackHTTP callBack;

    public AsyncHTTPGet(CallBackHTTP callBack) {
        this.callBack = callBack;
    }

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
    }
}
