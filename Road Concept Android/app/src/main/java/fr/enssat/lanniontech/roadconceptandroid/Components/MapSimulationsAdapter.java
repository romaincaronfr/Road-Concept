package fr.enssat.lanniontech.roadconceptandroid.Components;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;

/**
 * Created by Romain on 13/01/2017.
 */

public class MapSimulationsAdapter extends RecyclerView.Adapter<MapSimulationsAdapter.MapSimulationsHolder> {

    private List<Simulation> mSimulationList;

    public MapSimulationsAdapter(List<Simulation> mSimulationList) {
        this.mSimulationList = mSimulationList;
    }

    @Override
    public MapSimulationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MapSimulationsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MapSimulationsHolder extends RecyclerView.ViewHolder {

        public MapSimulationsHolder(View itemView) {
            super(itemView);

        }
    }
}
