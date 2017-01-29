package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.BaseFragment;

public class SimulationOverFragment extends BaseFragment {


    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerViewSimulationOver;


    public SimulationOverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulation_over, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefreshSimulationOver);
        mRecyclerViewSimulationOver = (RecyclerView) getView().findViewById(R.id.itemsRecyclerViewSimulationOver);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
