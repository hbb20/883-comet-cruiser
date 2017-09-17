package com.hbb20.mycometcruiser.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class LoginActivity extends AppCompatActivity {


    /** Android Views **/
    android.support.design.widget.TextInputLayout tilUsername;
    android.support.design.widget.TextInputEditText etUsername;
    android.support.design.widget.TextInputLayout tilPassword;
    android.support.design.widget.TextInputEditText etPassword;
    Button btnLogin;
    ProgressBar progressbar;
    /** Android Views **/

    /**
     * Binds XML views
     * Call this function after setContentView() in onCreate().
     **/
    private void bindViews(){
        tilUsername = (android.support.design.widget.TextInputLayout) findViewById(R.id.til_username);
        etUsername = (android.support.design.widget.TextInputEditText) findViewById(R.id.et_username);
        tilPassword = (android.support.design.widget.TextInputLayout) findViewById(R.id.til_password);
        etPassword = (android.support.design.widget.TextInputEditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupAppbar();
        bindViews();
        addEditTextTextwatcher();
        addLoginButtonClick();
    }

    private void setupAppbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Driver Login");
    }

    private void addLoginButtonClick() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClicked();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                onUpButtonPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onUpButtonPressed() {
        onBackPressed();
    }

    private void onLoginButtonClicked() {

        if(isValidForm()){
            String userName = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if(CometCruiserUtil.isDataAdapterOn(this)){
                freezeScreen();
                tilPassword.setError("");
                tilUsername.setError("");
                RetrofitHelper.getRetrofitService(this).login(userName, password).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.isSuccessful()) {
                            Log.d("Test", "onResponse: "+response.body());
                            JsonObject jResponse = response.body();
                            JsonObject jUser = jResponse.getAsJsonObject("user");
                            if(jUser.has("_id")){
                                String userId = jUser.get("_id").getAsString();
                                String userName = jUser.get("name").getAsString();
                                AppStorageManager.setSharedStoreString(getBaseContext(),Driver.KEY_USER_ID,userId);
                                AppStorageManager.setSharedStoreString(getBaseContext(),Driver.KEY_NAME, userName);
                                Intent intent = new Intent(getBaseContext(),DriverHomeActivity.class);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            }else{
                                tilPassword.setError("Please check username / password");
                                unfreezeScreen();
                            }
                        }else{
                            tilPassword.setError("Please check username / password");
                            unfreezeScreen();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        unfreezeScreen();
                        tilPassword.setError("Please try again");
                    }
                });
            }else{
                Toast.makeText(this, "Please, check internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void freezeScreen() {
        etPassword.setEnabled(false);
        etUsername.setEnabled(false);
        btnLogin.setEnabled(false);
        progressbar.setVisibility(View.VISIBLE);
    }

    private void unfreezeScreen() {
        etPassword.setEnabled(true);
        etUsername.setEnabled(true);
        btnLogin.setEnabled(true);
        progressbar.setVisibility(View.GONE);
    }



    private boolean isValidForm() {
        boolean isValidForm = true;
        if(etUsername.getText().toString().length()==0){
            tilUsername.setError("Username, please!");
            isValidForm = false;
        }

        if(etPassword.getText().toString().length()==0){
            tilPassword.setError("Password, please!");
            isValidForm = false;
        }

        return isValidForm;
    }

    private void addEditTextTextwatcher() {
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilPassword.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilUsername.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}
