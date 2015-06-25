package com.leftpark.android.locationbanana.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by P1410006 on 2015-06-25.
 */
public class NetworkHelper {

    private static final String TAG = "LocationBanana";

    // Context
    private Context mContext;

    public NetworkHelper() {

    }

    public NetworkHelper(Context context) {
        mContext = context;
    }

    public boolean isNetworkConnected() {
        boolean status = false;

        try {
            ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
