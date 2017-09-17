package com.hbb20.mycometcruiser.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hbb20 on 9/16/17.
 */

public class BusLocation {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("latitude")
    @Expose
    private float latitude;
    @SerializedName("longitude")
    @Expose
    private float longitude;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(getLatitude(),getLongitude());
    }

    public void increment(){
        latitude += 0.02;
        longitude += 0.02;
    }
}
