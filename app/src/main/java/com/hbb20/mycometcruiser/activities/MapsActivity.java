package com.hbb20.mycometcruiser.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.hbb20.mycometcruiser.models.Driver;
import com.hbb20.mycometcruiser.utils.RetrofitHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button btnOpenBottomSheet;
    /**
     * Android Views
     **/
    android.support.design.widget.CoordinatorLayout mainContent;
    CheckBox cbFrankford;
    CheckBox cbEast;
    CheckBox cbMeandering;
    CheckBox cbEastExpress;
    List<BusLocation> busLocations;
    /**
     * Android Views
     **/
    MarkerOptions markerOptions;
    Marker marker;
    private GoogleMap mMap;

    /**
     * Binds XML views
     * Call this function after setContentView() in onCreate().
     **/
    private void bindViews() {
        mainContent = (android.support.design.widget.CoordinatorLayout) findViewById(R.id.main_content);
        cbFrankford = (CheckBox) findViewById(R.id.cb_frankford);
        cbEast = (CheckBox) findViewById(R.id.cb_east);
        cbMeandering = (CheckBox) findViewById(R.id.cb_meandering);
        cbEastExpress = (CheckBox) findViewById(R.id.cb_east_express);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        bindViews();
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
        if(Driver.isAlreadyLoggedIn(this)){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_activity_menu, menu);
        return true;
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
        LatLng utdVillage = new LatLng(32.9851, -96.749427);
        mMap.addMarker(new MarkerOptions().position(utdVillage).title("UTD Village"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(utdVillage, 12.0f));
        callServer();
    }

    private void callServer() {
        RetrofitHelper.getRetrofitService(this).getLiveBusLocations(getSelectedRouteIDs()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray jLocations = response.body().getAsJsonArray("locations");
                    Type listType = new TypeToken<ArrayList<BusLocation>>() {
                    }.getType();
                    List<BusLocation> busLocations = new Gson().fromJson(jLocations, listType);
                    refreshMarkers(busLocations);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private String getSelectedRouteIDs() {
        String routeIDs = "[";
        if(cbEastExpress.isChecked()){
            routeIDs+=BusLocation.ROUTE_EAST_EXPRESS+",";
        }

        if(cbEast.isChecked()){
            routeIDs+=BusLocation.ROUTE_EAST+",";
        }

        if(cbFrankford.isChecked()){
            routeIDs+=BusLocation.ROUTE_FRANKFORD+",";
        }

        if(cbMeandering.isChecked()){
            routeIDs+=BusLocation.ROUTE_MEANDERING+",";
        }

        if(routeIDs.equals("[")){
            return "[1,2,3,4]";
        }else{
            return  routeIDs+"]";
        }
    }

    private void refreshMarkers(List<BusLocation> busLocations) {
        mMap.clear();
        this.busLocations = busLocations;
        for (BusLocation location : busLocations) {
            mMap.addMarker(new MarkerOptions().position(location.getLatLng()).title(location.getTitle()).icon(BitmapDescriptorFactory.fromResource(location.getBusResourceId())));
        }
    }


}
