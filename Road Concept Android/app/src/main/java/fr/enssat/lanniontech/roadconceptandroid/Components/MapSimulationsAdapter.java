package fr.enssat.lanniontech.roadconceptandroid.Components;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Map;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;
import fr.enssat.lanniontech.roadconceptandroid.R;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_map_simulation_list_card,parent,false);
        return new MapSimulationsHolder(view);
    }

    @Override
    public void onBindViewHolder(MapSimulationsHolder holder, int position) {
        Boolean printSeperator = true;
        if (position == mSimulationList.size()-1){
            Log.d("ADAPTER","met à false");
            printSeperator = false;
        }
        holder.bind(mSimulationList.get(position),printSeperator);
    }

    @Override
    public int getItemCount() {
        return mSimulationList.size();
    }

    public List<Simulation> getmSimulationList() {
        return mSimulationList;
    }

    public void setmSimulationList(List<Simulation> mSimulationList) {
        this.mSimulationList = mSimulationList;
        notifyDataSetChanged();
    }

    class MapSimulationsHolder extends RecyclerView.ViewHolder {

        private TextView textViewSimulationName;
        private TextView textViewSimulationDate;
        private View separatorBar;

        MapSimulationsHolder(View itemView) {
            super(itemView);
            textViewSimulationName = (TextView) itemView.findViewById(R.id.textSimuName);
            textViewSimulationDate = (TextView) itemView.findViewById(R.id.textDateSimu);
            separatorBar = itemView.findViewById(R.id.viewBarSeparatorOver);
        }

        void bind(Simulation simulation,Boolean printSeperator){
            if (!printSeperator){
                separatorBar.setVisibility(View.GONE);
            }
            Log.d("SimulationHolder","bind");
            textViewSimulationName.setText(simulation.getName());
            textViewSimulationDate.setText(simulation.getCreationDate());
        }
    }
}
