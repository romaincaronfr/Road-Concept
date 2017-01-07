package fr.enssat.lanniontech.roadconceptandroid.Utilities;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import fr.enssat.lanniontech.roadconceptandroid.Entities.Me;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Romain on 04/01/2017.
 */

public interface RoadConceptUserInterface {

    @POST("login")
    Call<Login> postLogin(@Body Login login);

    @GET("api/me")
    Call<Me> getMe();

}
