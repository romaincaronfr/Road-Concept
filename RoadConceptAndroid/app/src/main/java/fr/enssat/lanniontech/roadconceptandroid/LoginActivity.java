package fr.enssat.lanniontech.roadconceptandroid;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class LoginActivity extends BaseActivity{

    private static final String TAG = "LoginActivity";

    @BindView(R.id.email_login) EditText mEmailText;
    @BindView(R.id.password_login) EditText mPasswordText;
    @BindView(R.id.btn_login) Button mLoginButton;
    @BindView(R.id.checkBoxStayConnect) CheckBox mLoginCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Log.d(TAG,getApplicationContext().toString());
        fillForm();
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    saveID();
                    startLogin();
                }
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError(getString(R.string.error_please_valide_msg));
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        if (password.isEmpty()) {
            mPasswordText.setError(getString(R.string.please_valid_password));
            valid = false;
        } else {
            mPasswordText.setError(null);
        }
        return valid;
    }

    private void startLogin(){
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authent_in_progress));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void saveID(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (mLoginCheckbox.isChecked()) {
            String email = mEmailText.getText().toString();
            String password = mPasswordText.getText().toString();
            editor.putString(Constants.SHARE_USERNAME, email);
            editor.putString(Constants.SHARE_PASSWORD, password);
            editor.apply();
        } else {
            editor.remove(Constants.SHARE_USERNAME);
            editor.remove(Constants.SHARE_PASSWORD);
            editor.apply();
        }
    }

    private void fillForm(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        String strusername = sharedPreferences.getString(Constants.SHARE_USERNAME, "");
        String strpassword = sharedPreferences.getString(Constants.SHARE_PASSWORD, "");
        if (!strusername.equals("") && !strpassword.equals("")){
            mEmailText.setText(strusername);
            mPasswordText.setText(strpassword);
            mLoginCheckbox.setChecked(true);
        }
    }

}

/*Login login = new Login();
        login.setEmail("admin@enssat.fr");
        login.setPassword("admin");
        RoadConceptService roadConceptService = getRetrofit().create(RoadConceptService.class);
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

    */