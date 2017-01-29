package fr.enssat.lanniontech.roadconceptandroid.Components;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Map;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;
import fr.enssat.lanniontech.roadconceptandroid.R;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RecyclerViewClickListener;

/**
 * Created by Romain on 29/01/2017 for Road Concept Android project.
 */

public class SimulationAdapter extends RecyclerView.Adapter<SimulationAdapter.MapSimulationsHolder> {

    private List<Simulation> mSimulationList;
    private Boolean mPutListener;
    private RecyclerViewClickListener mRecyclerViewClickListener;

    public SimulationAdapter(List<Simulation> mSimulationList, Boolean putListener, RecyclerViewClickListener itemListener) {
        this.mSimulationList = mSimulationList;
        this.mPutListener = putListener;
        this.mRecyclerViewClickListener = itemListener;
    }

    @Override
    public MapSimulationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_card_simulation,parent,false);
        return new SimulationAdapter.MapSimulationsHolder(view,mPutListener);
    }

    @Override
    public void onBindViewHolder(MapSimulationsHolder holder, int position) {
        holder.bind(mSimulationList.get(position));
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

    class MapSimulationsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textViewSimulationName;
        private TextView textViewSimulationDate;
        private TextView textMapName;
        private ImageView imageViewMap;

        MapSimulationsHolder(View itemView, Boolean putListener) {
            super(itemView);
            textViewSimulationName = (TextView) itemView.findViewById(R.id.textCellCardNameSimulation);
            textViewSimulationDate = (TextView) itemView.findViewById(R.id.textCellCardDateSimulation);
            textMapName = (TextView) itemView.findViewById(R.id.textCellCardMapSimulation);
            imageViewMap = (ImageView) itemView.findViewById(R.id.imageCellCardSimulation);
            if (putListener) {
                itemView.setOnClickListener(this);
            }
        }

        void bind(Simulation simulation){
            Log.d("SimulationHolder","bind");
            textViewSimulationName.setText(simulation.getName());
            textViewSimulationDate.setText(simulation.getCreationDate());
            Map map = simulation.getMap();
            textMapName.setText(map.getName());
            if (map.getImageURL() != null && !Objects.equals(map.getImageURL(), "")){
                imageViewMap.setImageBitmap(ImageFactory.getBitmapWithBase64(map.getImageURL()));
            } else {
                imageViewMap.setImageResource(R.drawable.ic_google_maps);
            }
        }

        @Override
        public void onClick(View v) {
            mRecyclerViewClickListener.recyclerViewListClicked(v,getLayoutPosition());
        }
    }
}
