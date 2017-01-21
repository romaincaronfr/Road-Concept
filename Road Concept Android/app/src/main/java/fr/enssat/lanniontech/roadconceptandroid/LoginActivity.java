package fr.enssat.lanniontech.roadconceptandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Me;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptUserInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity{


    @BindView(R.id.email_login) EditText mEmailText;
    @BindView(R.id.password_login) EditText mPasswordText;
    @BindView(R.id.btn_login) Button mLoginButton;
    @BindView(R.id.checkBoxStayConnect) CheckBox mLoginCheckbox;
    RoadConceptUserInterface roadConceptUserInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        fillForm();
        roadConceptUserInterface = getRetrofitService(RoadConceptUserInterface.class);
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
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        Call<Login> loginCall = roadConceptUserInterface.postLogin(new Login(email,password));
        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    progressDialog.setMessage(getString(R.string.load_information));
                    loadUserInformations(progressDialog);
                } else {
                    if (response.code() >= 500){
                        displayNetworkErrorDialog();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(R.string.error_login_email_or_password);
                        builder.setTitle(R.string.sorry);
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                displayNetworkErrorDialog();
                progressDialog.dismiss();
            }
        });
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

    private void loadUserInformations(final ProgressDialog progressDialog){
        Call<Me> meCall = roadConceptUserInterface.getMe();
        meCall.enqueue(new Callback<Me>() {
            @Override
            public void onResponse(Call<Me> call, Response<Me> response) {
                if (response.isSuccessful()){
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Me me = response.body();
                    String userName = me.getFirstName()+" "+me.getLastName();
                    String email = me.getEmail();
                    int userId = me.getId();
                    editor.putString(Constants.SHARE_USER_NAME, userName);
                    editor.putString(Constants.SHARE_USER_EMAIL, email);
                    editor.putInt(Constants.SHARE_USER_ID, userId);
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);

                } else {
                    displayNetworkErrorDialog();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Me> call, Throwable t) {
                displayNetworkErrorDialog();
                progressDialog.dismiss();
            }
        });
    }

}