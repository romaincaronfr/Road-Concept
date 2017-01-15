package fr.enssat.lanniontech.roadconceptandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;

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
            // Handle the camera action
        } else if (id == R.id.nav_simulations) {

        } else if (id == R.id.nav_logout) {

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

}
