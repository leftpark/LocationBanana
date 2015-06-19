package com.leftpark.android.locationbanana;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leftpark.android.locationbanana.util.LocationHelper;
import com.leftpark.android.locationbanana.util.ShareHelper;


public class MainActivity extends Activity implements View.OnClickListener {

    // TAG
    private static final String TAG = "Location_Banana";

    private static Context mContext;

    // LocationHelper
    private static LocationHelper mLocationHelper;

    // ShareHelper
    private static ShareHelper mShareHelper;

    // Google Map
    private GoogleMap googleMap;

    // Marker
    private static Marker mMarker;

    // LatLng
    private static LatLng curLatLng;

    // Views
    private TextView mTvCoordiateLat;   // TextView for Latitude
    private TextView mTvCoordiateLon;   // TextView for Longitude
    private TextView mTvCoordiateLatDMS;   // TextView for Latitude (DMS)
    private TextView mTvCoordiateLonDMS;   // TextView for Longitude (DMS)
    private Button mBtnShare;
    private Button mBtnPosition;

    // Values
    private String strCoordinateLat;
    private String strCoordinateLon;
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

        // Initialize LocationProvider
        initLocation();

        // Initialize ShareHelper
        initShare();

        // Initialize Values
        initializeValue();

        // Initialize Views
        initializeView();

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
            //googleMap.setOnMyLocationChangeListener(mMLCListener);

            // Set Enable of Zoom
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    private void initializeValue() {

        if (mLocationHelper == null) {
            dLatitude = 0;
            dLongitude = 0;
        } else if (mLocationHelper.hasLastLocation()) {
            dLatitude = mLocationHelper.getLastLatitude();
            dLongitude = mLocationHelper.getLastLongitude();
        } else {
            dLatitude = mLocationHelper.LATITUDE_SUSINLEE;
            dLongitude = mLocationHelper.LONGITUDE_SUSINLEE;
        }

        strCoordinateLat = getString(R.string.coordinate_latitude, dLatitude);
        strCoordinateLon = getString(R.string.coordinate_longitude, dLongitude);
    }

    private void initializeView() {

        // Coordinate TextView
        mTvCoordiateLat = (TextView)findViewById(R.id.tv_coordinate_latitude);
        mTvCoordiateLon = (TextView)findViewById(R.id.tv_coordinate_longitude);

        mTvCoordiateLatDMS = (TextView)findViewById(R.id.tv_coordinate_latitude_dms);
        mTvCoordiateLonDMS = (TextView)findViewById(R.id.tv_coordinate_longitude_dms);

        // Set Shar Button
        mBtnShare = (Button)findViewById(R.id.btn_share);
        mBtnShare.setOnClickListener(this);

        // Set Position Button
        mBtnPosition = (Button)findViewById(R.id.btn_position);
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

    //+Initialize ShareHelper
    private void initShare() {
        // Create ShareHelper
        mShareHelper = new ShareHelper(mContext);
    }
    //-Initialize ShareHelper

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();

        // Test
        updateCoordinateString();
        updateView();
        showCurrentLocation();
        // Test
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocationHelper != null) {
            mLocationHelper.release();
            mLocationHelper = null;
        }

        if (mShareHelper != null) {
            mShareHelper = null;
        }

        if (mContext != null) {
            mContext = null;
        }

        if (mMarker != null) {
            mMarker = null;
        }
    }

    // GoogleMap.OnMyLocationChangeListener
    private GoogleMap.OnMyLocationChangeListener mMLCListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {

        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_share:
                Intent intent = mShareHelper.getShareIntent();
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_via)));
                //Toast.makeText(mContext, getResources().getString(R.string.share), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_position:
                showCurrentLocation();
                break;
        }
        return;
    }

    //+Update Location String
    public void updateCoordinateString() {
        strCoordinateLat = getString(R.string.coordinate_latitude, dLatitude);
        strCoordinateLon = getString(R.string.coordinate_longitude, dLongitude);
    }
    //-Update Location String

    //+Set Latitude
    public boolean setLatitude(double latitude) {
        if (dLatitude == latitude) {
            return false;
        }
        dLatitude = latitude;
        return true;
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
    public boolean setLongitude(double longitude) {
        if (dLongitude == longitude ) {
            return false;
        }
        dLongitude = longitude;
        return true;
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
        mTvCoordiateLat.setText(strCoordinateLat);
        mTvCoordiateLon.setText(strCoordinateLon);

        mTvCoordiateLatDMS.setText(getString(R.string.coordinate_latitude_dms, mLocationHelper.getLatitudeDMS()));
        mTvCoordiateLonDMS.setText(getString(R.string.coordinate_longitude_dms, mLocationHelper.getLongitudeDMS()));
    }
    //+Update Views

    //+Show Current Location
    private void showCurrentLocation() {

        if (mMarker != null) {
            mMarker.remove();
        }

        LatLng ll = new LatLng(dLatitude, dLongitude);

        // Move the camera instantly to current postion with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

        // Marker
        mMarker = googleMap.addMarker(new MarkerOptions()
                .title(getString(R.string.current_location))
                .snippet(mLocationHelper.getAddress(ll.latitude, ll.longitude).toString())
                .position(ll));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
    //-Show Current Location

    // Get Marker
    public Marker getMarker() {
        Marker marker;
        marker = mMarker;
        return marker;
    }
}
