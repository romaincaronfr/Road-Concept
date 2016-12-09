package fr.enssat.lanniontech.roadconceptandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;

import fr.enssat.lanniontech.roadconceptandroid.utilities.CallBackHTTPGet;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new CallBackHTTPGet() {
            @Override
            public void onResult() {

            }
        };
    }
}
