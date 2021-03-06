package com.leftpark.android.locationbanana.util;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.leftpark.android.locationbanana.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by leftpark on 2015-06-16.
 */
public class LocationHelper implements Parcelable{

    // TAG
    private static final String TAG = "LocationBanana";

    // Default Latitude and Longitude of SUSINLEE in SEOUL
    public static final double LATITUDE_SUSINLEE = 37.571006;
    public static final double LONGITUDE_SUSINLEE = 126.976940;

    // Values
    private static double dLatitude = LATITUDE_SUSINLEE;
    private static double dLongitude = LONGITUDE_SUSINLEE;

    // DMS
    public static final int DMS_TYPE_URL = 1;
    public static final int DMS_TYPE_STR = 2;

    // MainActivity
    private static MainActivity mMain;

    // Context
    private static Context mContext;

    // LocationManager
    private static LocationManager mLocationManager;

    // High accuracy provider
    private static LocationProvider mLocationProvider;

    // High Accuracy LocationListener
    private static LocationListener mHighListener;

    // Low Accuracy LocationListener
    private static LocationListener mLowListener;

    // GpsStatus.Listener
    private static GpsStatus.Listener mGpsStatusListener;

    // GpsStatus
    private static int mGpsStatus = 0;

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

        // [FIX] 2015.06.23. leftpark, fix IllegalArgumentException: invalid listener: null
        initListener();

        initGpsStatusListener();

        // Get high accuracy provider
        mLocationProvider = lm.getProvider(lm.getBestProvider(createFineCritera(),true));
        lm.requestLocationUpdates(mLocationProvider.getName(), 1000, 0, mHighListener);

        // Get low accuracy provider
        LocationProvider low = lm.getProvider(lm.getBestProvider(createCoarseCritera(), true));
        //lm.requestLocationUpdates(low.getName(), 1000, 0, mLowListener);

        lm.addGpsStatusListener(mGpsStatusListener);

