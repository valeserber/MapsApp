package com.example.valeriaserber.mapsapp;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback{

    private static final double HOUSE_LATITUDE = -34.600977;
    private static final double HOUSE_LONGITUDE = -58.4358383;
    private static final double WOLOX_LATITUDE = -34.5810711;
    private static final double WOLOX_LONGITUDE = -58.4243192;
    private static final double ITBA_LATITUDE = -34.602919;
    private static final double ITBA_LONGITUDE = -58.367996;

    private static final int ZOOM = 13;
    private static final int MORE_ZOOM = 14;

    private MapFragment mMapFragment;
    private TextView mTitleTextView;
    private GoogleMap mMap;

    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.ORIGIN_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.ORIGIN_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }

    public void handleGetDirectionsResult(ArrayList directionPoints)
    {
        Polyline newPolyline;
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.BLUE);
        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add((LatLng)directionPoints.get(i));
        }
        newPolyline = mMap.addPolyline(rectLine);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        moveCameraToLocation();
    }

    private void setUi() {
        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mTitleTextView = (TextView) findViewById(R.id.map_info_title);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        LatLng wolox = new LatLng(WOLOX_LATITUDE, WOLOX_LONGITUDE);
        LatLng house = new LatLng(HOUSE_LATITUDE, HOUSE_LONGITUDE);
        LatLng itba = new LatLng(ITBA_LATITUDE, ITBA_LONGITUDE);

        googleMap.setMyLocationEnabled(true);

        googleMap.addMarker(new MarkerOptions()
                .title("Wolox")
                .snippet("Driving innovation")
                .position(wolox)
                .anchor(0.1f, 0.9f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_flag_right_pink)));
        googleMap.addMarker(new MarkerOptions()
                .title("My house")
                .position(house)
                .anchor(0.1f, 0.9f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_flag_right_pink)));
        googleMap.addMarker(new MarkerOptions()
                .title("ITBA")
                .position(itba)
                .anchor(0.1f, 0.9f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_flag_right_pink)));

        setListeners(googleMap);
        moveCameraToLocation();
        drawRoute();
    }

    private void drawRoute() {
        findDirections(HOUSE_LATITUDE, HOUSE_LONGITUDE, WOLOX_LATITUDE, WOLOX_LONGITUDE, Directions.MODE_DRIVING );
        findDirections(WOLOX_LATITUDE, WOLOX_LONGITUDE, ITBA_LATITUDE, ITBA_LONGITUDE, Directions.MODE_DRIVING );
    }

    private void setListeners(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mTitleTextView.setText(marker.getTitle());
                if (mMap != null) {
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), MORE_ZOOM);
                    mMap.animateCamera(location);
                }
                return true;
            }
        });
    }

    private void moveCameraToLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = manager.getLastKnownLocation(manager.getBestProvider(new Criteria(), false));
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(ZOOM)
                .build();
        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(position);
        if (mMap != null) {
            mMap.moveCamera(camUpdate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}