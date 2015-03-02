package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfileCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vivek on 1/8/2015.
 */
public class QueryUserProfilesAsyncTask extends AsyncTask<Context, Void, String> {
    private static UserProfileApi ufService = null;
    private Context context;
    private String type = null;
    private String value = null;
    private static final String TAG = "RentalMatesDebug";

    public QueryUserProfilesAsyncTask(Context context, final String type, final String value) {
        this.context = context;
        this.type = type;
        this.value = value;
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
            UserProfileCollection ufc = ufService.queryUserProfiles(type, value).execute();
            if (ufc == null){
                Log.d(TAG, "No values are present");
                return msg;
            }
            List<UserProfile> profiles = ufc.getItems();
            if (profiles == null) {
                msg = "No profiles matched query";
                return msg;
            }
            for (UserProfile uf : profiles){
                msg = msg + "\n" + uf.getUserName();
                Log.d(TAG, "msg is: " + msg);
            }
            Log.d(TAG, "inside query");
        } catch (IOException e) {
            msg = "Exception occurred";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("QUERYUSERPROFILEAPI").log(Level.INFO, msg);
    }
}
