package com.leftpark.android.locationbanana.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leftpark.android.locationbanana.MainActivity;

/**
 * Created by leftpark on 2015-06-16.
 */
public class ShareHelper {

    // TAG
    private static final String TAG = "Location_Banana";

    // Google Map URl
    private static final String URL_GOOGLE_MAP = "https://www.google.com/maps/@";
    private static final String URL_GOOGLE_MAP_PLACE = "https://www.google.com/maps/place/";

    // Coordinate(Latitude, Longitude)
    private static String strCoordiante = "";

    // MainActivity
    private static MainActivity mMain;

    // Context
    private static Context mContext;

    // LocationHelper
    private static LocationHelper mLocationHelper;

    public ShareHelper() {
        // do something
    }

    public ShareHelper(Context context) {
        mMain = (MainActivity)context;
        mContext = context;

        // Create LocationHelper
        mLocationHelper = new LocationHelper();
    }

    public String getUrlGoogleMap() {
        String url = "";

        String latitudeDMS = mLocationHelper.getLatitudeDMS();
        String longitudeDMS = mLocationHelper.getLongitudeDMS();

        double latitude = mLocationHelper.getLatitude();
        double longitude = mLocationHelper.getLongitude();

        // Decimal Degrees
        //url = URL_GOOGLE_MAP+Double.toString(latitude)+","+Double.toString(longitude)+",15z";

        // DMS
        url = URL_GOOGLE_MAP_PLACE
                +latitudeDMS
                +"+"
                +longitudeDMS
                +"/@"
                +String.format("%.06f",latitude)
                +","
                +String.format("%.06f",longitude)
                +",15z";

        return url;
    }

    // Make Shared Intent
    public Intent getShareIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getUrlGoogleMap());

        return intent;
    }
}
