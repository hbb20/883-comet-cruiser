package com.hbb20.mycometcruiser.models;

import android.content.Context;

import com.hbb20.mycometcruiser.utils.AppStorageManager;

/**
 * Created by hbb20 on 9/17/17.
 */

public class Driver {
    public static String KEY_USER_ID = "user_name";
    public static String KEY_NAME = "name";

    public static boolean isAlreadyLoggedIn(Context context){
        //when key_user_name is already stored
        return AppStorageManager.getSharedStoredString(context, KEY_USER_ID).length() !=0;
    }

    public static void setLoggedIn(Context context,String superman) {
        AppStorageManager.setSharedStoreString(context, KEY_USER_ID,superman);
    }
}
