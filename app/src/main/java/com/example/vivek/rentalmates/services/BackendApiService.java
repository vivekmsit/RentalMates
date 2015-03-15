package com.example.vivek.rentalmates.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.registration.Registration;
import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfileCollection;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BackendApiService extends Service {
    private static final String TAG = "RentalMatesDebug";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String SENDER_ID = "56111997016";
    private static final String USER_PROFILE_ID = "user_profile_id";
    private static final String PRIMARY_FLAT_ID = "primary_flat_id";

    private GoogleCloudMessaging gcm;
    private static UserProfileApi ufService = null;
    private static Registration regService = null;
    private static FlatInfoApi flatService = null;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public BackendApiService() {
        UserProfileApi.Builder builder1 = new UserProfileApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("https://kinetic-wind-814.appspot.com/_ah/api/");
        Registration.Builder builder2 = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("https://kinetic-wind-814.appspot.com/_ah/api/");
        FlatInfoApi.Builder builder3 = new FlatInfoApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("https://kinetic-wind-814.appspot.com/_ah/api/");
        ufService = builder1.build();
        regService = builder2.build();
        flatService = builder3.build();
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

    public static void printSharedPreferenceValues(SharedPreferences prefs){
        Map<String,?> keys = prefs.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }


    public static void storeUserProfileId(Context context, Long id){
        String msg = "";
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        if (prefs.contains(USER_PROFILE_ID)){
            msg = "userProfileId is already stored in shared preferences";
            Log.d(TAG, msg);
        } else {
            Log.i(TAG, "Saving userProfileId in shared preferences" + id);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(USER_PROFILE_ID, id);
            editor.commit();
        }
    }

    public static void storePrimaryFlatId(Context context, Long id){
        String msg = "";
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.d(TAG, "Saving flatInfoId in shared preferences" + id);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PRIMARY_FLAT_ID, id);
        editor.commit();
    }

    public void uploadUserProfile(final Context context, final UserProfile userProfile){
        AsyncTask<Context, Void, String> uploadUserProfileTask = new AsyncTask<Context, Void, String> (){

            String msg = "";
            @Override
            protected String doInBackground(Context... params) {
                try {
                    ufService.insert(userProfile).execute();
                    msg = "user profile uploaded successfully";
                    Log.d(TAG, msg);
                } catch (IOException e) {
                    msg = "IOException occurred while uploading user profile";
                    Log.d(TAG, msg);
                    e.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        };
        uploadUserProfileTask.execute();
    }

    public void queryUserProfiles(final Context context, final String type, final String value) {
        AsyncTask<Context, Void, String> queryUserProfilesTask = new AsyncTask<Context, Void, String> (){

            String msg = "";
            @Override
            protected String doInBackground(Context... params) {
                try {
                    UserProfileCollection ufc = ufService.queryUserProfiles(type, value).execute();
                    if (ufc == null){
                        msg = "No values are present";
                        Log.d(TAG, msg);
                        return msg;
                    }
                    List<UserProfile> profiles = ufc.getItems();
                    if (profiles == null) {
                        msg = "No profiles matched query";
                        Log.d(TAG, msg);
                        return msg;
                    }
                    for (UserProfile uf : profiles){
                        String msg = "";
                        msg = msg + "\n" + uf.getUserName();
                        Log.d(TAG, "msg is: " + msg);
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Exception occurred");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context, s, Toast.LENGTH_SHORT);
            }
        };
        queryUserProfilesTask.execute();
    }


    public void registerWithGcm(final Context context){
        AsyncTask<Context, Void, String> queryUserProfilesTask = new AsyncTask<Context, Void, String> (){

            String msg ="";
            @Override
            protected String doInBackground(Context... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    SharedPreferences prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                            Context.MODE_PRIVATE);

                    if (prefs.contains(PROPERTY_REG_ID)){
                        msg = "Device is already registered with GCM";
                        Log.d(TAG, msg);
                        return msg;
                    } else {
                        String regId = gcm.register(SENDER_ID);
                        // You should send the registration ID to your server over HTTP,
                        // so it can use GCM/HTTP or CCS to send messages to your app.
                        // The request to your server should be authenticated if your app
                        // is using accounts.
                        //sendRegistrationIdToBackend(regId); To implement
                        regService.register(regId).execute();
                        // Persist the regID - no need to register again.
                        int appVersion = 0;
                        try {
                            PackageInfo packageInfo = context.getPackageManager()
                                    .getPackageInfo(getPackageName(), 0);
                            appVersion =  packageInfo.versionCode;
                        } catch (PackageManager.NameNotFoundException e) {
                            // should never happen
                            throw new RuntimeException("Could not get package name: " + e);
                        }
                        Log.i(TAG, "Saving regId on app version " + appVersion);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(PROPERTY_REG_ID, regId);
                        editor.putInt(PROPERTY_APP_VERSION, appVersion);
                        editor.commit();
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.d(TAG, "Error: " + ex.getMessage());
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context, s, Toast.LENGTH_SHORT);
            }
        };


    }

    public static void createFlatInfo(final Context context, final FlatInfo flatInfo){
        AsyncTask<Context, Void, String> createFlatInfoTask = new AsyncTask<Context, Void, String> (){
            String msg ="";
            @Override
            protected String doInBackground(Context... params) {
                try {
                    FlatInfo uploadedFlatInfo = flatService.registerNewFlat(flatInfo).execute();
                    msg = "flat info uploaded successfully";
                    Log.d(TAG, msg);
                } catch (IOException e) {
                    msg = "IOException occurred while uploading flat info";
                    Log.d(TAG, msg);
                    e.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context, s, Toast.LENGTH_SHORT);
            }
        };
    }
}
