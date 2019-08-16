package com.ford.cpp.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.projone.R;
import com.ford.cpp.android.contract.ChargingStationContract;
import com.ford.cpp.android.contract.ChargingStationList;
import com.ford.cpp.android.util.IntentMessengerUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private FusedLocationProviderClient fusedLocationClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private IntentMessengerUtil intentUtil = new IntentMessengerUtil();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object

                        System.out.println(" LOCATION ==========>>> <<<<<<<<<<  : " + location.getLongitude()+" LAT :"+location.getLatitude());
                    }
                }
            });
        }
        catch (SecurityException ee){
             Log.d("----SECURITY EXCEPTION",ee.getMessage());
             ee.printStackTrace();}
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
////                .addLocationRequest(createLocationRequest());
////
////        SettingsClient client = LocationServices.getSettingsClient(this);
////        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
////
////        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
////            @Override
////            public void onComplete(Task<LocationSettingsResponse> task) {
////                try {
////                    LocationSettingsResponse response = task.getResult(ApiException.class);
////                    // All location settings are satisfied. The client can initialize location
////                    try {
////                        if (response!=null)
////                        System.out.println(" LOCATION ==========>>> <<<<<<<<<<  : " + fusedLocationClient.getLastLocation().getResult().getLongitude());
////                    }catch (SecurityException ee) {ee.printStackTrace();}
////                    // requests here.
////
////                } catch (ApiException exception) {
////                    switch (exception.getStatusCode()) {
////                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
////                            // Location settings are not satisfied. But could be fixed by showing the
////                            // user a dialog.
////                            try {
////                                // Cast to a resolvable exception.
////                                ResolvableApiException resolvable = (ResolvableApiException) exception;
////                                // Show the dialog by calling startResolutionForResult(),
////                                // and check the result in onActivityResult().
////                                resolvable.startResolutionForResult(
////                                        MainActivity.this,
////                                        REQUEST_CHECK_SETTINGS);
////                            } catch (IntentSender.SendIntentException e) {
////                                // Ignore the error.
////                            } catch (ClassCastException e) {
////                                // Ignore, should be an impossible error.
////                            }
////                            break;
////                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
////                            // Location settings are not satisfied. However, we have no way to fix the
////                            // settings so we won't show the dialog.
////
////                            break;
////                    }
////                }
////            }
////
////        } );

    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {


        sendLocationIntentMessage();
    }

    public void sendLocationIntentMessage() {
        final Intent intent = new Intent(this, MapsActivity.class);
        RequestQueue queue = Volley.newRequestQueue(this);
        //  String url ="http://19.49.54.78:8090/station";
        // String url= "https://findmycharger.cfapps.io/station";
        String url ="http://192.168.225.53:8090/station";

// Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ChargingStationList contractList = new ChargingStationList();
                        List<ChargingStationContract> list = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject json = response.getJSONObject(i);
                                ChargingStationContract station = new ChargingStationContract();
                                station.setId(json.getLong("id"));
                                station.setName(json.getString("name"));

                                station.setLatitude(json.getDouble("latitude"));
                                station.setLongitude(json.getDouble("longitude"));
                                station.setStatus(json.getBoolean("status"));
                                station.setUsageCounter(json.getLong("usageCounter"));
                                list.add(station);

                            }
                            contractList.setStationList(list);
                        }
                        catch(Exception e)
                        {

                        }
                        intent.putExtra(EXTRA_MESSAGE,contractList);
                        startActivity(intent);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //editText.setText(error.getMessage());
              //  TextView view = new TextView()

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