        if (hasLastLocation()) {
            setLatitude(getLastLatitude());
            setLongitude(getLastLongitude());
        } else {
            setLatitude(LATITUDE_SUSINLEE);
            setLongitude(LONGITUDE_SUSINLEE);
        }
    }

    public static void release() {

        LocationManager lm = getLocationManager();

        if (mGpsStatusListener != null) {
            lm.removeGpsStatusListener(mGpsStatusListener);
            mGpsStatusListener = null;
        }

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

        if (mLocationProvider != null) {
            mLocationProvider = null;
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

    // [FIX_BEGIN] 2015.06.23. leftpark, fix IllegalArgumentException: invalid listener: null
    private static void initListener() {
        //+High Accuracy LocationListener
        mHighListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG,"onLocationChanged(h) S");
                boolean changed = false;
                Location l = location;

                // Latitude
                double latitude = l.getLatitude();
                if (setLatitude(latitude)) {
                    changed = true;
                }

                // Longitude
                double longitude = l.getLongitude();
                if (setLongitude(longitude)) {
                    changed = true;
                }

                if (changed) {
                    // Update View
                    mMain.updateView();
                }
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
        mLowListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged(l) S");
                boolean changed = false;
                Location l = location;

                // Latitude
                double latitude = l.getLatitude();
                if (setLatitude(latitude)) {
                    changed = true;
                }

                // Longitude
                double longitude = l.getLongitude();
                if (setLongitude(longitude)) {
                    changed = true;
                }

                if (changed) {
                    // Update View
                    mMain.updateView();
                }
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
    }
    // [FIX_EDN] 2015.06.23. leftpark, fix IllegalArgumentException: invalid listener: null

    /**
     * Initialize GpsStatus.Listener
     * 0x00000001 : GPS_EVENT_STARTED (The GPS system has started)
     * 0x00000002 : GPS_EVENT_STOPPED (The GPS system has stopped)
     * 0x00000003 : GPS_EVNET_FIRST_FIX (Call getTimeToFirstFix() to find the time from start to first fix)
     * 0x00000004 : GPS_EVENT_SATELLITE_STATUS(Call getSatellites() to retrieve the status fot each satellite)
     */
    private static void initGpsStatusListener() {
        mGpsStatusListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                // Set GpsStatus
                if (setGpsStatus(event)) {
                    Log.d(TAG,"onGpsStatusChanged("+event+")");
                    // UpdateView
                    mMain.updateView();
                }
            }
        };
    }

    // Get Address
    public String getAddress() {
        String add = "";

        add = getAddress(dLatitude, dLongitude);

        return add;
    }

    // Get an address with latitude and longitude
    public String getAddress(double lat, double lon) {

        // Geocoder
        Geocoder geoCoder = new Geocoder(mContext, getCurrentLocale());

        List<Address> lAddress;

        String address = "";

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

    // Check LastLocation
    public static boolean hasLastLocation() {

        if (mLocationManager == null || mLocationManager.getLastKnownLocation(mLocationProvider.getName()) == null) {
            return false;
        }
        return true;
    }

    // Return the last latitude value
    public static double getLastLatitude() {
        Location l = mLocationManager.getLastKnownLocation(mLocationProvider.getName());
        return l.getLatitude();
    }

    // Return the last longitude value
    public static double getLastLongitude() {
        Location l = mLocationManager.getLastKnownLocation(mLocationProvider.getName());
        return l.getLongitude();
    }

    // Get current locale
    private Locale getCurrentLocale() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        return locale;
    }

    // Set latitude
    public static boolean setLatitude(double latitude) {
        if (dLatitude != latitude) {
            dLatitude = latitude;
            return true;
        }
        return false;
    }

    // Set longitude
    public static boolean setLongitude(double longitude) {
        if (dLongitude != longitude) {
            dLongitude = longitude;
            return true;
        }
        return false;
    }

    // Get Latitude
    public double getLatitude() {
        double lat = 0;
        lat = dLatitude;
        return lat;
    }

    // Get Longitude
    public double getLongitude() {
        double lon = 0;
        lon = dLongitude;
        return lon;
    }

    // Return DMS latitude
    public String getLatitudeDMS(int type) {
        String ddLat = "";

        if (hasLastLocation()) {
            ddLat = dd2dms(type, getLastLatitude());
        } else {
            ddLat = dd2dms(type, LATITUDE_SUSINLEE);
        }

        if (type == DMS_TYPE_STR) {
            ddLat = ddLat + "\"N";
        } else if (type == DMS_TYPE_URL) {
            ddLat = ddLat + "%22N";
        }

        return ddLat;
    }

    // Return DMS longitude
    public String getLongitudeDMS(int type) {
        String ddLon = "";

        if (hasLastLocation()) {
            ddLon = dd2dms(type, getLastLongitude());
        } else {
            ddLon = dd2dms(type, LONGITUDE_SUSINLEE);
        }

        if (type == DMS_TYPE_STR) {
            ddLon = ddLon + "\"E";
        } else if (type == DMS_TYPE_URL) {
            ddLon = ddLon + "%22E";
        }

        return ddLon;
    }

    public String dd2dms(int type, double decimal_degrees) {
        double dd = decimal_degrees;

        int d = new Integer((int)dd);

        int m = new Integer ((int)((dd - d) * 60));

        double s = (dd - (double)d - ((double)m/60))*3600;

        String msg = "[D:"+d+"][M:"+m+"][S:"+s+"]";

        String strDMS = "";
        if (type == DMS_TYPE_STR) {
            strDMS = String.format("%d", d) + "�" + String.format("%02d", m) + "'" + String.format("%.1f", s);
        } else if (type == DMS_TYPE_URL) {
            strDMS = String.format("%d", d) + "%C2%B0" + String.format("%02d", m) + "'" + String.format("%.1f", s);
        }

        return strDMS;
    }

    private static boolean setGpsStatus(int status) {
        if (mGpsStatus != status) {
            mGpsStatus = status;
            return true;
        }
        return false;
    }

    public int getGpsStatus() {
        return mGpsStatus;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
