package com.example.vivek.rentalmates.tasks;

/**
 * Created by vivek on 3/14/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfileCollection;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.others.LocalUserProfile;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetUserProfileListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GetUserListTask_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    SharedPreferences prefs;
    IOException ioException;
    AppData appData;
    List<UserProfile> userProfiles = new ArrayList<>();

    public GetUserProfileListAsyncTask(Context context) {
        this.context = context;
        appData = AppData.getInstance();
        prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg = "";
        if (ufService == null){
            UserProfileApi.Builder builder1 = new UserProfileApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://kinetic-wind-814.appspot.com/_ah/api/");
            ufService = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            UserProfileCollection userProfileCollection= ufService.getUserProfileList(userProfileId).execute();
            if (userProfileCollection == null){
                Log.d(TAG, "expenses is null");
                msg = "SUCCESS_NO_PROFILES";
            }
            else {
                userProfiles = userProfileCollection.getItems();
                if (userProfiles != null) {
                    appData.storeUserProfileList(context, userProfiles);
                }
                msg = "SUCCESS_PROFILES";
            }
            Log.d(TAG, "inside addExpense");
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for GetUserProfileListAsyncTask");

        if (msg.equals("SUCCESS_PROFILES")){
            Toast.makeText(context, "UserProfile List retrieved successfully/Number of Users: " + userProfiles.size(), Toast.LENGTH_SHORT).show();
            appData.updateProfilePictures(context, userProfiles);
        }
        else if (msg.equals("SUCCESS_NO_PROFILES")) {
            Toast.makeText(context, "No user profiles available", Toast.LENGTH_LONG);
        }
        else if (msg.equals("EXCEPTION")){
            Log.d(TAG, "IOException: "+ ioException.getMessage());
            Toast.makeText(context, "IOException: "+ ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(TAG, "Unable to upload ExpenseData");
            Toast.makeText(context, "Unable to upload ExpenseData", Toast.LENGTH_LONG).show();
        }
    }
}
