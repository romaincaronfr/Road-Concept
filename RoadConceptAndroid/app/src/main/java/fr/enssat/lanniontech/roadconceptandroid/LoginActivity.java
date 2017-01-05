package fr.enssat.lanniontech.roadconceptandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RoadConceptService;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Login login = new Login();
        login.setEmail("admin@enssat.fr");
        login.setPassword("admin");
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SERVER_URL)
                .build();
        RoadConceptService roadConceptService = retrofit.create(RoadConceptService.class);
        Observable<Response<Login>> responseObservable = roadConceptService.postLogin(login);
        responseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Login>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<Login> response) {
                        Log.d("Response code", String.valueOf(response.code()));
                    }
                });

    }
}
