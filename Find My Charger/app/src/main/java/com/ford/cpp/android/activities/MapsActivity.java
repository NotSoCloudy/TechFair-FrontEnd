package com.ford.cpp.android.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projone.R;
import com.ford.cpp.android.contract.ChargingStationContract;
import com.ford.cpp.android.contract.ChargingStationList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        List<Marker> markerList = new ArrayList<Marker>();

        List<MarkerOptions> options = new ArrayList<>();
        Intent intent = getIntent();
        ChargingStationList list  = (ChargingStationList)intent.getSerializableExtra(LandingPageActivity.LANDING_EXTRA_MESSAGE);

        List<ChargingStationContract> innerList = list.getStationList();
        float color = 0;
        List<WeightedLatLng> weightedList = new ArrayList<>();

        for (int i =0; i<innerList.size();i++)
        {
            ChargingStationContract contract = innerList.get(i);

            MarkerOptions option = new MarkerOptions();
            option.position(new LatLng(contract.getLatitude(),contract.getLongitude()));
            option.title(contract.getName());
            String snippetText = "";
            if(contract.isStatus())
            {
                color = BitmapDescriptorFactory.HUE_GREEN;
                snippetText="Available";
            }
            else
            {
                if(contract.getChargePct()>50)
                color = BitmapDescriptorFactory.HUE_ORANGE;
                else
                    color = BitmapDescriptorFactory.HUE_RED;

                snippetText="In Use\nCharge Percentage: "+contract.getChargePct()+"\nTime to complete(min):"+contract.getTimeToFullyCharge();
            }
            weightedList.add(new WeightedLatLng(new LatLng(contract.getLatitude(),contract.getLongitude()),(contract.getUsageCounter()+1)*5));
            option.icon(BitmapDescriptorFactory.defaultMarker(color));
            option.snippet(snippetText);
            options.add(option);
            markerList.add(mMap.addMarker(option));

        }

        addHeatMap(weightedList);
      CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(options.get(0).getPosition())
               .zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                //   snippet.setText(marker.getSnippet());
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });

        for(Marker marker:markerList)
        {
            marker.showInfoWindow();
        }
    }

    private void addHeatMap(List<WeightedLatLng> list) {

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(list)
                .radius(38)
                .opacity(0.9)
                .build();
           mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
