package com.hbb20.mycometcruiser.models;

import android.content.Context;

import com.hbb20.mycometcruiser.utils.AppStorageManager;

/**
 * Created by hbb20 on 9/17/17.
 */

public class Driver {
    private static String KEY_USER_NAME = "user_name";

    public static boolean isAlreadyLoggedIn(Context context){
        //when key_user_name is already stored
        return AppStorageManager.getSharedStoredString(context, KEY_USER_NAME).length() == 0;
    }
}
