package fr.enssat.lanniontech.roadconceptandroid.Components;

import android.content.Context;
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
import fr.enssat.lanniontech.roadconceptandroid.R;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.RecyclerViewClickListener;

/**
 * Created by Romain on 08/01/2017.
 */

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapCardHolder> {

    private List<Map> mMapList;
    private RecyclerViewClickListener mRecyclerViewClickListener;

    public MapAdapter(List<Map> mMapList, RecyclerViewClickListener itemListener) {
        this.mMapList = mMapList;
        this.mRecyclerViewClickListener = itemListener;
    }

    @Override
    public MapCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_card_map,parent,false);
        return new MapCardHolder(view);
    }

    @Override
    public void onBindViewHolder(MapCardHolder holder, int position) {
        Map map = mMapList.get(position);
        holder.bind(map);
    }

    @Override
    public int getItemCount() {
        return mMapList.size();
    }

    public List<Map> getmMapList() {
        return mMapList;
    }

    public void setmMapList(List<Map> mMapList) {
        this.mMapList = mMapList;
        notifyDataSetChanged();
    }

    class MapCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textViewMapName;
        private ImageView imageViewMap;
        private Map mMap;

        MapCardHolder(View itemView) {
            super(itemView);
            textViewMapName = (TextView) itemView.findViewById(R.id.textCellCardMap);
            imageViewMap = (ImageView) itemView.findViewById(R.id.imageCellCardMap);
            itemView.setOnClickListener(this);
        }

        void bind(Map map){
            textViewMapName.setText(map.getName());
            if (map.getImageURL() != null && !Objects.equals(map.getImageURL(), "")){
                Log.d("MapCardHolder","if ok");
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
