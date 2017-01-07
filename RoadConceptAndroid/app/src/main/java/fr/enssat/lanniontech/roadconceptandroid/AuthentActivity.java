package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;

/**
 * Created by Romain on 07/01/2017.
 */

public abstract class AuthentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIfUserKeepLogin()){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkIfUserKeepLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        String strusername = sharedPreferences.getString(Constants.SHARE_USERNAME, "");
        String strpassword = sharedPreferences.getString(Constants.SHARE_PASSWORD, "");
        if (strusername.equals("") || !strpassword.equals("")){
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
