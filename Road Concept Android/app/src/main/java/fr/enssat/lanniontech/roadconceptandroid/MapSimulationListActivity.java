package fr.enssat.lanniontech.roadconceptandroid;

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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.enssat.lanniontech.roadconceptandroid.Components.MapSimulationsAdapter;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RoadConceptSimulationsInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapSimulationListActivity extends AuthentActivity implements SwipeRefreshLayout.OnRefreshListener, OnNeedLoginListener{

    private static final int GET_SIMULATION_LIST_REQUEST_CODE = 1003;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.backgroundImageView) ImageView mImageView;
    @BindView(R.id.swipeMapSimulationList) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.my_recycler_view) RecyclerView mRecyclerViewSimulationOver;
    @BindView(R.id.textViewDescriptionMap) TextView mTextDescriptionMap;
    @BindView(R.id.textViewNoSimulationOver) TextView mTextViewNoSimulationOver;
    RoadConceptSimulationsInterface roadConceptSimulationInterface;
    MapSimulationsAdapter mMapSimulationsOverAdapter;
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
        List<Simulation> simulationList = new ArrayList<>();
        mMapSimulationsOverAdapter = new MapSimulationsAdapter(simulationList);
        mRecyclerViewSimulationOver.setAdapter(mMapSimulationsOverAdapter);
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
                    mMapSimulationsOverAdapter.setmSimulationList(simulationOverList);
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
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
        }
    }
}
