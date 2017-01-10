package fr.enssat.lanniontech.roadconceptandroid.HomeComponents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Map;
import fr.enssat.lanniontech.roadconceptandroid.R;

/**
 * Created by Romain on 08/01/2017.
 */

public class MapAdapter extends RecyclerView.Adapter<MapCardHolder> {

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
}
