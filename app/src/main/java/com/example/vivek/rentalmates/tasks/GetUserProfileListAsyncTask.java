package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfileCollection;
import com.example.vivek.rentalmates.interfaces.OnUserProfileListReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetUserProfileListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GetUserListTask_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private AppData appData;
    private List<UserProfile> userProfiles = new ArrayList<>();
    private OnUserProfileListReceiver receiver;

    public GetUserProfileListAsyncTask(Context context) {
        this.context = context;
        appData = AppData.getInstance();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnUserProfileListReceiver(OnUserProfileListReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (ufService == null) {
            UserProfileApi.Builder builder1 = new UserProfileApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            ufService = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            UserProfileCollection userProfileCollection = ufService.getUserProfileList(userProfileId).execute();
            if (userProfileCollection == null) {
                Log.d(TAG, "expenses is null");
                msg = "SUCCESS_NO_PROFILES";
            } else {
                userProfiles = userProfileCollection.getItems();
                if (userProfiles != null) {
                    appData.storeUserProfileList(context, userProfiles);
                }
                msg = "SUCCESS_PROFILES";
            }
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

        switch (msg) {
            case "SUCCESS_PROFILES":
                if (receiver != null) {
                    receiver.onUserProfileListLoadSuccessful(userProfiles);
                }
                break;
            case "SUCCESS_NO_PROFILES":
                if (receiver != null) {
                    receiver.onUserProfileListLoadSuccessful(null);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onUserProfileListLoadFailed();
                }
                break;
            default:
                break;
        }
    }
}
