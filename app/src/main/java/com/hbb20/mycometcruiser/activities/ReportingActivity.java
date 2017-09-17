package com.hbb20.mycometcruiser.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hbb20.mycometcruiser.R;
import com.hbb20.mycometcruiser.models.Driver;
import com.hbb20.mycometcruiser.utils.AppStorageManager;
import com.hbb20.mycometcruiser.utils.CometCruiserUtil;
import com.hbb20.mycometcruiser.utils.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportingActivity extends AppCompatActivity implements LocationListener {
    private static final int PERMISSION_FOR_LOCATION = 2;
    public static String EXTRA_ROUTE_ID = "location";
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    protected String latitude, longitude;
    Button btnEndTrip;
    protected boolean gps_enabled, network_enabled;
    TextView txtLat;
    String lat;
    String provider;
    String userID;
    int routeID;

    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FOR_LOCATION);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting);
        txtLat = (TextView) findViewById(R.id.textview1);
        btnEndTrip = (Button) findViewById(R.id.btn_end_trip);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnEndTripClickListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FOR_LOCATION);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }


        routeID = getIntent().getIntExtra(EXTRA_ROUTE_ID,1);
        userID = AppStorageManager.getSharedStoredString(this, Driver.KEY_USER_ID);
    }

    private void btnEndTripClickListener() {
        btnEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTrip();
            }
        });
    }

    private void endTrip() {
        if(CometCruiserUtil.isDataAdapterOn(ReportingActivity.this)) {
            final ProgressDialog progressDialog = new ProgressDialog(ReportingActivity.this);
            progressDialog.setMessage("Please wait...");
            RetrofitHelper.getRetrofitService(ReportingActivity.this).endTrip(userID,routeID).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressDialog.dismiss();
                    if(response.isSuccessful()){
                        finish();
                    }else{
                        Toast.makeText(ReportingActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(ReportingActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(ReportingActivity.this,"Please connect to the internet.",Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(ReportingActivity.this).create();
        alertDialog.setTitle("End Trip?");
        alertDialog.setMessage("Do you want to end this trip?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes, end trip",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        endTrip();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        txtLat = (TextView) findViewById(R.id.textview1);
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        RetrofitHelper.getRetrofitService(ReportingActivity.this).reportLocation(userID,routeID, location.getLongitude(), location.getLatitude()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FOR_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Permission Required!").setMessage("This app requires your location service to work.").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ReportingActivity.this.checkPermissions();
                        }
                    })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return;
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
}