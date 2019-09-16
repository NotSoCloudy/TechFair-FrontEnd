package com.ford.cpp.android.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projone.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.location.Location.distanceBetween;

public class LandingPageActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    public static final String LANDING_EXTRA_MESSAGE = "com.ford.cpp.android.landing.MESSAGE";
    TextView view;
    Spinner searchOption;
    Spinner searchCriteriaValues;
    double selfLatitude=0;
    double selfLongitude=0;
    boolean boolDistanceSelected;
    long mileRadius=0;
    private Activity thisActivity;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolDistanceSelected=false;

        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchOption = findViewById(R.id.search_option_id);
        searchCriteriaValues = findViewById(R.id.city_list_id);
        if(findViewById(R.id.no_charger_error).getVisibility()==View.VISIBLE)
            findViewById(R.id.no_charger_error).setVisibility(View.INVISIBLE);

        thisActivity = this;
        searchOption.setOnItemSelectedListener(this);
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
                            selfLatitude=location.getLatitude();
                            selfLongitude=location.getLongitude();

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
        Spinner spinnerSearchOptions = findViewById(R.id.search_option_id);
        String valueSearchOptions = (String) spinnerSearchOptions.getSelectedItem();

        if(valueSearchOptions.equalsIgnoreCase("distance"))
        {
            if(!value.equalsIgnoreCase("all"))
            mileRadius=Long.valueOf(value);
            else
                mileRadius=-100;
            value="All";
            boolDistanceSelected=true;
        }


        RequestQueue queue = Volley.newRequestQueue(this);
      //    String url ="http://19.49.55.88:8090/station"+"/"+value;;
            String url= "http://findmycharger.apps.pcf.paltraining.perficient.com/station/"+ value;
       // String url ="http://19.49.54.132:8090/station"+"/"+value;
      //  String url = "http://findmycharger.apps.pcf.paltraining.perficient.com/station"+"/"+value;

// Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        double tempLat=0;
                        double tempLong=0;
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
                                ChargingStationContract station = null;
                                new ChargingStationContract();
                                tempLat=json.getDouble("latitude");
                                tempLong=json.getDouble("longitude");

                                if(boolDistanceSelected && !includeInResult(tempLat,tempLong,mileRadius))
                                continue;
                                else
                                {
                                    station = new ChargingStationContract();
                                    station.setId(json.getLong("chargerId"));
                                    station.setName(json.getString("name"));
                                    station.setLatitude(tempLat);
                                    station.setLongitude(tempLong);
                                    station.setStatus(json.getBoolean("status"));
                                    station.setUsageCounter(json.getLong("usageCounter"));
                                    station.setChargePct(json.getDouble("chargePct"));
                                    station.setCity(json.getString("city"));
                                    station.setVin(json.getString("vin"));
                                    station.setTimeToFullyCharge(json.getDouble("timeToFullyCharge"));

                                    list.add(station);
                                }

                            }
                           // if(list!=null)
                            if(list.size()==0) {
                                createNoSearchDialog();
                                //findViewById(R.id.no_charger_error).setVisibility(View.VISIBLE);
                                return;
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String sp1= String.valueOf(searchOption.getSelectedItem());
        Toast.makeText(this, sp1, Toast.LENGTH_SHORT).show();
        String[] array=null;
        if(sp1.contentEquals("City"))
            array = getResources().getStringArray(R.array.radius_values);
        else
            array = getResources().getStringArray(R.array.distance_values);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,array);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            searchCriteriaValues.setAdapter(dataAdapter);
        }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean includeInResult(double latitude,double longitude,long mileRadius)
    {
        if(mileRadius==-100)
            return true;
        System.out.println("called include in result");
        float[] results = new float[1];
        distanceBetween(selfLatitude, selfLongitude, latitude, longitude, results);
        if(results[0]*0.000621371<=mileRadius)
        return true;
        else return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(findViewById(R.id.no_charger_error).getVisibility()==View.VISIBLE)
            findViewById(R.id.no_charger_error).setVisibility(View.INVISIBLE);

    }

    private void createNoSearchDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
////        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialog, int id) {
////                // User cancelled the dialog
////            }
//        });
// Set other dialog properties

// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Activity getActivity()
    {
        return this;
    }
}
