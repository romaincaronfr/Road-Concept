package fr.enssat.lanniontech.roadconceptandroid.Utilities.RetrofitInterfaces;

import java.util.List;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Simulation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Romain on 12/01/2017.
 */

public interface RoadConceptSimulationsInterface {

    @GET("api/maps/{mapID}/simulations")
    Call<List<Simulation>> getSimulationsFor1Map(@Path("mapID") int mapId);
}
