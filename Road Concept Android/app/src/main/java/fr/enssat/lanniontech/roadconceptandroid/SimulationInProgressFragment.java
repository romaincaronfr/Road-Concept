package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.BaseFragment;
import fr.enssat.lanniontech.roadconceptandroid.Components.SimulationAdapter;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.Constants;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.OnNeedLoginListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RecyclerViewClickListener;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces.RoadConceptSimulationsInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static fr.enssat.lanniontech.roadconceptandroid.MapSimulationListActivity.INTENT_DEPARTURELIVINGS_SIMULATION;
import static fr.enssat.lanniontech.roadconceptandroid.MapSimulationListActivity.INTENT_MAPID_SIMULATION;
import static fr.enssat.lanniontech.roadconceptandroid.MapSimulationListActivity.INTENT_SAMPLINGRATE_SIMULATION;
import static fr.enssat.lanniontech.roadconceptandroid.MapSimulationListActivity.INTENT_UUID_SIMULATION;

/**
 * Created by Romain on 29/01/2017 for Road Concept Android project.
 */

public class SimulationInProgressFragment extends BaseFragment implements RecyclerViewClickListener, OnNeedLoginListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int GET_SIMULATION_LIST_REQUEST_CODE = 1003;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerViewSimulationOver;
    SimulationAdapter mMapSimulationsOverAdapter;


    public SimulationInProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulation_over, container, false);
        return view;
    }

    private void updateList(){
        mSwipeRefreshLayout.setRefreshing(true);
        RoadConceptSimulationsInterface roadConceptSimulationsInterface = getRetrofitService(RoadConceptSimulationsInterface.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARE_PREF_NAME,MODE_PRIVATE);
        int id = sharedPreferences.getInt(Constants.SHARE_USER_ID,-1);
        Call<List<Simulation>> listCall = roadConceptSimulationsInterface.getSimulationUserInProgress(id);
        listCall.enqueue(new Callback<List<Simulation>>() {
            @Override
            public void onResponse(Call<List<Simulation>> call, Response<List<Simulation>> response) {
                if (response.isSuccessful()){
                    mMapSimulationsOverAdapter.setmSimulationList(response.body());
                } else {
                    if (response.code() == 401){
                        refreshLogin(SimulationInProgressFragment.this,GET_SIMULATION_LIST_REQUEST_CODE);
                    } else {
                        displayNetworkErrorDialog();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Simulation>> call, Throwable t) {
                displayNetworkErrorDialog();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefreshSimulationOver);
        mRecyclerViewSimulationOver = (RecyclerView) getView().findViewById(R.id.itemsRecyclerViewSimulationOver);
        List<Simulation> simulationList = new ArrayList<>();
        mMapSimulationsOverAdapter = new SimulationAdapter(simulationList,false,this);
        mRecyclerViewSimulationOver.setLayoutManager(new GridLayoutManager(getContext(),1));
        mRecyclerViewSimulationOver.setAdapter(mMapSimulationsOverAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        //Nothing to do
    }

    @Override
    public void onNeedLogin(int code, boolean result) {
        switch (code){
            case GET_SIMULATION_LIST_REQUEST_CODE:
                if (result) {
                    updateList();
                } else {
                    goToLogin();
                }
        }
    }

    @Override
    public void onRefresh() {
        updateList();
    }
}
