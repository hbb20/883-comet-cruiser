package com.hbb20.mycometcruiser.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

import com.hbb20.mycometcruiser.R;

/**
 * Created by hbb20 on 9/17/17.
 */

public class CometCruiserUtil {
    /**
     * Checks if either of wifi and cellular data is on or not. If device do not have connection due to turned off adapter shows message in textView
     *
     * @param context:                 : context of activity on which this request made
     * @return true for data adapter on
     */
    public static boolean isDataAdapterOn(Context context) {
        //Log.d("Data adapter check initiated"," ");
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        } catch (Exception e) {
            return false;
        }
    }
}
