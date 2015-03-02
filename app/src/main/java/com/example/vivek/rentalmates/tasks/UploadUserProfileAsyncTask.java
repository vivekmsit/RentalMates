package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vivek on 1/8/2015.
 */
public class UploadUserProfileAsyncTask extends AsyncTask<Context, Void, String> {
    private static UserProfileApi ufService = null;
    private UserProfile uf = null;
    private String message = null;
    private Context context;
    private static final String TAG = "RentalMatesDebug";

    public UploadUserProfileAsyncTask(Context context, final UserProfile userProfile) {
        this.context = context;
        this.uf = userProfile;
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
            msg = "User Profile uploaded successfully";
            Log.d(TAG, "inside insert");
        } catch (IOException e) {
            msg = "Exception occurred";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("USERPROFILEAPI").log(Level.INFO, msg);
    }
}
