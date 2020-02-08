package com.an2t.uploadpicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, Callback<Response<ServiceResponse>> {

    private Apifactory mApi;
    private ServiceAPI serviceAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        serviceAPI = mApi.getServiceAPI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                //serviceAPI.fetchHomeOfferAPI().enqueue(this);
                break;
        }
    }

    @Override
    public void onResponse(Call<Response<ServiceResponse>> call, Response<Response<ServiceResponse>> response) {


        switch (response.code()){
            case 200:


                Toast.makeText(this, "asdasd" + response, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Error Code " + response.code(), Toast.LENGTH_SHORT).show();
                break;

        }

        //Toast.makeText(this, ""+ t.getMessage(), Toast.LENGTH_SHORT).show();
    }





    @Override
    public void onFailure(Call<Response<ServiceResponse>> call, Throwable t) {
        Toast.makeText(this, ""+ t.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
