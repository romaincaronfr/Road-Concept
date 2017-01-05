package fr.enssat.lanniontech.roadconceptandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import retrofit2.Retrofit;

/**
 * Created by Romain on 05/01/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Retrofit mRetrofit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new AddCookiesInterceptor());
        okHttpClient.interceptors().add(new ReceivedCookiesInterceptor());
    }
}
