package fr.enssat.lanniontech.roadconceptandroid.Utilities;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Romain on 05/01/2017.
 */

public class AddCookiesInterceptor implements Interceptor {
    // We're storing our stuff in a database made just for cookies called PREF_COOKIES.
    // I reccomend you do this, and don't change this default value.
    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> preferences = (HashSet<String>) context.getSharedPreferences(Constants.SHARE_PREF_NAME, Context.MODE_PRIVATE).getStringSet(Constants.SHARE_COOKIE, new HashSet<String>());
        Log.d("AddCookies",preferences.toString());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.d("AddCookies",cookie);
        }

        return chain.proceed(builder.build());
    }
}