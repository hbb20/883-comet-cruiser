package com.hbb20.mycometcruiser.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hbb20.mycometcruiser.R;
import com.hbb20.mycometcruiser.models.BusLocation;
import com.hbb20.mycometcruiser.models.Driver;
import com.hbb20.mycometcruiser.utils.AppStorageManager;
import com.hbb20.mycometcruiser.utils.CometCruiserUtil;
import com.hbb20.mycometcruiser.utils.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeActivity extends AppCompatActivity {


    private static final int PERMISSION_FOR_LOCATION = 1;
    /** Android Views **/
    TextView tvGreeting;
    RadioGroup rgRoute;
    RadioButton rbFrankford;
    RadioButton rbMeandering;
    RadioButton rbEast;
    RadioButton rbEastExpress;
    Button btnStartTrip;
    Button btnLogout;
    /** Android Views **/

    /**
     * Binds XML views
     * Call this function after setContentView() in onCreate().
     **/
    private void bindViews(){
        tvGreeting = (TextView) findViewById(R.id.tv_greeting);
        rgRoute = (RadioGroup) findViewById(R.id.rg_route);
        rbFrankford = (RadioButton) findViewById(R.id.rb_frankford);
        rbMeandering = (RadioButton) findViewById(R.id.rb_meandering);
        rbEast = (RadioButton) findViewById(R.id.rb_east);
        rbEastExpress = (RadioButton) findViewById(R.id.rb_east_express);
        btnStartTrip = (Button) findViewById(R.id.btn_start_trip);
        btnLogout = (Button) findViewById(R.id.btn_logout);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        bindViews();
        setGreeting();
        setRadioButtonClickListener();
        setLogoutButton();
        btnStartTripClick();
    }

    private void btnStartTripClick() {
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartButtonClicked();
            }
        });
    }

    private void onStartButtonClicked() {
        if(CometCruiserUtil.isDataAdapterOn(this)){
            final ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setMessage("Please wait....");
            progressDialog.show();

            RetrofitHelper.getRetrofitService(this).startTrip(AppStorageManager.getSharedStoredString(this,Driver.KEY_USER_ID),selectedRouteId,-96.7512587,32.9883086).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressDialog.dismiss();
                    if(response.isSuccessful()){
                        Intent intent=new Intent(getBaseContext(),ReportingActivity.class);
                        intent.putExtra(ReportingActivity.EXTRA_ROUTE_ID,selectedRouteId);
                        DriverHomeActivity.this.startActivity(intent);
                    }else {
                        Toast.makeText(DriverHomeActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(DriverHomeActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this,"Please check data connection",Toast.LENGTH_LONG);
        }
    }

    private void setLogoutButton() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppStorageManager.setSharedStoreString(getBaseContext(),Driver.KEY_USER_ID,"");
                finish();
            }
        });
    }

    int selectedRouteId = 0;
    private void setRadioButtonClickListener() {
        rgRoute.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                btnStartTrip.setEnabled(true);
                if(i== R.id.rb_east){
                    selectedRouteId = BusLocation.ROUTE_EAST;
                }else if(i== R.id.rb_east_express){
                    selectedRouteId = BusLocation.ROUTE_EAST_EXPRESS;
                }else if(i == R.id.rb_frankford){
                    selectedRouteId = BusLocation.ROUTE_FRANKFORD;
                }else if(i == R.id.rb_meandering){
                    selectedRouteId = BusLocation.ROUTE_MEANDERING;
                }else{
                    selectedRouteId = 0;
                    btnStartTrip.setEnabled(false);
                }
            }
        });
    }

    private void setGreeting() {
        // TODO: 9/17/17
        tvGreeting.setText("Hellow, "+ AppStorageManager.getSharedStoredString(this, Driver.KEY_NAME));

    }
}
