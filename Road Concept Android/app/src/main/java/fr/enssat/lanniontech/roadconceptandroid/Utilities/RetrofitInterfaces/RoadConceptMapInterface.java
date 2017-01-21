package fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces;

import java.util.List;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Map;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Romain on 08/01/2017.
 */

public interface RoadConceptMapInterface {

    @GET("api/maps")
    Call<List<Map>> getMapList();
}
