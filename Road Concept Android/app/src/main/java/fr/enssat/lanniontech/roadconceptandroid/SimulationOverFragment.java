package fr.enssat.lanniontech.roadconceptandroid;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.enssat.lanniontech.roadconceptandroid.AbstractActivities.BaseFragment;

public class SimulationOverFragment extends BaseFragment {


    SwipeRefreshLayout mSwipeRefreshLayout;


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simulation_over, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
