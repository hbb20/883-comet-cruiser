package com.hbb20.mycometcruiser.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hbb20.mycometcruiser.R;
import com.hbb20.mycometcruiser.models.BusLocation;
import com.hbb20.mycometcruiser.utils.RetrofitHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("883 Comet Cruiser");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_drive:
                onDriveMenuClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onDriveMenuClicked() {
        Toast.makeText(this, "You will be able to report location shortly......", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_activity_menu, menu);
        return true;
    }

    private void startMoving() {
        for(int i=0; i<100; i++){

            updateLocations();
        }
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
        LatLng utdVillage = new LatLng(32.9925584,-96.7440469);
        mMap.addMarker(new MarkerOptions().position(utdVillage).title("UTD Village"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(utdVillage));
        callServer();
    }

    BusLocation busLocation;
    MarkerOptions markerOptions;
    private void callServer() {
        RetrofitHelper.getRetrofitService(this).getSample().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray jLocations = response.body().getAsJsonArray("location");
                Type listType = new TypeToken<ArrayList<BusLocation>>() {
                }.getType();
                List<BusLocation> busLocations = new Gson().fromJson(jLocations, listType);
                busLocation = busLocations.get(0);
                updateLocations();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    Marker marker;

    private void updateLocations() {
        busLocation.increment();
        if(markerOptions == null){
            markerOptions = new MarkerOptions().position(busLocation.getLatLng()).title("Bus "+busLocation.getId());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp));
            marker = mMap.addMarker(markerOptions);
        }else {
            marker.remove();
            markerOptions.position(busLocation.getLatLng());
            marker = mMap.addMarker(markerOptions);
        }

        //
//        updateLocations();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
