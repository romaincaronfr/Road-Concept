package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.AuthentActivity;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Coordinates;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Feature;
import fr.enssat.lanniontech.roadconceptandroid.Entities.FeatureCollection;
import fr.enssat.lanniontech.roadconceptandroid.Entities.InfosMap;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptMapInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SimulationVisualisationActivity extends AuthentActivity implements OnMapReadyCallback, OnNeedLoginListener, View.OnClickListener {

    private static final int GET_FEATURES_LIST_REQUEST_CODE = 1004;

    @BindView(R.id.buttonBackSImu)
    Button mButtonBack;
    @BindView(R.id.buttonNextSimu)
    Button mButtonNext;
    private GoogleMap mMap;
    private String mUuid;
    private int mMapID;
    private int mSamplingRate;
    private int mCurrentTimestamp;

    private FeatureCollection mFeatureCollection;
    private Map<UUID, Polyline> mPolylines;
    private LatLngBounds.Builder mBoundsBuilder;
    private TileOverlay mTileOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_visualisation);
        ButterKnife.bind(this);
        mButtonBack.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mUuid = intent.getStringExtra(MapSimulationListActivity.INTENT_UUID_SIMULATION);
        mMapID = intent.getIntExtra(MapSimulationListActivity.INTENT_MAPID_SIMULATION,-1);
        mSamplingRate = intent.getIntExtra(MapSimulationListActivity.INTENT_SAMPLINGRATE_SIMULATION,-1);
        mCurrentTimestamp = intent.getIntExtra(MapSimulationListActivity.INTENT_DEPARTURELIVINGS_SIMULATION,-1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle(getSecondInStringFormat(mCurrentTimestamp));
        disableElements();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        handleMaxZoom();

        getFeaturesCollection();

    }

    private void fitZoom() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 30));
            }
        });
    }

    private void handleMaxZoom() {
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                float maxZoom = 18.0f; // Max zoom to don't see offset bettwen Google Maps and Open Street Map
                if (mMap.getCameraPosition().zoom > 18.0f) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
                }
                Log.d("ZOOM = ", Float.toString(mMap.getCameraPosition().zoom));
            }
        });
    }

    private String getSecondInStringFormat(int seconds){
        int h = (int) Math.floor(seconds / 3600);
        int m = (int) Math.floor((seconds % 3600) / 60);
        int s = (seconds % 3600) % 60;
        String hString = String.valueOf(h);
        String mString = String.valueOf(m);
        String sString = String.valueOf(s);
        hString = h<10 ? '0'+hString : hString;
        mString = m<10 ? '0'+mString : mString;
        sString = s<10 ? '0'+sString : sString;
        return hString+ ":"+mString+":"+sString;
    }

    private void getFeaturesCollection(){
        RoadConceptMapInterface roadConceptMapInterface = getRetrofitService(RoadConceptMapInterface.class);
        Call<InfosMap> infosMapCall = roadConceptMapInterface.getMapFeatures(mMapID);
        infosMapCall.enqueue(new Callback<InfosMap>() {
            @Override
            public void onResponse(Call<InfosMap> call, Response<InfosMap> response) {
                if (response.isSuccessful()){
                    mFeatureCollection = response.body().getFeatureCollection();
                    getZones();

                    drawFeatures();
                    fitZoom();
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
                t.printStackTrace();
                displayNetworkErrorDialog();
            }
        });
    }

    /**
     * Use the FeatureCollection to draw all the mPolylines on the Google Map.
     * Store the coordinates (LatLng) into the mBoundsBuilder
     */
    private void drawFeatures() {
        for (Feature feature : mFeatureCollection) {
            PolylineOptions options = new PolylineOptions();

            List<Coordinates> coordinates = feature.getGeometry().getCoordinates();
            for (Coordinates coordinate : coordinates) {
                // can't use "addAll(...) since 'coordinates' are not instance of 'LatLng'
                LatLng point = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
                options.add(point);
                mBoundsBuilder.include(point);
                options.color(Color.BLACK);
            }
            Polyline polyline = mMap.addPolyline(options);
            polyline.setZIndex(1000); // Any high value
            mPolylines.put(UUID.fromString(feature.getId()), polyline);
        }
    }

    private LatLng getPolylineMiddlePoint(Polyline polyline) {
        return polyline.getPoints().get((polyline.getPoints().size() - 1) / 2);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.buttonBackSImu):
                if (mCurrentTimestamp > 0){
                    mCurrentTimestamp -= mSamplingRate;
                    disableElements();
                    updateCongestion();
                }
                break;
            case (R.id.buttonNextSimu):
                if (mCurrentTimestamp < 86400){
                    mCurrentTimestamp += mSamplingRate;
                    disableElements();
                    updateCongestion();
                }
                break;
        }
    }

    private void getZones(){
        //TODO récupérer les zones et mettre les marques

        //A ajouter dans le response.isSuccessfull
        updateCongestion();
    }

    private void updateCongestion(){
        //TODO Récupérer les nouvelles congestions

        //A faire (que la requêtes soit ok ou non
        enableElements();
    }

    private void disableElements(){
        mButtonBack.setEnabled(false);
        mButtonNext.setEnabled(false);
    }

    private void enableElements(){
        mButtonBack.setEnabled(true);
        mButtonNext.setEnabled(true);
    }
}
