package com.ford.cpp.android.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ford.cpp.android.contract.ChargingStationContract;
import com.ford.cpp.android.contract.ChargingStationList;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.projone.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LandingPageActivity extends AppCompatActivity {

    public static final String LANDING_EXTRA_MESSAGE = "com.ford.cpp.android.landing.MESSAGE";
    TextView view;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            System.out.println("--------------------    LAT :"+location.getLatitude()+" LONG: "+location.getLongitude());
                        }
                    }
                });

    }

    public void sendMessage(View view) {


        sendLocationIntentMessage();
    }

    public void sendLocationIntentMessage() {
        final Intent intent = new Intent(this, MapsActivity.class);
        Spinner spinner = findViewById(R.id.city_list_id);
        String value = (String) spinner.getSelectedItem();



        RequestQueue queue = Volley.newRequestQueue(this);
          String url ="http://19.49.55.88:8090/station"+"/"+value;;
        // String url= "https://findmycharger.cfapps.io/station";
       // String url ="http://19.49.54.132:8090/station"+"/"+value;
      //  String url = "http://findmycharger.apps.pcf.paltraining.perficient.com/station"+"/"+value;

// Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response== null || response.length()==0)
                        {
                            findViewById(R.id.no_charger_error).setVisibility(View.VISIBLE);
                            return;
                        }
                        ChargingStationList contractList = new ChargingStationList();
                        List<ChargingStationContract> list = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject json = response.getJSONObject(i);
                                ChargingStationContract station = new ChargingStationContract();

                                station.setId(json.getLong("chargerId"));
                                station.setName(json.getString("name"));
                                station.setLatitude(json.getDouble("latitude"));
                                station.setLongitude(json.getDouble("longitude"));
                                station.setStatus(json.getBoolean("status"));
                                station.setUsageCounter(json.getLong("usageCounter"));
                                station.setChargePct(json.getDouble("chargePct"));
                                station.setCity(json.getString("city"));
                                station.setVin(json.getString("vin"));

                               list.add(station);

                            }
                            contractList.setStationList(list);
                        }
                        catch(Exception e)
                        {

                        }
                        intent.putExtra(LANDING_EXTRA_MESSAGE,contractList);
                        startActivity(intent);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //editText.setText(error.getMessage());
                //  TextView view = new TextView()
                findViewById(R.id.no_charger_error).setVisibility(View.VISIBLE);

            }
        });

        queue.add(stringRequest);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

}
