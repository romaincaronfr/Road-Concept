package fr.enssat.lanniontech.roadconceptandroid.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Romain on 05/01/2017.
 */

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;
    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    } // AddCookiesInterceptor()
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARE_PREF_NAME, Context.MODE_PRIVATE);
            HashSet<String> cookies = (HashSet<String>) sharedPreferences.getStringSet(Constants.SHARE_COOKIE, new HashSet<String>());

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
                Log.d("ReceivedCookies",header);
            }

            SharedPreferences.Editor memes = sharedPreferences.edit();
            memes.putStringSet(Constants.SHARE_COOKIE, cookies).apply();
            memes.apply();
        }

        return originalResponse;
    }
}