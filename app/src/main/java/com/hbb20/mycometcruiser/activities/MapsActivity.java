package com.hbb20.mycometcruiser.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    private static final int PERMISSION_FOR_LOCATION = 1;
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
    CountDownTimer timer;
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
        assignCBClicks();
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("883 Comet Cruiser");
        getLatestPositionsFromServer();
    }

    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FOR_LOCATION);

        } else {
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
            getLatestPositionsFromServer();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FOR_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getLatestPositionsFromServer();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    checkPermissions();
                }
                return;
            }
        }
    }

    private void assignCBClicks() {
        cbMeandering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getLatestPositionsFromServer();
            }
        });
        cbFrankford.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getLatestPositionsFromServer();
            }
        });
        cbEast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getLatestPositionsFromServer();
            }
        });
        cbEastExpress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getLatestPositionsFromServer();
            }
        });
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
        if (Driver.isAlreadyLoggedIn(this)) {
            Intent intent = new Intent(getBaseContext(), DriverHomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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
//        mMap.addMarker(new MarkerOptions().position(utdVillage).title("UTD Village"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(utdVillage, 12.0f));
        placeStationMarkers();
        checkPermissions();
        getLatestPositionsFromServer();
    }

    private void getLatestPositionsFromServer() {
        try {
            timer.cancel();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RetrofitHelper.getRetrofitService(this).getLiveBusLocations(getSelectedRouteIDs()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray jLocations = response.body().getAsJsonArray("locations");
                    Type listType = new TypeToken<ArrayList<BusLocation>>() {
                    }.getType();
                    List<BusLocation> busLocations = new Gson().fromJson(jLocations, listType);
                    refreshMarkers(busLocations);
                } else {
                    startTimer();
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
        if (cbEastExpress.isChecked()) {
            routeIDs += BusLocation.ROUTE_EAST_EXPRESS + ",";
        }

        if (cbEast.isChecked()) {
            routeIDs += BusLocation.ROUTE_EAST + ",";
        }

        if (cbFrankford.isChecked()) {
            routeIDs += BusLocation.ROUTE_FRANKFORD + ",";
        }

        if (cbMeandering.isChecked()) {
            routeIDs += BusLocation.ROUTE_MEANDERING + ",";
        }

        if (routeIDs.equals("[")) {
            return "[1,2,3,4]";
        } else {
            return routeIDs.substring(0, routeIDs.length() - 1) + "]";
        }
    }

    private void refreshMarkers(List<BusLocation> busLocations) {
        mMap.clear();
        this.busLocations = busLocations;
        for (BusLocation location : busLocations) {
            mMap.addMarker(new MarkerOptions().position(location.getLatLng()).title(location.getTitle()).icon(BitmapDescriptorFactory.fromResource(location.getBusResourceId())));
        }
        placeStationMarkers();
        startTimer();
    }

    private void placeStationMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9847522, -96.7506897)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9791404, -96.7524386)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9778638, -96.7656506)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9816651, -96.7684244)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.98416, -96.7727492)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.988035, -96.773536)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9971038, -96.7723462)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9935775, -96.7703298)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9879341, -96.7763365)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9857936, -96.7798168)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9797904, -96.7769712)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9782176, -96.7625161)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9785531, -96.7517798)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9860915, -96.7594903)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9917664, -96.7506743)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9956379, -96.7456768)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9971788, -96.738054)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(33.0081003, -96.7138225)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(33.0082958, -96.7094238)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9973455, -96.7115333)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9978047, -96.7336155)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9970783, -96.7368858)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(32.9874897, -96.754891)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));
        }
    }

    private void startTimer() {
        try {
            timer.cancel();
        } catch (Exception e) {

        }
        timer = new CountDownTimer(500, 500) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                getLatestPositionsFromServer();
            }
        }.start();
    }


}
