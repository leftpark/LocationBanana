package com.leftpark.android.locationbanana.util;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.leftpark.android.locationbanana.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by leftpark on 2015-06-16.
 */
public class LocationHelper {

    // MainActivity
    private static MainActivity mMain;

    // Context
    private static Context mContext;

    // LocationManager
    private static LocationManager mLocationManager;

    // Construction with nothing
    public LocationHelper() {
        // Do something
    }

    // Construction with Context
    public LocationHelper(Context context) {
        mMain = (MainActivity)context;
        mContext = context;
    }

    // Get LocationManager Instance
    public static LocationManager getLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        return mLocationManager;
    }

    // Init LocationProvider
    public static void init() {
        LocationManager lm = getLocationManager();

        // Get high accuracy provider
        LocationProvider high = lm.getProvider(lm.getBestProvider(createFineCritera(),true));
        lm.requestLocationUpdates(high.getName(), 1000, 0, mHighListener);

        // Get low accuracy provider
        LocationProvider low = lm.getProvider(lm.getBestProvider(createCoarseCritera(), true));
        //lm.requestLocationUpdates(low.getName(), 1000, 0, mLowListener);
    }

    public static void release() {

        LocationManager lm = getLocationManager();

        // High Accuracy LocationListener
        if (mHighListener != null) {
            lm.removeUpdates(mHighListener);
            mHighListener = null;
        }

        // Low Accuracy LocationListener
        if (mLowListener != null) {
            lm.removeUpdates(mLowListener);
            mLowListener = null;
        }

        // LocationManager
        if (mLocationManager != null) {
            mLocationManager = null;
        }

        // Context
        if (mContext != null) {
            mContext = null;
        }

        // MainActivity
        if (mMain != null) {
            mMain = null;
        }
    }

    //+High Accuracy LocationListener
    private static LocationListener mHighListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Location l = location;

            // Latitude
            double latitude = l.getLatitude();
            mMain.setLatitude(latitude);

            // Longitude
            double longitude = l.getLongitude();
            mMain.setLongitude(longitude);

            // Update Location String
            mMain.updateLocationString();

            // Update View
            mMain.updateView();
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
    //-High Accuracy LocationListener

    //+Low Accuracy LocationListener
    private static LocationListener mLowListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

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
    //-Low Accuracy LocationListener

    // Get an address with latitude and longitude
    public String getAddress(double lat, double lon) {

        // Geocoder
        Geocoder geoCoder = new Geocoder(mContext, getCurrentLocale());

        List<Address> lAddress;

        String address = null;

        try {
            if (geoCoder != null) {
                lAddress = geoCoder.getFromLocation(lat, lon, 1);
                if (lAddress != null && lAddress.size() > 0) {
                    address = lAddress.get(0).getAddressLine(0).toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    // Get current locale
    private Locale getCurrentLocale() {
        Locale locale = mContext.getResources().getConfiguration().locale;

        return locale;
    }

    // This Criteria is high accuracy, high power, and cost.
    public static Criteria createFineCritera() {
        Criteria c = new Criteria();

        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);

        return c;
    }

    // This Criteria is less accuracy, high power, and cost.
    public static Criteria createCoarseCritera() {
        Criteria c = new Criteria();

        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);

        return c;
    }
}
