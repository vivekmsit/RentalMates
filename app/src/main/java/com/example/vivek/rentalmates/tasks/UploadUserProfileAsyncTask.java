package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.DetermineFlatActivity;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MyLoginActivity;
import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by vivek on 1/8/2015.
 */
public class UploadUserProfileAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "UploadUser_Debug";

    private static UserProfileApi ufService = null;
    private UserProfile uf = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private MyLoginActivity activity;
    private Long primaryFlatId;

    public UploadUserProfileAsyncTask(MyLoginActivity myLoginActivity, Context context, final UserProfile userProfile) {
        this.context = context;
        this.uf = userProfile;
        activity = myLoginActivity;
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
            UserProfile uploadedUserProfile = ufService.insert(uf).execute();
            BackendApiService.storeUserProfileId(this.context, uploadedUserProfile.getId());
            if (uploadedUserProfile.getNumberOfFlats()==0){
                msg = "SUCCESS_NO_FLAT_REGISTERED";
            } else {
                msg = "SUCCESS_FLAT_REGISTERED";
                primaryFlatId = uploadedUserProfile.getPrimaryFlatId();
                BackendApiService.storePrimaryFlatId(this.context, primaryFlatId);
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(AppConstants.USER_PROFILE_UPDATED, 1);
            editor.commit();
            Log.d(TAG, "inside insert");
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for UploadUserProfileAsyncTask");
        activity.setSignInClicked(false);

        if (msg.equals("SUCCESS_NO_FLAT_REGISTERED")){
            Toast.makeText(context, "UserProfile uploaded", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, DetermineFlatActivity.class);
            intent.putExtra("FLAT_REGISTERED", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        else if (msg.equals("SUCCESS_FLAT_REGISTERED")) {
            Toast.makeText(context, "Flat already registered, Retrieving FlatInfo list", Toast.LENGTH_SHORT).show();
            new GetFlatInfoListAsyncTask(this.context, true).execute();
        }
        else if (msg.equals("EXCEPTION")){
            Log.d(TAG, "IOException: "+ ioException.getMessage());
            Toast.makeText(context, "IOException: "+ ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(TAG, "Unable to upload UserProfile data");
            Toast.makeText(context, "Unable to upload UserProfile data", Toast.LENGTH_LONG).show();
        }
    }
}
