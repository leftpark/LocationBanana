package com.leftpark.android.locationbanana.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.leftpark.android.locationbanana.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


/**
 * Created by leftpark on 2015-06-23.
 */
public class ShortenerHelper extends AsyncTask<String, Void, String> {

    private static final String TAG = "LocationBanana";

    // Debug
    private static final boolean DBG = true;

    private final String GOOGLE_URL = "https://www.googleapis.com/urlshortener/v1/url";

    private MainActivity mMain;

    private Context mContext;

    private String mLongURL = null;

    private String mShortURL = null;

    public ShortenerHelper(Context context) {
        mContext = context;
        mMain = (MainActivity)context;
    }

    @Override
    protected String doInBackground(String... params) {

        mLongURL = params[0];

        try {
            // Set connection timeout to 5 secs and socket timeout to 10 secs
            HttpParams httpParams = new BasicHttpParams();
            int timeoutConnection = 5000;
            HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);

            int timetoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParams, timetoutSocket);

            HttpClient hc = new DefaultHttpClient(httpParams);

            HttpPost request = new HttpPost(GOOGLE_URL);
            request.setHeader("Content-type", "application/json");
            request.setHeader("Accept", "application/json");

            JSONObject obj = new JSONObject();
            obj.put("longUrl", mLongURL);
            request.setEntity(new StringEntity(obj.toString(), "UTF-8"));

            HttpResponse response = hc.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                return out.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            return;
        }

        try {
            final JSONObject json = new JSONObject(result);
            final String id = json.getString("id");
            if (json.has("id")) {
                setShortURL(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setShortURL(String url) {
        if (DBG) Log.d(TAG, "setShortURL = " + url);
        Toast.makeText(mContext, url, Toast.LENGTH_LONG).show();
        mShortURL = url;
        mMain.setShortenerTextView(url);
    }

    public String getShortURL() {
        if (mShortURL == null) {
            return null;
        }
        String url;
        url = mShortURL;
        return url;
    }
}
