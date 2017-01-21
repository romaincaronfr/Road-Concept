package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptUserInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Romain on 07/01/2017.
 */

public abstract class AuthentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIfUserIsInit()){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkIfUserIsInit(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        String strusername = sharedPreferences.getString(Constants.SHARE_USER_EMAIL, "");
        String strpassword = sharedPreferences.getString(Constants.SHARE_USER_NAME, "");
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

    private boolean checkIfUserKeepLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
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

    protected void refreshLogin(final OnNeedLoginListener onNeedLoginListener, final int code){
        if (!checkIfUserKeepLogin()){
            onNeedLoginListener.onNeedLogin(code,false);
        } else {
            RoadConceptUserInterface roadConceptUserInterface = getRetrofitService(RoadConceptUserInterface.class);
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
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
}
