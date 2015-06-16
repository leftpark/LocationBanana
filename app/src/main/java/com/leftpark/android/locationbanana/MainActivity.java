package com.leftpark.android.locationbanana;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leftpark.android.locationbanana.util.LocationHelper;


public class MainActivity extends Activity implements View.OnClickListener {

    // TAG
    private static final String TAG = "Location_Banana";

    private static Context mContext;

    // LocationHelper
    private static LocationHelper mLocationHelper;

    // Google Map
    private GoogleMap googleMap;

    // LatLng
    private static LatLng curLatLng;

    // Views
    private Button mBtnPosition;

    // Values
    private String strPosition;
    double dLatitude;
    double dLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_main);

        try {
            // Loading map
            initializeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize Values
        initializeValue();

        // Initialize Views
        initializeView();

        // Initialize LocationProvider
        initLocation();

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

            // Set Enable of My Location
            //googleMap.setMyLocationEnabled(true);

            // Set Enable of Zoom
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    private void initializeValue() {
        strPosition = getString(R.string.coordinate, 0.0, 0.0);
    }

    private void initializeView() {
        mBtnPosition = (Button)findViewById(R.id.btn_position);
        mBtnPosition.setText(strPosition);
        mBtnPosition.setOnClickListener(this);
    }

    //+Initialize LocationProvider
    private void initLocation() {

        // Create LocationHelper
        mLocationHelper = new LocationHelper(mContext);

        if (mLocationHelper != null) {
            // Initialize LocationProvider
            mLocationHelper.init();
        }
    }
    //-Initialize LocationProvider

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocationHelper != null) {
            mLocationHelper.release();
            mLocationHelper = null;
        }

        if (mContext != null) {
            mContext = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_position:
                LatLng ll = new LatLng(dLatitude, dLongitude);
                showCurrentLocation(ll);
                break;
        }
    }

    //+Update Location String
    public void updateLocationString() {
        strPosition = getString(R.string.coordinate, dLatitude, dLongitude);
    }
    //-Update Location String

    //+Set Latitude
    public void setLatitude(double latitude) {
        dLatitude = latitude;
    }
    //-Set Latitude

    //+Get Latitude
    public double getLatitude() {
        double lat = 0.0;
        lat = dLatitude;

        return lat;
    }
    //-Get Latitude

    //+Set Longitude
    public void setLongitude(double longitude) {
        dLongitude = longitude;
    }
    //-Set Longitude

    //+Get Longitude
    public double getLongitude() {
        double lon = 0.0;
        lon = dLongitude;

        return lon;
    }
    //-Get Longitude

    //+Update Views
    public void updateView() {
        mBtnPosition.setText(strPosition);
    }
    //+Update Views

    //+Show Current Location
    private void showCurrentLocation(LatLng sll) {

        LatLng ll = sll;

        // Move the camera instantly to current postion with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

        // Marker
        googleMap.addMarker(new MarkerOptions()
                .title(getString(R.string.current_location))
                .snippet(mLocationHelper.getAddress(ll.latitude, ll.longitude).toString())
                .position(ll));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
    //-Show Current Location
}
