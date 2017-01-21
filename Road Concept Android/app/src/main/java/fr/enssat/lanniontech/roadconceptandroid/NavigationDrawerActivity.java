package fr.enssat.lanniontech.roadconceptandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptUserInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Romain on 07/01/2017.
 */

public abstract class NavigationDrawerActivity extends AuthentActivity implements NavigationView.OnNavigationItemSelectedListener{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView emailUserTextView = (TextView) headerView.findViewById(R.id.textViewEmailUser);
        TextView nameUserTextView = (TextView) headerView.findViewById(R.id.textViewNameUser);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        String strusername = sharedPreferences.getString(Constants.SHARE_USER_NAME, "");
        String stremail = sharedPreferences.getString(Constants.SHARE_USER_EMAIL, "");
        emailUserTextView.setText(stremail);
        nameUserTextView.setText(strusername);
        initMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_maps) {
            if (!(this instanceof HomeActivity)){
                startActivityWithClass(HomeActivity.class);
            }
        } else if (id == R.id.nav_simulations) {

        } else if (id == R.id.nav_logout) {
            confirmLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initMenu(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu navigationViewMenu = navigationView.getMenu();

        if (this instanceof HomeActivity){
            navigationViewMenu.findItem(R.id.nav_maps).setChecked(true);
        } //TODO ajouter la prochaine activité quand elle sera ok
    }

    private void confirmLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout_confirmation_message);
        builder.setTitle(R.string.confirmation);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.yes_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                logout();
            }
        });
        alertDialog.show();
    }

    private void logout(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.logout_in_progress));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RoadConceptUserInterface roadConceptUserInterface = getRetrofitService(RoadConceptUserInterface.class);
        Call<Void> call = roadConceptUserInterface.postLogout();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() || response.code() == 401){
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(Constants.SHARE_USERNAME);
                    editor.remove(Constants.SHARE_PASSWORD);
                    editor.remove(Constants.SHARE_USER_ID);
                    editor.remove(Constants.SHARE_DISPOSITION);
                    editor.remove(Constants.SHARE_COOKIE);
                    editor.remove(Constants.SHARE_USER_EMAIL);
                    editor.remove(Constants.SHARE_USER_NAME);
                    editor.apply();
                    Intent intent = new Intent(NavigationDrawerActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayNetworkErrorDialog();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                displayNetworkErrorDialog();
            }
        });
    }

    protected void startActivityWithClass (Class<?> T){
        Intent intent = new Intent(this,T);
        startActivity(intent);
    }

}
