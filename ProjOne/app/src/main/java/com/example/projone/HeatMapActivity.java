package com.example.projone;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ford.cpp.android.contract.ChargingStationContract;
import com.ford.cpp.android.contract.ChargingStationHeatMapContract;
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

public class HeatMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);
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
        //   TextView view = new TextView();

        List<MarkerOptions> options = new ArrayList<>();
        Intent intent = getIntent();
        ChargingStationList list  = (ChargingStationList)intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE_HEATMAP);
        List<ChargingStationHeatMapContract> innerList = list.getHeatMapStationList();
        float color = 0;

        List<WeightedLatLng> weightedList = new ArrayList<>();

        for (int i =0; i<innerList.size();i++)
        {
            ChargingStationHeatMapContract contract = innerList.get(i);

           weightedList.add(new WeightedLatLng(new LatLng(contract.getLatitude(),contract.getLongitude()),contract.getWeight()));
        }

        addHeatMap(weightedList,innerList);
//
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(options.get(0).getPosition())
//                .zoom(12).build();
////        //Zoom in and animate the camera.
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(allenpark));
    }

    private void addHeatMap(List<WeightedLatLng> list, List<ChargingStationHeatMapContract> contractList) {


        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(list)
                .radius(38)
                .opacity(0.9)
                .build();
      //  mMap.
        // Add a tile overlay to the map, using the heat map tile provider.
        Log.d("HEATMAP --->>>>  ",""+list.get(0).getPoint().x+"==="+list.get(0).getPoint().y);
    // Log.out.println(" LAT LONG FOR ZOOM ----->>>>>   :"+list.get(0).getPoint().x,list.get(0).getPoint().y);
     CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(contractList.get(0).getLatitude(),contractList.get(0).getLongitude()))
                .zoom(12)
             .build();
////        //Zoom in and animate the camera.
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

      //  mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
