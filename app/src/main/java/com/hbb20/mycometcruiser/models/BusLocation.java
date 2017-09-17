package com.hbb20.mycometcruiser.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hbb20.mycometcruiser.R;

/**
 * Created by hbb20 on 9/16/17.
 */

public class BusLocation {

    public static final int ROUTE_FRANKFORD = 1, ROUTE_MEANDERING = 2, ROUTE_EAST = 3, ROUTE_EAST_EXPRESS = 4;

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_rev")
    @Expose
    private String rev;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("route")
    @Expose
    private int route;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoute() {
        return route;
    }

    public void setRoute(int route) {
        this.route = route;
    }

    public LatLng getLatLng(){
        return new LatLng(getLatitude(),getLongitude());
    }

    public void increment(){
        latitude += 0.02;
        longitude += 0.02;
    }

    public String getTitle() {
        switch (getRoute()) {
            case ROUTE_EAST:
                return "East";
            case ROUTE_EAST_EXPRESS:
                return "East Express";
            case ROUTE_FRANKFORD:
                return "Frankford";
            case ROUTE_MEANDERING:
                return "Meandering";
            default:
                return "883 Comet Cruiser";
        }
    }

    public int getBusResourceId(){
        switch (getRoute()) {
            case ROUTE_EAST:
                return R.drawable.ic_directions_bus_black_24dp_bus_east;
            case ROUTE_EAST_EXPRESS:
                return R.drawable.ic_directions_bus_black_24dp_bus_east_express;
            case ROUTE_FRANKFORD:
                return R.drawable.ic_directions_bus_black_24dp_bus_frankford;
            case ROUTE_MEANDERING:
                return R.drawable.ic_directions_bus_black_24dp_bus_meandering;
            default:
                return R.drawable.ic_directions_bus_black_24dp;
        }
    }
}
