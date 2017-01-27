package fr.enssat.lanniontech.roadconceptandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.AuthentActivity;
import fr.enssat.lanniontech.roadconceptandroid.Entities.CongestionResult;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Feature;
import fr.enssat.lanniontech.roadconceptandroid.Entities.FeatureCollection;
import fr.enssat.lanniontech.roadconceptandroid.Entities.InfosMap;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Zone;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptMapInterface;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptSimulationsInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fr.enssat.lanniontech.roadconceptandroid.R.id.map;

public class SimulationVisualisationActivity extends AuthentActivity implements OnMapReadyCallback, OnNeedLoginListener, View.OnClickListener {

    private static final int GET_FEATURES_LIST_REQUEST_CODE = 1004;
    private static final int GET_ZONES_REQUEST_CODE = 1005;
    private static final int GET_CONGESTION_REQUEST_CODE = 1006;

    @BindView(R.id.buttonBackSImu)
    Button mButtonBack;
    @BindView(R.id.buttonNextSimu)
    Button mButtonNext;
    private GoogleMap mMap;
    private String mUuid;
    private int mMapID;
    private int mSamplingRate;
    private int mCurrentTimestamp;
    private float mTransparency;

    private FeatureCollection mFeatureCollection;
    private Map<UUID, Polyline> mPolylines;
    private LatLngBounds.Builder mBoundsBuilder;
    private TileOverlay mTileOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_visualisation);
        mBoundsBuilder = new LatLngBounds.Builder();
        mPolylines = new HashMap<>();
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        setTitle(getSecondInStringFormat(mCurrentTimestamp));
        disableElements();
        mTransparency = 0.5f;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        handleMaxZoom();
        handleMapTransparency();
        getFeaturesCollection();
        updateMapTransparency();
    }

    /**
     * Value must be > 0 and < 1
     * 1 => map background is fully visible
     * 0 => map background is invisible
     */
    private void updateMapTransparency() {
        mTileOverlay.setTransparency(mTransparency);
    }

    private void handleMapTransparency() {
        TileProvider tileProvider = new TileProvider() {
            @Override
            public Tile getTile(int i, int i1, int i2) {
                byte[] bytes = ImageFactory.drawableToBytes(getResources().getDrawable(R.drawable.white_square, null));
                View mapFragment = findViewById(map);
                return new Tile(mapFragment.getWidth(), mapFragment.getHeight(), bytes);
            }
        };

        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.tileProvider(tileProvider);

        mTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
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
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                float maxZoom = 17.0f; // Max zoom to don't see offset bettwen Google Maps and Open Street Map
                if (mMap.getCameraPosition().zoom > maxZoom) {
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

            List<List<Double>> coordinates = feature.getGeometry().getCoordinates();
            for (List<Double> coordinate : coordinates) {
                // can't use "addAll(...) since 'coordinates' are not instance of 'LatLng'
                LatLng point = new LatLng(coordinate.get(1), coordinate.get(0));
                options.add(point);
                mBoundsBuilder.include(point);
                options.color(Color.BLACK);
            }
            Polyline polyline = mMap.addPolyline(options);
            polyline.setZIndex(1000); // Any high value
            mPolylines.put(feature.getProperties().getId(), polyline);
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
                break;
            case GET_ZONES_REQUEST_CODE:
                if (result){
                    getZones();
                } else {
                    goToLogin();
                }
                break;
            case GET_CONGESTION_REQUEST_CODE:
                if (result) {
                    updateCongestion();
                } else {
                    goToLogin();
                }
                break;
            default:
                break;
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
            default:
                break;
        }
    }

    private void getZones(){
        RoadConceptSimulationsInterface roadConceptSimulationsInterface = getRetrofitService(RoadConceptSimulationsInterface.class);
        Call<Simulation> simulationCall = roadConceptSimulationsInterface.getZones(mUuid);
        simulationCall.enqueue(new Callback<Simulation>() {
            @Override
            public void onResponse(Call<Simulation> call, Response<Simulation> response) {
                if (response.isSuccessful()){
                    addZones(response.body().getZones());
                    updateCongestion();
                } else {
                    if (response.code() == 401){
                        refreshLogin(SimulationVisualisationActivity.this,GET_ZONES_REQUEST_CODE);
                    } else {
                        displayNetworkErrorDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<Simulation> call, Throwable t) {
                displayNetworkErrorDialog();
            }
        });
    }

    private void addZones(List<Zone> zones) {
        int index = 1;
        float[] colors = {0.0F, 30.0F, 60.0F, 120.0F, 180.0F, 210.0F, 240.0F, 270.0F, 300.0F, 330.0F};
        for (Zone zone : zones) {
            float color = colors[index-1%colors.length];
            addZoneMarker(UUID.fromString(zone.getLivingFeature()), color, "Zone d'habitation " + index);
            addZoneMarker(UUID.fromString(zone.getWorkingFeature()), color, "Zone de travail " + index);
            index++;
        }
    }

    private void addZoneMarker(UUID featureUUID, float color, String title) {
        Polyline polyline = mPolylines.get(featureUUID);
        mMap.addMarker(new MarkerOptions().position(getPolylineMiddlePoint(polyline)).icon(BitmapDescriptorFactory.defaultMarker(color)).title(title));
    }

    private void updateCongestion(){
        //TODO Récupérer les nouvelles congestions
        RoadConceptSimulationsInterface roadConceptSimulationsInterface = getRetrofitService(RoadConceptSimulationsInterface.class);
        Call<List<CongestionResult>> congestionCall = roadConceptSimulationsInterface.getCongestions(mUuid, mCurrentTimestamp);
        congestionCall.enqueue(new Callback<List<CongestionResult>>() {
            @Override
            public void onResponse(Call<List<CongestionResult>> call, Response<List<CongestionResult>> response) {
                if (response.isSuccessful()) {
                    for (CongestionResult congestion : response.body()) {
                        Polyline polyline = mPolylines.get(UUID.fromString(congestion.getFeatureUUID()));
                        int value = congestion.getCongestionPercentage();
                        int color = polyline.getColor();

                        if (value >= 0 && value <= 10 ) {
                            color = getResources().getColor(R.color.congestion1);
                        } else if (value > 10 && value <= 20) {
                            color = getResources().getColor(R.color.congestion2);
                        } else if (value > 20 && value <= 30) {
                            color = getResources().getColor(R.color.congestion3);
                        } else if (value > 30 && value <= 40) {
                            color = getResources().getColor(R.color.congestion4);
                        } else if (value > 40 && value <= 50) {
                            color = getResources().getColor(R.color.congestion5);
                        } else if (value > 50 && value <= 60) {
                            color = getResources().getColor(R.color.congestion6);
                        } else if (value > 60 && value <= 70) {
                            color = getResources().getColor(R.color.congestion7);
                        } else if (value > 70 && value <= 80) {
                            color = getResources().getColor(R.color.congestion8);
                        } else if (value > 80 && value <= 90) {
                            color = getResources().getColor(R.color.congestion9);
                        } else if (value > 90) {
                            color = getResources().getColor(R.color.congestion10);
                        } else {
                            Log.e(TAG, "ERROR ON CONGESTION VALUE");
                        }
                        if(color != polyline.getColor()){
                            polyline.setColor(color);
                        }
                    }
                    setTitle(getSecondInStringFormat(mCurrentTimestamp));
                } else {
                    if (response.code() == 401) {
                        refreshLogin(SimulationVisualisationActivity.this, GET_CONGESTION_REQUEST_CODE);
                    } else {
                        displayNetworkErrorDialog();
                    }
                }
                enableElements();
            }

            @Override
            public void onFailure(Call<List<CongestionResult>> call, Throwable t) {
                displayNetworkErrorDialog();
                enableElements();
            }
        });

        //A faire (que la requêtes soit ok ou non
    }

    private void disableElements(){
        mButtonBack.setEnabled(false);
        mButtonNext.setEnabled(false);
    }

    private void enableElements(){
        mButtonBack.setEnabled(true);
        mButtonNext.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simulation_visualisation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_timePicker) {
            launchTimePicker();
            return true;
        } else if (id == R.id.action_transparence){
            launchAlertTransparence();
        } else if (id == android.R.id.home){
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchTimePicker(){
        //TODO Timepicker
    }

    private void launchAlertTransparence(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(10);
        seek.setProgress((int) (mTransparency*10));

        popDialog.setTitle("Oppacité");
        popDialog.setView(seek);
        final AlertDialog dialog = popDialog.create();
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progressV, boolean fromUser) {
                mTransparency = (float) progressV/10;
                updateMapTransparency();
            }


            public void onStartTrackingTouch(SeekBar arg0) {
            }


            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        dialog.show();
    }
}
