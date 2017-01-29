package fr.enssat.lanniontech.roadconceptandroid.AbstractActivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import fr.enssat.lanniontech.roadconceptandroid.LoginActivity;
import fr.enssat.lanniontech.roadconceptandroid.R;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.AddCookiesInterceptor;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ReceivedCookiesInterceptor;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptUserInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Romain on 23/01/2017.
 */

public abstract class BaseFragment extends Fragment {

    private Retrofit mRetrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new AddCookiesInterceptor(getActivity()));
        builder.addInterceptor(new ReceivedCookiesInterceptor(getActivity()));
        OkHttpClient okHttpClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SERVER_URL)
                .client(okHttpClient)
                .build();
    }

    protected void refreshLogin(final OnNeedLoginListener onNeedLoginListener, final int code){
        if (!checkIfUserKeepLogin()){
            onNeedLoginListener.onNeedLogin(code,false);
        } else {
            RoadConceptUserInterface roadConceptUserInterface = getRetrofitService(RoadConceptUserInterface.class);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
            String email = sharedPreferences.getString(Constants.SHARE_USERNAME, "");
            String password = sharedPreferences.getString(Constants.SHARE_PASSWORD, "");
            Call<Login> loginCall = roadConceptUserInterface.postLogin(new Login(email,password));
            loginCall.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if (response.isSuccessful()){
                        onNeedLoginListener.onNeedLogin(code,true);
                    } else {
                        onNeedLoginListener.onNeedLogin(code,false);
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    onNeedLoginListener.onNeedLogin(code,false);
                }
            });
        }
    }

    protected void goToLogin(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    protected <T> T getRetrofitService(Class<T> serviceClass){
        return mRetrofit.create(serviceClass);
    }

    protected void displayNetworkErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.message_no_network);
        builder.setTitle(R.string.sorry);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private boolean checkIfUserKeepLogin(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        String strusername = sharedPreferences.getString(Constants.SHARE_USERNAME, "");
        String strpassword = sharedPreferences.getString(Constants.SHARE_PASSWORD, "");
        if (strusername.equals("") || strpassword.equals("")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Constants.SHARE_USER_EMAIL);
            editor.remove(Constants.SHARE_USER_NAME);
            editor.apply();
            return false;
        } else {
            return true;
        }
    }
}
