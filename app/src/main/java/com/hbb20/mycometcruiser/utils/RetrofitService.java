package com.hbb20.mycometcruiser.utils;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by hbb20 on 9/16/17.
 */

public interface RetrofitService {

    @GET("todo/api/v1.0/tasks")
    Call<JsonObject> getSample();

}
