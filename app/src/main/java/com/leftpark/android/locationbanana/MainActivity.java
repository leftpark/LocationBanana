package com.leftpark.android.locationbanana;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends Activity implements View.OnClickListener {

    // TAG
    private static final String TAG = "Location_Banana";

    private static Context mContext;

    // LocationManager
    private static LocationManager mLocationManager;

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
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize Values
        initializeValue();

        // Initialize Views
        initializeView();

        // Intialize Gps
        initGps();

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    private void initializeValue() {
        strPosition = getString(R.string.position, 0.0, 0.0);
    }

    private void initializeView() {
        mBtnPosition = (Button)findViewById(R.id.btn_position);
        mBtnPosition.setText(strPosition);
        mBtnPosition.setOnClickListener(this);
    }

    private void initGps() {
        LocationManager lm = getLocationManager();

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_position:
                LatLng ll = new LatLng(dLatitude, dLongitude);
                showCurrentPosition(ll);
                break;
        }
    }

    // Get Current Postion
    private void getCurrentPosition() {
        LocationManager lm = getLocationManager();
    }

    private LocationManager getLocationManager() {
        if (mLocationManager == null) {
            LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            mLocationManager = lm;
        }

        return mLocationManager;
    }

    //+LoncationListener
    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Location l = location;

            // Get latitude
            dLatitude = l.getLatitude();

            // Get longitude
            dLongitude = l.getLongitude();

            strPosition = getString(R.string.position, dLatitude, dLongitude);

            updateView();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    //-LocationListener

    private void updateView() {
        mBtnPosition.setText(strPosition);
    }

    private void showCurrentPosition(LatLng sll) {

        LatLng ll = sll;

        // Move the camera instantly to current postion with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

        // Marker
        googleMap.addMarker(new MarkerOptions()
                .title("Current Postion")
                .snippet(getString(R.string.position, dLatitude, dLongitude))
                .position(ll));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }
}
