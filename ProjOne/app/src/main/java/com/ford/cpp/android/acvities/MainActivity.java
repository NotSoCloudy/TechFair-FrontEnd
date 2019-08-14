package com.ford.cpp.android.acvities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.projone.R;
import com.ford.cpp.android.contract.ChargingStationContract;
import com.ford.cpp.android.contract.ChargingStationHeatMapContract;
import com.ford.cpp.android.contract.ChargingStationList;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String EXTRA_MESSAGE_HEATMAP = "com.example.myfirstapp.MESSAGE_HEATMAP";
    private FusedLocationProviderClient fusedLocationClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MainActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            break;
                    }
                }
            }

        } );

//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(),
//                        // and check the result in onActivityResult().
//                        ResolvableApiException resolvable = (ResolvableApiException) e;
//                        resolvable.startResolutionForResult(MainActivity.this,
//                                REQUEST_CHECK_SETTINGS);
//                    } catch (IntentSender.SendIntentException sendEx) {
//                        // Ignore the error.
//                    }
//                }
//            }
//        });



    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {



        final Intent intent = new Intent(this, MapsActivity.class);
      //  final EditText editText = (EditText) findViewById(R.id.editText);
      //  String message = editText.getText().toString();
       // intent.putExtra(EXTRA_MESSAGE, message);
     //   startActivity(intent);

        // Instantiate the RequestQueue.
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
                        ChargingStationContract station = new ChargingStationContract();
                        // Display the first 500 characters of the response string.
                       // textView.setText("Response is: "+ response.substring(0,500));
                      //  String message = editText.getText().toString();
                        intent.putExtra(EXTRA_MESSAGE,contractList);
                        startActivity(intent);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //editText.setText(error.getMessage());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void sendMessageHeatMap(View view) {


        final Intent intent = new Intent(this, HeatMapActivity.class);
      //  final EditText editText = (EditText) findViewById(R.id.editText);
      //  String message = editText.getText().toString();
        // intent.putExtra(EXTRA_MESSAGE, message);
        //   startActivity(intent);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        //  String url ="http://19.49.54.78:8090/station";
        String url= "https://findmycharger.cfapps.io/station";
        // String url ="http://192.168.0.14:9000/location";

         JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ChargingStationList contractList = new ChargingStationList();
                        List<ChargingStationHeatMapContract> list = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject json = response.getJSONObject(i);
                                ChargingStationHeatMapContract station = new ChargingStationHeatMapContract();
                                station.setId(json.getLong("id"));
                                station.setName(json.getString("name"));

                                station.setLatitude(json.getDouble("latitude"));
                                station.setLongitude(json.getDouble("longitude"));
                                station.setStatus(json.getBoolean("status"));
                              //  station.setUsage
                                list.add(station);
                                station.setWeight((i+1)*8);

                            }
                            contractList.setHeatMapStationList(list);
                        }
                        catch(Exception e)
                        {

                        }
                        ChargingStationContract station = new ChargingStationContract();
                        // Display the first 500 characters of the response string.
                        // textView.setText("Response is: "+ response.substring(0,500));
                       // String message = editText.getText().toString();
                        intent.putExtra(EXTRA_MESSAGE_HEATMAP,contractList);
                        startActivity(intent);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // editText.setText(error.getMessage());
            }
        });

// Add the request to the RequestQueue.
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
