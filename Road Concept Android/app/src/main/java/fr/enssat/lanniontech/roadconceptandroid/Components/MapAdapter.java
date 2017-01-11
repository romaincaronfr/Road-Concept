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
import fr.enssat.lanniontech.roadconceptandroid.R;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;

/**
 * Created by Romain on 08/01/2017.
 */

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapCardHolder> {

    List<Map> mapList;

    public MapAdapter(List<Map> mapList) {
        this.mapList = mapList;
    }

    @Override
    public MapCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_card_map,parent,false);
        return new MapCardHolder(view);
    }

    @Override
    public void onBindViewHolder(MapCardHolder holder, int position) {
        Map map = mapList.get(position);
        holder.bind(map);
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    public List<Map> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map> mapList) {
        this.mapList = mapList;
        notifyDataSetChanged();
    }

    class MapCardHolder extends RecyclerView.ViewHolder {

        private TextView textViewMapName;
        private ImageView imageViewMap;

        MapCardHolder(View itemView) {
            super(itemView);

            textViewMapName = (TextView) itemView.findViewById(R.id.textCellCardMap);
            imageViewMap = (ImageView) itemView.findViewById(R.id.imageCellCardMap);
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
    }
}
