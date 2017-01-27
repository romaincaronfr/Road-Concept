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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        handleMaxZoom();
        handleMapTransparency();
        getFeaturesCollection();
        updateMapTransparency(0.5f);
    }

    /**
     * Value must be > 0 and < 1
     * 1 => map background is fully visible
     * 0 => map background is invisible
     */
    private void updateMapTransparency(float value) {
        mTileOverlay.setTransparency(value);
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
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                float maxZoom = 17.0f; // Max zoom to don't see offset bettwen Google Maps and Open Street Map
                if (mMap.getCameraPosition().zoom > maxZoom) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
                    Log.d("DANS LE SACRE IF !!", Float.toString(mMap.getCameraPosition().zoom));
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
                        if (congestion.getCongestionPercentage() < 10) {
                            polyline.setColor(getResources().getColor(R.color.congestion1));
                        } else if (congestion.getCongestionPercentage() < 20) {
                            polyline.setColor(getResources().getColor(R.color.congestion2));
                        } else if (congestion.getCongestionPercentage() < 30) {
                            polyline.setColor(getResources().getColor(R.color.congestion4));
                        } else if (congestion.getCongestionPercentage() < 40) {
                            polyline.setColor(getResources().getColor(R.color.congestion4));
                        } else if (congestion.getCongestionPercentage() < 50) {
                            polyline.setColor(getResources().getColor(R.color.congestion5));
                        } else if (congestion.getCongestionPercentage() < 60) {
                            polyline.setColor(getResources().getColor(R.color.congestion6));
                        } else if (congestion.getCongestionPercentage() < 70) {
                            polyline.setColor(getResources().getColor(R.color.congestion7));
                        } else if (congestion.getCongestionPercentage() < 80) {
                            polyline.setColor(getResources().getColor(R.color.congestion8));
                        } else if (congestion.getCongestionPercentage() < 90) {
                            polyline.setColor(getResources().getColor(R.color.congestion9));
                        } else {
                            polyline.setColor(getResources().getColor(R.color.congestion10));
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
}
