package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import fr.enssat.lanniontech.roadconceptandroid.Utilities.AddCookiesInterceptor;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ReceivedCookiesInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Romain on 05/01/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Retrofit mRetrofit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new AddCookiesInterceptor(this));
        builder.addInterceptor(new ReceivedCookiesInterceptor(this));
        OkHttpClient okHttpClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SERVER_URL)
                .client(okHttpClient)
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
