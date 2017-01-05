package fr.enssat.lanniontech.roadconceptandroid.Utilities;

import fr.enssat.lanniontech.roadconceptandroid.Entities.Login;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Romain on 04/01/2017.
 */

public interface RoadConceptService {

    @POST("login")
    Observable<Response<Login>> postLogin(@Body Login login);

}
