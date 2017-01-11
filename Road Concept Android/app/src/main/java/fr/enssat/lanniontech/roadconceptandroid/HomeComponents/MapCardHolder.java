package fr.enssat.lanniontech.roadconceptandroid.HomeComponents;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Map;
import fr.enssat.lanniontech.roadconceptandroid.R;
import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;

/**
 * Created by Romain on 08/01/2017.
 */

public class MapCardHolder extends RecyclerView.ViewHolder {

    private TextView textViewMapName;
    private ImageView imageViewMap;

    public MapCardHolder(View itemView) {
        super(itemView);

        textViewMapName = (TextView) itemView.findViewById(R.id.textCellCardMap);
        imageViewMap = (ImageView) itemView.findViewById(R.id.imageCellCardMap);
    }

    public void bind(Map map){
        textViewMapName.setText(map.getName());
        if (map.getImageURL() != null && !Objects.equals(map.getImageURL(), "")){
            Log.d("MapCardHolder","if ok");
            String base64 = map.getImageURL();
            imageViewMap.setImageBitmap(ImageFactory.getBitmapWithBase64(base64));
        } else {
            imageViewMap.setImageResource(R.drawable.ic_google_maps);
        }
    }
}
