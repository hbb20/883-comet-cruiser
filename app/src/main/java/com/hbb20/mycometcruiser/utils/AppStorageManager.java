package com.hbb20.mycometcruiser.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hbb20 on 9/17/17.
 */
public class AppStorageManager extends Application {

    private static String PREFERENCE = "cometPreference";
    private static SharedPreferences sharedPreference;
    Context activityContext;

    public AppStorageManager(Context activityContext) {
        this.activityContext = activityContext;
    }

/*
These methods do use of activityContext so they can not be used as static
 */

    public static String getSharedStoredString(Context context, String key) {
        try {
            String demandedValue = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE).getString(key, "");
            return demandedValue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Returns INT value for givenKey. -99 shows that value is not stored.
     */
    public static int getSharedStoredInt(Context context, String key) {
        int demandedValue = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE).getInt(key, -99);
        return demandedValue;
    }

    /**
     * Stores string value with key "key"
     *  @param key :   key for storage
     * @param value : value to store for given key value
     */
    public static void setSharedStoreString(Context context, String key, String value) {
        sharedPreference = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Stores integer value with key "key"
     *  @param key :   key for storage
     * @param value : value to store for given key value
     */
    public static void setSharedStoreInt(Context context, String key, int value) {
        sharedPreference = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void removeEntry(Context context, String key) {
        sharedPreference = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        try {
            editor.remove(key);
        } catch (Exception ex) {
            Log.e("Remove preference error", ex.toString());
            Log.d("Probable error", "The value for " + key + " is not set yet"
            );
        }
        editor.commit();
    }

    /**
     * Stores string value with key "key"
     *  @param key :   key for storage
     * @param value : value to store for given key value
     */
    public static void setSharedStoreBoolean(Context context, String key, boolean value) {
        sharedPreference = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getSharedStoredBoolean(Context context, String key) {
        try {
            boolean demandedValue = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE).getBoolean(key, false);
            return demandedValue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean getSharedStoredBoolean(Context context, String key, boolean defaultValue) {
        try {
            boolean demandedValue = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE).getBoolean(key, defaultValue);
            return demandedValue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

