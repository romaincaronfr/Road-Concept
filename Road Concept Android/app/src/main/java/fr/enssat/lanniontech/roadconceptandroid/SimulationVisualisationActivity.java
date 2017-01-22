package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.AuthentActivity;
import fr.enssat.lanniontech.roadconceptandroid.Entities.FeatureCollection;
import fr.enssat.lanniontech.roadconceptandroid.Entities.InfosMap;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptMapInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimulationVisualisationActivity extends AuthentActivity implements OnMapReadyCallback, OnNeedLoginListener {

    private static final int GET_FEATURES_LIST_REQUEST_CODE = 1004;

    private GoogleMap mMap;
    private String mUuid;
    private int mMapID;
    private int mSamplingRate;
    private String mLivingFeatureUUID;
    private String mWorkingFeatureUUID;
    private int mDepartureLivingS;
    private FeatureCollection mFeatureCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_visualisation);
        Intent intent = getIntent();
        mUuid = intent.getStringExtra(MapSimulationListActivity.INTENT_UUID_SIMULATION);
        mMapID = intent.getIntExtra(MapSimulationListActivity.INTENT_MAPID_SIMULATION,-1);
        mSamplingRate = intent.getIntExtra(MapSimulationListActivity.INTENT_SAMPLINGRATE_SIMULATION,-1);
        mLivingFeatureUUID = intent.getStringExtra(MapSimulationListActivity.INTENT_LIVINGUUID_SIMULATION);
        mWorkingFeatureUUID = intent.getStringExtra(MapSimulationListActivity.INTENT_WORKINGUUID_SIMULATION);
        mDepartureLivingS = intent.getIntExtra(MapSimulationListActivity.INTENT_DEPARTURELIVINGS_SIMULATION,-1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle(mDepartureLivingS);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private String getSecondInStringFormat(int seconds){
        int h = (int) Math.floor(seconds / 3600);
        int m = (int) Math.floor((seconds % 3600) / 60);
        int s = (seconds % 3600) % 60;
        h = h < 10 ? '0' + h : h;
        m = m < 10 ? '0' + m : m;
        s = s < 10 ? '0' + s : s;
        String time = String.valueOf(h + ':' + m + ':' + s);
        return time;
    }

    private void getFeaturesCollection(){
        RoadConceptMapInterface roadConceptMapInterface = getRetrofitService(RoadConceptMapInterface.class);
        Call<InfosMap> infosMapCall = roadConceptMapInterface.getMapFeatures(String.valueOf(mMapID));
        infosMapCall.enqueue(new Callback<InfosMap>() {
            @Override
            public void onResponse(Call<InfosMap> call, Response<InfosMap> response) {
                if (response.isSuccessful()){
                    mFeatureCollection = response.body().getFeatureCollection();
                    //TODO draw les features
                } else {
                    if (response.code() == 401){
                        refreshLogin(SimulationVisualisationActivity.this, GET_FEATURES_LIST_REQUEST_CODE);
                    } else {
                        displayNetworkErrorDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<InfosMap> call, Throwable t) {
                displayNetworkErrorDialog();
            }
        });
    }

    @Override
    public void onNeedLogin(int code, boolean result) {
        switch (code){
            case GET_FEATURES_LIST_REQUEST_CODE:
                if (result) {
                    getFeaturesCollection();
                } else {
                    goToLogin();
                }
        }
    }
}
