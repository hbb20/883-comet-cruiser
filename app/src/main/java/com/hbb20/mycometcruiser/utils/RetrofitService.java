package com.hbb20.mycometcruiser.utils;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by hbb20 on 9/16/17.
 */

public interface RetrofitService {

    @GET("get_locations")
    Call<JsonObject> getLiveBusLocations(@Query("route_id") String routeIds);

    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> login(@Field("username") String userName, @Field("password") String password);

    @FormUrlEncoded
    @POST("start")
    Call<JsonObject> startTrip(@Field("driver_id") String userID, @Field("route_id") int route_id, @Field("longitude") double longitude, @Field("latitude") double latitude);

    @FormUrlEncoded
    @PUT("report_location")
    Call<JsonObject> reportLocation(@Field("driver_id") String userID, @Field("route_id") int route_id, @Field("longitude") double longitude, @Field("latitude") double latitude);

    @FormUrlEncoded
    @POST("end")
    Call<JsonObject> endTrip(@Field("driver_id") String userID, @Field("route_id") int route_id);

}
