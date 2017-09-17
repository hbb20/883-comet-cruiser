package com.hbb20.mycometcruiser.utils;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hbb20 on 9/16/17.
 */

public interface RetrofitService {

    @GET("get_locations")
    Call<JsonObject> getLiveBusLocations(@Query("route_id") String routeIds);


}
