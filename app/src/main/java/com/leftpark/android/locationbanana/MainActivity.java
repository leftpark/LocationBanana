package com.leftpark.android.locationbanana;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leftpark.android.locationbanana.util.LocationHelper;
import com.leftpark.android.locationbanana.util.NetworkHelper;
import com.leftpark.android.locationbanana.util.ShareHelper;
import com.leftpark.android.locationbanana.util.ShortenerHelper;

/**
 * Created by leftpark.
 */
public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {

    // TAG
    private static final String TAG = "LocationBanana";

    // Debug
    private static final boolean DBG = true;

    private static Context mContext;

    // LocationHelper
    private static LocationHelper mLocationHelper;

    // ShareHelper
    private static ShareHelper mShareHelper;

    // Google Map
    private GoogleMap googleMap;

    // Google Map.Camera.zoom
    private float mZoom = 0f;

    // Marker
    private static Marker mMarker;

    // SensorManager for Compass
    private SensorManager mSensorManager;
    // Record the compass picture angle turned
    private float mCurrentDegree = 0f;

    // Views
    private TextView mTvCoordiateLat;   // TextView for Latitude
    private TextView mTvCoordiateLon;   // TextView for Longitude
    private TextView mTvCoordiateLatDMS;   // TextView for Latitude (DMS)
    private TextView mTvCoordiateLonDMS;   // TextView for Longitude (DMS)
    private TextView mTvAddress;    // TextView for Address
    //private Button mBtnShare;
    private ImageButton mBtnShare;
    //private Button mBtnPosition;
    private ImageButton mBtnPosition;
    private Button mBtnShortener;   // Button for URL Shortener
    private TextView mTvShortener;  // TextView for URL Shortener
    private ImageView mIvCompass;   // Compass Needle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        mContext = this;

        if (isNetworkConnected()) {
            setContentView(R.layout.activity_main);

            initMainLayout();
        } else {
            setContentView(R.layout.activity_empty);

            initEmptyLayout();
        }

    }

    @Override
    public void recreate() {
        super.recreate();
    }

    // activity_main.xml
    private void initMainLayout() {

        try {
            // Loading map
            initializeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize SensorManager
        getSensorManager();

        // Initialize LocationProvider
        initLocation();

        // Initialize ShareHelper
        initShare();

        // Initialize Views
        initializeView();
    }

    // Get SensorManager Instance
    private SensorManager getSensorManager() {
        if (mSensorManager == null) {
            mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        }
        return mSensorManager;
    }

    // activity_empty.xml
    private void initEmptyLayout() {

        Button btnRefresh = (Button)findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.btn_refresh:
                        //recreate();
                        break;
                }
            }
        });
    }

    // Check the status of Network Connection
    private boolean isNetworkConnected() {
        NetworkHelper networkHelper = new NetworkHelper(mContext);
        if (networkHelper != null) {
            return networkHelper.isNetworkConnected();
        }
        return false;
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

            // Setting a ChangeListener of my location for the Google Map
            //googleMap.setOnMyLocationChangeListener(mMLCListener);

            // Setting a ClickListener for the Google Map
            googleMap.setOnMapClickListener(mMCListener);

            // Set Enable of Zoom
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        }
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

    private void initializeView() {

        // Coordinate TextView
        mTvCoordiateLat = (TextView)findViewById(R.id.tv_coordinate_latitude);
        mTvCoordiateLon = (TextView)findViewById(R.id.tv_coordinate_longitude);

        mTvCoordiateLatDMS = (TextView)findViewById(R.id.tv_coordinate_latitude_dms);
        mTvCoordiateLonDMS = (TextView)findViewById(R.id.tv_coordinate_longitude_dms);

        // Address TextView
        mTvAddress = (TextView)findViewById(R.id.tv_address);

        // Initialize Shar Button
        //mBtnShare = (Button)findViewById(R.id.btn_share);
        mBtnShare = (ImageButton)findViewById(R.id.btn_share);
        mBtnShare.setOnClickListener(this);

        // Initialize Position Button
        //mBtnPosition = (Button)findViewById(R.id.btn_position);
        mBtnPosition = (ImageButton)findViewById(R.id.btn_position);
        mBtnPosition.setOnClickListener(this);

        // Initialize Shortener Button
        mBtnShortener = (Button)findViewById(R.id.btn_shortener);
        mBtnShortener.setOnClickListener(this);

        // Initialize Shortener TextView
        mTvShortener = (TextView)findViewById(R.id.tv_shortener);

        // Initialize Compass Needle ImageView
        mIvCompass = (ImageView)findViewById(R.id.iv_compass_needle);
    }

    //+Update Views
    public void updateView() {
        synchronized (this) {
            mTvCoordiateLat.setText(getStrLatitude());
            mTvCoordiateLon.setText(getStrLongitude());

            mTvCoordiateLatDMS.setText(getStrLatitudeDMS());
            mTvCoordiateLonDMS.setText(getStrLongitudeDMS());

            mTvAddress.setText(getAddress());

            if (mLocationHelper != null) {
                int gpsStatus = mLocationHelper.getGpsStatus();
                if (gpsStatus == GpsStatus.GPS_EVENT_STOPPED) {
                    mTvShortener.setText(R.string.gps_status_stopped);
                    mTvShortener.setBackgroundResource(R.drawable.four_status_gray);
                } else if (gpsStatus == GpsStatus.GPS_EVENT_STARTED) {
                    mTvShortener.setText(R.string.gps_status_started);
                    mTvShortener.setBackgroundResource(R.drawable.four_status_red);
                } else if (gpsStatus == GpsStatus.GPS_EVENT_FIRST_FIX) {
                    mTvShortener.setText(R.string.gps_status_first_fix);
                    mTvShortener.setBackgroundResource(R.drawable.four_status_green);
                } else if (gpsStatus == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                    mTvShortener.setText(R.string.gps_status_satellite);
                    mTvShortener.setBackgroundResource(R.drawable.four_status_blue);
                }
            }
        }
    }
    //+Update Views

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        if (isNetworkConnected()) {

            // Register SensorEventListener
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

            initializeMap();

            // Test
            updateView();
            showCurrentLocation();
            // Test
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // How long the animation will take place
        ra.setDuration(210);

        // Set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        mIvCompass.startAnimation(ra);
        mCurrentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save LocationHelper to Bundle
        outState.putParcelable("LocationHelepr", mLocationHelper);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Get LocationHelper from Bundle
        mLocationHelper = savedInstanceState.getParcelable("LocationHelper");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");

        // Unregister SensorEventListener
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestory()");

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

    /**
     * 2015.06.26. Friday
     * Set a ClickListener for Google Map
     */
    private GoogleMap.OnMapClickListener mMCListener = new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng latLng) {
            showCurrentLocation(latLng);
        }
    };

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
                break;
            case R.id.btn_position:
                showCurrentLocation();
                break;
            case R.id.btn_shortener:
                //getShortener();
                break;
        }
        return;
    }

    private String getStrLatitude() {
        String latitude = "";
        if (mLocationHelper != null) {
            latitude = getString(R.string.coordinate_latitude, mLocationHelper.getLatitude());
        }
        return latitude;
    }

    private String getStrLatitudeDMS() {
        String latitudeDMS = "";
        if (mLocationHelper != null) {
            latitudeDMS = getString(R.string.coordinate_latitude_dms, mLocationHelper.getLatitudeDMS(LocationHelper.DMS_TYPE_STR));
        }
        return latitudeDMS;
    }

    private String getStrLongitude() {
        String longitude = "";
        if (mLocationHelper != null) {
            longitude = getString(R.string.coordinate_longitude, mLocationHelper.getLongitude());
        }
        return longitude;
    }

    private String getStrLongitudeDMS() {
        String longitudeDMS = "";
        if (mLocationHelper != null) {
            longitudeDMS = getString(R.string.coordinate_longitude_dms, mLocationHelper.getLongitudeDMS(LocationHelper.DMS_TYPE_STR));
        }
        return longitudeDMS;
    }

    //+Get Latitude
    public double getLatitude() {
        double lat = 0.0;
        if (mLocationHelper != null) {
            lat = mLocationHelper.getLatitude();
        }
        return lat;
    }
    //-Get Latitude

    //+Get Longitude
    public double getLongitude() {
        double lon = 0.0;
        if (mLocationHelper != null) {
            lon = mLocationHelper.getLongitude();
        }

        return lon;
    }
    //-Get Longitude

    // Get Address
    private String getAddress() {
        String add = "";
        if (mLocationHelper != null) {
            add = getString(R.string.address, mLocationHelper.getAddress());
        }
        return add;
    }

    /**
     * Show Current Location
     */
    private void showCurrentLocation() {

        LatLng ll = new LatLng(getLatitude(), getLongitude());

        showCurrentLocation(ll);
    }

    /**
     * 2015.06.26. Friday
     * Show Current Location
     * @param ll LatLng object for adding Marker on the Map
     */
    private void showCurrentLocation(LatLng ll) {
        if (mMarker != null) {
            mMarker.remove();
        }

        if (mZoom == 0) {
            mZoom = 15;
        } else {
            mZoom = googleMap.getCameraPosition().zoom;
        }

        // Move the camera instantly to current postion
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, mZoom));

        // Marker
        mMarker = googleMap.addMarker(new MarkerOptions()
                .title(getString(R.string.current_location))
                .snippet(mLocationHelper.getAddress(ll.latitude, ll.longitude).toString())
                .position(ll));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom), 2000, null);
    }

    // Get Marker
    public Marker getMarker() {
        Marker marker;
        marker = mMarker;
        return marker;
    }

    // Set Shortener
    public void setShortenerTextView(String url) {
        mTvShortener.setText(url);
    }

    // Get Shortener
    private boolean getShortener() {
        if (DBG) Log.d(TAG,"getShortener()");

        ShortenerHelper shortenerHelper = new ShortenerHelper(mContext);
        if (shortenerHelper != null) {
            if (mShareHelper != null) {
                String longURL = mShareHelper.getUrlGoogleMap();
                shortenerHelper.execute(longURL);
                return true;
            }
        }
        return false;
    }
}
