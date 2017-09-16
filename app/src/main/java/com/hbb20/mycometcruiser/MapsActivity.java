package com.hbb20.mycometcruiser;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Toast.makeText(this, "I'm here now", Toast.LENGTH_SHORT).show();
        callServer();
    }

    private void callServer() {
        RetrofitHelper.getRetrofitService(this).getSample().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray jLocations = response.body().getAsJsonArray("location");
                Type listType = new TypeToken<ArrayList<BusLocation>>() {
                }.getType();
                List<BusLocation> busLocations = new Gson().fromJson(jLocations, listType);
                for (BusLocation busLocation : busLocations) {
                    LatLng busLocationLatLng = new LatLng(busLocation.getLatitude(), busLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(busLocationLatLng).title("Bus "+busLocation.getId()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(busLocationLatLng));
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(MapsActivity.this, "SOmething went wrong", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
