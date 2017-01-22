package fr.enssat.lanniontech.roadconceptandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.AuthentActivity;
import fr.enssat.lanniontech.roadconceptandroid.Components.MapSimulationsAdapter;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RecyclerViewClickListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptSimulationsInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapSimulationListActivity extends AuthentActivity implements SwipeRefreshLayout.OnRefreshListener, OnNeedLoginListener, RecyclerViewClickListener{

    private static final int GET_SIMULATION_LIST_REQUEST_CODE = 1003;
    public static final String INTENT_UUID_SIMULATION = "uuid_simulation";
    public static final String INTENT_MAPID_SIMULATION = "mapid_simulation";
    public static final String INTENT_SAMPLINGRATE_SIMULATION = "samplingRate_simulation";
    public static final String INTENT_LIVINGUUID_SIMULATION = "livingFeatureUUID_simulation";
    public static final String INTENT_WORKINGUUID_SIMULATION = "workingFeatureUUID_simulation";
    public static final String INTENT_DEPARTURELIVINGS_SIMULATION = "departureLivingS_simulation";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.backgroundImageView) ImageView mImageView;
    @BindView(R.id.swipeMapSimulationList) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.my_recycler_view) RecyclerView mRecyclerViewSimulationOver;
    @BindView(R.id.textViewDescriptionMap) TextView mTextDescriptionMap;
    @BindView(R.id.textViewNoSimulationOver) TextView mTextViewNoSimulationOver;
    @BindView(R.id.my_recycler_view_in_progress) RecyclerView mRecyclerViewSimulationInProgress;
    @BindView(R.id.textViewNoSimulationInProgress) TextView mTextViewNoSimulationInProgress;
    @BindView(R.id.buttonMoreInfoSimulationOver) Button mButtonMoreInfoSimulationOver;
    @BindView(R.id.buttonMoreInfoSimulationInProgress) Button mButtonMoreInfoSimulationInProgress;
    RoadConceptSimulationsInterface roadConceptSimulationInterface;
    MapSimulationsAdapter mMapSimulationsOverAdapter;
    MapSimulationsAdapter mMapSimulationsInProgressAdapter;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_simulation_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(intent.getStringExtra(HomeActivity.INTENT_MAP_NAME));
        mRecyclerViewSimulationOver.setVisibility(View.GONE);
        mRecyclerViewSimulationInProgress.setVisibility(View.GONE);
        if (intent.getStringExtra(HomeActivity.INTENT_MAP_IMAGE) != null && !Objects.equals(intent.getStringExtra(HomeActivity.INTENT_MAP_IMAGE), "")){
            mImageView.setImageBitmap(ImageFactory.getBitmapWithBase64(intent.getStringExtra(HomeActivity.INTENT_MAP_IMAGE)));
        }
        mId = intent.getIntExtra(HomeActivity.INTENT_MAP_ID,-1);
        String mapDescription = intent.getStringExtra(HomeActivity.INTENT_MAP_DESCRIPTION);
        mTextDescriptionMap.setText(mapDescription);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        roadConceptSimulationInterface = getRetrofitService(RoadConceptSimulationsInterface.class);
        mRecyclerViewSimulationOver.setLayoutManager(new GridLayoutManager(this,1));
        mRecyclerViewSimulationInProgress.setLayoutManager(new GridLayoutManager(this,1));
        List<Simulation> simulationList = new ArrayList<>();
        mMapSimulationsOverAdapter = new MapSimulationsAdapter(simulationList,true,this);
        mMapSimulationsInProgressAdapter = new MapSimulationsAdapter(simulationList,false,this);
        mRecyclerViewSimulationOver.setAdapter(mMapSimulationsOverAdapter);
        mRecyclerViewSimulationInProgress.setAdapter(mMapSimulationsInProgressAdapter);
        mButtonMoreInfoSimulationInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printInformation(false);
            }
        });
        mButtonMoreInfoSimulationOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printInformation(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSimulationList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSimulationList(){
        mSwipeRefreshLayout.setRefreshing(true);
        Call<List<Simulation>> simulationList = roadConceptSimulationInterface.getSimulationsFor1Map(String.valueOf(mId));
        simulationList.enqueue(new Callback<List<Simulation>>() {
            @Override
            public void onResponse(Call<List<Simulation>> call, Response<List<Simulation>> response) {
                if (response.isSuccessful()){
                    List<Simulation> simulationList1 = response.body();
                    List<Simulation> simulationOverList = new ArrayList<>();
                    List<Simulation> simulationInProgressList = new ArrayList<>();
                    for (Simulation simulation: response.body()) {
                        if (simulation.getFinish()){
                            simulationOverList.add(simulation);
                        } else {
                            simulationInProgressList.add(simulation);
                        }
                    }
                    if (simulationOverList.isEmpty()){
                        mTextViewNoSimulationOver.setVisibility(View.VISIBLE);
                        mRecyclerViewSimulationOver.setVisibility(View.GONE);
                    } else {
                        mTextViewNoSimulationOver.setVisibility(View.GONE);
                        mRecyclerViewSimulationOver.setVisibility(View.VISIBLE);
                    }

                    if (simulationInProgressList.isEmpty()){
                        mTextViewNoSimulationInProgress.setVisibility(View.VISIBLE);
                        mRecyclerViewSimulationInProgress.setVisibility(View.GONE);
                    } else {
                        mTextViewNoSimulationInProgress.setVisibility(View.GONE);
                        mRecyclerViewSimulationInProgress.setVisibility(View.VISIBLE);
                    }
                    mMapSimulationsOverAdapter.setmSimulationList(simulationOverList);
                    mMapSimulationsInProgressAdapter.setmSimulationList(simulationInProgressList);
                } else {
                    if (response.code() == 401){
                        Log.d(TAG,"401,try");
                        refreshLogin(MapSimulationListActivity.this,GET_SIMULATION_LIST_REQUEST_CODE);
                    } else {
                        displayNetworkErrorDialog();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Simulation>> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                displayNetworkErrorDialog();
            }
        });
    }

    @Override
    public void onRefresh() {
        getSimulationList();
    }

    @Override
    public void onNeedLogin(int code, boolean result) {
        switch (code){
            case GET_SIMULATION_LIST_REQUEST_CODE:
                if (result) {
                    getSimulationList();
                } else {
                    goToLogin();
                }
        }
    }

    public void printInformation(Boolean info){
        String message = getString(R.string.info_simulation_over);
        if (!info){
            message = getString(R.string.info_simulation_in_progress);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MapSimulationListActivity.this);
        builder.setMessage(message);
        builder.setTitle(R.string.information);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Simulation simulation = mMapSimulationsOverAdapter.getmSimulationList().get(position);
        Intent intent = new Intent(this,SimulationVisualisationActivity.class);
        intent.putExtra(INTENT_UUID_SIMULATION,simulation.getUuid());
        intent.putExtra(INTENT_MAPID_SIMULATION,simulation.getMapID());
        intent.putExtra(INTENT_SAMPLINGRATE_SIMULATION,simulation.getSamplingRate());
        intent.putExtra(INTENT_DEPARTURELIVINGS_SIMULATION,simulation.getDepartureLivingS());
        startActivity(intent);
    }
}
