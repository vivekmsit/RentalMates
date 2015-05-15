package com.example.vivek.rentalmates.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.vivek.rentalmates.others.AppConstants;

import java.util.Map;

public class BackendApiService extends Service {
    private static final String TAG = "RentalMatesDebug";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public BackendApiService() {
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public BackendApiService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BackendApiService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void printSharedPreferenceValues(SharedPreferences prefs) {
        Map<String, ?> keys = prefs.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }


    public static void storeUserProfileId(Context context, Long id) {
        String msg;
        SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(AppConstants.USER_PROFILE_ID)) {
            msg = "userProfileId is already stored in shared preferences";
            Log.d(TAG, msg);
        } else {
            Log.i(TAG, "Saving userProfileId in shared preferences" + id);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(AppConstants.USER_PROFILE_ID, id);
            editor.commit();
        }
    }

    public static void storePrimaryFlatId(Context context, Long id) {
        SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        Log.d(TAG, "Saving flatInfoId in shared preferences" + id);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(AppConstants.PRIMARY_FLAT_ID, id);
        editor.commit();
    }

    public static void storeFlatExpenseGroupId(Context context, Long id) {
        SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        Log.d(TAG, "Saving flatE in shared preferences" + id);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(AppConstants.FLAT_EXPENSE_GROUP_ID, id);
        editor.commit();
    }

    public static void storePrimaryFlatName(Context context, String flatName) {
        SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        Log.d(TAG, "Saving flatName in shared preferences" + flatName);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PRIMARY_FLAT_NAME, flatName);
        editor.commit();
    }
}
