package fr.enssat.lanniontech.roadconceptandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Entity;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import fr.enssat.lanniontech.roadconceptandroid.utilities.AsyncHTTPGet;
import fr.enssat.lanniontech.roadconceptandroid.utilities.CallBackHTTP;

import static fr.enssat.lanniontech.roadconceptandroid.utilities.Constants.*;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String[] stringList = new String[3];
        stringList[0] = LOGIN_URL;
        stringList[1] = "admin@enssat.fr";
        stringList[2] = "admin";
        AsyncHTTPGet asyncHTTPGet = new AsyncHTTPGet(new CallBackHTTP() {
            @Override
            public void onResult(Entity s) {
                Login login = (Login) s;
                Log.d(TAG, login.getCookie());
            }
        });
        asyncHTTPGet.execute(stringList);
    }
}
